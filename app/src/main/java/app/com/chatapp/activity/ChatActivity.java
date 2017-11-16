package app.com.chatapp.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import app.com.chatapp.R;
import app.com.chatapp.adapter.MessagesAdapter;
import app.com.chatapp.constant.Constants;
import app.com.chatapp.data.MessageData;
import app.com.chatapp.emoji.Emojicon;
import app.com.chatapp.emoji.EmojiconEditText;
import app.com.chatapp.emoji.EmojiconGridView;
import app.com.chatapp.emoji.EmojiconsPopup;
import app.com.chatapp.utility.CustomPreferences;
import app.com.chatapp.utility.Utilities;
import app.com.chatapp.view.CustomEditText;
import app.com.chatapp.view.CustomImageButton;
import app.com.chatapp.view.LoadingDialog;
import cz.msebera.android.httpclient.Header;

public class ChatActivity extends BaseActivity {
    ListView listView;
    MessagesAdapter mAdapter;
    CustomImageButton btnSend;
    ImageView btnEmojicon;
    TextView txtUserName;
    TextView txtState;
    EmojiconEditText edtMessage;
    ImageButton leftBack;
    ImageView imgProfile;
    TextView txtTypeState;

    private ArrayList<MessageData> mMessageList;
    LoadingDialog dialog;
    int contactID;
    int maxID = 0;
    int lastTypeState = 0;

    boolean threadflag = true;

    android.os.Handler customHandler;

    int userID, state;

    private DisplayImageOptions options;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
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

    @Override
    public void onBackPressed() {
        // backFragment(false);
        Constants.threadFlag = false;
        edtMessage.setText("");
        customHandler.removeCallbacksAndMessages(updateTimerThread);
        super.onBackPressed();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        edtMessage.setText("");
        Constants.threadFlag = false;
        customHandler.removeCallbacksAndMessages(updateTimerThread);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        Constants.threadFlag = false;
        customHandler.removeCallbacksAndMessages(updateTimerThread);
    }
    @Override
    protected int addView() {
        return R.layout.activity_chats;
    }

    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        super.init(savedInstanceState);
        contactID = getIntent().getIntExtra("ContactID", 0);
        final View rootView = findViewById(R.id.root_view);
        final EmojiconsPopup popup = new EmojiconsPopup(rootView, this);
        Constants.threadFlag = true;

