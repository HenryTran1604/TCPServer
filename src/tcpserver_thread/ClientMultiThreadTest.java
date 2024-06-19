/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tcpserver_thread;

/**
 *
 * @author QuangHuy
 */
public class ClientMultiThreadTest {

    public static void main(String[] args) {
        for (int i = 0; i < 10000; i++) {
            String studentCode = "B20DCCN" + String.format("%03d", (int) (Math.random() * 1000));
            int questionCode = (int) (Math.random() * 1000);
            String serverAddress = "localhost";
            int serverPort = 806;
            Thread t = new Thread(new TCPClient(studentCode, questionCode, serverAddress, serverPort));
            t.start();
        }
    }
}
