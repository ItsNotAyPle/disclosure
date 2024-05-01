package org.ayplesoftware.networking;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.json.JSONObject;

public class SocketData {
    public static enum BlockType {
        SVR_REQ_PUB_KEY,
        CLI_RES_PUB_KEY, 
        SVR_RES_RECV_PUB_KEY,
        SVR_RES_NEW_CONNECTION,
        MESSAGE
    }
    
    // public static byte[] createSocketPacketData() {
    //     StringBuilder builder = new StringBuilder();
    //     builder.append("{START_BLOCK}");
    //     builder.append("packet_type: " + )


    //     builder.append("{END_BLOCK}");
    //     return builder.toString().getBytes();
    // }

    public static String createSocketPacketData(BlockType type, Map<String, String> data) throws UnsupportedEncodingException {
        JSONObject jsonobj = new JSONObject();
        jsonobj.put("packet_type", type);
        jsonobj.put("data", data);

        StringBuilder sb = new StringBuilder();
        sb.append("{START_BLOCK}");
        sb.append(jsonobj.toString());
        sb.append("{END_BLOCK}");

        // System.out.println(sb.toString());

        return sb.toString();
    }

    
}
