package app.com.chatapp.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import app.com.chatapp.utility.CustomPreferences;
import butterknife.ButterKnife;

/**
 * Hai Nguyen - 8/27/15.
 */
public class BaseActivity extends AppCompatActivity
		implements
			View.OnClickListener {

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		int layoutId = addView();
		setContentView(layoutId);
		ButterKnife.bind(this);
		CustomPreferences.init(this);
		init(savedInstanceState);
	}

	/**
	 * Add layout view for activity
	 *
	 * @return Layout view id
	 */
	protected int addView() {

		return 0;
	}

	protected void init(@Nullable Bundle savedInstanceState) {

	}

	@Override
	public void onClick(View view) {


	}
}
