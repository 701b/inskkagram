package edu.skku.map.inskkagram;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class PostFragment extends Fragment {

    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private ArrayList<UserContentPost> posts;
    private UserContentPostAdapter userContentPostAdapter;

    private ListView listView;

    private UserAccountPost userAccountPost;


    public PostFragment() {
        this.userAccountPost = null;
    }

    public PostFragment(UserAccountPost userAccountPost) {
        this.userAccountPost = userAccountPost;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        posts = new ArrayList<>();

        if (userAccountPost == null) {
            userContentPostAdapter = new UserContentPostAdapter(getContext(), posts, true);
            databaseReference.child(UserContentPost.PUBLIC_POST_TABLE_NAME).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    posts.clear();

                    for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                        UserContentPost userContentPost = postSnapShot.getValue(UserContentPost.class);

                        posts.add(userContentPost);
                    }

                    userContentPostAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            userContentPostAdapter = new UserContentPostAdapter(getContext(), posts, false);
            databaseReference.child(UserContentPost.PRIVATE_POST_TABLE_NAME).child(userAccountPost.username).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    posts.clear();

                    for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                        UserContentPost userContentPost = postSnapShot.getValue(UserContentPost.class);

                        posts.add(userContentPost);
                    }

                    userContentPostAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        listView.setAdapter(userContentPostAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.post_fragment, container, false);

        listView = view.findViewById(R.id.post_fragment_list_view);

        return view;
    }
}
