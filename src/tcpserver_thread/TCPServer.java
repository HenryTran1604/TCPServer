/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package tcpserver_thread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 *
 * @author QuangHuy
 */
public class TCPServer implements Runnable {
    private ServerSocket server;
    private ExecutorService pool;

    public TCPServer() {
    }
    
    /**
     * @param args the command line arguments
     */
    @Override
    public void run() {
        try {
            this.server = new ServerSocket(806);
//            this.pool = Executors.newCachedThreadPool();
            this.pool = Executors.newFixedThreadPool(20);
            System.out.println("Server is ready ...");
            while (true) {
                Socket conn = null;
                try {
                    conn = this.server.accept();
                    ClientHandler clientHandler = new ClientHandler(conn);
                    pool.execute(clientHandler);
//                    Thread t = new Thread(clientHandler);
//                    t.start();
                } catch (IOException ex) {
                    System.out.println(ex.getCause());
                }
            }
        } catch (IOException ex) {
            // TODO: handle
        }
    }
    public void shutdown() {
        if(this.server != null && this.server.isClosed()) {
            try {
                this.server.close();
            } catch (IOException ex) {
                // TODO: handle
            }
        }
    }

    public static void main(String[] args) throws IOException {
        TCPServer server = new TCPServer();
        server.run();
    }

}
