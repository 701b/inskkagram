package edu.skku.map.inskkagram;

import android.net.Uri;

import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class UserAccountPost implements Serializable {

    public static final String ACCOUNT_TABLE_NAME = "account_list";
    public static final String PROFILE_IMAGE_ADDRESS = "ProfileImages";

    public String username;
    public String password;
    public String fullName;
    public String birthday;
    public String email;


    public UserAccountPost() {}


    public UserAccountPost(String username, String password, String fullName, String birthday, String email) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.birthday = birthday;
        this.email = email;
    }


    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("username", username);
        result.put("password", password);
        result.put("fullName", fullName);
        result.put("birthday", birthday);
        result.put("email", email);

        return result;
    }

    public void postFirebaseDatabase(DatabaseReference databaseReference) {
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;

        postValues = toMap();

        childUpdates.put("/" + ACCOUNT_TABLE_NAME + "/" + username, postValues);
        databaseReference.updateChildren(childUpdates);
    }
}
