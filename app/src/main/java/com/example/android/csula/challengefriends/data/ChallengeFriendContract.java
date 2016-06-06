package com.example.android.csula.challengefriends.data;

import android.provider.BaseColumns;

/**
 * Created by Savin on 6/5/2016.
 */
public class ChallengeFriendContract  {

    public static final class Challenges implements BaseColumns{
        public static final String TABLE_NAME = "challenges";
        public static final String COLUMN_CHALLENGE_ID= "challenge_id";
        public static final String COLUMN_CHALLENGE_TITLE= "challenge_title";
        public static final String COLUMN_CHALLENGE_DESCRIPTION = "challenge_description";
    }

    public static final class ReceivedChallenges implements BaseColumns{
        public static final String TABLE_NAME = "received_challenges";
        public static final String COLUMN_CHALLENGE_ID= "r_challenge_id";
        public static final String COLUMN_CHALLENGE_TITLE= "r_challenge_title";
        public static final String COLUMN_CHALLENGE_DESCRIPTION = "r_challenge_description";
    }


}




