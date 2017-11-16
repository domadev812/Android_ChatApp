package app.com.chatapp.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import app.com.chatapp.R;
import app.com.chatapp.constant.Constants;
import app.com.chatapp.fragment.ChatsFragment;
import app.com.chatapp.fragment.ContactFragment;
import app.com.chatapp.fragment.FriendFragment;
import app.com.chatapp.fragment.SettingFragment;
import app.com.chatapp.utility.BadgeTabLayout;
import app.com.chatapp.utility.CustomPreferences;
import app.com.chatapp.utility.Utilities;
import cz.msebera.android.httpclient.Header;

import java.util.TimeZone;

public class MainActivity extends BaseActivity {
    private BadgeTabLayout tabLayout;
    int state = 1;
    int userID;
    private ViewPager viewPager;
    android.os.Handler customHandler;

    private int[] tabIcons = {
            R.drawable.ic_tab_favourite,
            R.drawable.ic_tab_call,
            R.drawable.ic_tab_contacts
    };

    @Override
    public void onStop()
    {
        super.onStop();
    }

    @Override
    protected int addView() {
        return R.layout.activity_main;
    }

    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        super.init(savedInstanceState);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (BadgeTabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        userID = CustomPreferences.getPreferences(
                Constants.PREF_USER_ID, 0);

        Constants.stateFlag = true;
        customHandler = new android.os.Handler();
        customHandler.postDelayed(updateTimerThread, 5000);
        updateState();
        Constants.currentActivity = this;

        //setupTabIcons();
    }
    private void updateState()
    {
        int state = CustomPreferences.getPreferences(Constants.PREF_USER_STATE, 2);
        if(state != 0) state = 1;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("userid", userID);
        params.put("state", state);
        client.post(Constants.API_URL + "updatestate", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                CustomPreferences.setPreferences(Constants.PREF_USER_STATE, 1);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

            }
        });
    }
    private Runnable updateTimerThread = new Runnable()
    {
        public void run()
        {
            updateLastVisit();
        }
    };

    private void updateLastVisit()
    {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("userid", userID);

        client.post(Constants.API_URL + "updatelastvisit", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if(Constants.stateFlag)
                    customHandler.postDelayed(updateTimerThread, 5000);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if(Constants.stateFlag)
                    customHandler.postDelayed(updateTimerThread, 5000);
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new ChatsFragment(), "Chats");
        adapter.addFrag(new FriendFragment(), "Friends");
        adapter.addFrag(new ContactFragment(), "Contact Us");
        adapter.addFrag(new SettingFragment(), "Settings");
        viewPager.setAdapter(adapter);
}

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        //tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
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

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
