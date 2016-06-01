package com.example.android.csula.challengefriends.models;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.io.Serializable;

/**
 * Created by Mahdiye on 5/10/2016.
 */
@DynamoDBTable(tableName = "users")
public class User implements Serializable{

    private String id;
    private String name;
    private String email;
    private String faceBookId;
    private String gender;

    public User(){}

    public User(String id, String name, String email, String faceBookId, String gender) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.faceBookId = faceBookId;
        this.gender = gender;
    }

    @DynamoDBHashKey(attributeName = "id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFaceBookId() {
        return faceBookId;
    }

    public void setFaceBookId(String faceBookId) {
        this.faceBookId = faceBookId;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
