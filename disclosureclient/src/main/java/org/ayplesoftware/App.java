package org.ayplesoftware;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;

import org.ayplesoftware.networking.ClientConnectionHandler;
import org.ayplesoftware.networking.SocketData;
import org.ayplesoftware.utils.EncryptionHandler;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

    private EncryptionHandler encryptionHandler;
    private ClientConnectionHandler clientHandler;

    public void initBackgroundProcesses() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, UnknownHostException, IOException {
        this.encryptionHandler = new EncryptionHandler();
        this.clientHandler = new ClientConnectionHandler("127.0.1.1", 8000);

    }

    @Override
    public void start(Stage stage) {
        try {
            var javaVersion = SystemInfo.javaVersion();
            var javafxVersion = SystemInfo.javafxVersion();
            
            initBackgroundProcesses();

            String data = SocketData.createSocketPacketData(SocketData.SocketType.TEST, null);
            this.clientHandler.sendRawDataToServer(data);
            System.out.println(":" + data.toString());
            // System.out.println(new String(data, "UTF8"));

            // var scene = new Scene(new StackPane(), 640, 480);
            // stage.setScene(scene);
            // stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        launch();
    }

}