package com.example.android.csula.challengefriends.utils;

import com.example.android.csula.challengefriends.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mahdiye on 6/2/2016.
 */
public class JsonUtils {

    public static List<User> getFriends(String json) {
        List<User> users = new ArrayList<>();
        User user;
        JSONObject userJsonObject;

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray data = jsonObject.getJSONArray("data");
            for(int i = 0; i<data.length(); i++) {
                user = new User();
                userJsonObject = data.getJSONObject(i);
                user.setName(userJsonObject.getString("name"));
                user.setFacebookId(userJsonObject.getString("id"));
                users.add(user);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return users;
    }

    public static User getUserInfo(String json) {
        User user = new User();

        try {
            JSONObject jsonObject = new JSONObject(json);
            user.setName(jsonObject.getString("name"));
            user.setFacebookId(jsonObject.getString("id"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }
}
