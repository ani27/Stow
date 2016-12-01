package com.vp6.anish.stow;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class GroupUploadActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    ArrayList<String> photosaddress;
    PhotosFragment photosFragment;
    ArrayList<String> filesaddress;
    FileFragment fileFragment;
    ArrayList<String> musicaddress;
    MusicFragment musicFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_upload);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       getSupportActionBar().setTitle("Upload...");

        photosFragment = new PhotosFragment();
        musicFragment = new MusicFragment();
        fileFragment = new FileFragment();
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

    }


    private void setupViewPager(ViewPager viewPager) {
        GroupViewPagerAdapter adapter = new GroupViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(photosFragment, "Photos");
        adapter.addFragment(fileFragment, "Files");
        adapter.addFragment(musicFragment, "Music");
        viewPager.setAdapter(adapter);
    }


    public void groupupload(View v)
    {
        // photosaddress=mListener.onFragmentPhotos();
        photosaddress = new ArrayList<>();
        filesaddress = new ArrayList<>();
        musicaddress = new ArrayList<>();


        photosaddress = photosFragment.onFrag();
        filesaddress = fileFragment.getitems();
        musicaddress = musicFragment.getitems();
        //ArrayList<ArrayList> addresses = new ArrayList<>();

        Intent intent = new Intent();
        intent.putStringArrayListExtra("photosurl", photosaddress);
        intent.putStringArrayListExtra("musicurl", musicaddress);
        intent.putStringArrayListExtra("fileurl", filesaddress);
        setResult(RESULT_OK,intent);
        finish();
    }
}

class GroupViewPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public GroupViewPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }
}