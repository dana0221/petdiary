package com.example.emirim_java_petdiary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class AddPet extends AppCompatActivity {
    ImageButton btn_home, btn_add_diary, btn_add_pet;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pet);

        btn_home = findViewById(R.id.img_home_btn);
        btn_add_diary = findViewById(R.id.img_diary_btn);
        btn_add_pet = findViewById(R.id.img_add_pet_btn);

        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getApplicationContext(), DiaryList.class);
                startActivity(intent);
            }
        });

        btn_add_diary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getApplicationContext(), Diary.class);
                startActivity(intent);
            }
        });

        btn_add_pet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getApplicationContext(), AddPet.class);
                startActivity(intent);
            }
        });
    }
}