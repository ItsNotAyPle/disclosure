package org.ayplesoftware.networking;

import java.io.UnsupportedEncodingException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
// import java.util.Queue;

import org.json.JSONObject;
import org.json.JSONException;
// import org.json.JsonSerializer;

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
    public boolean recieved_thumbs_up; // mmmmmmmmmmm
    // private Queue<String> blocks_to_send = new Queue<String>();

    private DataInputStream inputStream; // svr -> cli
    private DataOutputStream outputStream; // cli -> svr

    public ClientConnectionHandler(String serverIp, int port) throws UnknownHostException, IOException {
        if (instance != null) {
            return;
        }
        
        ClientConnectionHandler.instance = this;
        this.serverIp = serverIp;
        this.port = port;
        this.socket = new Socket(serverIp, port);
        this.inputStream = new DataInputStream(socket.getInputStream());
        this.outputStream = new DataOutputStream(socket.getOutputStream());
        this.connected = true;
        this.recieved_thumbs_up = false;

    }

    @Override
    public void run() {
        StringBuilder builder = new StringBuilder();
        
        while (this.connected) {
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

                // this is pissing me off
            } catch (IOException e) {
                try {
                    this.disconnect();
                } catch (Exception e2) {
                    e2.printStackTrace();
                } 
            } 
        }
    }

    private void exectuteBlock(String blockdata) throws UnsupportedEncodingException, IOException {
        // System.out.println("Recieved data: " + blockdata);

        blockdata = blockdata.replace("{START_BLOCK}", "");
        blockdata = blockdata.replace("{END_BLOCK}", "");
        // JSONObject jsonobj = JSONSerializer.toJSON(blockdata);
        // System.out.println(blockdata);
        JSONObject jsonobj = new JSONObject(blockdata);
        BlockType blockType = BlockType.valueOf(jsonobj.getString("packet_type"));

        JSONObject data;
        try {
            data = jsonobj.getJSONObject("data");
        } catch (JSONException e) {
            data = null;            
        }

        // todo: move this as a class variable to not have to create a new one
        // on every incoming block
        // HashMap<String, String> dataToSend = new HashMap<String,String>();

        switch (blockType) {
            case SVR_REQ_PUB_KEY:
                // System.out.println("Sending public key to server...");
                // dataToSend.put("public_key", EncryptionHandler.getInstance().getPublicKeyB64());
                // String block_to_send = SocketData.createSocketPacketData(SocketData.BlockType.CLI_RES_PUB_KEY, dataToSend);
                // this.sendRawDataToServer(data);
                this.sendPublicKeyToServer();
                break;


            
            case SVR_RES_NEW_CONNECTION:
                String id = data.getString("id");
                String public_key = data.getString("public_key");
                this.room_clients.put(id, public_key);
                System.out.println("Storing id [" + id + "] to publickey: [" + public_key + "]");
                break;
            
            case SVR_RES_RECV_PUB_KEY:
                System.out.println("Recieved thumbs up from server!");
                this.recieved_thumbs_up = true;    
                break;

            case MESSAGE:
                break;

            default:
                break;
        }

    }
    
    public void sendUserMessageToServer(String message) {
        try {
            byte[] encryptedMsg = EncryptionHandler.getInstance().encryptString(message.getBytes());
            HashMap keydata = new HashMap<String, String>();
            keydata.put("to", "");
            keydata.put("from", "");
            keydata.put("message", encryptedMsg);
            String data = SocketData.createSocketPacketData(BlockType.MESSAGE, keydata);
            // this.blocks.add(data);
            this.sendRawDataToServer(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendPublicKeyToServer() throws UnsupportedEncodingException, IOException {
        System.out.println("sending public key to server...");
        HashMap keydata = new HashMap<String, String>();
        keydata.put("public_key", EncryptionHandler.getInstance().getPublicKeyB64());
        String data = SocketData.createSocketPacketData(BlockType.CLI_RES_PUB_KEY, keydata);
        // this.blocks.add(data);
        this.sendRawDataToServer(data);
    }

    // public void sendRawDataToServer(byte[] data) throws IOException {
    //     this.outputStream.writeByte(MESSAGE_DATA_TYPE);
    //     this.outputStream.writeUTF(data.toString() + "\n");
    //     this.outputStream.flush();
    // }

    public void sendRawDataToServer(String data) throws IOException {
        System.out.println("writing bytes to server... " + data);
        this.outputStream.writeBytes(data);
        this.outputStream.flush();
    }


    // TODO: callback feature
    public void disconnect() throws IOException {
        System.out.println("-[]-[]-[]--][-][-][-][]- Disconnected!");
        this.connected = false;
        this.socket.close();
        this.inputStream.close();
        this.outputStream.close();
    }



}
