package view;

import dao.PhimDAO;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import model.Phim;

public class Server {
    public static void main(String args[]) {
        try{
            ServerSocket server= new ServerSocket(1107);
            while(true){
                Socket connection= server.accept();
                DataInputStream in=new DataInputStream(connection.getInputStream());
                String key=in.readUTF();
                System.out.println(key);
                PhimDAO phimDAO = new PhimDAO();
                ArrayList <Phim> list = phimDAO.searchPhimKey(key);
                ObjectOutputStream out=new ObjectOutputStream(connection.getOutputStream());
                //out.write(list.size());
                for(int i=0; i<list.size(); i++){
                    out.writeObject(list.get(i));
                    System.out.println(list.get(i).getTen());
                }
                connection.close();
            }
        }
        catch(IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
