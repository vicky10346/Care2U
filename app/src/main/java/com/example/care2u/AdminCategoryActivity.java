package com.example.care2u;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import io.paperdb.Paper;

public class AdminCategoryActivity extends AppCompatActivity {

    private ImageView skincare, haircare, bath, supplements, medicalSupplies, personalCare;
    private Button logoutByn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);

        skincare = (ImageView) findViewById(R.id.icon_skincare);
        haircare = (ImageView) findViewById(R.id.icon_haircare);
        bath = (ImageView) findViewById(R.id.icon_bath);
        supplements = (ImageView) findViewById(R.id.icon_vitamins);
        medicalSupplies = (ImageView) findViewById(R.id.icon_medicalsupplies);
        personalCare = (ImageView) findViewById(R.id.icon_personal);
        logoutByn = (Button) findViewById(R.id.admin_logout_btn);

        skincare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this,AdminAddProductActivity.class);
                intent.putExtra("category","skincare");
                startActivity(intent);
            }
        });

        haircare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this,AdminAddProductActivity.class);
                intent.putExtra("category","haircare");
                startActivity(intent);
            }
        });

        bath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this,AdminAddProductActivity.class);
                intent.putExtra("category","bath");
                startActivity(intent);
            }
        });

        supplements.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this,AdminAddProductActivity.class);
                intent.putExtra("category","supplements");
                startActivity(intent);
            }
        });

        medicalSupplies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this,AdminAddProductActivity.class);
                intent.putExtra("category","medicalSupplies");
                startActivity(intent);
            }
        });

        personalCare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this,AdminAddProductActivity.class);
                intent.putExtra("category","personalCare");
                startActivity(intent);
            }
        });

        logoutByn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Paper.book().destroy();

                Intent intent = new Intent(AdminCategoryActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}