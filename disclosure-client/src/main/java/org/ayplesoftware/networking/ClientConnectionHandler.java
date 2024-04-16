package org.ayplesoftware.networking;

import java.io.UnsupportedEncodingException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import org.ayplesoftware.utils.EncryptionHandler;
import org.ayplesoftware.networking.SocketData.BlockType;


// TODO: research streams better
// TODO: make a networking manager in a seperate thread

public class ClientConnectionHandler {
    private static ClientConnectionHandler instance;
    private HashMap<String, String> room_clients = new HashMap<String,String>(); // uuid -> pub_key
    private Socket socket;
    private String serverIp;
    private int port;

    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    public ClientConnectionHandler(String serverIp, int port) throws UnknownHostException, IOException {
        if (instance != null) {
            return;
        }
        
        this.serverIp = serverIp;
        this.port = port;
        this.socket = new Socket(serverIp, port);
        this.outputStream = new DataOutputStream(socket.getOutputStream());
    }

    public void sendPublicKeyToServer() throws UnsupportedEncodingException {
        HashMap keydata = new HashMap<String, String>();
        keydata.put("public_key", EncryptionHandler.getInstance().getPublicKeyB64());
        SocketData.createSocketPacketData(BlockType.CLI_RES_PUB_KEY, keydata);
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
