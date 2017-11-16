package app.com.chatapp;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import app.com.chatapp.activity.BaseActivity;
import app.com.chatapp.activity.LoginActivity;
import app.com.chatapp.activity.MainActivity;
import app.com.chatapp.constant.Constants;
import app.com.chatapp.utility.CustomPreferences;

public class SplashActivity extends BaseActivity {
    @Override
    protected int addView() {
        return R.layout.activity_splash;
    }
    ProgressBar progress;
    TextView txtComment;
    int value = 0;

    android.os.Handler customHandler;
    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        super.init(savedInstanceState);
        initImageLoader(this);
        customHandler = new android.os.Handler();
        txtComment = (TextView)findViewById(R.id.txtComment);
        progress = (ProgressBar)findViewById(R.id.progress_limit);
        progress.setProgress(value);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 100);

        customHandler.postDelayed(updateValue, 100);
    }

    private Runnable updateValue = new Runnable()
    {
        public void run()
        {
            setValue();
        }
    };

    private void setValue()
    {
        value += 5;
        if(value <= 100)
        {
            progress.setProgress(value);
            customHandler.postDelayed(updateValue, 100);
            if((value / 10) % 3 == 0)
                txtComment.setText("Loading...");
            else if((value / 10) % 3 == 1)
                txtComment.setText("Loading.");
            else if((value / 10) % 3 == 2)
                txtComment.setText("Loading..");
        }
        else
        {
            txtComment.setText("Loading...");
            customHandler.removeCallbacks(updateValue);
            gotoActivity();
        }
    }
    public void gotoActivity()
    {
        int userID = CustomPreferences.getPreferences(
                Constants.PREF_USER_ID, 0);
        if (userID == 0) {
            startActivity(new Intent(SplashActivity.this,
                    LoginActivity.class));
            finish();
            return;
        }
        Constants.state = 0;
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }
    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }
}
