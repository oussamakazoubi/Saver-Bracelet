package com.demogui.gps_demo;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.io.FileInputStream;
import java.util.HashMap;

public class Main extends Application
{
    static private Boolean isConnected = false;
    static private Button connectButton = new Button();
    private Connection conn = null;
    private static boolean targetInBound = false;
    private static double lon = 33.547291958730675;
    private static double lat = -7.649906873308493;
    MqttManager mainManager = new MqttManager("127.0.0.1","1886");

    static public void main(String[] args)
    {
        Application.launch(args);
    }
    public void start(Stage mainStage)
    {
        BorderPane rootPane = new BorderPane();
        Scene mainScene = new Scene(rootPane,Color.BEIGE);
        Insets insets = new Insets(10);
        FlowPane leftPane = (FlowPane) serverPanel.generatePanel();
        VBox dashboard = (VBox) Dashboard.generateDashboard();
        leftPane.getChildren().add(new HBox()
        {{
            setSpacing(10);
            getChildren().add(new Button("Connect"){{setOnMousePressed(e -> connect());}});
            getChildren().add(new Button("Disconnect"){{setOnMousePressed(e -> disconnect());}});
        }});

        rootPane.setLeft(leftPane);
        rootPane.setCenter(dashboard);

        rootPane.setMargin(leftPane,insets);
        rootPane.setMargin(dashboard,insets);

        Dashboard.loadMapView(lon,lat);
        mainStage.setScene(mainScene);
        mainStage.sizeToScene();
        mainStage.setResizable(false);
        mainStage.show();
        new Thread(() ->
        {
            while(true)
            {
                Platform.runLater
                        (
                                () ->
                                {

                                    if (isConnected) {
                                       try {
                                           lon = mainManager.getLongitude();
                                           lat = mainManager.getLatitude();
                                           targetInBound = isPointInsidePolygon(lat, lon);
                                       } catch (Exception e) {
                                       }
                                    }
                                    Dashboard.updateActiveTile(true,5,isConnected);
                                    if(isConnected)
                                    {
                                        serverPanel.disableFields();
                                    }
                                    else
                                    {
                                        serverPanel.enableFields();
                                    }
                                }
                        );
                if (!mainStage.isShowing()) System.exit(0);
                try
                {
                    Thread.sleep(166);
                }
                catch (Exception e)
                {
                    System.out.println(e.getMessage());
                }
            }
        }
        ).start();
    }
    public void connect()
    {
        System.out.println(serverPanel.getFieldValues());
        HashMap<String,String> serverParams = serverPanel.getFieldValues();
        String dbUrl = "jdbc:postgresql://" + serverParams.get("serverAddress")
                        + ":" + serverParams.get("serverPort")
                        + "/" + serverParams.get("database");
        System.out.println(dbUrl);
        String username = serverParams.get("username");
        String password = serverParams.get("password");

        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
            conn = DriverManager.getConnection(dbUrl, username, password);
            isConnected = true;
            Dashboard.Connection();
            MqttManager mainManager = new MqttManager("127.0.0.1","1886");
            mainManager.waitForConnection();
            mainManager.subscribeToTopics();
        } catch (Exception e) {
            Dashboard.FailedConnection();}
        Dashboard.loadMapView(lon,lat);
    }
    public void disconnect()
    {
        Dashboard.updateActiveTile(false,0,false);
        isConnected = false;
        conn = null;
        Dashboard.Connection();
    }
    private boolean isPointInsidePolygon(double lat, double lon) throws Exception {
        String query = "SELECT id, name FROM Authorized WHERE ST_Contains(geom, ST_SetSRID(ST_Point(?, ?), 4326))";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setDouble(1, 1); //
            pstmt.setDouble(2, -0.5);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            throw new Exception("Database query failed: " + e.getMessage());
        }
    }

}
