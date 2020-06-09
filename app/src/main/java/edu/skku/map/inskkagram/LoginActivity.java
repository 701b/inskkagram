package edu.skku.map.inskkagram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;

public class LoginActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_TO_SIGN_UP_ACTIVITY = 512;

    private TextView signUpText;
    private Button loginButton;
    private EditText usernameInput;
    private EditText passwordInput;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signUpText = findViewById(R.id.signup_text);
        loginButton = findViewById(R.id.login_button);
        usernameInput = findViewById(R.id.login_username_input);
        passwordInput = findViewById(R.id.login_password_input);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        signUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);

                startActivityForResult(intent, REQUEST_CODE_TO_SIGN_UP_ACTIVITY);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!usernameInput.getText().toString().equals("")) {
                    // username이 공백이 아닐 때
                    databaseReference.child(UserAccountPost.ACCOUNT_TABLE_NAME).child(usernameInput.getText().toString())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            UserAccountPost post = dataSnapshot.getValue(UserAccountPost.class);

                            if (post != null) {
                                // 데이터베이스에 같은 username이 존재할 때
                                if (post.password.equals(passwordInput.getText().toString())) {
                                    // 비밀번호가 데이터베이스의 비밀번호와 같을 때
                                    Intent intent = new Intent(LoginActivity.this, MainPageActivity.class);

                                    intent.putExtra("user_account", post);

                                    startActivity(intent);
                                } else {
                                    // 비밀번호가 데이터베이스의 비밀번호와 다를 때
                                    Toast.makeText(LoginActivity.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // 데이터베이스에 같은 username이 존재하지 않을 때
                                Toast.makeText(LoginActivity.this, "Wrong Username", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    // username이 공백일 때
                    Toast.makeText(LoginActivity.this, "Wrong Username", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_TO_SIGN_UP_ACTIVITY) {

            if (resultCode == RESULT_OK) {
                usernameInput.setText(data.getStringExtra("username"));
                passwordInput.setText("");
            }
        }
    }
}
