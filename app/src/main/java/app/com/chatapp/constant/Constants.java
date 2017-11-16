package app.com.chatapp.constant;

import android.app.Activity;
import android.graphics.Bitmap;
import android.widget.ImageView;

import app.com.chatapp.SplashActivity;
import app.com.chatapp.activity.LoginActivity;

/**
 * Hai Nguyen - 8/27/15.
 */
public class Constants {

	public static final String BASE_URL = "https://app.textus.com/api/";

	public static int INTENT_REQUEST_PERMISSION = 1001;
	public static ImageView imgView;
	// Pref
	public static final String PREF_USER_ID = "user_id";
	public static final String PREF_ACC_EMAIL = "account_email";
	public static final String PREF_FIRSTNAME = "firstname";
	public static final String PREF_LASTNAME = "lastname";
	public static final String PREF_IMG_URL = "imgurl";
	public static final String PREF_USER_STATE = "user_state";
	public static final String PREF_PUSH_STATE = "push_state";

	public static final String PREF_IS_APP_ACTIVE = "is_app_active";

	// Intent
	public static final String INTENT_IS_NEW = "new_contact";
	public static final String INTENT_NEW_MESSAGE = "new_message";
	public static final String INTENT_SAVE_CONTACT = "save_contact";
	public static final String INTENT_TEMPLATE_CONTENT = "template_content";
	public static final String INTENT_TEMPLATE_SELECTED = "template_selected";
	public static final String INTENT_NEW_MESSAGE_CONTENT = "new_message_content";

	//public static final String API_URL = "http://192.168.1.110/chatapp/v1/";
	public static final String API_URL = "https://honestdeveloper.000webhostapp.com/chatapp/v1/";
	public static Bitmap bmpUserProfile = null;
	public static Bitmap bmpContactProfile = null;

	public static String imgContactURL;
	public static String strContactName;
	public static String strContactEmail;
	public static int contactState = 0;
	public static String strLastTime = "";
	public static boolean updatedProfile = false;
	public static int pos = -1;
	public static String strMessage = "";
	public static String strDate = "";
	public static int state = 0;
	public static int profileState = 0;

	public static boolean threadFlag = false;
	public static boolean stateFlag = false;
	public static Activity currentActivity = new SplashActivity();
}
