package app.com.chatapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import app.com.chatapp.R;
import app.com.chatapp.constant.Constants;
import app.com.chatapp.utility.CustomPreferences;
import app.com.chatapp.utility.Utilities;
import app.com.chatapp.view.LoadingDialog;
import butterknife.Bind;
import cz.msebera.android.httpclient.Header;


public class LoginActivity extends BaseActivity implements TextWatcher {
    @Bind(R.id.login_btn_login)
    TextView btnLogin;

    @Bind(R.id.login_edt_email)
    EditText edtEmail;

    @Bind(R.id.login_edt_password)
    EditText edtPassword;

    @Override
    protected int addView() {
        return R.layout.activity_login;
    }

    LoadingDialog dialog;
    String strToken = "";
    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        super.init(savedInstanceState);

        btnLogin.setEnabled(false);
        btnLogin.setOnClickListener(this);
        edtEmail.addTextChangedListener(this);
        edtPassword.addTextChangedListener(this);

        strToken = "" + FirebaseInstanceId.getInstance().getToken();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.login_btn_login :
                Utilities.hideSoftKeyBoard(LoginActivity.this);
                doLogin();
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1,
                                  int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        String strEmail = edtEmail.getText().toString().trim();
        String strPassword = edtPassword.getText().toString().trim();
        if (strEmail.equals("") || strPassword.equals("")) {

            btnLogin.setEnabled(false);
            return;
        }
        btnLogin.setEnabled(true);
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    /**
     * Do login
     */
    private void doLogin() {

        String strPassword = edtPassword.getText().toString();
        String strEmail = edtEmail.getText().toString().toLowerCase();
        //CustomPreferences.setPreferences(Constants.PREF_USERNAME, strEmail);
       // CustomPreferences.setPreferences(Constants.PREF_PASSWORD, strPassword);
        //AsyncHttpPost post = new AsyncHttpPost("http://localhost/chatapp/v1/friend/1");

        dialog = LoadingDialog.show(this);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("email", strEmail);
        params.put("password", strPassword);
        params.put("device_id", strToken);
        //params.put("device_id", "aa");
        //AsyncHttpClient client = new AsyncHttpClient();
        client.post(Constants.API_URL + "login", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                finishLogin(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Utilities.dismissDialog(dialog);
            }
        });

    }

    /**
     * Finish login
     *
     * @param response
     *            response
     */
    private void finishLogin(JSONObject response) {
        String strEmail = edtEmail.getText().toString().trim();
        String strPassword = edtPassword.getText().toString().trim();
        try {
            if (response.getBoolean("error")) {
               Utilities.initPreference();

                Toast.makeText(this, getString(R.string.login_fail) + "\n" + getString(R.string.try_again),
                        Toast.LENGTH_LONG).show();
                edtPassword.setText("");
                edtPassword.requestFocus();
                Utilities.dismissDialog(dialog);
                return;
            }
        }catch (JSONException e) {
        }

        try{
            CustomPreferences.setPreferences(Constants.PREF_USER_ID, response.getInt("user_id"));
            CustomPreferences.setPreferences(Constants.PREF_ACC_EMAIL, strEmail);
            CustomPreferences.setPreferences(Constants.PREF_FIRSTNAME, response.getString("firstName"));
            CustomPreferences.setPreferences(Constants.PREF_LASTNAME, response.getString("lastName"));
            CustomPreferences.setPreferences(Constants.PREF_IMG_URL, response.getString("imgURL"));
            CustomPreferences.setPreferences(Constants.PREF_USER_STATE, response.getInt("state"));
            CustomPreferences.setPreferences(Constants.PREF_PUSH_STATE, response.getInt("push_state"));
            Utilities.dismissDialog(dialog);
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }catch (JSONException e){

        }
    }
}
