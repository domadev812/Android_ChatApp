package app.com.chatapp.activity;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import app.com.chatapp.R;
import app.com.chatapp.constant.Constants;
import app.com.chatapp.utility.AvatarDrawableFactory;
import app.com.chatapp.utility.CustomPreferences;
import app.com.chatapp.utility.Utilities;
import app.com.chatapp.view.CustomClickTextView;
import app.com.chatapp.view.LoadingDialog;
import cz.msebera.android.httpclient.Header;

import static app.com.chatapp.constant.Constants.strMessage;

public class AccountActivity  extends BaseActivity{
    TextView txtName, txtEmail;
    ImageView imgProfile;
    ImageButton leftBack;
    CustomClickTextView btnSave;
    LoadingDialog dialog;
    Spinner spinState;

    private DisplayImageOptions options;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    Context context;
    String imgPath; //saved local path
    String strURL, strName, strEmail; //cached url
    private static final int CAMERA_REQUEST = 1888;
    private static final int SELECT_FILE = 1999;

    String[] items = new String[]{"Hey,i'm using whatsapp!", "Online", "Offline"};
    int userID, state;
//    @Override
//    public void onResume()
//    {
//        super.onResume();
//        state = 0;
//        Utilities.changeState(userID, state);
//    }
//
//    @Override
//    public void onPause()
//    {
//        super.onPause();
//        state = 2;
//        Utilities.changeState(userID, state);
//    }

    @Override
    protected int addView() {
        return R.layout.activity_account;
    }

    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        super.init(savedInstanceState);
        context = this;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.setting_profile)
                .showImageForEmptyUri(R.drawable.setting_profile)
                .showImageOnFail(R.drawable.setting_profile)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new CircleBitmapDisplayer(Color.WHITE, 0))
                .build();
        imgPath = "";

        txtEmail = (TextView)findViewById(R.id.txtEmail);
        txtName = (TextView)findViewById(R.id.txtName);
        imgProfile = (ImageView)findViewById(R.id.imgProfile);
        leftBack = (ImageButton)findViewById(R.id.leftBack);
        leftBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btnSave = (CustomClickTextView)findViewById(R.id.btn_save);

        spinState = (Spinner)findViewById(R.id.spinState);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spinState.setAdapter(adapter);

        if(Constants.profileState == 0) {
            strName = CustomPreferences.getPreferences(
                    Constants.PREF_FIRSTNAME, "") + " " + CustomPreferences.getPreferences(
                    Constants.PREF_LASTNAME, "");
            strEmail = CustomPreferences.getPreferences(
                    Constants.PREF_ACC_EMAIL, "");
            strURL = CustomPreferences.getPreferences(
                    Constants.PREF_IMG_URL, "");
            userID = CustomPreferences.getPreferences(
                    Constants.PREF_USER_ID, 0);
            state = CustomPreferences.getPreferences(
                    Constants.PREF_USER_STATE, 2);

            spinState.setEnabled(true);
            btnSave.setVisibility(View.VISIBLE);
        }
        else
        {
            state = Constants.contactState;
            strName = Constants.strContactName;
            strEmail = Constants.strContactEmail;
            strURL = Constants.imgContactURL;

            btnSave.setVisibility(View.INVISIBLE);
            spinState.setEnabled(false);
        }
        spinState.setSelection(state);
        txtEmail.setText(strEmail);
        txtName.setText(strName);

        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Constants.profileState == 0)
                    selectImage();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postImage();
            }
        });
        ImageLoader.getInstance().displayImage(strURL, imgProfile, options, animateFirstListener);
    }

    private void selectImage()
    {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result= Utilities.checkPermission(context);
                if (items[item].equals("Take Photo")) {
//                    userChoosenTask ="Take Photo";
//                    if(result)
                    cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
//                    userChoosenTask ="Choose from Library";
//                    if(result)
                    galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    public void cameraIntent() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    private String getImgPath(Bitmap bm, int quality)
    {
        File f = new File(context.getCacheDir(), "capture.jpg");
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, quality, bos);
        byte[] bitmapdata = bos.toByteArray();

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(f);
            try {
                fos.write(bitmapdata);
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return f.getAbsolutePath();
    }

    private void postImage()
    {
        RequestParams params = new RequestParams();
        CustomPreferences.setPreferences(Constants.PREF_USER_STATE, spinState.getSelectedItemPosition());
        if(imgPath.equals(""))
        {
            dialog = LoadingDialog.show(this);
            params.put("userid", userID);
            params.put("state", spinState.getSelectedItemPosition());
            AsyncHttpClient client = new AsyncHttpClient();
            client.post(Constants.API_URL + "updatestate", params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Utilities.dismissDialog(dialog);
                    Constants.state = spinState.getSelectedItemPosition();
                    return;
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Utilities.dismissDialog(dialog);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                    Utilities.dismissDialog(dialog);
                }
            });
            return;
        }

        try {
            params.put("file", new File(imgPath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        dialog = LoadingDialog.show(this);
        params.put("userid", userID);
        params.put("state", spinState.getSelectedItemPosition());

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(Constants.API_URL + "updateprofile", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Utilities.dismissDialog(dialog);
                Constants.state = spinState.getSelectedItemPosition();
                imgPath = "";
                DiskCacheUtils.removeFromCache(strURL, ImageLoader.getInstance().getDiskCache());
                MemoryCacheUtils.removeFromCache(strURL, ImageLoader.getInstance().getMemoryCache());
                Constants.updatedProfile = true;
                Toast.makeText(getApplicationContext(), "Profile image is successfully updated.", Toast.LENGTH_LONG).show();
                try{
                    strURL = response.getString("fulladdress");
                } catch (JSONException e){}

                CustomPreferences.setPreferences(Constants.PREF_IMG_URL, strURL);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Utilities.dismissDialog(dialog);
                Toast.makeText(getApplicationContext(), "Updaing image is failure.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Utilities.dismissDialog(dialog);
                Toast.makeText(getApplicationContext(), "Updaing image is failure.", Toast.LENGTH_LONG).show();
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                Bitmap bm=null;
                if (data != null) {
                    try {
                        bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                imgPath = getImgPath(bm, 20);
                AvatarDrawableFactory avatarDrawableFactory = new AvatarDrawableFactory(getResources());
                Drawable roundedAvatarDrawable = avatarDrawableFactory.getRoundedAvatarDrawable(bm);
                imgProfile.setImageDrawable(roundedAvatarDrawable);
            }
            else if (requestCode == CAMERA_REQUEST)
            {
                String strMessage = " ";
                Bitmap bm = (Bitmap) data.getExtras().get("data");
                imgPath = getImgPath(bm, 50);
                AvatarDrawableFactory avatarDrawableFactory = new AvatarDrawableFactory(getResources());
                Drawable roundedAvatarDrawable = avatarDrawableFactory.getRoundedAvatarDrawable(bm);
                imgProfile.setImageDrawable(roundedAvatarDrawable);

                //imgProfile.setImageBitmap(bm);
            }
        }
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
