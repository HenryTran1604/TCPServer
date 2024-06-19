/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package tcpserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 *
 * @author QuangHuy
 */
public class TCPServer {

    /**
     * @param args the command line arguments
     */
    private static int[] genRandomArr(int sz, int seed) {
        int[] a = new int[sz];
        Random rand = new Random(seed);

        for (int i = 0; i < sz; i++) {
            a[i] = rand.nextInt(30);
        }
        return a;
    }

    private static String genQuestion(int[] arr) {
        String question = "";
        for (int i = 0; i < arr.length - 1; i++) {
            question += arr[i] + "|";
        }
        question += arr[3];
        return question;
    }

    private static int getAnswer(int[] arr) {
        int serverAns = 0;
        for (int i = 0; i < arr.length; i++) {
            serverAns += arr[i];
        }
        return serverAns;
    }
     private static String currentTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH-mm-ss.SSS");
        return format.format(new Date());
    }
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(806);
        System.out.println("Server is ready ...");
        int TIME_OUT = 5000;
        
        while (true) {
            Socket conn = null;
            try {
                conn = server.accept();
                // set time out = 5000ms
                conn.setSoTimeout(TIME_OUT);
                DataInputStream dis = new DataInputStream(conn.getInputStream());
                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                
                // get requestcode = studentcode + questioncode
                String[] requestCode = dis.readUTF().split(";");
                String studentCode = requestCode[0];
                int qCode = 0;
                try {
                    qCode = Integer.parseInt(requestCode[1]);
                } catch (NumberFormatException ex) {
                    System.out.println(ex.getCause());
                }
                // gen 4 number
                int[] randArr = genRandomArr(4, qCode);
                
                // gen question from 4 number
                String question = genQuestion(randArr);
                dos.writeUTF(question);
                // get client answer
                int clientAns = dis.readInt();
                int serverAns = getAnswer(randArr);
                // Log
                System.out.format("%s %d: %s: question: %s \n\t\t%s server answer: %d, client answer: %d, status: %s\n", 
                        conn.getInetAddress(), conn.getPort(), studentCode, question, currentTime(), clientAns, serverAns, clientAns == serverAns);
                
                dis.close();
                dos.close();
                conn.close();
            } catch (SocketTimeoutException ex) {
                if(conn != null)
                    System.out.println(conn.getInetAddress() + " " + conn.getPort() + ": Timeout");
            } catch(IOException ex) {
                System.out.println(ex.getCause());
            }
        }
    }

}
