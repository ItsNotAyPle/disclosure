package org.ayplesoftware.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;


// TODO: research streams better
// TODO: make a networking manager in a seperate thread

public class ClientConnectionHandler {
    public static int MESSAGE_DATA_TYPE = 1;
    private Socket socket;
    private String serverIp;
    private int port;

    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    public ClientConnectionHandler(String serverIp, int port) throws UnknownHostException, IOException {
        this.serverIp = serverIp;
        this.port = port;
        this.socket = new Socket(serverIp, port);
        this.outputStream = new DataOutputStream(socket.getOutputStream());
    }

    // public void sendRawDataToServer(byte[] data) throws IOException {
    //     this.outputStream.writeByte(MESSAGE_DATA_TYPE);
    //     this.outputStream.writeUTF(data.toString() + "\n");
    //     this.outputStream.flush();
    // }

    public void sendRawDataToServer(String data) throws IOException {
        System.out.println("writing bytes to server...");
        this.outputStream.writeBytes(data);
        this.outputStream.flush();
    }


    public void disconnect() throws IOException {
        this.socket.close();
        this.inputStream.close();
        this.outputStream.close();
    }



}
