package com.example.android.csula.challengefriends.models;

import java.io.Serializable;

/**
 * Created by Mahdiye on 5/10/2016.
 */
public class User implements Serializable{

    private String cognitoId;
    private String facebookId;
    private String name;
    private String GCMId;

    public User(){}

    public User(String cognitoId, String facebookId, String name) {
        this.cognitoId = cognitoId;
        this.facebookId = facebookId;
        this.name = name;
    }

    public String getCognitoId() {
        return cognitoId;
    }

    public void setCognitoId(String cognitoId) {
        this.cognitoId = cognitoId;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGCMId() {
        return GCMId;
    }

    public void setGCMId(String GCMId) {
        this.GCMId = GCMId;
    }

    @Override
    public String toString() {
        return name;
    }

}
