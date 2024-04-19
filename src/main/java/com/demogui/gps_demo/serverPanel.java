package com.demogui.gps_demo;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;

import java.util.HashMap;

public class serverPanel
{
    static private Label addressLabel = new Label("PostgreSQL server address");
    static private Label portLabel = new Label("PostgreSQL server port");
    static private Label databaseLabel= new Label("PostgreSQL Database");
    static private Label usernameLabel = new Label("Username");
    static private Label passwordLabel = new Label("Password");
    static private Label mqttAddressLabel = new Label("MQTT broker address");
    static private Label mqttPortLabel = new Label("MQTT broker port");
    static private TextField addressField = new TextField(){{setMinWidth(10);}};
    static private TextField portField = new TextField(){{setMinWidth(10);}};
    static private TextField databaseField = new TextField(){{setMinWidth(10);}};
    static private TextField usernameField = new TextField(){{setMinWidth(10);}};
    static private PasswordField passwordField = new PasswordField(){{setMinWidth(10);}};
    static private TextField mqttAddressField = new TextField(){{setText("127.0.0.1");setMinWidth(10);}};
    static private TextField mqttPortField = new TextField(){{setText("1886");setMinWidth(10);}};
    static private HashMap<String,String> serverParams = new HashMap<>();
    static private FlowPane basePane = new FlowPane();
    static public Node generatePanel()
    {
        basePane.setOrientation(Orientation.VERTICAL);
        basePane.setVgap(10);
        basePane.setHgap(10);
        basePane.getChildren().add(addressLabel);
        basePane.getChildren().add(addressField);
        basePane.getChildren().add(portLabel);
        basePane.getChildren().add(portField);
        basePane.getChildren().add(databaseLabel);
        basePane.getChildren().add(databaseField);
        basePane.getChildren().add(usernameLabel);
        basePane.getChildren().add(usernameField);
        basePane.getChildren().add(passwordLabel);
        basePane.getChildren().add(passwordField);
        basePane.getChildren().add(mqttAddressLabel);
        basePane.getChildren().add(mqttAddressField);
        basePane.getChildren().add(mqttPortLabel);
        basePane.getChildren().add(mqttPortField);
        return basePane;
    }
    static private Node generateRow(Label rowLabel,Node rowField)
    {
        return new HBox(){{getChildren().add(rowLabel);getChildren().add(rowField);}};
    }
    static public HashMap<String,String> getFieldValues()
    {
        serverParams.put("serverAddress",addressField.getText());
        serverParams.put("serverPort",portField.getText());
        serverParams.put("database",databaseField.getText());
        serverParams.put("username",usernameField.getText());
        serverParams.put("password",passwordField.getText());
        serverParams.put("mqttAddress",mqttAddressField.getText());
        serverParams.put("mqttPort",mqttPortField.getText());
        return serverParams;
    }
    static public void disableFields()
    {
        addressField.setDisable(true);
        portField.setDisable(true);
        usernameField.setDisable(true);
        passwordField.setDisable(true);
        mqttAddressField.setDisable(true);
        mqttPortField.setDisable(true);
    }

    static public void enableFields()
    {
        addressField.setDisable(false);
        portField.setDisable(false);
        usernameField.setDisable(false);
        passwordField.setDisable(false);
        mqttAddressField.setDisable(false);
        mqttPortField.setDisable(false);
    }
}
