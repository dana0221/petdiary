package com.example.emirim_java_petdiary;

import android.Manifest;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;

public class Diary extends AppCompatActivity {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String titleDb;
    String noteDb;
    String imgName;
    boolean walkDb;
    boolean feedDb;
    boolean playDb;


    EditText title, note;
    ImageView imgv;
    Button btnSave, btnGallrey;
    CheckBox checkWalk, checkPlay, checkFeed;
    ImageButton btn_home, btn_add_diary, btn_add_pet;
    Intent intent;

    final private static String TAG = "petdiary";

    private Boolean isPermission = true;

    private static final int PICK_FROM_ALBUM = 1;
    private static final int PICK_FROM_CAMERA = 2;

    private File tempFile;

    final static int TAKE_PICTURE = 1;

    String mCurrentPhotoPaht;
    final static int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        editor = pref.edit();

        titleDb = pref.getString("??????", "");
        noteDb = pref.getString("??????", "");
        walkDb = pref.getBoolean("??????", false);
        feedDb = pref.getBoolean("???", false);
        playDb = pref.getBoolean("??????", false);
        imgName = pref.getString("??????", "");

        btnSave = findViewById(R.id.btn_save);
        btnGallrey = findViewById(R.id.btn_gallery);
        title = findViewById(R.id.diary_title);
        note = findViewById(R.id.diary_note);
        checkWalk = findViewById(R.id.check_walk);
        checkPlay = findViewById(R.id.check_play);
        checkFeed = findViewById(R.id.check_feed);
        imgv = findViewById(R.id.imgv);

        btn_home = findViewById(R.id.img_home_btn);
        btn_add_diary = findViewById(R.id.img_diary_btn);
        btn_add_pet = findViewById(R.id.img_add_pet_btn);

        title.setText(titleDb);
        note.setText(noteDb);
        checkWalk.setChecked(walkDb);
        checkFeed.setChecked(feedDb);
        checkPlay.setChecked(playDb);

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

        btnGallrey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPermission) {
                    goToAlbum();
                } else {
                    Toast.makeText(view.getContext(), getResources().getString(R.string.permission_2), Toast.LENGTH_LONG).show();
                } //end of if
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titleDb = title.getText().toString();
                editor.putString("??????", titleDb);

                noteDb = note.getText().toString();
                editor.putString("??????", noteDb);

                editor.putBoolean("??????", checkWalk.isChecked());

                editor.putBoolean("???", checkFeed.isChecked());

                editor.putBoolean("??????", checkPlay.isChecked());

                editor.apply();
                Toast.makeText(getApplicationContext(), "????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), DiaryList.class);
                intent.putExtra("??????", title.getText().toString());
                startActivity(intent);
            }
        });

        checkWalk.setOnClickListener(checkListener);
        checkPlay.setOnClickListener(checkListener);
        checkFeed.setOnClickListener(checkListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(this, "?????????????????????.", Toast.LENGTH_SHORT).show();

            if (tempFile != null) {
                if (tempFile.exists()) {
                    if (tempFile.delete()) {
                        Log.e(TAG, tempFile.getAbsolutePath() + " ?????? ??????!");
                        tempFile = null;
                    } //end of if
                } //end of if
            } //end of if
            return;
        } //end of if

        if (requestCode == PICK_FROM_ALBUM) {
            Uri photoUri = data.getData();
            Log.d(TAG, "PICK_FROM_ALBUM photoUri : " + photoUri);

            Cursor cursor = null;

            try {
                String[] proj = {MediaStore.Images.Media.DATA};

                assert photoUri != null;
                cursor = getContentResolver().query(photoUri, proj, null, null, null);

                assert cursor != null;
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                cursor.moveToFirst();

                tempFile = new File(cursor.getString(column_index));

                Log.d(TAG, "tempFile Uri : " + Uri.fromFile(tempFile));
            } finally {
                if (cursor != null) {
                    cursor.close();
                } //end of if
            } //end of try-finally
            setImage();
        } else if (requestCode == PICK_FROM_CAMERA) {
            setImage();
        } //end of if
    }

    private void goToAlbum(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            tempFile = createImageFile();
        } catch(IOException e) {
            Toast.makeText(this, "????????? ?????? ??????! ?????? ????????? ?????????.", Toast.LENGTH_SHORT).show();
            finish();
            e.printStackTrace();
        } //end of try-catch

        if(tempFile != null) {
            Uri photoUri = Uri.fromFile(tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, PICK_FROM_CAMERA);
        } //end of if
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "MyPet_" + timeStamp + "_";

        File storageDir = new File(Environment.getExternalStorageDirectory() + "/MyPet/");
        if(!storageDir.exists()) storageDir.mkdirs();

        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        Log.d(TAG, "createImageFile : " + image.getAbsolutePath());

        return image;
    }

    private void setImage(){
        ImageView imgv = findViewById(R.id.imgv);

        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap originalBm = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);
        Log.d(TAG, "setImage : " + tempFile.getAbsolutePath());

        imgv.setImageBitmap(originalBm);
        editor.putString("??????", String.valueOf(tempFile));
        editor.apply();

        tempFile = null;
    }

    private void tedPermission() {
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                //?????? ?????? ??????
                isPermission = true;
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                //?????? ?????? ??????
                isPermission = false;
            }
        };
        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage(getResources().getString(R.string.permission_2))
                .setDeniedMessage(getResources().getString(R.string.permission_1))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();
    }

    View.OnClickListener checkListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.check_walk:
                    Toast.makeText(getApplicationContext(), "??????????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.check_play:
                    Toast.makeText(getApplicationContext(), "???????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.check_feed:
                    Toast.makeText(getApplicationContext(), "???????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult");

        if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "Permission : " + permissions[0] + "was" + grantResults[0]);
        }
    }

    private void dispatchTakePictureIntent(){
        Intent tackPictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(tackPictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;

            try {
                photoFile = createImageFile();
            }catch (IOException ex){}

            if(photoFile != null){
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.emirim_java_petdiary", photoFile);
                tackPictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(tackPictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    public void clickGetBt(View view) {     // Get?????? ?????? ???   SharedPreferences??? ??? ????????????.
        SharedPreferences sharedPreferences= getSharedPreferences("test", MODE_PRIVATE);    // test ????????? ???????????? ??????, ?????? test key?????? ????????? ?????? ?????? ?????????.
        String titleText = sharedPreferences.getString("title","");
        String noteText = sharedPreferences.getString("note","");
        title.setText(titleText);    // TextView??? SharedPreferences??? ?????????????????? ??? ??????.
        note.setText(noteText);    // TextView??? SharedPreferences??? ?????????????????? ??? ??????.
        Toast.makeText(this, "???????????? ???????????????..", Toast.LENGTH_SHORT).show();
    }

}