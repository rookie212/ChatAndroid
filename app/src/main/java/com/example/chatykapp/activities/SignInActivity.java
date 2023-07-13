package com.example.chatykapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatykapp.databinding.ActivitySignInBinding;
import com.example.chatykapp.utilities.Constants;
import com.example.chatykapp.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignInActivity extends AppCompatActivity {


    //    #22
    private ActivitySignInBinding binding;
    //As we've enabled viewBinding for our project, the binding class for each XML layout will be generated automatically.
    //Here 'ActivitySignInBinding' class is automatically generated from layout file: 'activity_sign_in;

    //    #95 starts
    private PreferenceManager preferenceManager;
//    #95 ends

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        #96 starts
        preferenceManager = new PreferenceManager(getApplicationContext());
//        #96 ends

//        #98starts
        if (preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
//        #98ends and runs the app again so you will be already signed in and in main activity
//        #23
        binding = ActivitySignInBinding.inflate(getLayoutInflater());

//        #24 binding.getRoot()
        setContentView(binding.getRoot());

//        #26
        setListeners();
    }

    //    #25
    private void setListeners() {
        //when you write binding. 'As you can see, an instance of binding class contains direct references to all views that
        //have an ID in the corresponding layout.'
        binding.textCreateNewAccount.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class)));
//        #90 starts
        binding.buttonSignIn.setOnClickListener(v -> {
            if (isValidSignInDetails()) {
                signIn();
            }
        });
//        #90 ends

//        #55 button will be activated
//        binding.buttonSignIn.setOnClickListener(v -> addDataToFirestore());

    }
//    #54 We will add dummy data to the cloud firestore database in order to check whether the cloud firestore is set up correctly or not
//    private void addDataToFirestore(){
//        FirebaseFirestore database= FirebaseFirestore.getInstance();
//        HashMap<String, Object> data = new HashMap<>();
//        data.put("first_name", "Che");
//        data.put("last_name", "Guevara");
//        database.collection("users")
//                .add(data)
//                .addOnSuccessListener(documentReference -> {
//                    Toast.makeText(getApplicationContext(), "data inserted", Toast.LENGTH_SHORT).show();
//                })
//                .addOnFailureListener(exception -> {
//                    Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
//                });
//    }
//    #59 if 58 is ok you can delete 54 55 and then optimized related import packages.

    //#89 starts
    private void signIn() {
//        #97starts
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL, binding.inputEmail.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD, binding.inputPassword.getText().toString())
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                        preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
                        preferenceManager.putString(Constants.KEY_NAME, documentSnapshot.getString(Constants.KEY_NAME));
                        preferenceManager.putString(Constants.KEY_IMAGE, documentSnapshot.getString(Constants.KEY_IMAGE));
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        loading(false);
                        showToast("Unable to sign in");
                    }
                });
//        #97ends go to database check one accoun already you created and run app and sign in with that credentials.
    }

    //#89 ends
//    #94 starts

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.buttonSignIn.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);

        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.buttonSignIn.setVisibility(View.VISIBLE);
        }
    }
//    #94Ends

    //    #87
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    //    #88
    private Boolean isValidSignInDetails() {
        if (binding.inputEmail.getText().toString().trim().isEmpty()) {

            showToast("Enter email");
            return false;

        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()) {

            showToast("Enter valid email");
            return false;

        } else if (binding.inputPassword.getText().toString().trim().isEmpty()) {
            showToast("Enter password");
            return false;

        } else {
            return true;
        }

    }

}