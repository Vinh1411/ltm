/* ------------------
 Client
 usage: java Client [Server hostname] [Server RTSP listening port] [Video file requested]
 ---------------------- */

package videostreaming;

import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;

public class Client {
    //Giao diện
    JFrame f=new JFrame("Client");
    JButton setupButton=new JButton("Setup");
    JButton playButton=new JButton("Play");
    JButton pauseButton=new JButton("Pause");
    JButton tearButton=new JButton("Teardown");
    JPanel mainPanel=new JPanel();
    JPanel buttonPanel=new JPanel();
    JLabel iconLabel=new JLabel();

    ImageIcon icon;
    // Các biến RTP
    DatagramPacket rcvdp; // UDP packet tiếp nhận gói tin từ server.
    DatagramSocket RTPsocket; // socket dùng để gửi hoặc nhận UDP packet cho server.
    static int RTP_RCV_PORT=25000; // port mà client sẽ tiếp nhận RTP packets
    Timer timer; // timer used to receive data from the UDP socket
    byte[] buf; // bộ đệm được sử dụng để lưu trữ dữ liệu nhận được từ máy chủ.
    // rtsp states
    final static int INIT=0;
    final static int READY=1;
    final static int PLAYING=2;
    static int state; // RTSP state==INIT or READY or PLAYING
    Socket RTSPsocket; // socket dùng để gửi nhận RTSP messages
    // input and output stream filters
    static BufferedReader RTSPBufferedReader;
    static BufferedWriter RTSPBufferedWriter;
    static String VideoFileName; // video file dùng để yêu cầu tới server.
    int RTSPSeqNb=0; // Số thứ tự của RTSP messages trong phiên.
    int RTSPid=0; // ID của phiên RTSP (cung cấp bởi RTSP Server).
    final static String CRLF="\r\n";
    // Video constants:
    static int MJPEG_TYPE=26; // Loại RTP payload cho video MJPEG
    // Constructor
    public Client() {
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                    System.exit(0);
            }
        });
        buttonPanel.setLayout(new GridLayout(1, 0));
        buttonPanel.add(setupButton);
        buttonPanel.add(playButton);
        buttonPanel.add(pauseButton);
        buttonPanel.add(tearButton);
        setupButton.addActionListener(new setupButtonListener());
        playButton.addActionListener(new playButtonListener());
        pauseButton.addActionListener(new pauseButtonListener());
        tearButton.addActionListener(new tearButtonListener());
        // Image display label
        iconLabel.setIcon(null);
        // frame layout
        mainPanel.setLayout(null);
        mainPanel.add(iconLabel);
        mainPanel.add(buttonPanel);
        iconLabel.setBounds(0, 0, 380, 280);
        buttonPanel.setBounds(0, 280, 380, 50);
        f.getContentPane().add(mainPanel, BorderLayout.CENTER);
        f.setSize(new Dimension(390, 370));
        f.setVisible(true);

        // init timer
        timer = new Timer(20, new timerListener());
        timer.setInitialDelay(0);
        timer.setCoalesce(true);

        buf=new byte[15000];
    }
    public static void main(String argv[]) throws Exception {
        Client theClient = new Client();
        int RTSP_server_port=25001;
        String ServerHost="localhost";
        InetAddress ServerIPAddr=InetAddress.getByName(ServerHost);
        // Tên file dùng để yêu cầu server.
        //VideoFileName="movie.Mjpeg";
        VideoFileName="Talk.mp4";
        // Thiết lập kết nôi TCP với server để trao đổi thông điệp RTSP
        // ------------------
        try {
            theClient.RTSPsocket=new Socket(ServerIPAddr, RTSP_server_port);
            RTSPBufferedReader=new BufferedReader(new InputStreamReader(
                            theClient.RTSPsocket.getInputStream()));
            RTSPBufferedWriter=new BufferedWriter(new OutputStreamWriter(
                            theClient.RTSPsocket.getOutputStream()));
            //khởi tạo RTSP state:
            state=INIT;
        } catch (ConnectException e) {
            System.out.println("Could not connect to '" + ServerHost + ":"
                            + RTSP_server_port + "'");
            System.exit(0);
        }
    }
    //Sự kiện khi click nút setup
    class setupButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (state==INIT) {
                try {
                    RTPsocket=new DatagramSocket(RTP_RCV_PORT);
                    RTPsocket.setSoTimeout(5);
                } catch (SocketException se) {
                    System.out.println("Socket exception: " + se);
                    System.exit(0);
                }
                //Khởi tạo RTSP sequence number
                RTSPSeqNb=1;
                //Gửi tin nhắn setup tới server.
                send_RTSP_request("SETUP");

                //Chờ đợi tin nhắn từ server.
                if (parse_server_response()!=200)
                    System.out.println("Invalid Server Response");
                else {
                    // Chuyển trạng thái của state
                    state=READY;
                    System.out.println("New RTSP state: ...."+state);
                }
            }
        }
    }
    //Sự kiện khi click nút play
    class playButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            System.out.println("Play Button pressed!");
            if (state==READY) {
                //Tăng số thứ tự của RTSPSeqNb
                RTSPSeqNb++;
                //Gửi tin nhắn play tới server
                send_RTSP_request("PLAY");
                //Chờ server phản hồi
                if (parse_server_response()!=200)
                    System.out.println("Invalid Server Response");
                else {
                    //Chuyển trạng thái state
                    state=PLAYING;
                    System.out.println("New RTSP state: ..."+state);
                    //Bắt đầu thời gian
                    timer.start();
                }
            }
        }
    }

    //Sự kiện trên nút pause
    class pauseButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            System.out.println("Pause Button pressed !");
            if (state==PLAYING) {
                //Tăng số thứ tự RTSPSeqNb
                RTSPSeqNb++;
                //Gửi tin nhắn dừng tới server
                send_RTSP_request("PAUSE");
                //Chờ đợi phản hồi
                if (parse_server_response()!=200)
                    System.out.println("Invalid Server Response");
                else {
                    //Chuyển trạng thái của state
                    state=READY;
                    System.out.println("New RTSP state: ...");
                    //Dừng thời gian
                    timer.stop();
                }
            }
        }
    }

    //Sự kiện Teardown
    class tearButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            System.out.println("Teardown Button pressed !");
            //Tăng số thứ tự RTSPSeqNb
            RTSPSeqNb++;
            //Gửi tin nhắn TEARDOWN tới server
            send_RTSP_request("TEARDOWN");
            //Chờ đợi phản hồi tử server
            if (parse_server_response()!=200)
                System.out.println("Invalid Server Response");
            else {
                //Chuyển trạng thái state.
                state=INIT;
                System.out.println("New RTSP state: ..."+state);
                //Dừng timer
                timer.stop();
                System.exit(0);
            }
        }
    }

    //Xử lý bộ đệm thời gian
    class timerListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            //Khởi tạo datagram để nhận gói tin từ UDPSocket
            rcvdp=new DatagramPacket(buf, buf.length);
            try {
                //Nhận gói tin
                RTPsocket.receive(rcvdp);
                //Tạo đối tượng RTPpacket từ Datagram vừa nhận
                RTPpacket rtp_packet=new RTPpacket(rcvdp.getData(), rcvdp.getLength());
                int payload_length=rtp_packet.getpayload_length();
                byte[] payload=new byte[payload_length];
                rtp_packet.getpayload(payload);
                //lấy một đối tượng Image từ payload bitstream
                Toolkit toolkit=Toolkit.getDefaultToolkit();
                Image image=toolkit.createImage(payload, 0, payload_length);
                //Hiển thị ảnh như là đối tượng ImageIcon
                icon=new ImageIcon(image);
                iconLabel.setIcon(icon);
            } catch (InterruptedIOException iioe) {
            }
            catch (IOException ioe) {
                System.out.println("Exception caught: "+ioe);
            }
        }
    }

    //Tiếp nhận response từ server
    private int parse_server_response() {
        int reply_code=0;
        try {
            String StatusLine=RTSPBufferedReader.readLine();
            System.out.println(StatusLine);
            StringTokenizer tokens=new StringTokenizer(StatusLine);
            tokens.nextToken();
            reply_code=Integer.parseInt(tokens.nextToken());
            if (reply_code==200) {
                String SeqNumLine=RTSPBufferedReader.readLine();
                System.out.println(SeqNumLine);
                String SessionLine=RTSPBufferedReader.readLine();
                System.out.println(SessionLine);
                tokens=new StringTokenizer(SessionLine);
                tokens.nextToken();
                RTSPid=Integer.parseInt(tokens.nextToken());
            }
        } catch (Exception ex) {
            System.out.println("Exception caught: "+ex);
            System.exit(0);
        }
        return (reply_code);
    }

    // Gửi các yêu cầu RTSP
    private void send_RTSP_request(String request_type) {
        try {
            //Sử dụng giao thức TCP để gửi các tin nhắn
            RTSPBufferedWriter.write(request_type+" "+VideoFileName+" RTSP/1.0"+CRLF);
            // write the CSeq line:
            RTSPBufferedWriter.write("CSeq: "+RTSPSeqNb+CRLF);
            if(request_type.equals("SETUP")) {
                //Hiển thị thông tin kết nối tới server.
                RTSPBufferedWriter.write("Transport: RTP/UDP; client_port= "+RTP_RCV_PORT+CRLF);
            } else {
                RTSPBufferedWriter.write("Session: "+RTSPid+CRLF);
            }
            RTSPBufferedWriter.flush();
        } catch (Exception ex) {
            System.out.println("Exception caught: "+ex);
            System.exit(0);
        }
    }
}
