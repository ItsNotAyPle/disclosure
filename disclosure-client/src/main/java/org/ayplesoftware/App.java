package org.ayplesoftware;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Scanner;

import javax.crypto.NoSuchPaddingException;

import org.ayplesoftware.networking.ClientConnectionHandler;
import org.ayplesoftware.networking.SocketData;
import org.ayplesoftware.utils.EncryptionHandler;
import org.ayplesoftware.frontend.MainWindow;

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
    private MainWindow mainWindow;
    // private ClientConnectionHandler clientHandler;

    public void initBackgroundProcesses() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, UnknownHostException, IOException {
        this.encryptionHandler = new EncryptionHandler();
        // this.clientHandler = new ClientConnectionHandler("127.0.1.1", 8000);
        // this.clientHandler.start();
    }

    @Override
    public void start(Stage stage) {
        try {
            this.mainWindow = new MainWindow(640, 480);
            
            
            stage.setScene(this.mainWindow.getScene());
            stage.setTitle("Disclosure");
            stage.setMinWidth(640);
            stage.setMinHeight(480);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        launch();
    }

}