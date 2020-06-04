package edu.skku.map.inskkagram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText passwordInput;
    private EditText fullNameInput;
    private EditText birthdayInput;
    private EditText emailInput;
    private Button signUpButton;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        usernameInput = findViewById(R.id.signup_username_input);
        passwordInput = findViewById(R.id.signup_password_input);
        fullNameInput = findViewById(R.id.signup_fullname_input);
        birthdayInput = findViewById(R.id.signup_birthday_input);
        emailInput = findViewById(R.id.signup_email_input);
        signUpButton = findViewById(R.id.signup_button);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!usernameInput.getText().toString().equals("")
                        && !passwordInput.getText().toString().equals("")
                        && !fullNameInput.getText().toString().equals("")
                        && !birthdayInput.getText().toString().equals("")
                        && !emailInput.getText().toString().equals("")) {
                    // 모든 칸을 채웠을 때
                    databaseReference.child(UserAccountPost.ACCOUNT_TABLE_NAME).child(usernameInput.getText().toString())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            UserAccountPost post = dataSnapshot.getValue(UserAccountPost.class);

                            if (post == null) {
                                // 데이터베이스에 같은 username이 존재하지 않을 때
                                Intent intent = new Intent();
                                UserAccountPost newPost = new UserAccountPost(usernameInput.getText().toString(),
                                        passwordInput.getText().toString(),
                                        fullNameInput.getText().toString(),
                                        birthdayInput.getText().toString(),
                                        emailInput.getText().toString());

                                newPost.postFirebaseDatabase(databaseReference);
                                intent.putExtra("username", usernameInput.getText().toString());
                                setResult(RESULT_OK, intent);
                                finish();
                            } else {
                                // 데이터베이스에 같은 username이 존재할 때
                                Toast.makeText(SignUpActivity.this, "Please use another username", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    // 입력을 하나라도 빼먹었을 때
                    Toast.makeText(SignUpActivity.this, "Please fill all blanks", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
