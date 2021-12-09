package com.example.emirim_java_petdiary;

import android.Manifest;
import android.Manifest;
import android.app.Activity;
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
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Diary extends AppCompatActivity {
    EditText title, note;
    ImageView imgv;
    Button btnSave, btnPhoto, btnGallrey;
    CheckBox checkWalk, checkPlay, checkFeed;
    Data dbHelper;
    Date nowDate;
    int feed = 0, play = 0, walk = 0;
    SimpleDateFormat ft = new SimpleDateFormat("yyyy년 MM월 dd일");
    ImageButton btn_home, btn_add_diary, btn_add_pet, btn_setting;
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

        tedPermission();

        btnSave = findViewById(R.id.btn_save);
        btnPhoto = findViewById(R.id.btn_photo);
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
        btn_setting = findViewById(R.id.img_setting_btn);

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

        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getApplicationContext(), Diary.class);
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

        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPermission){
                    takePhoto();
                } else {
                    Toast.makeText(view.getContext(), getResources().getString(R.string.permission_2), Toast.LENGTH_LONG).show();
                } //end of if
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                dbHelper.insert(title.getText().toString(), note.getText().toString(), feed, walk, play, nowDate.toString());
                Toast.makeText(getApplicationContext(), "저장을 완료했습니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), DiaryList.class);
                intent.putExtra("제목", title.getText().toString());
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
            Toast.makeText(this, "취소되었습니다.", Toast.LENGTH_SHORT).show();

            if (tempFile != null) {
                if (tempFile.exists()) {
                    if (tempFile.delete()) {
                        Log.e(TAG, tempFile.getAbsolutePath() + " 삭제 성공!");
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
            Toast.makeText(this, "이미지 처리 오류! 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
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

        tempFile = null;
    }

    private void tedPermission() {
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                //권한 요청 성공
                isPermission = true;
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                //권한 요청 실패
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
                    feed = 1;
                    Toast.makeText(getApplicationContext(), "산책하기를 완료했습니다.", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.check_play:
                    play = 1;
                    Toast.makeText(getApplicationContext(), "놀아주기 완료했습니다.", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.check_feed:
                    feed = 1;
                    Toast.makeText(getApplicationContext(), "밥주기를 완료했습니다.", Toast.LENGTH_SHORT).show();
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

    public void clickGetBt(View view) {     // Get버튼 클릭 시   SharedPreferences에 값 불러오기.
        SharedPreferences sharedPreferences= getSharedPreferences("test", MODE_PRIVATE);    // test 이름의 기본모드 설정, 만약 test key값이 있다면 해당 값을 불러옴.
        String titleText = sharedPreferences.getString("title","");
        String noteText = sharedPreferences.getString("note","");
        title.setText(titleText);    // TextView에 SharedPreferences에 저장되어있던 값 찍기.
        note.setText(noteText);    // TextView에 SharedPreferences에 저장되어있던 값 찍기.
        Toast.makeText(this, "불러오기 하였습니다..", Toast.LENGTH_SHORT).show();
    }

}