        listView=(ListView)findViewById(R.id.listMessage);
        txtUserName = (TextView)findViewById(R.id.txtUserName);
        txtState = (TextView)findViewById(R.id.txtState);
        txtTypeState = (TextView)findViewById(R.id.txtTypeState);
        txtTypeState.setText("");
        btnSend = (CustomImageButton)findViewById(R.id.chat_btn_send);
        imgProfile = (ImageView) findViewById(R.id.imgProfile);
        btnEmojicon = (ImageView)findViewById(R.id.chat_btn_emonicon);
        edtMessage = (EmojiconEditText)findViewById(R.id.chat_edt_msg);
        edtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String strMessage = edtMessage.getText().toString();
                if(strMessage.equals("")) {
                    if(lastTypeState != 0)
                        lastTypeState = 0;
                    else return;
                }
                else{
                    if(lastTypeState != 1) lastTypeState = 1;
                    else return;
                }
                changeTypeState();
            }
        });
        leftBack = (ImageButton)findViewById(R.id.leftBack);
        txtUserName.setText(getIntent().getStringExtra("ContactName"));
        mMessageList= new ArrayList<>();
        mAdapter= new MessagesAdapter(mMessageList, this);

        listView.setAdapter(mAdapter);
        listView.setDivider(null);
        listView.setDividerHeight(0);

        userID = CustomPreferences.getPreferences(
                Constants.PREF_USER_ID, 0);
        getMessages();


        popup.setSizeForSoftKeyboard();

        popup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });

        popup.setOnSoftKeyboardOpenCloseListener(new EmojiconsPopup.OnSoftKeyboardOpenCloseListener() {
            @Override
            public void onKeyboardOpen(int keyBoardHeight) {
            }

            @Override
            public void onKeyboardClose() {
                if (popup.isShowing())
                    popup.dismiss();
            }
        });

        popup.setOnEmojiconClickedListener(new EmojiconGridView.OnEmojiconClickedListener() {

            @Override
            public void onEmojiconClicked(Emojicon emojicon) {
                if (edtMessage == null || emojicon == null) {
                    return;
                }

                int start = edtMessage.getSelectionStart();
                int end = edtMessage.getSelectionEnd();
                if (start < 0) {
                    edtMessage.append(emojicon.getEmoji());
                } else {
                    edtMessage.getText().replace(Math.min(start, end),
                            Math.max(start, end), emojicon.getEmoji(), 0,
                            emojicon.getEmoji().length());
                }
            }
        });

        popup.setOnEmojiconBackspaceClickedListener(new EmojiconsPopup.OnEmojiconBackspaceClickedListener() {

            @Override
            public void onEmojiconBackspaceClicked(View v) {
                KeyEvent event = new KeyEvent(
                        0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                edtMessage.dispatchKeyEvent(event);
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utilities.hideSoftKeyBoard(ChatActivity.this);
                sendMessage();
            }
        });
        leftBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btnEmojicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //If popup is not showing => emoji keyboard is not visible, we need to show it
                if (!popup.isShowing()) {

                    //If keyboard is visible, simply show the emoji popup
                    if (popup.isKeyBoardOpen()) {
                        popup.showAtBottom();
                        //changeEmojiKeyboardIcon(btnEmojicon, R.drawable.emonicon);
                    }

                    //else, open the text keyboard first and immediately after that show the emoji popup
                    else {
                        edtMessage.setFocusableInTouchMode(true);
                        edtMessage.requestFocus();
                        popup.showAtBottomPending();
                        final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(edtMessage, InputMethodManager.SHOW_IMPLICIT);
                        //changeEmojiKeyboardIcon(btnEmojicon, R.drawable.emonicon);
                    }
                }

                //If popup is showing, simply dismiss it to show the undelying text keyboard
                else {
                    popup.dismiss();
                }
            }
        });
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               gotoProfile();
            }
        });
        customHandler = new android.os.Handler();
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.empty_profile)
                .showImageForEmptyUri(R.drawable.empty_profile)
                .showImageOnFail(R.drawable.empty_profile)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new CircleBitmapDisplayer(Color.BLACK, 0))
                .build();
        ImageLoader.getInstance().displayImage(Constants.imgContactURL, imgProfile, options, animateFirstListener);
        //setupTabIcons();
    }
    private void gotoProfile()
    {
        Intent intent = new Intent(this, AccountActivity.class);
        startActivity(intent);
    }
    private void changeTypeState()
    {

        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("userid", userID);
        params.put("type_state", lastTypeState);
        params.put("type_contact_id", contactID);
        client.post(Constants.API_URL + "type_state", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.v("Result", "Success");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.v("Result", "Failure");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)

            }
        });
    }
    private Runnable updateTimerThread = new Runnable()
    {
        public void run()
        {
            //write here whaterver you want to repeat
            getMessage();
            getState();
        }
    };

    private void getMessages()
    {
        dialog = LoadingDialog.show(this);
        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("userid", userID);
        params.put("contact_id", contactID);
        params.put("page_num", 0);
        params.put("maxId", maxID);
        //AsyncHttpClient client = new AsyncHttpClient();
        client.post(Constants.API_URL + "getmessages", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                setAdapterData(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Utilities.dismissDialog(dialog);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                System.out.println("Failure");
                Utilities.dismissDialog(dialog);
            }
        });
    }

    private void getState()
    {
        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("contact_id", contactID);

        client.post(Constants.API_URL + "getlastvisit", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Constants.strLastTime = response.getString("last_time");
                    if(response.getInt("state") == 2)
                    {
                        txtState.setText("(Last seen - " + Constants.strLastTime + " )");
                    }
                    else if(response.getInt("state") == 1)
                    {
                        txtState.setText("(Online)");
                    } else
                    {
//                        txtState.setText("(Hey,i'm using whatsapp! - " + Constants.strLastTime + " )");
                        txtState.setText("(Hey,i'm using whatsapp!)");
                    }

                    if(response.getInt("type_state") == 0 || response.getInt("state") != 1)
                        txtTypeState.setText("");
                    else if(response.getInt("type_contact_id") == userID)
                        txtTypeState.setText(txtUserName.getText() + " is typing");
                    mAdapter.setItemData(response.getInt("state"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {

            }
        });
    }
    private void getMessage()
    {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("userid", userID);
        params.put("contact_id", contactID);
        params.put("page_num", 0);
        params.put("maxId", maxID);
        //AsyncHttpClient client = new AsyncHttpClient();
        client.post(Constants.API_URL + "getmessages", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray arrChatsList = response.getJSONArray("messages");

                    for(int i = 0; i < arrChatsList.length(); i++)
                    {
                        MessageData data = new MessageData();
                        JSONObject object = arrChatsList.getJSONObject(i);
                        if(object.getInt("fromId") == userID) return;
                        data.setToID(object.getInt("toId"));
                        data.setFromID(object.getInt("fromId"));
                        String addMsg = object.getString("message");
                        String message = addMsg;
                        if(addMsg.length() < 15)
                        {
                            for(int j = 0; j < 15- addMsg.length(); j++)
                                message += " ";
                        }
                        data.setMessage(message);
                        data.setDeliverDate(object.getString("deliverDate"));
                        Constants.strMessage = addMsg;
                        Constants.strDate = object.getString("deliverDate");
                        maxID = object.getInt("id");
                        //addMessage(data);
                        mAdapter.add(data);
                        mAdapter.notifyDataSetChanged();
                        scrollListViewToBottom();
                    }
                }catch (JSONException e){

                }
                System.out.println("Success");
                if(Constants.threadFlag)
                    customHandler.postDelayed(updateTimerThread, 1000);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if(Constants.threadFlag)
                    customHandler.postDelayed(updateTimerThread, 1000);
            }
        });
    }
    private void setAdapterData(JSONObject response)
    {
        try {
            JSONArray arrChatsList = response.getJSONArray("messages");

            for(int i = 0; i < arrChatsList.length(); i++)
            {
                MessageData data = new MessageData();
                JSONObject object = arrChatsList.getJSONObject(i);
                data.setToID(object.getInt("toId"));
                data.setFromID(object.getInt("fromId"));
                data.setState(object.getInt("state"));
                String addMsg = object.getString("message");
                String message = addMsg;
                if(addMsg.length() < 25)
                {
                    for(int j = 0; j < 25- addMsg.length(); j++)
                        message += " ";
                }
                data.setMessage(message);
                data.setDeliverDate(object.getString("deliverDate"));
                maxID = object.getInt("id");
                mMessageList.add(data);
            }
        }catch (JSONException e){

        }
        mAdapter.setMessages(mMessageList);
        Utilities.dismissDialog(dialog);
        scrollListViewToBottom();
        if(Constants.threadFlag)
            customHandler.postDelayed(updateTimerThread, 1000);
    }

    private void addMessage(MessageData messageData)
    {
        mAdapter.add(messageData);
        mAdapter.notifyDataSetChanged();

        scrollListViewToBottom();
    }

    private void scrollListViewToBottom() {
        listView.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                listView.setSelection(mAdapter.getCount() - 1);
            }
        });
    }

    private String getCurrentDate()
    {
        //TimeZone tz = TimeZone.getTimeZone("GMT+00:00");
        Calendar c = Calendar.getInstance();
        String strCurrentDate = "";
        strCurrentDate += String.format("%02d", c.get(Calendar.YEAR))+"-"+ String.format("%02d" , c.get(Calendar.MONTH) + 1)+"-"+ String.format("%02d" , c.get(Calendar.DATE)) + " ";
        strCurrentDate += String.format("%02d", c.get(Calendar.HOUR_OF_DAY))+":"+ String.format("%02d" , c.get(Calendar.MINUTE))+":"+ String.format("%02d" , c.get(Calendar.SECOND));

        return strCurrentDate;
    }
    private void sendMessage()
    {
        final String strMessage = edtMessage.getText().toString();
        if(strMessage.equals("")) {
            Toast.makeText(this, "Input message content.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        final MessageData messageData = new MessageData();
        final String strCurrentDate = getCurrentDate();

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("fromId", userID);
        params.put("toId", contactID);
        params.put("message", strMessage);
        params.put("deliverDate", strCurrentDate);

        messageData.setDeliverDate(strCurrentDate);
        String addMsg = strMessage;
        if(strMessage.length() < 25)
        {
            for(int i = 0; i < 25- strMessage.length(); i++)
                addMsg += " ";
        }
        messageData.setMessage(addMsg);
        messageData.setFromID(userID);
        messageData.setToID(contactID);
        messageData.setState(0);
        //AsyncHttpClient client = new AsyncHttpClient();
        edtMessage.setText("");

        client.post(Constants.API_URL + "sendmessage", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Constants.strMessage = strMessage;
                Constants.strDate = strCurrentDate;
                addMessage(messageData);
                edtMessage.setText("");
                Utilities.hideSoftKeyBoard(ChatActivity.this);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Utilities.hideSoftKeyBoard(ChatActivity.this);
                Toast.makeText(ChatActivity.this, "Connection failed!!!",
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                edtMessage.setText("");
                System.out.println("Failure");
                edtMessage.setText(res + "Failure");
            }
        });
    }

    private void changeEmojiKeyboardIcon(ImageView iconToBeChanged, int drawableResourceId) {
        iconToBeChanged.setImageResource(drawableResourceId);
    }
}
