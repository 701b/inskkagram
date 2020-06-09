package edu.skku.map.inskkagram;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserContentPostAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<UserContentPost> posts;
    private Map<String, Bitmap> contentImageBitmaps;
    private Map<String, Bitmap> profileImageBitmaps;
    private boolean isPublic;


    public UserContentPostAdapter(Context context, ArrayList<UserContentPost> posts, boolean isPublic) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.posts = posts;
        this.isPublic = isPublic;
        contentImageBitmaps = new HashMap<>();
        profileImageBitmaps = new HashMap<>();
    }


    @Override
    public int getCount() {
        return posts.size();
    }

    @Override
    public Object getItem(int position) {
        return posts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.post_item, parent, false);
        }

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        final UserContentPost userContentPost = posts.get(position);

        TextView username = convertView.findViewById(R.id.post_username);
        TextView content = convertView.findViewById(R.id.post_content);
        TextView tags = convertView.findViewById(R.id.post_tags);
        final ImageView profileImage = convertView.findViewById(R.id.post_profile_image);
        final ImageView contentImage = convertView.findViewById(R.id.post_content_image);

        username.setText(userContentPost.username);
        content.setText(userContentPost.contents);
        tags.setText(userContentPost.tags);

        if (profileImageBitmaps.get(userContentPost.username) == null) {
            storageReference.child(UserAccountPost.PROFILE_IMAGE_ADDRESS).child(userContentPost.username)
                    .getBytes(4 * 1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                    profileImage.setImageBitmap(bitmap);
                    profileImageBitmaps.put(userContentPost.username, bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    profileImage.setImageResource(R.drawable.ic_launcher_foreground);
                }
            });
        } else {
            profileImage.setImageBitmap(profileImageBitmaps.get(userContentPost.username));
        }

        if (!userContentPost.imageName.equals("")) {
            if (contentImageBitmaps.get(userContentPost.imageName) == null) {
                if (isPublic) {
                    storageReference.child(UserContentPost.PUBLIC_POST_TABLE_NAME).child(userContentPost.imageName)
                            .getBytes(4 * 1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                            contentImage.setImageBitmap(bitmap);
                            contentImage.setAdjustViewBounds(true);

                            contentImageBitmaps.put(userContentPost.imageName, bitmap);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                } else {
                    storageReference.child(UserContentPost.PRIVATE_POST_TABLE_NAME).child(userContentPost.username).child(userContentPost.imageName)
                            .getBytes(4 * 1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                            contentImage.setImageBitmap(bitmap);
                            contentImage.setAdjustViewBounds(true);

                            contentImageBitmaps.put(userContentPost.imageName, bitmap);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }
            } else {
                contentImage.setImageBitmap(contentImageBitmaps.get(userContentPost.imageName));
                contentImage.setAdjustViewBounds(true);
            }
        } else {
            contentImage.setImageBitmap(null);
            contentImage.setAdjustViewBounds(false);
        }

        return convertView;
    }
}
