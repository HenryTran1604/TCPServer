/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tcpserver_thread;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author QuangHuy
 */
public class ClientHandler implements Runnable {
    private final Socket socket;
    private static final int TIMEOUT = 5000;

    public ClientHandler(Socket socket){
        this.socket = socket;
    }

    public int[] genRandomArr(int sz, int seed) {
        int[] a = new int[sz];
        Random rand = new Random(seed);

        for (int i = 0; i < sz; i++) {
            a[i] = rand.nextInt(30);
        }
        return a;
    }

    public String genQuestion(int[] arr) {
        String question = "";
        for (int i = 0; i < arr.length - 1; i++) {
            question += arr[i] + "|";
        }
        question += arr[3];
        return question;
    }

    public int getAnswer(int[] arr) {
        int serverAns = 0;
        for (int i = 0; i < arr.length; i++) {
            serverAns += arr[i];
        }
        return serverAns;
    }

    private String currentTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH-mm-ss.SSS");
        return format.format(new Date());
    }

    public boolean isValidateRequestCode(String requestCode) {
        String regexRequest = "^[Bb]\\d{2}[A-Za-z]{4}\\d{3};\\d+$";
        Pattern pattern = Pattern.compile(regexRequest);
        Matcher matcher = pattern.matcher(requestCode);
        return matcher.matches();
    }

    @Override
    public void run() {
        try {
            socket.setSoTimeout(TIMEOUT);
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            // get requestcode = studentcode + questioncode
            String requestCode = dis.readUTF();
            // check requestCode
            if (!isValidateRequestCode(requestCode)) {
                dos.writeUTF("INVALID REQUEST");
            } else {
                String[] code = requestCode.split(";");

                String studentCode = code[0];
                int qCode  = Integer.parseInt(code[1]);
                
                // gen 4 number
                int[] randArr = genRandomArr(4, qCode);
                // gen question from 4 number
                String question = genQuestion(randArr);
                dos.writeUTF(question);
                // get client answer
                int clientAns = dis.readInt();
                int serverAns = getAnswer(randArr);
                // Log
                System.out.format("%s %d: %s: question: %s \n\t%s server answer: %d, client answer: %d, status: %s\n",
                        this.socket.getInetAddress(), this.socket.getPort(), studentCode, question, currentTime(), clientAns, serverAns, clientAns == serverAns);
            }

//             send result to client
//            dos.writeUTF("Result: " +  (clientAns == serverAns));
        } catch (SocketTimeoutException ex) {
            if (socket != null) {
                System.out.println(this.socket.getInetAddress() + " " + this.socket.getPort() + ": Timeout");
            }
        } catch (IOException ex) {
            // TODO: handle
        } finally {
            shutdown();
        }
    }

    public void shutdown() {
        try {
            if (!this.socket.isClosed()) {
                this.socket.close();
            }
        } catch (IOException ex) {
            // TODO: handle
        }
    }
}
