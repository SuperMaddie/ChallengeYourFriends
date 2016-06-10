package com.example.android.csula.challengefriends.models;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.io.Serializable;

/**
 * Created by Mahdiye on 6/1/2016.
 */
@DynamoDBTable(tableName = "challenges")
public class Challenge implements Serializable{
    private String id;
    private String title;
    private String description;
    private String senderName;
    private String senderId;

    public Challenge(){}

    public Challenge(String id, String title, String description, String senderName, String senderId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.senderName = senderName;
        this.senderId = senderId;
    }


    @DynamoDBHashKey(attributeName = "id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @DynamoDBAttribute(attributeName = "title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @DynamoDBAttribute(attributeName = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @DynamoDBAttribute(attributeName = "senderName")
    public String getSenderName() { return senderName; }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    @DynamoDBAttribute(attributeName = "senderId")
    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    @Override
    public String toString() {
        String sender = (senderName == null || senderName.isEmpty()) ? ""  : senderName + ": ";
        return sender + title + " " + description;
    }
}