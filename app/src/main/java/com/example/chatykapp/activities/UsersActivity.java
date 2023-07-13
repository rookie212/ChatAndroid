package com.example.chatykapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.chatykapp.adapters.UsersAdapter;
import com.example.chatykapp.databinding.ActivityUsersBinding;
import com.example.chatykapp.listeners.UserListener;
import com.example.chatykapp.models.User;
import com.example.chatykapp.utilities.Constants;
import com.example.chatykapp.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

//#141
//#197 implements UserListener
//#307 AppCompatActivity replaced with BaseActivity and run one device and check user availability in firebase console.

public class UsersActivity extends BaseActivity implements UserListener {

    //    #156 starts
    private ActivityUsersBinding binding;
//    #156 ends

    //    #160 starts
    private PreferenceManager preferenceManager;
//    #160 ends

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        #157 starts
        binding = ActivityUsersBinding.inflate(getLayoutInflater());
//        #157 ends
//        #158 binding.getRoot() added
        setContentView(binding.getRoot());
//    #161 starts
        preferenceManager = new PreferenceManager(getApplicationContext());
//    #162 ends

//        #167 starts
        setListeners();
//            #167 ends here clean unused imports then run the app

//        #165 starts
        getUsers();
//        #165 ends
    }

    //    #166 starts
    private void setListeners() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
    }
//    #166 ends

    //    #164 starts
    private void getUsers() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<User> users = new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            if (currentUserId.equals(queryDocumentSnapshot.getId())) {
                                continue;
                            }
                            User user = new User();
                            user.name = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                            user.email = queryDocumentSnapshot.getString(Constants.KEY_EMAIL);
                            user.image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                            user.token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
//                            #240 starts
                            user.id = queryDocumentSnapshot.getId();
//                            #240 ends
                            users.add(user);
                        }
                        if (users.size() > 0) {
//                            #198 adds this as parameter next to the users
                            UsersAdapter usersAdapter = new UsersAdapter(users, this);
                            binding.usersRecyclerView.setAdapter(usersAdapter);
                            binding.usersRecyclerView.setVisibility(View.VISIBLE);
                        } else {
                            showErrorMessage();
                        }
                    } else {
                        showErrorMessage();
                    }
                });
    }
//    #164 ends

    //    #163 starts
    private void showErrorMessage() {
        binding.textErrorMessage.setText(String.format("%s", "No user available"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }
//    #163 ends
//    #159 starts

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }
//    #159 ends
//    #200 starts

    @Override
    public void onUserClicked(User user) {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
        finish();
    }

//    #200 ends
}