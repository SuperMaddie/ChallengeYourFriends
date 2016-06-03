package com.example.android.csula.challengefriends.models;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Mahdiye on 6/2/2016.
 */

@DynamoDBTable(tableName = "profiles")
public class Profile implements Serializable{

    private String cognitoId;
    private Set<String> receivedChallengesList;
    private Set<String> sentChallengesList;

    public Profile(){
        receivedChallengesList = new HashSet<>();
        sentChallengesList = new HashSet<>();
    }

    @DynamoDBHashKey(attributeName = "cognito_id")
    public String getCognitoId() {
        return cognitoId;
    }

    public void setCognitoId(String cognitoId) {
        this.cognitoId = cognitoId;
    }

    @DynamoDBAttribute(attributeName = "received_challenges_list")
    public Set<String> getReceivedChallengesList() {
        return receivedChallengesList;
    }

    public void setReceivedChallengesList(Set<String> receivedChallengesList) {
        this.receivedChallengesList = receivedChallengesList;
    }

    @DynamoDBAttribute(attributeName = "sent_challenges_list")
    public Set<String> getSentChallengesList() {
        return sentChallengesList;
    }

    public void setSentChallengesList(Set<String> sentChallengesList) {
        this.sentChallengesList = sentChallengesList;
    }
}
