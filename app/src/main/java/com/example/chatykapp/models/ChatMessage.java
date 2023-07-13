package com.example.chatykapp.models;

import java.util.Date;

//#208
public class ChatMessage {

    public String senderId, receiverId, message, dateTime;
    //    #243 starts
    public Date dateObject;
//    #243 ends

    //    #263 starts
    public String conversionId, conversionName, conversionImage;
//    #263 ends
}
