package com.example.android.csula.challengefriends.models;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mahdiye on 6/2/2016.
 */

@DynamoDBTable(tableName = "profiles")
public class MyProfile implements Serializable {
    private String GCMID;
    private String cognitoId;
    private String facebookId;
    private String name;
    private List<String> receivedChallengesList;
    private List<String> sentChallengesList;

    public MyProfile() {
    }

    public MyProfile(String name, String cognitoId, String facebookId, String GCMID) {
        this.name = name;
        this.GCMID = GCMID;
        this.cognitoId = cognitoId;
        this.facebookId = facebookId;
        receivedChallengesList = new ArrayList<>();
        sentChallengesList = new ArrayList<>();

    }

    @DynamoDBHashKey(attributeName = "userId")
    public String getCognitoId() {
        return cognitoId;
    }

    public void setCognitoId(String cognitoId) {
        this.cognitoId = cognitoId;
    }

    @DynamoDBAttribute(attributeName = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @DynamoDBAttribute(attributeName = "facebookId")
    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    @DynamoDBAttribute(attributeName = "receivedChallengesList")
    public List<String> getReceivedChallengesList() {
        return receivedChallengesList;
    }

    public void setReceivedChallengesList(List<String> receivedChallengesList) {
        this.receivedChallengesList = receivedChallengesList;
    }

    @DynamoDBAttribute(attributeName = "GCMID")
    public String getGCMID() {
        return GCMID;
    }

    public void setGCMID(String GCMID) {
        this.GCMID = GCMID;
    }

    @DynamoDBAttribute(attributeName = "sentChallengesList")
    public List<String> getSentChallengesList() {
        return sentChallengesList;
    }

    public void setSentChallengesList(List<String> sentChallengesList) {
        this.sentChallengesList = sentChallengesList;
    }
}
