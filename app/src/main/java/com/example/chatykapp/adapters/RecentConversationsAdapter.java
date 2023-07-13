package com.example.chatykapp.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatykapp.databinding.ItemContainerRecentConversionBinding;
import com.example.chatykapp.listeners.ConversionListener;
import com.example.chatykapp.models.ChatMessage;
import com.example.chatykapp.models.User;

import java.util.List;

//#264
//#269 extends RecyclerView.adapter
public class RecentConversationsAdapter extends RecyclerView.Adapter<RecentConversationsAdapter.ConversionViewHolder> {

    //    #267 Starts
    private final List<ChatMessage> chatMessages;
//    #267 Ends

    //    #292 starts
    private final ConversionListener conversionListener;
//    #292 ends

    //    #268 starts generate/constructor
// #293 an extra parameter added RecentConversationsAdapter: ConversionListener
    public RecentConversationsAdapter(List<ChatMessage> chatMessages, ConversionListener conversionListener) {
        this.chatMessages = chatMessages;
//        #294 related 293
        this.conversionListener = conversionListener;
    }
//    #268 ends

    //    #270 starts
    @NonNull
    @Override
    public ConversionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        #271 starts
        return new ConversionViewHolder(
                ItemContainerRecentConversionBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
//        #271 ends
    }

    @Override
    public void onBindViewHolder(@NonNull ConversionViewHolder holder, int position) {
//        #272 starts
        holder.setData(chatMessages.get(position));
//        #272 ends
    }

    @Override
    public int getItemCount() {
//        #273 starts
        return chatMessages.size();
//        #273 ends
    }
    //    #270 ends

    //    #265
    private Bitmap getConversionImage(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
//    #266 ends

    //    #266 starts
    class ConversionViewHolder extends RecyclerView.ViewHolder {
        final ItemContainerRecentConversionBinding binding;

        ConversionViewHolder(ItemContainerRecentConversionBinding itemContainerRecentConversionBinding) {
            super(itemContainerRecentConversionBinding.getRoot());
            binding = itemContainerRecentConversionBinding;
        }

        void setData(ChatMessage chatMessage) {
            binding.imageProfile.setImageBitmap(getConversionImage(chatMessage.conversionImage));
            binding.textName.setText(chatMessage.conversionName);
            binding.textRecentMessage.setText(chatMessage.message);
//            #295 starts
            binding.getRoot().setOnClickListener(v -> {
                User user = new User();
                user.id = chatMessage.conversionId;
                user.name = chatMessage.conversionName;
                user.image = chatMessage.conversionImage;
                conversionListener.onConversionClicked(user);
            });
//                #295 ends
        }
    }
}
