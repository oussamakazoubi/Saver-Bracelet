module com.demogui.gps_demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires org.eclipse.paho.client.mqttv3;
    requires java.sql;
    opens com.demogui.gps_demo to javafx.fxml;
    exports com.demogui.gps_demo;
}