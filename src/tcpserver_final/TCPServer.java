/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tcpserver_final;

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
    private static final int MAX_CONNECTION = Math.max(2, Runtime.getRuntime().availableProcessors() - 2);
    private static final int MAX_CONNECTION_PER_MINUTE = 10;
    private static final int WAITING_TIME = 10;
    
    private ServerSocket server;
    private ExecutorService pool;
    private Set<String> blackListIP;
    private Map<String, Integer> requestCounts; // count number of request in 1 time unit
    private Map<String, Integer> remainDeniedTime;
//    private Map<String, Integer> deniedCounts; // count number of time was denied
    
    public TCPServer() {
        // using as synchornized, thread safe
        this.requestCounts = new ConcurrentHashMap<>();
        this.blackListIP = ConcurrentHashMap.newKeySet();
        this.remainDeniedTime = new ConcurrentHashMap<>();

        loadBlackList();
    }
    
    private void loadBlackList() {
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
            scheduler.scheduleAtFixedRate(() -> {
                this.requestCounts.clear();
                Set<Map.Entry<String, Integer>> entrySet = this.remainDeniedTime.entrySet();
                for(Map.Entry<String, Integer> entry : entrySet) {
                    String key = entry.getKey();
                    int remain = entry.getValue();
                    if(remain > 1)
                        this.remainDeniedTime.put(entry.getKey(), entry.getValue() - 1);
                    else
                        this.remainDeniedTime.remove(key);
                }
            }, 0, 1, TimeUnit.SECONDS);
            
            System.out.println("Server is ready ...");
            
            while(true) {
                Socket conn = this.server.accept();
                String clientIP = conn.getInetAddress().getHostAddress();
                
                if(this.remainDeniedTime.containsKey(clientIP)) {
//                    System.out.println("Block IP: " + clientIP);
                    this.closeClientSocket(conn);
                }
                else {
                    int count = this.requestCounts.getOrDefault(clientIP, 1);
                    this.requestCounts.put(clientIP, count + 1);
                    if(isSpam(clientIP, count + 1)) {
                        System.out.format("IP: %s reach limit %d request per %s -> Denied, Wait for %d %s\n", 
                                clientIP, MAX_CONNECTION_PER_MINUTE, TimeUnit.SECONDS, WAITING_TIME, TimeUnit.SECONDS);
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
    public void shutdown() throws IOException {
        if(!this.server.isClosed()) {
            this.pool.shutdown();
            this.server.close();
        }
    }
    public boolean isSpam(String clientIP, int requestCounts) {
        if(requestCounts > MAX_CONNECTION_PER_MINUTE) {
            this.remainDeniedTime.put(clientIP, WAITING_TIME);
            return true;
        }
        return false;
    }
    
}
