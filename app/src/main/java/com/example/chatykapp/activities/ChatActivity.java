package com.example.chatykapp.activities;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.chatykapp.adapters.ChatAdapter;
import com.example.chatykapp.databinding.ActivityChatBinding;
import com.example.chatykapp.models.ChatMessage;
import com.example.chatykapp.models.User;
import com.example.chatykapp.network.ApiClient;
import com.example.chatykapp.network.ApiService;
import com.example.chatykapp.utilities.Constants;
import com.example.chatykapp.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//#168
//#305 AppCompatActivty replaced with BaseActivity
public class ChatActivity extends BaseActivity {

//    #201 starts
    private ActivityChatBinding binding;
//    #201 ends
//    #203 starts
    private User receiverUser;
//    #203 ends

    //    #232 starts

    private List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore database;
    //    #232 ends
//    #280 starts
    private String conversionId = null;
//    #280 ends
//    #311 starts
    private Boolean isReceiverAvailable = false;
//    #311 ends
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        202 starts
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        //202 continues binding.getRoot
        setContentView(binding.getRoot());
//        #207 starts
        setListeners();
//            #207 ends run and check it you will see the progress bar dont forget to check manifest xml to put .activities on your new activity.
//        #205 starts
        loadReceiverDetails();
//            #205 ends
//        #236 starts
        init();
//        236 ends
//        #251 starts
        listenMessages();
//        #251 ends and clean unused imports
    }

//    #234 starts
    private void init(){
        preferenceManager = new PreferenceManager(getApplicationContext());
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(
                chatMessages,
                getBitmapFromEncodedString(receiverUser.image),
                preferenceManager.getString(Constants.KEY_USER_ID)
        );
        binding.chatRecyclerView.setAdapter(chatAdapter);
        database = FirebaseFirestore.getInstance();
    }
//    #234 ends

//    #241 starts
    private void sendMessage(){
        HashMap<String, Object> message = new HashMap<>();
        message.put(Constants.KEY_SENDER_ID, preferenceManager. getString(Constants.KEY_USER_ID));
        message.put(Constants.KEY_RECEIVER_ID, receiverUser.id);
        message.put(Constants.KEY_MESSAGE, binding.inputMessage.getText().toString());
        message.put(Constants.KEY_TIMESTAMP, new Date());
        database.collection(Constants.KEY_COLLECTION_CHAT).add(message);
//        #287 starts
        if(conversionId != null) {
            updateConversion(binding.inputMessage.getText().toString());
        }else{
            HashMap<String, Object> conversion = new HashMap<>();
            conversion.put(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
            conversion.put(Constants.KEY_SENDER_NAME, preferenceManager.getString(Constants.KEY_NAME));
            conversion.put(Constants.KEY_SENDER_IMAGE, preferenceManager.getString(Constants.KEY_IMAGE));
            conversion.put(Constants.KEY_RECEIVER_ID, receiverUser.id);
            conversion.put(Constants.KEY_RECEIVER_NAME, receiverUser.name);
            conversion.put(Constants.KEY_RECEIVER_IMAGE, receiverUser.image);
            conversion.put(Constants.KEY_LAST_MESSAGE, binding.inputMessage.getText().toString());
            conversion.put(Constants.KEY_TIMESTAMP, new Date());
            addConversion(conversion);
        }
//        #287 ends go to firestore database check collection and delete chat collection then run again it with multiple devices.
// and start message again with two users you will see in database will create chat and conversation collections.
//        #327 starts
        if(!isReceiverAvailable) {
            try{
                JSONArray tokens = new JSONArray();
                tokens.put(receiverUser.token);

                JSONObject data = new JSONObject();
                data.put(Constants.KEY_USER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
                data.put(Constants.KEY_NAME, preferenceManager.getString(Constants.KEY_NAME));
                data.put(Constants.KEY_FCM_TOKEN, preferenceManager.getString(Constants.KEY_FCM_TOKEN));
                data.put(Constants.KEY_MESSAGE, binding.inputMessage.getText().toString());

                JSONObject body = new JSONObject();
                body.put(Constants.REMOTE_MSG_DATA,data);
                body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens);

                sendNotification(body.toString());
            }catch (Exception exception){
                showToast(exception.getMessage());
            }
        }
//        #327 ends now uninstall previously installed apps from all your devices/emulators. So that we can have fresh FCM
//        tokens for our accounts. Otherwise, it will throw an 'invalidRegistration' error message.
        binding.inputMessage.setText(null);
    }
//    #241 ends

//    #322 starts
    public void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
//    #322 ends
//    #323 starts
    private void sendNotification(String messageBody) {
        ApiClient.getClient().create(ApiService.class).sendMessage(
                Constants.getRemoteMsgHeaders(),
                messageBody
        ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call,@NonNull Response<String> response) {
//                #324 starts
                if(response.isSuccessful()) {
                    try {
                        if (response.body() != null) {
                            JSONObject responseJson = new JSONObject(response.body());
                            JSONArray results = responseJson.getJSONArray("results");
                            if(responseJson.getInt("failure") == 1) {
                                JSONObject error = (JSONObject) results.get(0);
                                showToast(error.getString("error"));
                                return;
                            }
                        }

                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                    showToast("Notification sent successfully");
                }else {
                    showToast("Error: " + response.code() );
                }
//                    #324 ends

            }

            @Override
            public void onFailure(@NonNull Call<String> call,@NonNull Throwable t) {

//                #325 starts
                showToast(t.getMessage());
//                #325 ends
            }
        });
    }
//    #323 ends

