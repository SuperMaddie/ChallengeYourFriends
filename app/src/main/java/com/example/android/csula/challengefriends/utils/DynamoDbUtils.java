package com.example.android.csula.challengefriends.utils;

import android.content.Context;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapperConfig;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.example.android.csula.challengefriends.models.Challenge;
import com.example.android.csula.challengefriends.models.MyProfile;
import com.example.android.csula.challengefriends.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static MyProfile loadProfileByFacebookId(CognitoCachingCredentialsProvider credentialsProvider, String facebookId){
        AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
        DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

        Condition scanFilterCondition = new Condition()
                .withComparisonOperator(ComparisonOperator.EQ.toString())
                .withAttributeValueList(new AttributeValue().withS(facebookId));

        Map<String, Condition> conditions = new HashMap<>();
        conditions.put("facebookId", scanFilterCondition);

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withScanFilter(conditions);

        List<MyProfile> profiles = new ArrayList<>();

        try {
            PaginatedScanList result = mapper.scan(
                    MyProfile.class,
                    scanExpression);
            for(int i = 0; i<result.size(); i++){
                profiles.add((MyProfile) result.get(i));
            }
        }catch(Exception e) {
            e.printStackTrace();
        }

        return (profiles.size() == 0) ? null : profiles.get(0);
    }

    public static MyProfile saveProfile(CognitoCachingCredentialsProvider credentialsProvider, User currentUser) {
        MyProfile profile = new MyProfile(currentUser.getCognitoId(), currentUser.getFacebookId(),currentUser.getGCMId());
        AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
        DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);
        mapper.save(profile);
        return profile;
    }

    public static MyProfile saveProfile(CognitoCachingCredentialsProvider credentialsProvider, MyProfile profile) {
        AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
        DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);
        mapper.save(profile);
        return profile;
    }

    public static List<Challenge> getChallenges(CognitoCachingCredentialsProvider credentialsProvider) {
        AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
        DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

        List<Challenge> challenges = new ArrayList<>();
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        PaginatedScanList result = mapper.scan(
                Challenge.class,
                scanExpression);
        for(int i = 0; i<result.size(); i++){
            challenges.add((Challenge)result.get(i));
        }
        return challenges;
    }

    public static List<Challenge> getReceivedChallenges(CognitoCachingCredentialsProvider credentialsProvider, User currentUser) {
        AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
        DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

        MyProfile profile = loadProfile(credentialsProvider, currentUser.getCognitoId());

        List<String> challengesIds = profile.getReceivedChallengesList();
        List<Challenge> result = new ArrayList<>();
        List<Challenge> challenges = getChallenges(credentialsProvider);

        for(Challenge c : challenges){
            if(challengesIds.contains(c.getId())) {
                result.add(c);
            }
        }

        /*if(challengesIds.size() > 0) {
            Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
            expressionAttributeValues.put(":pr", new AttributeValue().withSS(challengesIds));

            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                    .withFilterExpression("id IN (:pr)")
                    .withExpressionAttributeValues(expressionAttributeValues);

            PaginatedScanList result = mapper.scan(
                    Challenge.class,
                    scanExpression);

            for (int i = 0; i < result.size(); i++) {
                challenges.add((Challenge) result.get(i));
            }
        }*/

        return result;
    }
}
