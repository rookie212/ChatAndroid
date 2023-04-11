package com.example.chatykapp.utilities;

import java.util.HashMap;

//#72
public class Constants {
//    #73
    public static final String KEY_COLLECTION_USERS = "users";
    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_PREFERENCE_NAME = "chatAppPreference";
    public static final String KEY_IS_SIGNED_IN = "isSignedIn";
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_IMAGE = "image";
//    #106
    public static final String KEY_FCM_TOKEN = "fcmToken";
//    #199
    public static final String KEY_USER = "user";
//    #238
    public static final String KEY_COLLECTION_CHAT = "chat";
    public static final String KEY_SENDER_ID = "senderId";
    public static final String KEY_RECEIVER_ID = "receiverId";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_TIMESTAMP = "timestamp";
//    #279
    public static final String KEY_COLLECTION_CONVERSATIONS = "conversations";
    public static final String KEY_SENDER_NAME = "senderName";
    public static final String KEY_RECEIVER_NAME = "receiverName";
    public static final String KEY_SENDER_IMAGE = "senderImage";
    public static final String KEY_RECEIVER_IMAGE = "receiverImage";
    public static final String KEY_LAST_MESSAGE = "lastMessage";
//    #300
    public static final String KEY_AVAILABILITY = "availability";
//    #320
    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";

//    #321
    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";
//    Above 320 and 321 keys are important and be sure you wrote them correct
//    #320 continues go to firebase console next of the project overview click the project settings and cloud messaging you need to find a key value

    public static HashMap<String, String> remoteMsgHeaders = null;
    public static HashMap<String, String> getRemoteMsgHeaders() {
        if(remoteMsgHeaders == null ) {
            remoteMsgHeaders = new HashMap<>();
            remoteMsgHeaders.put(
                    REMOTE_MSG_AUTHORIZATION,
                    "key=AAAA3poi-1Y:APA91bEYftgKvQ2AQ2L-yKuTXmxEgpmAJnSKhyrT-OiLa8IIsnfkS_5RG5Z518n0WQKuJlPOxY4sUo8oAfIu_RjRJTZ6uWekPdSWDjYapA8PMX47LhQn8AcAHE8wibOn7_DFpnBc1Yax"

            );
            remoteMsgHeaders.put(
                    REMOTE_MSG_CONTENT_TYPE,
                    "application/json"
            );
        }
        return remoteMsgHeaders;
    }
//    #320 continues end

}
