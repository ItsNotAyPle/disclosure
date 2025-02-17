package org.ayplesoftware.frontend;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class MainWindow {
    private static MainWindow instance;
    public static MainWindow getInstance() {
        return instance;
    }

    private BorderPane borderpane;
    private Scene scene;

    private TextField inputField;
    private EventHandler<ActionEvent> enterPressedEvent;

    private TextArea outputArea;
    public static TextArea getOutputArea() {
        return instance.outputArea;
    }
    public static void outputStringToOutputArea(String data) {
        instance.outputArea.setText(instance.outputArea.getText() + data + "\n");
    }


    public MainWindow(int initial_x, int initial_y) {
        if (instance != null) {
            return;
        }

        instance = this;
        this.borderpane = new BorderPane();
        this.scene = new Scene(this.borderpane, initial_x, initial_y); 

        this.setupTextInputField();
        this.setupTextArea();
    }

    private void setupTextArea() {
        this.outputArea = new TextArea();
        this.outputArea.setEditable(false);
        this.outputArea.setBackground(Background.fill(Color.BLACK));
        this.borderpane.setCenter(this.outputArea);
        this.borderpane.setPrefHeight(Double.MAX_VALUE);
    }
    
    private void setupTextInputField() {
        this.inputField = new TextField();

        this.enterPressedEvent = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                CommandHandler.exectute(inputField.getText());
                inputField.setText("");
            }
        };


        this.inputField.setOnAction(this.enterPressedEvent);
        // this.borderpane.getChildren().add(this.inputField);
        this.borderpane.setBottom(this.inputField);

    }


    public BorderPane getBorderPane() {
        return this.borderpane;
    }

    public Scene getScene() {
        return this.scene;
    }
}