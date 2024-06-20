/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tcpserver_filnal;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author QuangHuy
 */
public class TCPServer implements Runnable{
    private static final int SERVER_PORT = 806;
    private static final int MAX_CONNECTION = 20;
    private static final int MAX_CONNECTION_PER_MINUTE = 60;
    
    private ServerSocket server;
    private ExecutorService pool;
    private Set<String> blackList;
    private Map<String, Integer> requestCounts;
    
    public TCPServer() {
        // using as synchornized
        this.requestCounts = new ConcurrentHashMap<>();
        loadBlackList();
    }
    
    private void loadBlackList() {
        this.blackList = new HashSet<>();
        // TODO: save blacklist to file
    }
    
    @Override
    public void run() {
        try {
            this.server = new ServerSocket(SERVER_PORT);
            this.pool = Executors.newFixedThreadPool(MAX_CONNECTION);
            
            // set scheduler to reset requestCounts
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1); // 1 stands for 1 pool for this task
            // 1 minute to reset requestCount to 0
            scheduler.scheduleAtFixedRate(() -> this.requestCounts.clear(), 0, 1, TimeUnit.MINUTES);
            
            System.out.println("Server is ready ...");
            
            while(true) {
                Socket conn = this.server.accept();
                String clientIP = conn.getInetAddress().getHostAddress();
                
                if(this.blackList.contains(clientIP)) {
                    System.out.println("Block IP: " + clientIP);
                    this.closeClientSocket(conn);
                }
                else {
                    int count = this.requestCounts.getOrDefault(clientIP, 1);
                    this.requestCounts.put(clientIP, count + 1);
                    if(isSpam(clientIP, count + 1)) {
                        System.out.format("IP: %s reach limit %d request per minute -> Blocking", clientIP, MAX_CONNECTION_PER_MINUTE);
                        this.closeClientSocket(conn);
                    }
                    else {
                        ClientHandler clientHandler = new ClientHandler(conn);
                        pool.execute(clientHandler);
                    }
                    
                }
            }
            
        } catch (IOException ex) {
            // TODO: handle
        }
    }
    public void closeClientSocket(Socket socket) throws IOException {
        if(!socket.isClosed()) {
            socket.close();
        }
    }
    public boolean isSpam(String clientIP, int requestCounts) {
        if(requestCounts > MAX_CONNECTION_PER_MINUTE) {
            this.blackList.add(clientIP);
            return true;
        }
        return false;
    }
    
}
