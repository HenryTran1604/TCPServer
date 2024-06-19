package tcpserver_thread;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author QuangHuy
 */
class ClientThread extends Thread {
    private int calcSum(String question) {
        String[] arr = question.split("\\|");
        int sum = 0;
        for (int i = 0; i < arr.length; i++) {
            sum += Integer.parseInt(arr[i]);
        }
        return sum;
    }
    
    @Override
    public void run() {
        String studentCode = "B20DCCN" + (int) (Math.random() * 1000);
        int questionCode = (int) (Math.random() * 1000);
        String serverAddress = "localhost";
        int serverPort = 806;
        try (Socket socket = new Socket(serverAddress, serverPort); 
                DataInputStream dis = new DataInputStream(socket.getInputStream()); 
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());) {

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
        } catch (IOException ex) {
            Logger.getLogger(ClientMultiThreadTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("---------End--------");
    }
}
public class ClientMultiThreadTest {
    public static void main(String[] args) {
        for(int i = 0; i < 50; i++) {
            Thread t = new ClientThread();
            t.start();
        }
    }
}