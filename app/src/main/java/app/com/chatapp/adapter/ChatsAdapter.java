package app.com.chatapp.adapter;

import android.content.Context;
import android.content.Intent;
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

import app.com.chatapp.R;
import app.com.chatapp.activity.AccountActivity;
import app.com.chatapp.activity.ChatActivity;
import app.com.chatapp.constant.Constants;
import app.com.chatapp.data.ChatListData;
import app.com.chatapp.data.FriendData;
import app.com.chatapp.utility.AvatarDrawableFactory;
import app.com.chatapp.utility.Utilities;

public class ChatsAdapter extends ArrayAdapter<ChatListData> implements View.OnClickListener{

    private ArrayList<ChatListData> dataSet;
    Context mContext;

    private DisplayImageOptions options;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    // View lookup cache
    private static class ViewHolder {
        ImageView imgUser;
        TextView txtName;
        TextView txtMessage;
        TextView txtDate;
        TextView txtCount;
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

    public ChatsAdapter(ArrayList<ChatListData> data, Context context) {
        super(context, R.layout.chat_row_item, data);
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
        ChatListData dataModel=(ChatListData)object;

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
        }
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatListData dataModel = getItem(position);
        AvatarDrawableFactory avatarDrawableFactory = new AvatarDrawableFactory(getContext().getResources());
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.chat_row_item, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.txtName);
            viewHolder.txtMessage = (TextView) convertView.findViewById(R.id.txtMessage);
            viewHolder.txtDate = (TextView) convertView.findViewById(R.id.txtDate);
            viewHolder.txtCount = (TextView) convertView.findViewById(R.id.txtCount);
            viewHolder.txtState = (TextView) convertView.findViewById(R.id.txtState);
            viewHolder.imgUser = (ImageView) convertView.findViewById(R.id.imgUser);
            viewHolder.imgUser.setTag(position);
            viewHolder.imgUser.setOnClickListener(this);
            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.txtName.setText(dataModel.getFirstName() + " " + dataModel.getLastName());
        viewHolder.txtMessage.setText(dataModel.getLastMessage());
        viewHolder.txtDate.setText(Utilities.getChatTime(dataModel.getLastDate()));
        if(dataModel.getMsgCount() == 0)
            viewHolder.txtCount.setVisibility(View.INVISIBLE);
        else
        {
            viewHolder.txtCount.setVisibility(View.VISIBLE);
            viewHolder.txtCount.setText(dataModel.getMsgCount() + "");
        }

        if(dataModel.getState() == 0)
            viewHolder.txtState.setBackgroundResource(R.drawable.state_whatsapp);
        else if(dataModel.getState() == 1)
            viewHolder.txtState.setBackgroundResource(R.drawable.state_online);
        else
            viewHolder.txtState.setBackgroundResource(R.drawable.state_offline);

        ImageLoader.getInstance().displayImage(dataModel.getImgURL(), viewHolder.imgUser, options, animateFirstListener);

        return convertView;
    }

    public void setChats(ArrayList<ChatListData> chats) {
        this.dataSet = chats;
        notifyDataSetChanged();
    }

    public void setItemData(int pos, int state)
    {
        this.dataSet.get(pos).setState(state);
        notifyDataSetChanged();
    }
}