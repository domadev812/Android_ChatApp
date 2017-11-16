package app.com.chatapp.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import app.com.chatapp.R;
import app.com.chatapp.activity.ChatActivity;
import app.com.chatapp.activity.MainActivity;
import app.com.chatapp.adapter.ChatsAdapter;
import app.com.chatapp.constant.Constants;
import app.com.chatapp.data.ChatListData;
import app.com.chatapp.utility.CustomPreferences;
import app.com.chatapp.utility.Utilities;
import app.com.chatapp.view.LoadingDialog;
import cz.msebera.android.httpclient.Header;
import android.util.Log;

public class ChatsFragment extends Fragment {
    private ArrayList<ChatListData> mChatList;
    private ChatsAdapter mAdapter;
    ListView listView;
    LoadingDialog dialog;
    android.os.Handler stateHandler;
    boolean threadFlag = false;
    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        if(Constants.pos == -1) return;
        mChatList.get(Constants.pos).setLastMessage(Constants.strMessage);
        mChatList.get(Constants.pos).setLastDate(Constants.strDate);
        mAdapter.notifyDataSetChanged();
        Constants.pos = -1;
        threadFlag = true;
        stateHandler.postDelayed(updateUserStates, 2000);
    }
    @Override
    public void onPause()
    {
        super.onPause();
        threadFlag = false;
        stateHandler.removeCallbacksAndMessages(updateUserStates);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Create", "Create");
        stateHandler = new android.os.Handler();
        threadFlag = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat,
                container, false);
        listView=(ListView)view.findViewById(R.id.listChats);
        mChatList= new ArrayList<>();

        mAdapter= new ChatsAdapter(mChatList, getActivity());
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new OnItemClickListener() {
            //@Override
            public void onItemClick(AdapterView arg0, View view,
                                    int position, long id) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("ContactID", mChatList.get(position).getContactID());
                intent.putExtra("ContactName", mChatList.get(position).getFirstName() + " " + mChatList.get(position).getLastName());
                TextView txtMsgCount = (TextView) view.findViewById(R.id.txtCount);
                ImageView imgContact = (ImageView) view.findViewById(R.id.imgUser);

                //txtMsgCount.setVisibility(View.INVISIBLE);
                mChatList.get(position).setMsgCount(0);

                Constants.imgContactURL = mChatList.get(position).getImgURL();
                Constants.pos = position;
                Constants.strMessage =  mChatList.get(position).getLastMessage();
                Constants.strDate =  mChatList.get(position).getLastDate();

                Constants.imgContactURL = mChatList.get(position).getImgURL();
                Constants.strContactEmail = mChatList.get(position).getEmail();
                Constants.contactState = mChatList.get(position).getState();
                Constants.strContactName = mChatList.get(position).getFirstName() + " " + mChatList.get(position).getLastName();
                Constants.profileState = 1;

                startActivity(intent);
            }
        });
        getChatsList();
        threadFlag = true;
        Log.d("Create", "CreateView");
        return view;
        //return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    public void getUserStates()
    {
        for(int i = 0; i < mChatList.size(); i++) {
            final int index = i;
            AsyncHttpClient client = new AsyncHttpClient();
            int contactID = mChatList.get(i).getContactID();
            RequestParams params = new RequestParams();
            params.put("contact_id", contactID);

            String strURL = Constants.API_URL + "getlastvisit";
            client.post(strURL, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        mAdapter.setItemData(index, response.getInt("state"));
                        Constants.strLastTime = response.getString("last_time");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
        if(threadFlag)
            stateHandler.postDelayed(updateUserStates, 10000);
    }
    private void getChatsList()
    {
        dialog = LoadingDialog.show(getActivity());
        AsyncHttpClient client = new AsyncHttpClient();
        int userID = CustomPreferences.getPreferences(
                Constants.PREF_USER_ID, 0);
        RequestParams params = new RequestParams();
        params.put("userid", userID);

        String strURL = Constants.API_URL + "chats";
        client.post(strURL, params, new JsonHttpResponseHandler() {
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

    private void setAdapterData(JSONObject response)
    {
        try {
            JSONArray arrChatsList = response.getJSONArray("chats");

            for(int i = 0; i < arrChatsList.length(); i++)
            {
                ChatListData data = new ChatListData();
                JSONObject object = arrChatsList.getJSONObject(i);
                data.setContactID(object.getInt("contact_id"));
                data.setFirstName(object.getString("firstName"));
                data.setLastName(object.getString("lastName"));
                data.setImgURL(object.getString("imgURL"));
                data.setMsgCount(object.getInt("msgCount"));
                data.setMaxID(object.getInt("msgId"));
                data.setLastMessage(object.getString("msgContent"));
                data.setLastDate(object.getString("deliverDate"));
                data.setEmail(object.getString("email"));
                data.setState(object.getInt("state"));
                mChatList.add(data);
            }
        }catch (JSONException e){

        }
        mAdapter.setChats(mChatList);
        listView.scrollTo(0, listView.getHeight());
        stateHandler.postDelayed(updateUserStates, 10000);
        Utilities.dismissDialog(dialog);
    }

    private Runnable updateUserStates = new Runnable()
    {
        public void run()
        {
            getUserStates();
        }
    };
}
