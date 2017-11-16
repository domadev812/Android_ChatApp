package app.com.chatapp.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;

import app.com.chatapp.R;
import app.com.chatapp.constant.Constants;
import app.com.chatapp.utility.CustomPreferences;
import app.com.chatapp.utility.Utilities;

public class AboutActivity extends BaseActivity{

    ImageButton leftBack;
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
        return R.layout.activity_about;
    }

    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        super.init(savedInstanceState);
        leftBack = (ImageButton)findViewById(R.id.leftBack);
        leftBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        userID = CustomPreferences.getPreferences(
                Constants.PREF_USER_ID, 0);
    }
}
