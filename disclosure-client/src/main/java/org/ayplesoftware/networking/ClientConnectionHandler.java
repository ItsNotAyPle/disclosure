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

import org.json.JSONObject;

import org.ayplesoftware.utils.EncryptionHandler;
import org.ayplesoftware.networking.SocketData.BlockType;


// TODO: research streams better
// TODO: make a networking manager in a seperate thread

public class ClientConnectionHandler extends Thread {
    private static ClientConnectionHandler instance;
    private HashMap<String, String> room_clients = new HashMap<String,String>(); // uuid -> pub_key
    private Socket socket;
    private String serverIp;
    private int port;
    private boolean connected;

    private DataInputStream inputStream; // svr -> cli
    private DataOutputStream outputStream; // cli -> svr

    public ClientConnectionHandler(String serverIp, int port) throws UnknownHostException, IOException {
        if (instance != null) {
            return;
        }
        
        this.serverIp = serverIp;
        this.port = port;
        this.socket = new Socket(serverIp, port);
        this.inputStream = new DataInputStream(socket.getInputStream());
        this.outputStream = new DataOutputStream(socket.getOutputStream());
        this.connected = true;

    }

    @Override
    public void run() {
        StringBuilder builder = new StringBuilder();
        
        while (true) {
            try {
                // TODO: double check this 'readNbytes', good solution?
                builder.append(new String(this.inputStream.readNBytes(1), "UTF8"));
                
                /*
                 *  I had a fear that data could potentially be cut off and may be
                 *  a reason for concern in the future. The idea is that the client
                 *  could recieve something like "{END_BLOCK} {STA" and then the 
                 *  {STA would be cut off and this would break everything. It should
                 *  however be ok since the client is recieving 1 byte at a time but
                 *  this causes performance issues. For future: perhaps some form of
                 *  acknowledgement for each block between the client and server? Or
                 *  even better just handle the strings better, detect when a {END_BLOCK}
                 *  is in the stream then extract that block out and handle it.
                 */

                if (builder.toString().endsWith("{END_BLOCK}")) {
                    exectuteBlock(builder.toString());
                    builder.setLength(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void exectuteBlock(String blockdata) {
        // System.out.println("Recieved data: " + blockdata);

        blockdata.replace("{START_BLOCK}", "");
        blockdata.replace("{END_BLOCK}", "");
        JSONObject jsonobj = JSONSerializer.toJSON(blockdata);
        String blockType = jsonobj.getString("packet_type");
        JSONObject data = jsonobj.getJSONObject("data");

        // todo: move this as a class variable to not have to create a new one
        // on every incoming block
        HashMap<String, String> dataToSend = new HashMap<String,String>();

        switch (blockType) {
            case SocketData.BlockType.SVR_REQ_PUB_KEY:
                System.out.println("Sending public key to server...");
                dataToSend.put("public_key", EncryptionHandler.getInstance().getPublicKeyB64());
                SocketData.createSocketPacketData(SocketData.BlockType.CLI_RES_PUB_KEY, dataToSend);
                break;
            
            case SocketData.BlockType.SVR_RES_NEW_CONNECTION:
                String id = data.getString("id");
                String public_key = data.getString("public_key");
                this.room_clients.put(id, public_key);
                System.out.println("Storing id [" + id + "] to publickey: [" + public_key + "]");
                break;

            case SocketData.BlockType.MESSAGE:
                break;

            default:
                break;
        }

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
