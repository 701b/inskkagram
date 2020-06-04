package edu.skku.map.inskkagram;

import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class UserContentPost {

    public static final String PUBLIC_POST_TABLE_NAME = "post_list/public";
    public static final String PRIVATE_POST_TABLE_NAME = "post_list/private";
    public static final String CONTENT_IMAGE_ADDRESS = "contentImages";

    public String username;
    public String contents;
    public String tags;
    public String imageName;


    public UserContentPost() {}

    public UserContentPost(String username, String contents, String tags, String imageName) {
        this.username = username;
        this.contents = contents;
        this.tags = tags;
        this.imageName = imageName;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("username", username);
        result.put("contents", contents);
        result.put("tags", tags);
        result.put("imageName", imageName);

        return result;
    }

    public void postFirebaseDatabase(DatabaseReference databaseReference, boolean isPublic) {
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;
        Random random = new Random();
        String pk = String.valueOf(random.nextInt());

        postValues = toMap();

        if (isPublic) {
            childUpdates.put("/" + PUBLIC_POST_TABLE_NAME + "/" + pk, postValues);
        } else {
            childUpdates.put("/" + PRIVATE_POST_TABLE_NAME + "/" + username + "/" + pk, postValues);
        }

        databaseReference.updateChildren(childUpdates);
    }

}
