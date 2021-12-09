package com.example.emirim_java_petdiary;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class Home extends AppCompatActivity {
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    ImageButton btn_home, btn_add_diary, btn_add_pet;
    Intent intent;
    ListView list;

    ArrayList data = new ArrayList();
    ArrayList diary = new ArrayList();
    ArrayList title = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_list);

        pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        editor = pref.edit();

        title.add(pref.getString("제목",""));
        setListView();

        list = findViewById(R.id.list_diary);
        btn_home = findViewById(R.id.img_home_btn);
        btn_add_diary = findViewById(R.id.img_diary_btn);
        btn_add_pet = findViewById(R.id.img_add_pet_btn);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                intent = new Intent(getApplicationContext(), Diary.class);
                startActivity(intent);
            }
        });

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

    private void setListView(){
        Intent intent = getIntent();

        ArrayAdapter listAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, title);
        ListView listView = (ListView) findViewById(R.id.list_diary);
        listView.setAdapter(listAdapter);
    }
}