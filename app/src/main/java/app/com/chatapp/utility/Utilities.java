package app.com.chatapp.utility;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import app.com.chatapp.constant.Constants;
import app.com.chatapp.view.DividerItemDecoration;
import cz.msebera.android.httpclient.Header;

/**
 * Hai Nguyen - 8/27/15.
 */
public class Utilities {
	public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
	/**
	 * Dismiss dialog
	 */
	public static void dismissDialog(Dialog dialog) {
		try {

			dialog.dismiss();
		} catch (Exception e) {

			LogUtil.e("Dismiss dialog", "Dismiss dialog");
		}
	}

	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you may tune some of them,
		// or you can create default configuration by
		//  ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
		config.threadPriority(Thread.NORM_PRIORITY - 2);
		config.denyCacheImageMultipleSizesInMemory();
		config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
		config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
		config.tasksProcessingOrder(QueueProcessingType.LIFO);
		config.writeDebugLogs(); // Remove for release app

		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config.build());
	}
	/**
	 * Show alert dialog
	 *
	 * @param title
	 *            Dialog title
	 * @param message
	 *            Dialog message
	 * @param positiveText
	 *            Positive button text
	 * @param negativeText
	 *            Negative button text
	 * @param positiveButtonClick
	 *            Positive button click listener
	 * @param negativeButtonClick
	 *            Negative button click listener
	 * @param isCancelAble
	 *            True if can cancel
	 */
	public static void showAlertDialog(Context context, String title,
                                       String message, String positiveText, String negativeText,
                                       DialogInterface.OnClickListener positiveButtonClick,
                                       DialogInterface.OnClickListener negativeButtonClick,
                                       boolean isCancelAble) {

		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
		dialogBuilder.setCancelable(isCancelAble);
		dialogBuilder.setMessage(message);
		if (!title.equals("")) {

			dialogBuilder.setTitle(title);
		}

		// Positive button
		if (!positiveText.equals("")) {

			if (positiveButtonClick != null) {

				dialogBuilder.setPositiveButton(positiveText,
						positiveButtonClick);
			} else {

				dialogBuilder.setPositiveButton(positiveText,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								dialog.dismiss();
							}
						});
			}
		}

		// Negative button
		if (!negativeText.equals("")) {

			if (negativeButtonClick != null) {

				dialogBuilder.setNegativeButton(negativeText,
						negativeButtonClick);
			} else {

				dialogBuilder.setNegativeButton(negativeText,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								dialog.dismiss();
							}
						});
			}
		}

		AlertDialog dialog = dialogBuilder.create();
		dialog.show();
	}


	public static void hideSoftKeyBoard(Activity act) {

		InputMethodManager imm = (InputMethodManager) act
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isAcceptingText()) {

			View currentView = act.getCurrentFocus();
			if (currentView == null) {

				return;
			}

			imm.hideSoftInputFromWindow(currentView.getWindowToken(), 0);
		}
	}

	// /**
	// * Display network image
	// * */
	// public static void displayImage(Context ctx, ImageView imageView, String
	// url) {
	//
	// Picasso.with(ctx).load(url).placeholder(R.mipmap.ic_launcher)
	// .into(imageView);
	// }

	/**
	 * Set recycler view layout manager
	 *
	 * @param ctx
	 *            Context
	 */
	public static RecyclerView.LayoutManager setLayoutManager(Context ctx,
			RecyclerView view, boolean isFixedSize, boolean hasDivider) {

		LinearLayoutManager mLayoutManager = new LinearLayoutManager(ctx,
				RecyclerView.VERTICAL, false);
		view.setLayoutManager(mLayoutManager);
		view.setHasFixedSize(isFixedSize);
		if (hasDivider) {

			DividerItemDecoration divider = new DividerItemDecoration(ctx,
					null, false, false);
			view.addItemDecoration(divider);
		}

		return mLayoutManager;
	}

	/**
	 * Get delivered time
	 * 
	 * @param strTime
	 *            Input time
	 * @return time string
	 */
	public static String getDeliveredTime(String strTime) {

		SimpleDateFormat format = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
		format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
		try {

			Date date = format.parse(strTime);
			format = new SimpleDateFormat("HH:mm a", Locale.US);
			return format.format(date);
		} catch (Exception ignore) {

		}

		return "";
	}

	/**
	 * Get chat time
	 *
	 * @param strTime
	 *            Input time
	 * @return time string
	 */
	public static String getChatTime(String strTime) {

		SimpleDateFormat format = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		try {
			Date date = format.parse(strTime);
			format = new SimpleDateFormat("hh:mm a", Locale.getDefault());
			if (DateUtils.isToday(date.getTime())) {
				format = new SimpleDateFormat("hh:mm",
						Locale.getDefault());
				return String.format("Today  %s", format.format(date));
			}

			format = new SimpleDateFormat("yyyy-MM-dd",
					Locale.getDefault());
			return format.format(date);
		} catch (Exception ignore) {

		}

		return "";
	}

	/**
	 * Play sound
	 */
	public static void playSound(Context ctx, int rawId) {

		final MediaPlayer player = MediaPlayer.create(ctx, rawId);
		player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mediaPlayer) {

				player.stop();
			}
		});

		player.start();
	}

	/**
	 * Check user permission
	 *
	 * @param context
	 *            Application context
	 * @param permissions
	 *            List of permissions
	 * @return true if has permission
	 */
	public static boolean hasPermissions(Context context, String... permissions) {
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
				&& context != null && permissions != null) {

			for (String permission : permissions) {

				if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {

					return false;
				}
			}
		}

		return true;
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public static boolean checkPermission(final Context context)
	{
		int currentAPIVersion = Build.VERSION.SDK_INT;
		if(currentAPIVersion>=android.os.Build.VERSION_CODES.M)
		{
			if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
				if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
					AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
					alertBuilder.setCancelable(true);
					alertBuilder.setTitle("Permission necessary");
					alertBuilder.setMessage("External storage permission is necessary");
					alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
						@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
						public void onClick(DialogInterface dialog, int which) {
							ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
						}
					});
					AlertDialog alert = alertBuilder.create();
					alert.show();

				} else {
					ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
				}
				return false;
			} else {
				return true;
			}
		} else {
			return true;
		}
	}

	public static void initPreference()
	{
		CustomPreferences.setPreferences(Constants.PREF_USER_ID, 0);
		CustomPreferences.setPreferences(Constants.PREF_ACC_EMAIL, "");
		CustomPreferences.setPreferences(Constants.PREF_FIRSTNAME, "");
		CustomPreferences.setPreferences(Constants.PREF_LASTNAME, "");
		if(CustomPreferences.getPreferences(Constants.PREF_USER_STATE, 2) != 0)
			CustomPreferences.setPreferences(Constants.PREF_USER_STATE, 2);
		CustomPreferences.setPreferences(Constants.PREF_IMG_URL, null);
	}

	public static void changeState(int userID, int state)
	{

		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.put("userid", userID);
		params.put("state", state);

		String strURL = Constants.API_URL + "updatestate";
		client.post(strURL, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

			}
			@Override
			public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {

			}
		});
	}
}
