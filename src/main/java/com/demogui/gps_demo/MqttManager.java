package com.demogui.gps_demo;

import org.eclipse.paho.client.mqttv3.*;

import java.util.HashMap;

public class MqttManager implements MqttCallback
{
    private final int maxAttempts = 10;
    private MqttClient mainClient;
    private String brokerURI;
    private HashMap<String,String> mqttMap;
    private String longTopic = "longTopic";
    private String latTopic = "latTopic";
    private String satTopic = "satTopic";
    public MqttManager(String brokerAddress, String port)
    {
         mqttMap = new HashMap<>();
         this.brokerURI = "tcp://"+brokerAddress+":"+port;
         this.initMqtt();
         this.addTopic(longTopic);
         this.addTopic(latTopic);
        this.addTopic(satTopic);
    }
    public boolean getConnectionStatus()
    {
        return mainClient.isConnected();
    }
    private void initMqtt()
    {
        try
        {
        mainClient = new MqttClient(this.brokerURI,"");
        mainClient.setCallback(this);
        }
        catch(MqttException e)
        {
            System.out.println(e.getCause());
        }
    }

    public void connect()
    {
        try
        {
            for(int i = 0; i < maxAttempts;i++)
            {
                if(mainClient.isConnected()) break;
                else mainClient.connect();

            }
        }
        catch(MqttException e)
        {
            System.out.println(e.getCause());
        }
    }
    public void disconnect()
    {
        try
        {
            mainClient.disconnect();
        }
        catch(MqttException e)
        {
            System.out.println(e.getCause());
        }
    }
    public int subscribeToTopics()
    {
        for(String topic : mqttMap.keySet())
        {
            try
            {
                mainClient.subscribe(topic, 0);
                System.out.println("subbed to" + topic);
            }
            catch(Exception e)
            {
                System.out.println(e.getMessage());
                return -1;
            }
        }
        return 0;
    }
    public void addTopic(String topic,String defaultValue)
    {
        mqttMap.put(topic,defaultValue);
    }
    public void addTopic(String topic)
    {
        mqttMap.put(topic,"0.0");
    }
    public int removeTopic(String topic)
    {
        mqttMap.remove(topic);
        try
        {
            mainClient.unsubscribe(topic);
            return 0;
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            return -1;
        }
    }
    public int publish(String topic,String payload)
    {
        mqttMap.put(topic,payload);

        try
        {
            if(mainClient.isConnected()) mainClient.publish(topic, new MqttMessage(payload.getBytes()));
            return 0;
        }
        catch(MqttException e)
        {
            System.out.println(e.getCause());
            return -1;
        }
    }
    public void setLongTopic(String topicName)
    {
        removeTopic(longTopic);
        longTopic = topicName;
        this.addTopic(longTopic);
    }
    public void setLatTopic(String topicName)
    {
        removeTopic(longTopic);
        latTopic = topicName;
        this.addTopic(latTopic);
    }
    public void setSatTopic(String topicName)
    {
        removeTopic(satTopic);
        satTopic = topicName;
        this.addTopic(satTopic);
    }
    public double getLongitude()
    {
        if(mqttMap.get(longTopic) != null) return Double.valueOf(mqttMap.get(longTopic));
        else return 0.0;
    }
    public double getLatitude()
    {
        if(mqttMap.get(latTopic) != null) return Double.valueOf(mqttMap.get(latTopic));
        else return 0.0;
    }
    public int getSatelliteCount()
    {
        if(mqttMap.get(satTopic) != null) return Integer.valueOf(mqttMap.get(satTopic));
        else return 0;
    }
    public void waitForConnection()
    {
        this.connect();
        while(!this.getConnectionStatus())
        {
            this.connect();
            try
            {
                Thread.sleep(500);
            }
            catch(Exception e)
            {
                System.out.println(e.getMessage());
            }
        }
    }
    @Override
    public void connectionLost(Throwable throwable) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception
    {
        mqttMap.put(topic,new  String(mqttMessage.getPayload()));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}
