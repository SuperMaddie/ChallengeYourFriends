package com.example.android.csula.challengefriends.utils;

import android.content.Context;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapperConfig;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.example.android.csula.challengefriends.models.MyProfile;
import com.example.android.csula.challengefriends.models.User;

/**
 * Created by Mahdiye on 6/3/2016.
 */
public class DynamoDbUtils {

    public static CognitoCachingCredentialsProvider init(Context context) {
        /* Initialize the Amazon Cognito credentials provider */
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                context,
                "us-east-1:764851a6-88d3-4ec3-932f-fa716472f6f8", // Identity Pool ID
                Regions.US_EAST_1 // Region
        );

        return credentialsProvider;
    }

    public static MyProfile loadProfile(CognitoCachingCredentialsProvider credentialsProvider, String cognitoId) {
        AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
        DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);
        MyProfile profile = null;
        try {
            profile = mapper.load(MyProfile.class, cognitoId,
                    new DynamoDBMapperConfig(DynamoDBMapperConfig.ConsistentReads.CONSISTENT));

        }catch(Exception e){
            e.printStackTrace();
        }

        return profile;
    }

    public static MyProfile saveProfile(CognitoCachingCredentialsProvider credentialsProvider, User currentUser) {
        MyProfile profile = new MyProfile(currentUser.getCognitoId(), currentUser.getFacebookId());
        AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
        DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);
        mapper.save(profile);
        return profile;
    }
}
