/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tcpserver_thread;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author QuangHuy
 */
public class TCPClient implements Runnable{
    private final String studentCode;
    private final int questionCode;
    private final String serverAddress;
    private final int serverPort;
    private Socket client;
    
    private int calcSum(String question) {
        String[] arr = question.split("\\|");
        int sum = 0;
        for (String x : arr) {
            sum += Integer.parseInt(x);
        }
        return sum;
    }
    public TCPClient(String studentCode, int questionCode, String serverAddress, int serverPort) {
        this.studentCode = studentCode;
        this.questionCode = questionCode;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }
    
    @Override
    public void run() {
        try {
            this.client = new Socket(this.serverAddress, this.serverPort);
            DataInputStream dis = new DataInputStream(this.client.getInputStream());
            DataOutputStream dos = new DataOutputStream(this.client.getOutputStream());
            
            String requestCode = studentCode + ";" + questionCode;
            // send request with studentcode and question code to server
            dos.writeUTF(requestCode);
            // get question from server
            String question = dis.readUTF();
            int sum = calcSum(question);
            System.out.println("question: " + question);
//            TimeUnit.SECONDS.sleep((int) (Math.random() * 6));
            // send back to server
            dos.writeInt(sum);
            dis.close();
            dos.close();
            System.out.println("---------End--------");
        } catch(IOException ex) {
            // TODO: handle
        }
        finally {
            shutdown();
        }
    }
    public void shutdown() {
        try {
            if (this.client != null && !this.client.isClosed()) {
                this.client.close();
            }
        } catch (IOException ex) {
            // TODO: handle
        }
    }
    public static void main(String[] args) throws IOException, InterruptedException {
        String studentCode = "B20DCCN320";
        int questionCode = 40;
        String serverAddress = "localhost";
        int serverPort = 806;
        TCPClient client = new TCPClient(studentCode, questionCode, serverAddress, serverPort);
        client.run();
    }

}
