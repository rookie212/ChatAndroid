package com.example.chatykapp.adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatykapp.databinding.ItemContainerReceivedMessageBinding;
import com.example.chatykapp.databinding.ItemContainerSentMessageBinding;
import com.example.chatykapp.models.ChatMessage;

import java.util.List;

//#209
//#219 extends recyclerView.adapter
public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

//    #216 starts
    private final List<ChatMessage> chatMessages;
//    #216 ends

//    #214 starts
//    #333 final is reduced
    private Bitmap receiverProfileImage;
//    #214 ends

//    #217 starts
    private final String senderId;
//    #217 ends

//    #221 starts
    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;
//    #221 endss
//    #334 start
    public void setReceiverProfileImage(Bitmap bitmap){
        receiverProfileImage = bitmap;
    }
//    #334 ends

//    #218 GENERATE/CONSTRUCTOR


    public ChatAdapter(List<ChatMessage> chatMessages, Bitmap receiverProfileImage, String senderId) {
        this.chatMessages = chatMessages;
        this.receiverProfileImage = receiverProfileImage;
        this.senderId = senderId;
    }

//    #218 ends

//    #220 Generate/implement methods


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        #223 starts
        if(viewType == VIEW_TYPE_SENT){
            return new SentMessageViewHolder(
                    ItemContainerSentMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        }else {
            return new ReceivedMessageViewHolder(
                    ItemContainerReceivedMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        }
//        #223 ends
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

//        #224 starts
        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            ((SentMessageViewHolder) holder).setData(chatMessages.get(position));
        } else {
            ((ReceivedMessageViewHolder) holder).setData(chatMessages.get(position),receiverProfileImage);
        }
//        #224 ends
    }

    @Override
    public int getItemCount() {
//        #225
        return chatMessages.size();
    }

//    #220ends

//    #222 starts

    @Override
    public int getItemViewType(int position) {
        if(chatMessages.get(position).senderId.equals(senderId)) {
            return VIEW_TYPE_SENT;
        }else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    //    #222 ends
    static class SentMessageViewHolder extends RecyclerView.ViewHolder {

//        #210
        private final ItemContainerSentMessageBinding binding;

        //#211 As we've enabled viewBinding for our project, the binding class
//        for each XML layout will be generated automatically.
        //Her 'ItemContainerSentMessageBinding' class is automatically
//        generated from our layout file: 'item_container_sent_message'

        SentMessageViewHolder(ItemContainerSentMessageBinding itemContainerSentMessageBinding){

//            #212
            super(itemContainerSentMessageBinding.getRoot());
            binding = itemContainerSentMessageBinding;
        }

//        #213
        void setData(ChatMessage chatMessage) {
            binding.textMessage.setText(chatMessage.message);
            binding.textDateTime.setText(chatMessage.dateTime);
        }
    }
//    #215 starts
    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {

        private final ItemContainerReceivedMessageBinding binding;

        //here 'itemContainerReceivedMessageBinding' class is automatically generated from our layout file:
//        'item_container_received_message'

        ReceivedMessageViewHolder(ItemContainerReceivedMessageBinding itemContainerReceivedMessageBinding) {
            super(itemContainerReceivedMessageBinding.getRoot());
            binding = itemContainerReceivedMessageBinding;
        }
        void setData(ChatMessage chatMessage, Bitmap receiverProfileImage){
            binding.textMessage.setText(chatMessage.message);
            binding.textDateTime.setText(chatMessage.dateTime);
//            #331 starts
            if(receiverProfileImage != null) {
//#332 below code was below the if block and it is added into it
                binding.imageProfile.setImageBitmap(receiverProfileImage);
            }
//            #331 ends

        }
    }
//    #215 ends
}
