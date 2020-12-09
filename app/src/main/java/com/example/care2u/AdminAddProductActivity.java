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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddProductActivity extends AppCompatActivity {

    private String categoryName, pDesc, pName, pPrice, pCode, saveCurrentDate, saveCurrentTime;
    private Button addProductBtn;
    private EditText productName, productDesc, productPrice, productCode;
    private ImageView productImage;
    private static final int galleryPick = 1;
    private Uri imageUrl;
    private String productRanKey, downloadImageUrl;
    private StorageReference productImageRef;
    private DatabaseReference productsRef;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_product);

        categoryName = getIntent().getExtras().get("category").toString();
        productImageRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        productsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        loadingBar = new ProgressDialog(this);
        addProductBtn = (Button) findViewById(R.id.add_product_btn);
        productName = (EditText) findViewById(R.id.product_name);
        productCode = (EditText) findViewById(R.id.product_code);
        productDesc = (EditText) findViewById(R.id.product_desc);
        productPrice = (EditText) findViewById(R.id.product_price);
        productImage = (ImageView) findViewById(R.id.product_Image);

        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productValidation();
            }
        });
    }

    public void openGallery(){
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, galleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==galleryPick && resultCode == RESULT_OK && data!=null){
            imageUrl = data.getData();
            productImage.setImageURI(imageUrl);
        }
    }

    private void productValidation(){
        pDesc = productDesc.getText().toString();
        pName = productName.getText().toString();
        pPrice = productPrice.getText().toString();
        pCode = productCode.getText().toString();

        if (imageUrl == null){
            Toast.makeText(this, "Product image is mandatory.", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(pName)){
            Toast.makeText(this, "Please fill in the product name.", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(pDesc)){
            Toast.makeText(this, "Please fill in the product description.", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(pPrice)) {
            Toast.makeText(this, "Please fill in the product price.", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(pCode)){
            Toast.makeText(this, "Please fill in the product code", Toast.LENGTH_SHORT).show();
        } else{
            storeProduct();
        }
    }

    private void storeProduct(){

        loadingBar.setTitle("Adding New Product");
        loadingBar.setMessage("Please wait, while we are adding this new product");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd,yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRanKey = saveCurrentDate + saveCurrentTime;

        final StorageReference filePath = productImageRef.child(imageUrl.getLastPathSegment() + productRanKey + ".jpg");

        final UploadTask uploadTask = filePath.putFile(imageUrl);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(AdminAddProductActivity.this, "Error:" + message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AdminAddProductActivity.this, "Product image uploaded successfully.",
                        Toast.LENGTH_SHORT).show();

                Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()){
                            throw task.getException();
                        }

                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()){

                            downloadImageUrl = task.getResult().toString();

                            Toast.makeText(AdminAddProductActivity.this,
                                    "Getting product image Url successfully.", Toast.LENGTH_SHORT).show();
                            saveProductInDb();
                        }
                    }
                });
            }
        });
    }

    private void saveProductInDb(){
        HashMap<String, Object> productMap = new HashMap<>();

        productMap.put("pId", productRanKey);
        productMap.put("date", saveCurrentDate);
        productMap.put("time", saveCurrentTime);
        productMap.put("pCode", pCode);
        productMap.put("pName", pName);
        productMap.put("pDesc", pDesc);
        productMap.put("pPrice", pPrice);
        productMap.put("pImage", downloadImageUrl);
        productMap.put("category", categoryName);

        productsRef.child(productRanKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                            Intent intent = new Intent(AdminAddProductActivity.this, AdminCategoryActivity.class);
                            startActivity(intent);

                            loadingBar.dismiss();
                            Toast.makeText(AdminAddProductActivity.this, "Product is added successfully.",
                                    Toast.LENGTH_SHORT).show();

                        }else{
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(AdminAddProductActivity.this, "Error: " + message,
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
}