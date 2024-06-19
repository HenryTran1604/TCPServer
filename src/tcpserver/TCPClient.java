/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tcpserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author QuangHuy
 */
public class TCPClient {
    private Socket client;
    
    private static int calcSum(String question) {
        String[] arr = question.split("\\|");
        int sum = 0;
        for(int i = 0; i < arr.length; i++) {
            sum += Integer.parseInt(arr[i]);
        }
        return sum;
    }
    public static void main(String[] args) throws IOException, InterruptedException {
        try(Socket socket = new Socket("localhost", 806); 
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());) {
            
            String studentCode = "B20DCCN320";
            String questionCode = "700";
            String requestCode = studentCode + ";" + questionCode;
            // send request with studentcode and question code to server
            dos.writeUTF(requestCode);
            // get question from server
            String question = dis.readUTF();
            int sum = calcSum(question);
            System.out.println("question: " + question);
//            TimeUnit.SECONDS.sleep(5);
            // send back to server
            dos.writeInt(sum);
        }
        System.out.println("---------End--------");
    }
}
