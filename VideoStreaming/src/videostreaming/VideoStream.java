//VideoStream

package videostreaming;

import java.io.*;

public class VideoStream {
    FileInputStream fis;
    int frame_nb; //Khung hiện tại
    public VideoStream(String filename) throws Exception {
        fis=new FileInputStream(filename);
        frame_nb=0;
    }

    //trả về khung tiếp theo dưới dạng một mảng byte và kích thước của khung
    public int getnextframe(byte[] frame) throws Exception {
        int length=0;
        String length_string;
        byte[] frame_length=new byte[5];
        //Độ dài khung hiện tại
        fis.read(frame_length, 0, 5);
        length_string=new String(frame_length);
        length=Integer.parseInt(length_string);
        return (fis.read(frame, 0, length));
    }
}
