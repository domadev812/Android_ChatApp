package app.com.chatapp.view;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by ShangTao on 28/07/2017.
 */

public class CustomTextView extends TextView {
    public CustomTextView(Context paramContext) {
        super(paramContext);
        setFont();
    }

    public CustomTextView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        setFont();
    }

    public CustomTextView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        setFont();
    }

    public void setFont() {
    }
}
