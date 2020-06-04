package edu.skku.map.inskkagram;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class myFragmentStateAdapter extends FragmentStateAdapter {

    private UserAccountPost userAccountPost;

    public myFragmentStateAdapter(@NonNull FragmentActivity fragmentActivity, UserAccountPost userAccountPost) {
        super(fragmentActivity);

        this.userAccountPost = userAccountPost;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position){
            case 0:
                return new PostFragment(userAccountPost);
            case 1:
                return new PostFragment();
        }

        return null;
    }

    @Override
    public int getItemCount() {
        return 2;
    }

}
