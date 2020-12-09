package com.example.care2u;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.care2u.Prevalent.Prevalent;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private CircleImageView profileImageView;
    private EditText inputEmail, inputPhone, inputPassword, inputAddress;
    private TextView closeSettingsBtn, updateSettingsBtn, changeProfile;

    private Uri imageUri;
    private String myUrl = "";
    private StorageTask uploadTask;
    private StorageReference storageRProfilePicRef;
    private String checker = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        storageRProfilePicRef = FirebaseStorage.getInstance().getReference().child("Profile pictures");

        profileImageView = (CircleImageView) findViewById(R.id. settings_profile);

        inputEmail = (EditText) findViewById(R.id.settings_email);
        inputPhone = (EditText) findViewById(R.id.settings_phone);
        inputAddress = (EditText) findViewById(R.id.settings_address);
        inputPassword = (EditText) findViewById(R.id.settings_password);

        closeSettingsBtn = (TextView) findViewById(R.id.close_settings_btn);
        updateSettingsBtn = (TextView) findViewById(R.id.update_settings_btn);
        changeProfile = (TextView) findViewById(R.id.settings_change_profile);

        displayUserInfo(profileImageView, inputEmail, inputPhone, inputAddress, inputPassword);

        closeSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        updateSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checker.equals("clicked")){
                    userInfoSaved();
                }else{
                    updateOnlyUserInfo();
                }
            }
        });

        changeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checker = "clicked";

                CropImage.activity(imageUri)
                        .setAspectRatio(1,1)
                        .start(SettingsActivity.this);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data!=null){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            profileImageView.setImageURI(imageUri);

        }else{
            Toast.makeText(this, "Error, please try again.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SettingsActivity.this, SettingsActivity.class));
            finish();
        }

    }

    private void updateOnlyUserInfo(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("email", inputEmail.getText().toString());
        userMap.put("phone", inputPhone.getText().toString());
        userMap.put("address", inputAddress.getText().toString());
        userMap.put("password", inputPassword.getText().toString());
        ref.child(Prevalent.currentUser.getUsername()).updateChildren(userMap);

        startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
        Toast.makeText(SettingsActivity.this, "User Info update successfully.", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void userInfoSaved() {

        if (TextUtils.isEmpty(inputEmail.getText().toString())){
            Toast.makeText(this, "Email is mandatory.", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(inputPhone.getText().toString())){
            Toast.makeText(this, "Phone Number is mandatory.", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(inputAddress.getText().toString())){
            Toast.makeText(this, "Address is mandatory.", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(inputPassword.getText().toString())){
            Toast.makeText(this, "Password is mandatory.", Toast.LENGTH_SHORT).show();
        }else if(checker == "clicked"){
            uploadImage();
        }
    }

    private void uploadImage(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Updating Profile");
        progressDialog.setMessage("Please wait, while we are updating your account information.");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (imageUri != null){
            final StorageReference fileRef = storageRProfilePicRef
                    .child(Prevalent.currentUser.getUsername() + ".jpg");

            uploadTask = fileRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {

                    if (!task.isSuccessful()){
                        throw  task.getException();
                    }

                    return  fileRef.getDownloadUrl();
                }
            })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {

                            if (task.isSuccessful()){
                                Uri downloadUrl = task.getResult();
                                myUrl = downloadUrl.toString();

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

                                HashMap<String, Object> userMap = new HashMap<>();
                                userMap.put("email", inputEmail.getText().toString());
                                userMap.put("phone", inputPhone.getText().toString());
                                userMap.put("address", inputAddress.getText().toString());
                                userMap.put("password", inputPassword.getText().toString());
                                userMap.put("image", myUrl);
                                ref.child(Prevalent.currentUser.getUsername()).updateChildren(userMap);

                                progressDialog.dismiss();

                                startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
                                Toast.makeText(SettingsActivity.this, "User Info update successfully.", Toast.LENGTH_SHORT).show();
                                finish();

                            }else{
                                progressDialog.dismiss();
                                Toast.makeText(SettingsActivity.this, "Error.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }else{
            Toast.makeText(this, "Image is not selected.", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayUserInfo(final CircleImageView profileImageView, final EditText inputEmail,
                                 final EditText inputPhone, final EditText inputAddress, final EditText inputPassword) {

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().
                child("Users").child(Prevalent.currentUser.getUsername());

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){
                    if (snapshot.child("image").exists()){
                        String image = snapshot.child("image").getValue().toString();
                        String email = snapshot.child("email").getValue().toString();
                        String phone = snapshot.child("phone").getValue().toString();
                        String password = snapshot.child("password").getValue().toString();
                        String address = snapshot.child("address").getValue().toString();

                        Picasso.get().load(image).into(profileImageView);
                        inputEmail.setText(email);
                        inputPhone.setText(phone);
                        inputAddress.setText(address);
                        inputPassword.setText(password);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}