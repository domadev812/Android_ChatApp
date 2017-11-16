package app.com.chatapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import app.com.chatapp.R;
import app.com.chatapp.activity.ChatActivity;
import app.com.chatapp.adapter.FriendsAdapter;
import app.com.chatapp.constant.Constants;
import app.com.chatapp.data.FriendData;
import app.com.chatapp.utility.CustomPreferences;
import app.com.chatapp.utility.Utilities;
import app.com.chatapp.view.LoadingDialog;
import cz.msebera.android.httpclient.Header;

public class FriendFragment extends Fragment {
    private ArrayList<FriendData> mFriendsList;
    private FriendsAdapter mAdapter;
    ListView listView;
    EditText edtSearch;
    Context context;
    LoadingDialog dialog;
    android.os.Handler stateHandler;
    boolean threadFlag = false;
    public FriendFragment() {
        // Required empty public constructor
    }
    @Override
    public void onStart() {
        super.onStart();
        threadFlag = true;
        stateHandler.postDelayed(updateUserStates, 10000);
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
        stateHandler = new android.os.Handler();
        threadFlag = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friend,
                container, false);
        edtSearch = (EditText)view.findViewById(R.id.edtSearch);
        listView=(ListView)view.findViewById(R.id.listFriends);
        mFriendsList= new ArrayList<>();
        mAdapter= new FriendsAdapter(mFriendsList, getActivity());
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            //@Override
            public void onItemClick(AdapterView arg0, View view,
                                    int position, long id) {
//                Intent intent = new Intent(getActivity(), ChatActivity.class);
//                intent.putExtra("ContactID", mFriendsList.get(position).getUserID());
//                intent.putExtra("ContactName", mFriendsList.get(position).getFirstName() + " " + mFriendsList.get(position).getLastName());
//                Constants.imgContactURL = mFriendsList.get(position).getImgURL();
//                startActivity(intent);
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchFriends();
            }
        });

        getFriendsList();
        threadFlag = true;
        return view;
    }
    public void getUserStates()
    {
        for(int i = 0; i < mFriendsList.size(); i++) {
            final int index = i;
            AsyncHttpClient client = new AsyncHttpClient();
            int contactID = mFriendsList.get(i).getUserID();
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
                    Utilities.dismissDialog(dialog);
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
    private void getFriendsList()
    {
        dialog = LoadingDialog.show(getActivity());
        AsyncHttpClient client = new AsyncHttpClient();
        int userID = CustomPreferences.getPreferences(
                Constants.PREF_USER_ID, 0);
        RequestParams params = new RequestParams();
        params.put("userid", userID);

        client.post(Constants.API_URL + "friends", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                setAdapterData(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Utilities.dismissDialog(dialog);
            }
        });
        //mAdapter.setData(mChatList);
    }

    private void setAdapterData(JSONObject response){
        try {
            JSONArray arrChatsList = response.getJSONArray("friends");

            for(int i = 0; i < arrChatsList.length(); i++)
            {
                FriendData data = new FriendData();
                JSONObject object = arrChatsList.getJSONObject(i);
                data.setUserID(object.getInt("friend_id"));
                data.setFirstName(object.getString("firstName"));
                data.setLastName(object.getString("lastName"));
                data.setFullName(data.getFirstName() + " " + data.getLastName());
                data.setEmail(object.getString("email"));
                data.setImgURL(object.getString("imgURL"));
                data.setState(object.getInt("state"));
                mFriendsList.add(data);
            }
        }catch (JSONException e){

        }
        mAdapter.setFriends(mFriendsList);
        Utilities.dismissDialog(dialog);
    }
    private Runnable updateUserStates = new Runnable()
    {
        public void run()
        {
            getUserStates();
        }
    };
    private void searchFriends()
    {
        mAdapter.searchFriends(edtSearch.getText().toString());
    }
}
