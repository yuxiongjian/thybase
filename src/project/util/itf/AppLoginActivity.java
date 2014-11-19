package project.util.itf;

import hardware.config.Handphone;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.SharedPreferences;

import org.alexd.jsonrpc.JSONRPCException;

import project.config.Config;
import project.config.DebugSetting;
import project.ui.ProjectActivity;
import project.util.MyLog;
import project.util.ProjPref;
import project.util.UpdateActivity;

import com.thybase.R;
import com.jt.appservice.JtService;
import com.jt.pojo.User;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public abstract class AppLoginActivity extends ProjectActivity {

	public static final int DURATION = 10 * 1000;
	public static final String SHAREDPREFERENCES = "jiatuapp";
	public static int iLogin;
	public static IUser User;

	public static IUser getUser() {
		return User;
	}

	public static void setUser(IUser user) {
		User = user;
	}

	/**
	 * A dummy authentication store containing known user names and passwords.
	 * TODO: remove after connecting to a real authentication system.
	 */

	Activity MyAct = AppLoginActivity.this;

	/**
	 * The default email to populate the email field with.
	 */
	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL"; //$NON-NLS-1$

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mUserName;
	private String mPassword;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	private ImageView mImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Config.getVerCode(getBaseContext());

		setContentView(R.layout.act_login);
		// PriceUtil.init(this,R.drawable.ic_launcher,5);
		// Bitmap btm = installShortCut();
		// ImageView imgview = (ImageView)findViewById(R.id.icontest);
		// imgview.setImageBitmap(btm);
		Handphone handphone = Handphone.getSetting(this);

		String username = handphone.hp.getDeviceID();
		MyLog.Log(username);
		// project.util.Test.testjtService(this);
		String password = getUserPref()[1];

		TextView mVersion = (TextView) findViewById(R.id.login_version);
		String vname = Config.getVerName(MyAct);
		Resources rc = this.getResources();
		vname = rc.getText(R.string.version) + " : " + vname;
		mVersion.setText(vname);

		// showNotification(R.drawable.ic_launcher,"图标边的文字","标题","内容");
		// setContentView(R.layout.activity_login);

		// Set up the login form.
		// mEmail = getIntent().getStringExtra(EXTRA_EMAIL);
		mEmailView = (EditText) findViewById(R.id.username);
		mEmailView.setText(username);

		// BpmService.debug=1;
		if (DebugSetting.isDebug()) {
			Toast.makeText(MyAct, "Debug Mode=" + DebugSetting.debug, DURATION).show(); //$NON-NLS-1$
			if (username == null)
				mEmailView.setText("thomasy"); //$NON-NLS-1$

		}

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {

					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});
		mPasswordView.setText(password);
		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {

					public void onClick(View view) {
						attemptLogin();
					}
				});

		if (username != null && password != null) {

			Button bt = (Button) findViewById(R.id.sign_in_button);
			bt.performClick();

		}

	}

	public void PostLogin(int success, Exception e) {
		mAuthTask = null;
		int ret = 0;
		// showProgress(false);
		Intent _intent = new Intent(MyAct, AppListActivity.class);
		if (success == 1) {
			try {

				iLogin = 1;
				// UpdateActivity.ShowUpgrade( getApplicationContext(),1);
				// Intent _intent = new Intent(MyAct,
				// TaskFlowListActivity.class);
				MyAct.startActivityForResult(_intent, 0);
			} catch (Exception e1) {
				e = e1;
			}
			;
			// finish();
		} else if (success == -1) {
			MyLog.Assert(e == null, "");
			iLogin = 0;
		} else if (success == 0) {
			mPasswordView
					.setError(getString(R.string.error_incorrect_password));
			mPasswordView.requestFocus();
			iLogin = 0;
		} else if (success == -2) {
			iLogin = 0;
			MyLog.Assert(e != null, "");

			MyAct.startActivityForResult(_intent, 0);
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_CANCELED && data != null) {
			// Bundle bundle = data.getExtras();
			// Exception e = (Exception)
			// bundle.get(TaskFlowListActivity.EXCEPTION);
			// PriceUtil.catchException(MyAct,e.getCause());
			// finish();

		}
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mUserName = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			// mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} /*
		 * else if (mPassword.length() < 4) {
		 * mPasswordView.setError(getString(R.string.error_invalid_password));
		 * focusView = mPasswordView; cancel = true; }
		 */

		/*
		 * Check for a valid email address. if (TextUtils.isEmpty(mUserName)) {
		 * mEmailView.setError(getString(R.string.error_field_required));
		 * focusView = mEmailView; cancel = true; } else if
		 * (!mUserName.contains("@")) {
		 * mEmailView.setError(getString(R.string.error_invalid_email));
		 * focusView = mEmailView; cancel = true; }
		 */

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);

		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public void saveUserPref(IUser User) {

		project.util.ProjPref.SavePref(MyAct, "username", User.getUsername());
		project.util.ProjPref.SavePref(MyAct, "password", User.getPassword());

	}

	public String[] getUserPref() {

		// IUser = new IUser();
		String[] ret = new String[2];
		ret[0] = (String) project.util.ProjPref
				.GetPref(MyAct, "username", "");
		ret[1] = (String) project.util.ProjPref
				.GetPref(MyAct, "password", "");

		return ret;

	}

	public abstract Object onLogin(String u, String p) throws JSONRPCException;

	public class UserLoginTask extends AsyncTask<Void, Exception, Integer> {

		Exception mye = null;

		@Override
		protected Integer doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.

			Object ret = 0;
			try {

				ret = onLogin(mUserName, mPassword);
				if (ret.getClass().isAssignableFrom(User.class)) {

					User = (IUser) ret;
					saveUserPref(User);

				}
				// ret = 1;
				mye = null;
				// Simulate network access.
				// Thread.sleep(2000);

			} catch (JSONRPCException e) {

				mye = e;
				ret = -2;
				// throw new RuntimeException(e);

				// TODO Auto-generated catch block
				// e.printStackTrace();
			} catch (Exception e) {
				mye = e;
				ret = -2;
				// throw new RuntimeException(e);
			} finally {

			}

			// TODO: register the new account here.
			return (Integer) ret;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onProgressUpdate(Progress[])
		 */
		@Override
		protected void onProgressUpdate(Exception... e) {
			// TODO Auto-generated method stub
			// super.onProgressUpdate(i);
		}

		@Override
		protected void onPostExecute(final Integer success) {
			mAuthTask = null;
			showProgress(false);

			PostLogin(success, mye);

		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int menuId = item.getItemId();

		switch (menuId) {
		case R.id.action_settings:
			// Intent intent1 = new Intent();
			// intent1.setClass(this, SettingsActivity.class);
			// startActivity(intent1);

			break;

		case R.id.action_exit:
			finish();
			break;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	private Bitmap installShortCut() {
		Intent shortcutIntent = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");
		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME,
				getString(R.string.app_name));
		// 是否可以有多个快捷方式的副本，参数如果是true就可以生成多个快捷方式，如果是false就不会重复添加
		shortcutIntent.putExtra("duplicate", false);
		Intent mainIntent = new Intent(Intent.ACTION_MAIN);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

		// 要删除的应用程序的ComponentName，即应用程序包名+activity的名字
		// intent2.setComponent(new ComponentName(this.getPackageName(),
		// this.getPackageName() + ".MainActivity"));
		mainIntent.setClass(this, this.getClass());
		Bitmap bt = generatorContactCountIcon(((BitmapDrawable) (getResources()
				.getDrawable(R.drawable.ic_launcher))).getBitmap());
		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, mainIntent);
		// shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
		// Intent.ShortcutIconResource.fromContext(this, R.drawable.icon));
		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, bt);
		sendBroadcast(shortcutIntent);
		return bt;
	}

	private Bitmap generatorContactCountIcon(Bitmap icon) {
		// 初始化画布
		int iconSize = (int) getResources().getDimension(
				android.R.dimen.app_icon_size);
		Bitmap contactIcon = Bitmap.createBitmap(iconSize, iconSize,
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(contactIcon);

		// 拷贝图片
		Paint iconPaint = new Paint();
		iconPaint.setDither(true);// 防抖动
		iconPaint.setFilterBitmap(true);// 用来对Bitmap进行滤波处理，这样，当你选择Drawable时，会有抗锯齿的效果
		Rect src = new Rect(0, 0, icon.getWidth(), icon.getHeight());
		Rect dst = new Rect(0, 0, iconSize, iconSize);
		canvas.drawBitmap(icon, src, dst, iconPaint);

		// 在图片上创建一个覆盖的联系人个数
		int contacyCount = 11;
		// 启用抗锯齿和使用设备的文本字距
		Paint countPaint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.DEV_KERN_TEXT_FLAG);
		countPaint.setColor(Color.RED);
		countPaint.setTextSize(20f);
		countPaint.setTypeface(Typeface.DEFAULT_BOLD);
		canvas.drawText(String.valueOf(contacyCount), iconSize - 18, 25,
				countPaint);
		return contactIcon;
	}
}