//    #250 starts again follow correctly
//    #312 starts
    private void listenAvailabilityOfReceiver(){
        database.collection(Constants.KEY_COLLECTION_USERS).document(
                receiverUser.id
        ).addSnapshotListener(ChatActivity.this, (value, error) -> {
           if(error != null) {
               return;
           }
           if(value != null){
               if(value.getLong(Constants.KEY_AVAILABILITY) != null) {
                   int availability = Objects.requireNonNull(
                           value.getLong(Constants.KEY_AVAILABILITY)
                   ).intValue();
                   isReceiverAvailable = availability == 1;
               }
               //            #319 starts
               receiverUser.token = value.getString(Constants.KEY_FCM_TOKEN);
//            #319 ends
//               #337 Starts
               if(receiverUser.image == null) {
                   receiverUser.image = value.getString(Constants.KEY_IMAGE);
                   chatAdapter.setReceiverProfileImage(getBitmapFromEncodedString(receiverUser.image));
                   chatAdapter.notifyItemRangeChanged(0,chatMessages.size());
               }
//              #337 ends
           }
//           #315 Starts
           if(isReceiverAvailable){
               binding.textAvailability.setVisibility(View.VISIBLE);

           } else {
               binding.textAvailability.setVisibility(View.GONE);
           }
//               #315 ends
        });
    }
//    #312 ends
    private void listenMessages() {
        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverUser.id)
                .addSnapshotListener(eventListener);
        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID, receiverUser.id)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }
//    #250 ends

//    #245 starts Next logic is a bit complicated. Make sure you follow carefully
    //eventlistener from firebase firestore
    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if(error != null) {
            return;
        }
        if(value != null) {
            int count = chatMessages.size();
            //document change firebasestore
            for (DocumentChange documentChange : value.getDocumentChanges()) {
//                #246 starts
                if(documentChange.getType() == DocumentChange.Type.ADDED) {

//                    #247 starts the whole block part of the 245 comes into (if created only one top{})here

                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    chatMessage.receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    chatMessage.message = documentChange.getDocument().getString(Constants.KEY_MESSAGE);
                    chatMessage.dateTime = getReadableDateTime(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));
                    chatMessage.dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    chatMessages.add(chatMessage);

//                    #247 ends
                }
//                #246 ends


            }
//            #248 starts
            Collections.sort(chatMessages, (obj1, obj2) -> obj1.dateObject.compareTo(obj2.dateObject));
            if(count ==0) {
                chatAdapter.notifyDataSetChanged();

            }else {
                chatAdapter.notifyItemRangeInserted(chatMessages.size(), chatMessages.size());
                binding.chatRecyclerView.smoothScrollToPosition(chatMessages.size() -1);
            }
            binding.chatRecyclerView.setVisibility(View.VISIBLE);
//            #248 ends
        }
//        #249 starts
        binding.progressBar.setVisibility(View.GONE);
//        #249 ends
//    #284 starts
        if(conversionId == null) {
            checkForConversion();
        }
//    #284 ends
    };
//    @245 ends


//    #233 starts
    private Bitmap getBitmapFromEncodedString(String encodedImage) {
//        #335 starts
        if(encodedImage != null) {
//            #336 below codes added into the if block
            byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0,bytes.length);
        }else {
            return null;
        }
//        #335 ends

    }
//    #233 ends

//    #204 starts
    private void loadReceiverDetails(){
        receiverUser = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        binding.textName.setText(receiverUser.name);
    }
//    #204 ends

//    #206 starts
    private void setListeners() {

        binding.imageBack.setOnClickListener(v -> onBackPressed());
//      #242 starts
        binding.layoutSend.setOnClickListener(v -> sendMessage());
//      #242 ends go to firestoredatabase click the rules and change the rules like below publish it then run the app
        //choose a user and type a message then click send then go to firestoreDatabase
        //refresh the page a new chat collection will be added
        /*
        rules_version = '2';
        service cloud.firestore {
            match /databases/{database}/documents {
                match /{document=**} {
                    allow read, write: if
                        true;
                }
            }
        }
242 comments ended here         */
    }
//    #206 ends

//    #244 starts
    private String getReadableDateTime(Date date){
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }
//    #244 ends

//    #285 starts
    private void addConversion(HashMap<String, Object> conversion) {
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .add(conversion)
                .addOnSuccessListener(documentReference -> conversionId = documentReference.getId());
    }
//    #285 ends

//    #286 starts
    private void updateConversion(String message) {
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_CONVERSATIONS).document(conversionId);
        documentReference.update(
                Constants.KEY_LAST_MESSAGE, message,
                Constants.KEY_TIMESTAMP, new Date()
        );
    }
//    #286 ends

//    #283 starts
    private void checkForConversion(){
        if(chatMessages.size() != 0) {
            checkForConversionRemotely(
                    preferenceManager.getString(Constants.KEY_USER_ID),
                    receiverUser.id

            );checkForConversionRemotely(
                    receiverUser.id,
                    preferenceManager.getString(Constants.KEY_USER_ID)
            );
        }
    }
//    #283 ends

//    #282 starts
    private void checkForConversionRemotely(String senderId, String receiverId) {
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID, senderId)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverId)
                .get()
                .addOnCompleteListener(conversionOnCompleteListener);
    }
//    #282 ends

    //    #281 starts
    private final OnCompleteListener<QuerySnapshot> conversionOnCompleteListener = task -> {

        if(task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() >0){
            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
            conversionId = documentSnapshot.getId();
        }
    };
//            #281 ends
//    #317 starts
    @Override
    protected void onResume() {
        super.onResume();
        listenAvailabilityOfReceiver();
    }
//    #317 ends run multi device
}