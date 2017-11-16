package app.com.chatapp.adapter;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
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
import android.widget.RelativeLayout;
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
import app.com.chatapp.constant.Constants;
import app.com.chatapp.data.ChatListData;
import app.com.chatapp.data.MessageData;
import app.com.chatapp.utility.AvatarDrawableFactory;
import app.com.chatapp.utility.CustomPreferences;
import app.com.chatapp.utility.Utilities;

public class MessagesAdapter extends ArrayAdapter<MessageData> implements View.OnClickListener{
    private ArrayList<MessageData> data;
    Context mContext;
    int userID;
    int state = Constants.contactState;
    String userProfileURL = "";
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
    // View lookup cache
    private static class ViewHolder {
        ImageView imgLeft;
        TextView txtLeftMessage;
        TextView txtLeftDate;
        ImageView imgRight;
        TextView txtRightMessage;
        TextView txtRightDate;
        TextView txtState;
        RelativeLayout layout_right;
        RelativeLayout layout_left;
        ImageView imgSend;
        ImageView imgRead;
    }

    public MessagesAdapter(ArrayList<MessageData> data, Context context) {
        super(context, R.layout.layout_item_chat, data);
        userID = CustomPreferences.getPreferences(
                Constants.PREF_USER_ID, 0);
        userProfileURL = CustomPreferences.getPreferences(
                Constants.PREF_IMG_URL, "");
        this.data = data;
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
        switch (v.getId())
        {
            case R.id.imgContact: {
                Intent intent = new Intent(mContext, AccountActivity.class);
                Constants.profileState = 1;
                mContext.startActivity(intent);
            }
            break;
        }
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        MessageData dataModel = getItem(position);
        AvatarDrawableFactory avatarDrawableFactory = new AvatarDrawableFactory(getContext().getResources());
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.layout_item_chat, parent, false);
            viewHolder.txtLeftMessage = (TextView) convertView.findViewById(R.id.chat_item_msg_left);
            viewHolder.txtLeftDate = (TextView) convertView.findViewById(R.id.chat_item_time_left);
            viewHolder.txtRightMessage = (TextView) convertView.findViewById(R.id.chat_item_msg_right);
            viewHolder.txtRightDate = (TextView) convertView.findViewById(R.id.chat_item_time_right);
            viewHolder.imgLeft = (ImageView)convertView.findViewById(R.id.imgContact);
            viewHolder.imgRight = (ImageView)convertView.findViewById(R.id.imgUser);
            viewHolder.imgSend = (ImageView)convertView.findViewById(R.id.imgSend);
            viewHolder.imgRead = (ImageView)convertView.findViewById(R.id.imgRead);
            viewHolder.layout_left = (RelativeLayout)convertView.findViewById(R.id.layout_left);
            viewHolder.layout_right = (RelativeLayout)convertView.findViewById(R.id.layout_right);
            viewHolder.txtState = (TextView)convertView.findViewById(R.id.txtState);
            result=convertView;
            viewHolder.imgLeft.setOnClickListener(this);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        if(dataModel.getFromID() != userID) {
            viewHolder.txtLeftMessage.setText(dataModel.getMessage());
            viewHolder.txtLeftDate.setText(Utilities.getChatTime(dataModel.getDeliverDate()));
            ImageLoader.getInstance().displayImage(Constants.imgContactURL, viewHolder.imgLeft, options, animateFirstListener);
            viewHolder.layout_left.setVisibility(View.VISIBLE);
            viewHolder.layout_right.setVisibility(View.GONE);
            viewHolder.imgSend.setVisibility(View.INVISIBLE);
            viewHolder.imgRead.setVisibility(View.INVISIBLE);
            if(state == 0)
                viewHolder.txtState.setBackgroundResource(R.drawable.state_whatsapp);
            else if(state == 1)
                viewHolder.txtState.setBackgroundResource(R.drawable.state_online);
            else
                viewHolder.txtState.setBackgroundResource(R.drawable.state_offline);
        }
        else {
            viewHolder.txtRightMessage.setText(dataModel.getMessage());
            viewHolder.txtRightDate.setText(Utilities.getChatTime(dataModel.getDeliverDate()));
            ImageLoader.getInstance().displayImage(userProfileURL, viewHolder.imgRight, options, animateFirstListener);

            viewHolder.layout_left.setVisibility(View.GONE);
            viewHolder.layout_right.setVisibility(View.VISIBLE);
            viewHolder.imgSend.setVisibility(View.VISIBLE);
            if(dataModel.getState() == 1)
                viewHolder.imgRead.setVisibility(View.VISIBLE);
            else
                viewHolder.imgRead.setVisibility(View.INVISIBLE);
        }
        // Return the completed view to render on screen


        return convertView;
    }

    public void setMessages(ArrayList<MessageData> messageDatas) {
        this.data = messageDatas;
        notifyDataSetChanged();
    }

    public void setItemData(int userstate)
    {
        state = userstate;
        Constants.contactState = state;
        notifyDataSetChanged();
    }
}
