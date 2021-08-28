package com.hako.dreamproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.installations.Utils;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hako.dreamproject.utils.AppController;
import com.hako.dreamproject.utils.Constant;
import com.hako.dreamproject.utils.RequestHandler;
import com.hako.dreamproject.utils.ServiceInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.hako.dreamproject.utils.Constant.API;
import static com.hako.dreamproject.utils.Constant.BASEURL;
import static com.hako.dreamproject.utils.Constant.ERROR;
import static com.hako.dreamproject.utils.Constant.FALSE;
import static com.hako.dreamproject.utils.Constant.MESSAGE;
import static com.hako.dreamproject.utils.Constant.TOKEN;
import static com.hako.dreamproject.utils.Constant.USERID;

public class EditProfileActivity extends AppCompatActivity {
    private static final int PROFILE_IMG_REQ_CODE = 1;
    private static final int PERMISSION_REQUEST_CODE = 2;
    ImageView profile_image;
    TextView profile_name;

    private FirebaseFirestore db;
    private StorageReference mStorageRef;
    private DocumentReference myDocRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);


        initViews();
        db= FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        myDocRef=db.collection("ProfileData").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        myDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
              if (task.isSuccessful()&&task.getResult()!=null){
                  profile_name.setText(task.getResult().getString("name"));
                  Glide.with(EditProfileActivity.this).load(task.getResult().getString("profile")).into(profile_image);
              }
            }
        });

        profile_image.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= 23)
            {
                if (checkPermission())
                {
                    startImagePicker();
                } else {
                    requestPermission();
                }
            }else {
                startImagePicker();
            }

        });
        profile_name.setOnClickListener(v -> {
            EditText editText=new EditText(this);
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("Enter new Name")
                    .setView(editText)
                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                         if (TextUtils.isEmpty(editText.getText())) {
                             Toast.makeText(EditProfileActivity.this, "Enter Something", Toast.LENGTH_SHORT).show();
                              return;
                         }
                          updateName(editText.getText().toString().trim(),dialog);

                        }
                    }).setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            }).create().show();
        });
    }

    private void startImagePicker() {
        Intent intent=new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,PROFILE_IMG_REQ_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK&&requestCode==PROFILE_IMG_REQ_CODE&&data!=null){
            UpdateImage(data.getData());
        }

    }
    private  String getFileExtension(Uri uri){
        ContentResolver cr=getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }
    private void UpdateImage(Uri mImageUri) {
        if (mImageUri!=null){
            StorageReference fileRef= mStorageRef.child("profile/"+System.currentTimeMillis()+"."+getFileExtension(mImageUri));
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                byte[] data = baos.toByteArray();

                fileRef.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Map<String,Object> hasmap =new HashMap<>();
                                hasmap.put("profile",uri.toString().trim());
                                myDocRef.update(hasmap);
                                Glide.with(EditProfileActivity.this).load(uri.toString().trim()).into(profile_image);
                                AppController.getInstance().sharedPref.edit().putString("sprofile",uri.toString().trim()).apply();
                                Toast.makeText(EditProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }catch (Exception ignored){}
        }else {
            Toast.makeText(EditProfileActivity.this, "No files Selected", Toast.LENGTH_SHORT).show();
        }

    }

    private void initViews() {
        profile_image=findViewById(R.id.profile_image);
        profile_name =findViewById(R.id.profile_name);
    }
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(EditProfileActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(EditProfileActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

        } else {
            ActivityCompat.requestPermissions(EditProfileActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startImagePicker();
            }
        }
    }


    public void updateName(String name, DialogInterface dialog) {
    db.collection("ProfileData").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
            .update("name",name).addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
            dialog.cancel();
            profile_name.setText(name);
            AppController.getInstance().sharedPref.edit().putString("sname", name).apply();
        }
    });
    }
}