package edu.skku.map.inskkagram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Random;

public class AddPostActivity extends AppCompatActivity {

    private static final int PICK_PROFILE_IMAGE = 777;
    private static final int PICK_CONTENT_IMAGE = 666;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private UserAccountPost userAccountPost;

    private ImageView drawerHeaderProfileImage;
    private ImageButton imageButton;

    private Uri contentImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        ActionBarDrawerToggle drawerToggle;

        Toolbar tb = findViewById(R.id.add_post_toolbar);
        Button createPostButton = findViewById(R.id.add_post_create_post_button);
        final EditText contentInput = findViewById(R.id.add_post_content_input);
        final EditText tagsInput = findViewById(R.id.add_post_tags_input);
        final CheckBox isPublicCheckBox = findViewById(R.id.add_post_is_public_checkbox);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.add_post_navigation_view);

        View headerView = navigationView.getHeaderView(0);
        TextView drawerHeaderUsername = headerView.findViewById(R.id.drawer_header_username);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        userAccountPost = (UserAccountPost) getIntent().getSerializableExtra("user_account");

        drawerHeaderProfileImage = headerView.findViewById(R.id.drawer_header_profile_image);
        imageButton = findViewById(R.id.add_post_content_image);

        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // 저장된 프로필 사진이 있다면 가져옴
        storageReference.child(userAccountPost.username).getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                drawerHeaderProfileImage.setImageBitmap(bitmap);
                drawerHeaderProfileImage.getLayoutParams().width = 270;
                drawerHeaderProfileImage.getLayoutParams().height = 270;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        // navigationView 프로필 텍스트 설정
        drawerHeaderUsername.setText(userAccountPost.username);
        navigationView.getMenu().getItem(0).setTitle(userAccountPost.fullName);
        navigationView.getMenu().getItem(1).setTitle(userAccountPost.birthday);
        navigationView.getMenu().getItem(2).setTitle(userAccountPost.email);

        // navigationView 사진을 눌렀을 때 갤러리에서 고르도록 설정
        drawerHeaderProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);

                startActivityForResult(gallery, PICK_PROFILE_IMAGE);
            }
        });

        // drawerToggle
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, tb, R.string.app_name, R.string.app_name);
        drawerToggle.syncState();

        // imageButton 클릭 시 갤러리에서 사진 고르도록 설정
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);

                startActivityForResult(gallery, PICK_CONTENT_IMAGE);
            }
        });

        // post button 클릭 시 끝나도록 설정
        createPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!contentInput.getText().toString().equals("")) {
                    // content 입력란이 공백이 아닐 때
                    UserContentPost userContentPost;

                    if (contentImageUri != null) {
                        userContentPost = new UserContentPost(userAccountPost.username,
                                                            contentInput.getText().toString(),
                                                            tagsInput.getText().toString(),
                                                            contentImageUri.getLastPathSegment());
                    } else {
                        userContentPost = new UserContentPost(userAccountPost.username,
                                contentInput.getText().toString(),
                                tagsInput.getText().toString(),
                                "");
                    }


                    if (isPublicCheckBox.isChecked()) {
                        userContentPost.postFirebaseDatabase(databaseReference, true);
                        if (contentImageUri != null) {
                            storageReference.child(UserContentPost.PUBLIC_POST_TABLE_NAME).child(contentImageUri.getLastPathSegment()).putFile(contentImageUri);
                        }
                    } else {
                        userContentPost.postFirebaseDatabase(databaseReference, false);
                        if (contentImageUri != null) {
                            storageReference.child(UserContentPost.PRIVATE_POST_TABLE_NAME).child(userContentPost.username).child(contentImageUri.getLastPathSegment()).putFile(contentImageUri);
                        }
                    }

                    finish();
                } else {
                    // content 입력란이 공백일 때
                    Toast.makeText(AddPostActivity.this, "Please input contents", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PROFILE_IMAGE) {
            if (resultCode == RESULT_OK) {
                UploadTask task = storageReference.child(UserAccountPost.PROFILE_IMAGE_ADDRESS).child(userAccountPost.username).putFile(data.getData());

                drawerHeaderProfileImage.setImageURI(data.getData());
                drawerHeaderProfileImage.getLayoutParams().width = 270;
                drawerHeaderProfileImage.getLayoutParams().height = 270;

                task.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    }
                });
            }
        } else if (requestCode == PICK_CONTENT_IMAGE) {
            if (resultCode == RESULT_OK) {
                imageButton.setImageURI(data.getData());
                contentImageUri = data.getData();
            }
        }
    }
}
