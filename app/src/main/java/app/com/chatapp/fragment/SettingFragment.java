package app.com.chatapp.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import app.com.chatapp.R;
import app.com.chatapp.activity.AboutActivity;
import app.com.chatapp.activity.AccountActivity;
import app.com.chatapp.activity.ChatActivity;
import app.com.chatapp.activity.ContactActivity;
import app.com.chatapp.activity.LoginActivity;
import app.com.chatapp.adapter.SettingAdapter;
import app.com.chatapp.constant.Constants;
import app.com.chatapp.utility.AvatarDrawableFactory;
import app.com.chatapp.utility.CustomPreferences;
import app.com.chatapp.utility.Utilities;
import app.com.chatapp.view.LoadingDialog;
import cz.msebera.android.httpclient.Header;

import android.widget.AdapterView.OnItemClickListener;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;

import org.json.JSONObject;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static app.com.chatapp.R.id.imgProfile;

public class SettingFragment extends Fragment {
    private DisplayImageOptions options;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    String strURL;
    ListView listView;
    SettingAdapter mAdapter;
    ImageView imgUser;
    TextView txtName;
    int userID = 0;
    LoadingDialog dialog;

    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        if(Constants.updatedProfile)
        {
            strURL = CustomPreferences.getPreferences(
                    Constants.PREF_IMG_URL, "");
            DiskCacheUtils.removeFromCache(strURL, ImageLoader.getInstance().getDiskCache());
            MemoryCacheUtils.removeFromCache(strURL, ImageLoader.getInstance().getMemoryCache());
            ImageLoader.getInstance().displayImage(strURL, imgUser, options, animateFirstListener);
            Constants.updatedProfile = false;
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting,
                container, false);

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.empty_profile)
                .showImageForEmptyUri(R.drawable.empty_profile)
                .showImageOnFail(R.drawable.empty_profile)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new CircleBitmapDisplayer(Color.WHITE, 0))
                .build();

        listView=(ListView)view.findViewById(R.id.listSetting);
        mAdapter= new SettingAdapter(getActivity());
        listView.setAdapter(mAdapter);

        userID = CustomPreferences.getPreferences(
                Constants.PREF_USER_ID, 0);

        listView.setOnItemClickListener(new OnItemClickListener() {
            //@Override
            public void onItemClick(AdapterView arg0, View view,
                                    int position, long id) {
                if(position == 0)
                {
                    Constants.profileState = 0;
                    Intent intent = new Intent(getActivity(), AccountActivity.class);
                    startActivity(intent);
                }
                else if(position == 2)
                {
                    Intent intent = new Intent(getActivity(), AboutActivity.class);
                    startActivity(intent);
                }
                else if(position == 3)
                {
                    Intent intent = new Intent(getActivity(), ContactActivity.class);
                    startActivity(intent);
                }
                else if(position == 4)
                {
                    Utilities.initPreference();
                    Constants.stateFlag = false;

                    DiskCacheUtils.removeFromCache(strURL, ImageLoader.getInstance().getDiskCache());
                    MemoryCacheUtils.removeFromCache(strURL, ImageLoader.getInstance().getMemoryCache());

                    AsyncHttpClient client = new AsyncHttpClient();
                    RequestParams params = new RequestParams();
                    params.put("userid", userID);
                    //AsyncHttpClient client = new AsyncHttpClient();
                    dialog = LoadingDialog.show(getActivity());
                    client.post(Constants.API_URL + "logout", params, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            if(CustomPreferences.getPreferences(Constants.PREF_USER_STATE, 2) != 0)
                                Utilities.changeState(userID, 2);
                            Utilities.dismissDialog(dialog);
                            startActivity(new Intent(getActivity(), LoginActivity.class));
                            getActivity().finish();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Utilities.dismissDialog(dialog);
                        }
                    });
                }
            }
        });

        imgUser = (ImageView)view.findViewById(R.id.imgUser);
        txtName = (TextView)view.findViewById(R.id.txtName);
        String strName = CustomPreferences.getPreferences(
                Constants.PREF_FIRSTNAME, "") + " " + CustomPreferences.getPreferences(
                Constants.PREF_LASTNAME, "");
        txtName.setText(strName);

        strURL = CustomPreferences.getPreferences(
                Constants.PREF_IMG_URL, "");
        ImageLoader.getInstance().displayImage(strURL, imgUser, options, animateFirstListener);
        return view;
    }

    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }
}
