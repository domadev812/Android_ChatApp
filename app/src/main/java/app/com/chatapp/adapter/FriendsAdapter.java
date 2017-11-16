package app.com.chatapp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import app.com.chatapp.R;
import app.com.chatapp.activity.AccountActivity;
import app.com.chatapp.data.FriendData;
import app.com.chatapp.utility.AvatarDrawableFactory;
import android.content.Intent;
import app.com.chatapp.activity.ChatActivity;
import app.com.chatapp.constant.Constants;

import static android.R.attr.data;

public class FriendsAdapter extends ArrayAdapter<FriendData> implements View.OnClickListener{

    private ArrayList<FriendData> dataSet;
    private ArrayList<FriendData> dataBackup = new ArrayList<>();
    Context mContext;

    private DisplayImageOptions options;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    // View lookup cache
    private static class ViewHolder {
        ImageView imgUser;
        TextView txtName;
        TextView txtState;
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

    public FriendsAdapter(ArrayList<FriendData> data, Context context) {
        super(context, R.layout.friend_row_item, data);
        this.dataSet = data;

        this.mContext=context;

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.empty_profile)
                .showImageForEmptyUri(R.drawable.empty_profile)
                .showImageOnFail(R.drawable.empty_profile)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new CircleBitmapDisplayer(Color.WHITE, 0))
                .build();
    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        FriendData dataModel=(FriendData)object;

        switch (v.getId())
        {
            case R.id.imgUser: {
                Intent intent = new Intent(mContext, AccountActivity.class);
                Constants.imgContactURL = dataModel.getImgURL();
                Constants.strContactEmail = dataModel.getEmail();
                Constants.contactState = dataModel.getState();
                Constants.strContactName = dataModel.getFirstName() + " " + dataModel.getLastName();
                Constants.profileState = 1;
                mContext.startActivity(intent);
            }
                break;
            case R.id.txtName: {
                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra("ContactID", dataModel.getUserID());
                intent.putExtra("ContactName", dataModel.getFirstName() + " " + dataModel.getLastName());
                Constants.imgContactURL = dataModel.getImgURL();
                Constants.strContactEmail = dataModel.getEmail();
                Constants.contactState = dataModel.getState();
                Constants.strContactName = dataModel.getFirstName() + " " + dataModel.getLastName();
                Constants.profileState = 1;

                mContext.startActivity(intent);
            }
                break;
        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FriendData dataModel = getItem(position);
        AvatarDrawableFactory avatarDrawableFactory = new AvatarDrawableFactory(getContext().getResources());
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.friend_row_item, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.txtName);
            viewHolder.imgUser = (ImageView) convertView.findViewById(R.id.imgUser);
            viewHolder.txtState = (TextView) convertView.findViewById(R.id.txtState);
            viewHolder.imgUser.setTag(position);
            viewHolder.imgUser.setOnClickListener(this);
            viewHolder.txtName.setTag(position);
            viewHolder.txtName.setOnClickListener(this);
            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        if(dataModel.getState() == 0)
            viewHolder.txtState.setBackgroundResource(R.drawable.state_whatsapp);
        else if(dataModel.getState() == 1)
            viewHolder.txtState.setBackgroundResource(R.drawable.state_online);
        else
            viewHolder.txtState.setBackgroundResource(R.drawable.state_offline);

        lastPosition = position;

        viewHolder.txtName.setText(dataModel.getFirstName() + " " + dataModel.getLastName());
        ImageLoader.getInstance().displayImage(dataModel.getImgURL(), viewHolder.imgUser, options, animateFirstListener);
        // Return the completed view to render on screen
        return convertView;
    }

    public void setFriends(ArrayList<FriendData> friendDatas) {
        this.dataSet = friendDatas;
        for (FriendData friend : friendDatas) {
            this.dataBackup.add(friend);
        }
        notifyDataSetChanged();
    }

    public void searchFriends(String strName)
    {
        strName = strName.toLowerCase(Locale.getDefault());
        this.dataSet.clear();
        if (strName.length() == 0) {
            this.dataSet.addAll(this.dataBackup);
        } else {
            for (FriendData friend : this.dataBackup) {
                if (friend.getFullName().toLowerCase(Locale.getDefault())
                        .contains(strName)) {
                    this.dataSet.add(friend);
                }
            }
        }
        notifyDataSetChanged();
    }
    public void setItemData(int pos, int state)
    {
        this.dataSet.get(pos).setState(state);
        notifyDataSetChanged();
    }
}