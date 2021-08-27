package com.hako.dreamproject.adapters.chat;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.hako.dreamproject.fragment.ChatFragment;
import com.hako.dreamproject.fragment.RecentFragment;

import java.util.ArrayList;
import java.util.List;

public class FragPageAdapter extends FragmentStateAdapter {


    public FragPageAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position==0){
            return new ChatFragment();
        }
        return new RecentFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
