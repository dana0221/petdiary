package com.example.emirim_java_petdiary;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class AddPet extends AppCompatActivity {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    ImageButton btn_home, btn_add_diary, btn_add_pet;
    Intent intent;
    String nameDb;
    int ageDb;
    String birDb;
    String adoDb;
    String spiDb;
    String etcDb;
    RadioGroup radioGroup;

    EditText name, age, birday, adoday, spi, etc;
    RadioGroup rg_gender;
    RadioButton man, woman;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pet);

        pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        editor = pref.edit();

        nameDb = pref.getString("이름", "");
        ageDb = pref.getInt("나이", 0);
        birDb = pref.getString("생일", "");
        adoDb = pref.getString("데려온날", "");
        spiDb = pref.getString("종류", "");
        etcDb = pref.getString("기타", "");

        name = findViewById(R.id.edit_name);
        age = findViewById(R.id.edit_age);
        birday = findViewById(R.id.edit_birthday);
        adoday = findViewById(R.id.edit_adoptionday);
        spi = findViewById(R.id.edit_species);
        etc = findViewById(R.id.edit_uniqueness);
        rg_gender = (RadioGroup) findViewById(R.id.rg_gender);
        man = findViewById(R.id.man);
        woman = findViewById(R.id.woman);

        name.setText(nameDb);
        age.setText(String.valueOf(adoDb));
        int id = 0;
        try {
            id = Integer.parseInt(pref.getString("성별", "0"));
        } catch (NullPointerException e) { }
        if (id == 0)
            ((RadioButton) findViewById(R.id.man)).setChecked(true);
        else
            ((RadioButton) findViewById(id)).setChecked(true);
        birday.setText(birDb);
        adoday.setText(adoDb);
        spi.setText(spiDb);
        etc.setText(etcDb);

        btnSave = findViewById(R.id.btn_add);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nameDb = name.getText().toString();
                editor.putString("이름", nameDb);
                editor.apply();

                ageDb = Integer.parseInt(age.getText().toString());
                editor.putInt("나이", 0);
                editor.apply();

                birDb = birday.getText().toString();
                editor.putString("생일", birDb);
                editor.apply();

                adoDb = adoday.getText().toString();
                editor.putString("데려온날", adoDb);
                editor.apply();

                spiDb = spi.getText().toString();
                editor.putString("종류", spiDb);
                editor.apply();

                etcDb = etc.getText().toString();
                editor.putString("기타", etcDb);

                editor.putString("성별", String.valueOf((int)rg_gender.getCheckedRadioButtonId()));

                editor.apply();

                Toast.makeText(getApplicationContext(), "저장을 완료했습니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), DiaryList.class);
                startActivity(intent);
            }
        });

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

        rg_gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if( i == R.id.man){
                    Toast.makeText(getApplicationContext(), "남자", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "여자", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}