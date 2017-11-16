package app.com.chatapp.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by ShangTao on 28/07/2017.
 */

public class CustomImageButton extends ImageView {
    private boolean isDefaultBg;
    public CustomImageButton(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public CustomImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public CustomImageButton(Context context, AttributeSet attrs,
                             int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void setEnabled(boolean enabled) {

        if (enabled) {

            setAlpha(1f);
        } else {

            setAlpha(0.5f);
        }
        super.setEnabled(enabled);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.view.View#setPressed(boolean)
     */
    @Override
    public void setPressed(boolean pressed) {
        // TODO Auto-generated method stub
        if (pressed) {
            setAlpha(0.7f);
        } else {
            setAlpha(1f);
        }

        super.setPressed(pressed);
    }
}
