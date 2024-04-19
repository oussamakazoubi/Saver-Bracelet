package com.demogui.gps_demo;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.web.*;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Dashboard
{
    static private StackPane activeTile = (StackPane) makeTile("gray","Disconnected");
    static private StackPane satelliteTile = (StackPane) makeTile("satellite","0");
    static private StackPane longitudeTile = (StackPane) makeTile("longitude","0.0");
    static private StackPane latitudeTile = (StackPane) makeTile("latitude","0.0");
    static private boolean isFailedConnect = false;
    static private WebView mainView = new WebView();
    static private VBox baseBox = new VBox();
    static public Node generateDashboard()
    {
        mainView.setMaxHeight(480);
        mainView.setMaxWidth(640);

        baseBox.getChildren().add(activeTile);
        baseBox.getChildren().add(mainView);
        baseBox.getChildren().add(
                new HBox()
                {{
                    setSpacing(3);
                    getChildren().add(satelliteTile);
                    getChildren().add(longitudeTile);
                    getChildren().add(latitudeTile);
                }}
        );
        baseBox.setSpacing(10);
        baseBox.setAlignment(Pos.CENTER);
        return baseBox;
    }
    static private Node makeTile(String tileColor, String tileText)
    {
        try
        {
            StackPane testTile_pane = new StackPane();
            Label testTile_label = new Label(tileText);

            testTile_pane.getChildren().add(getStaticAsset("tile_"+tileColor.toLowerCase()+".png"));
            testTile_pane.getChildren().add(testTile_label);
            testTile_label.setTextFill(Color.WHITE);
            return testTile_pane;
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            return null;
        }
    }
    static private ImageView getStaticAsset(String assetName)
    {
        try
        {
            Image asset = new Image(new FileInputStream(System.getProperty("user.dir") +"/static/" + assetName));
            ImageView assetView = new ImageView(asset);
            return assetView;
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            return null;
        }
    }
    static private void updateTile(StackPane tile,String newColor,String newText)
    {
        try
        {
            for (Node node : tile.getChildren())
            {
                if (node instanceof Label) ((Label) node).setText(newText);
                else if (node instanceof ImageView)
                    ((ImageView) node).setImage(new Image(new FileInputStream(System.getProperty("user.dir") + "/static/" + "tile_" + newColor.toLowerCase() + ".png")));
            }
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
    static public void loadPageToView(String pageAddress)
    {
        mainView.getEngine().load(pageAddress);
    }
    static public void loadMapView(double longitude,double latitude)
    {
        String template = "<html>\n" +
                "<body>\n" +
                "    <iframe\n" +
                "      width=\"600\"\n" +
                "      height=\"450\"\n" +
                "      style=\"border:0\"\n" +
                "      loading=\"lazy\"\n" +
                "      allowfullscreen\n" +
                "      src=\"https://www.google.com/maps/embed/v1/view?key=AIzaSyC2vIzzSrUfiLEI8A8rtFK5u6_l-RXj86U&center="+longitude+","+latitude+"&zoom=15&maptype=roadmap\">\n" +
                "    </iframe>\n" +
                "</body>\n" +
                "</html>";
        mainView.getEngine().loadContent(template);
    }
    static public void updateActiveTile(boolean targetInbound,int satellitesFound, boolean serverConnected)
    {

        if (isFailedConnect) {
            updateTile(activeTile,"gray","Failed to Connect to Database or Mqt Server.\n" +
                    "Ensure your login details are correct.\nDisconnected");
            return;
        }

        if(serverConnected )
        {
            if(satellitesFound >= 4)
            {
                if(targetInbound) updateTile(activeTile,"green","Connected To Database\nTarget inbound");
                else updateTile(activeTile,"red","Connected To Database\nTarget out of bounds");
            }
            else
            {
                updateTile(activeTile,"gray","Connected To Database\nLooking for satellites...");
            }
        }
        else
        {
            updateTile(activeTile,"gray","Disconnected");
        }
    }
    static public void updateActiveTile()
    {
        updateTile(activeTile,"gray","Failed to Connect to Database.\n" +
                "Ensure your login details are correct.\nDisconnected");
    }
    static public void updateSatelliteTile(int satelliteCount)
    {
        updateTile(satelliteTile,"satellite",Integer.toString(satelliteCount));
    }
    static public void updateLongitudeTile(double longitude)
    {
        updateTile(longitudeTile,"longitude",Double.toString(longitude));
    }
    static public void updateLatitudeTile(double latitude)
    {
        updateTile(latitudeTile,"latitude",Double.toString(latitude));
    }
    static public void FailedConnection()
    {
        isFailedConnect=true;
    }
    static public void Connection()
    {
        isFailedConnect=false;
    }
    private boolean isPointInsidePolygon(double lat, double lon, Connection conn) throws Exception {
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
