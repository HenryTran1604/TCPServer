/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tcpserver_final;

/**
 *
 * @author QuangHuy
 */
public class Main {
    public static void main(String[] args) {
        TCPServer server = new TCPServer();
        Thread t = new Thread(server);
        t.start();
    }
}
