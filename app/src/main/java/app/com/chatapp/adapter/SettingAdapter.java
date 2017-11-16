package app.com.chatapp.adapter;

import android.content.Context;
import android.support.annotation.IdRes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import app.com.chatapp.R;
import app.com.chatapp.constant.Constants;
import app.com.chatapp.utility.CustomPreferences;
import app.com.chatapp.utility.Utilities;
import cz.msebera.android.httpclient.Header;

public class SettingAdapter extends BaseAdapter implements View.OnClickListener{
    Context mContext;
    private int[] settingIcons = {
            R.drawable.setting_account,
            R.drawable.setting_notification,
            R.drawable.setting_about,
            R.drawable.setting_contact,
            R.drawable.setting_logout
    };
    private String[] settingNames ={"Account", "Notification", "About and Help", "Contact Us", "Log out"};
    // View lookup cache
    private static class ViewHolder {
        ImageView imgIcon;
        TextView settingName;
        RadioGroup rdoPush;
    }
    public SettingAdapter(Context context) {
        this.mContext=context;
    }

    @Override
    public int getCount() {
        return settingNames.length;
    }

    @Override
    public Object getItem(int position) {
        return settingNames[position]; //returns list item at the specified position
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onClick(View v) {
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // inflate the layout for each list row
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            if(position == 1) {
                convertView = inflater.
                        inflate(R.layout.setting_row_notification_item, parent, false);
                viewHolder.rdoPush = (RadioGroup) convertView.findViewById(R.id.toggle);
                if(CustomPreferences.getPreferences(Constants.PREF_PUSH_STATE, 1) == 1)
                    viewHolder.rdoPush.check(R.id.push_on);
                else
                    viewHolder.rdoPush.check(R.id.push_off);
            }
            else
                convertView = inflater.
                    inflate(R.layout.setting_row_item, parent, false);
            viewHolder.settingName = (TextView)
                    convertView.findViewById(R.id.txtName);
            viewHolder.imgIcon = (ImageView)
                    convertView.findViewById(R.id.imgIcon);
            convertView.setTag(viewHolder);
        }
        else
            viewHolder = (ViewHolder) convertView.getTag();
        // get the TextView for item name and item description
        if(position == 1) {
            viewHolder.rdoPush.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                    if(i == R.id.push_on)
                        setPushState(1);
                    else
                        setPushState(0);
                }
            });
        }
        viewHolder.settingName.setText(settingNames[position]);
        viewHolder.imgIcon.setImageResource(settingIcons[position]);

        // returns the view for the current row
        return convertView;
    }

    private void setPushState(int state)
    {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("userid", CustomPreferences.getPreferences(
                Constants.PREF_USER_ID, 0));
        params.put("push_state", state);
        CustomPreferences.setPreferences(Constants.PREF_PUSH_STATE, state);
        client.post(Constants.API_URL + "push_state", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.v("Result", "Success");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.v("Result", "Failure");
            }
        });
    }
}
