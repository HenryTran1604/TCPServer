/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tcpserver_filnal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author QuangHuy
 */
public class ClientHandler implements Runnable{
    private static final int TIME_OUT = 5000;
    
            
    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }
    
    @Override
    public void run() {
        try {
            this.socket.setSoTimeout(TIME_OUT);
            DataInputStream dis = new DataInputStream(this.socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(this.socket.getOutputStream());
            
            // TODO: Logic here
            
            dos.close();
            dis.close();
            
        } catch(SocketTimeoutException ex) {
            System.out.println("Timeout");
        } catch (SocketException ex) {
            // TODO: handle
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    public void shutdown() {
        if(!this.socket.isClosed()) {
            try {
                this.socket.close();
            } catch (IOException ex) {
                // TODO: handle
            }
        }
    }
    
}
