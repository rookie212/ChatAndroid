package com.example.chatykapp.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatykapp.databinding.ItemContainerUserBinding;
import com.example.chatykapp.listeners.UserListener;
import com.example.chatykapp.models.User;

import java.util.List;

//#128 adapters package and this file
//#136 extends RecylerView....
public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder>{

//    #135 starts
    private final List<User> users;

//    #193 starts
    private final UserListener userListener;
//    #193 ends
//    #194 starts and adds another parameter (UserListener)  into public UsersAdapter
    //Generate constructor
    public UsersAdapter(List<User> users, UserListener userListener) {

        this.users = users;
//        #195
        this.userListener = userListener;
    }
    //    #135 ends

//    #137 startsgenerate implement method for recylerview extension in 136

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        #138 starts
        ItemContainerUserBinding itemContainerUserBinding = ItemContainerUserBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new UserViewHolder(itemContainerUserBinding);
//        #138 ends
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {

//        #139 starts
        holder.setUserData(users.get(position));
//        #139 ends
    }

    @Override
    public int getItemCount() {
//        #140 return 0 changes with users list size
        return users.size();
    }
//#137 ends

    //    #130 starts
    class UserViewHolder extends RecyclerView.ViewHolder {

//        #131 starts
        ItemContainerUserBinding binding;
//        #131 ends
        UserViewHolder(ItemContainerUserBinding itemContainerUserBinding) {
            super(itemContainerUserBinding.getRoot());

//            #132 starts
            binding = itemContainerUserBinding;
//            #132 ends
        }
//        #133 starts You may see more than one "User" class here. Make sure you select the one from our
//    "models" package.
        void setUserData(User user) {
            binding.textName.setText(user.name);
            binding.textEmail.setText(user.email);
            binding.imageProfile.setImageBitmap(getUserImage(user.image));
//            #196
            binding.getRoot().setOnClickListener(v -> userListener.onUserClicked(user));
        }
//    #134 ends
    }
//    #130 ends As we' ve enabled viewBinding for our project, the binding class for each XML layout will be generated automatically.
    //Here, 'ItemContainerUserBinding' class is automatically generated from our layout file: 'item_container_user'

//    #129
    private Bitmap getUserImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
