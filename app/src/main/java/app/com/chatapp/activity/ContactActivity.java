package app.com.chatapp.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import app.com.chatapp.R;
import app.com.chatapp.constant.Constants;
import app.com.chatapp.utility.CustomPreferences;
import app.com.chatapp.utility.Utilities;

public class ContactActivity  extends BaseActivity{
    TextView txtName, txtEmail;
    ImageView imgProfile;
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
        return R.layout.activity_contact;
    }

    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        super.init(savedInstanceState);
        String strName = CustomPreferences.getPreferences(
                Constants.PREF_FIRSTNAME, "") + " " + CustomPreferences.getPreferences(
                Constants.PREF_LASTNAME, "");
        String strEmail = CustomPreferences.getPreferences(
                Constants.PREF_ACC_EMAIL, "");
        String strURL = CustomPreferences.getPreferences(
                Constants.PREF_IMG_URL, "");
        userID = CustomPreferences.getPreferences(
                Constants.PREF_USER_ID, 0);
        txtEmail = (TextView)findViewById(R.id.txtEmail);
        txtName = (TextView)findViewById(R.id.txtName);

        txtEmail.setText(strEmail);
        txtName.setText(strName);
        leftBack = (ImageButton)findViewById(R.id.leftBack);
        leftBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }
}
