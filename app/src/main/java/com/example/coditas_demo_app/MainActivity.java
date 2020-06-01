package com.example.coditas_demo_app;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.swipe.SwipeCancelState;
import com.mindorks.placeholderview.annotations.swipe.SwipeIn;
import com.mindorks.placeholderview.annotations.swipe.SwipeInState;
import com.mindorks.placeholderview.annotations.swipe.SwipeOut;
import com.mindorks.placeholderview.annotations.swipe.SwipeOutState;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final String JSON_URL = "https://randomuser.me/api/0.4/?randomapi";
    public TextView nameText;
    public TextView locationText;
    public TextView dobText;
    ViewPager viewPager;
    TabLayout tabLayout;
    ViewPagerAdapter adapter;
    private SwipePlaceHolderView mSwipeView;
    private Context mContext;
    private List<Profile> mProfileList;
    private ProgressBar progressBar;
    private ProfileDBHelper db;
    private Button mFetchButton;
    private Profile currentProfile;
    private TabFirst tabName;
    private TabThird tabLocaton;
    private TabSecond tabdob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSwipeView = (SwipePlaceHolderView) findViewById(R.id.swipeView);
        mContext = getApplicationContext();
        currentProfile = new Profile();
        mProfileList = new ArrayList<>();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        mFetchButton = findViewById(R.id.fetchProfile);
        db = new ProfileDBHelper(this);
        mFetchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetProfile().execute();
            }
        });

        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tablayout);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
    }

    @Override
    protected void onStart() {
        super.onStart();

        mSwipeView.getBuilder()
            .setDisplayViewCount(3)
            .setSwipeDecor(new SwipeDecor()
                .setPaddingTop(20)
                .setRelativeScale(0.01f)
                .setSwipeInMsgLayoutId(R.layout.tinder_swipe_in_msg_view)
                .setSwipeOutMsgLayoutId(R.layout.tinder_swipe_out_msg_view));
        new GetProfile().execute();
        Bundle bundle = new Bundle();
        bundle.putString("name", currentProfile.getName());
        bundle.putString("dob", currentProfile.getDob());
        bundle.putString("location", currentProfile.getLocation());
        tabName = new TabFirst();
        tabName.setArguments(bundle);
        tabdob = new TabSecond();
        tabdob.setArguments(bundle);
        tabLocaton = new TabThird();
        tabLocaton.setArguments(bundle);
        adapter.addFrag(tabName, "Name");
        adapter.addFrag(tabdob, "DOB");
        adapter.addFrag(tabLocaton, "Location");
        viewPager.setAdapter(adapter);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.clear();
        mProfileList.clear();
        mSwipeView.removeAllViews();
    }

    private class GetProfile extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            mProfileList = Utils.loadProfiles(JSON_URL);
            if (mProfileList.isEmpty()) {
                mProfileList = db.getAllDBProfiles();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressBar.setVisibility(View.GONE);
            if (mProfileList.isEmpty()) {
                mFetchButton.setVisibility(View.VISIBLE);
            } else {
                mFetchButton.setVisibility(View.GONE);
            }
            for (Profile profile : mProfileList) {
                mSwipeView.addView(new TinderCard(mContext, profile, mSwipeView));
                currentProfile = profile;
                Bundle bundle = new Bundle();
                bundle.putString("name", currentProfile.getName());
                bundle.putString("dob", currentProfile.getDob());
                bundle.putString("location", currentProfile.getLocation());
                tabName.setArguments(bundle);
                tabdob.setArguments(bundle);
                tabLocaton.setArguments(bundle);
            }
            mProfileList.clear();
        }
    }

    @Layout(R.layout.tinder_card_view)
    private class TinderCard extends FragmentActivity {

        @com.mindorks.placeholderview.annotations.View(R.id.profileImageView)
        private ImageView profileImageView;
        @com.mindorks.placeholderview.annotations.View(R.id.nameAgeTxt)
        private TextView nameAgeTxt;
        @com.mindorks.placeholderview.annotations.View(R.id.locationNameTxt)
        private TextView locationNameTxt;
        private Profile mProfile;
        private Context mContext;
        private SwipePlaceHolderView mSwipeView;
        public TinderCard(Context context, Profile profile, SwipePlaceHolderView swipeView) {
            mContext = context;
            mProfile = profile;
            mSwipeView = swipeView;
        }

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Resolve
        private void onResolved() {
            Glide.with(mContext).load(mProfile.getImageUrl()).into(profileImageView);
            tabLayout.setupWithViewPager(viewPager);
            nameAgeTxt.setText(mProfile.getName() + ", DOB : " + mProfile.getDob());
            locationNameTxt.setText("Location : " + mProfile.getLocation());
            nameText.setText(mProfile.getName());
            dobText.setText(mProfile.getDob());
            locationText.setText(mProfile.getLocation());
        }

        @SwipeOut
        private void onSwipedOut() {
            Log.d("EVENT", "MonSwipedOut mProfile" + mProfile.getName());
            if (db.getProfile(mProfile.getId()) != null) {
                db.deleteOne(mProfile);
            }
            new GetProfile().execute();
        }

        @SwipeCancelState
        private void onSwipeCancelState() {
            Log.d("EVENT", "MainActivity onSwipeCancelState");
        }

        @SwipeIn
        private void onSwipeIn() {
            Log.d("EVENT", "MainActivity onSwipedIn mProfile: " + mProfile.getName());
            if (db.getProfile(mProfile.getId()) == null) {
                db.addProfile(mProfile);
            }
            new GetProfile().execute();
        }

        @SwipeInState
        private void onSwipeInState() {
            Log.d("EVENT", "MainActivity onSwipeInState");
        }

        @SwipeOutState
        private void onSwipeOutState() {
            Log.d("EVENT", "MainActivity onSwipeOutState");
        }
    }
}
