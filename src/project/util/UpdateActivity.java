package project.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import project.config.Config;
import project.ui.ProjectActivity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

public class UpdateActivity extends ProjectActivity {
	private static final String TAG = "Update";
	public ProgressDialog pBar;
	private Handler handler = new Handler();
	private int silence;
	private int newVerCode = 0;
	private String newVerName = "";

	public static void ShowUpgrade(Context p, int silence) {

		Intent intent2 = new Intent();
		intent2.putExtra("silence", silence);
		intent2.setClass(p, project.util.UpdateActivity.class);
		p.startActivity(intent2);

	}

	private class myHandler extends Handler {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.Handler#handleMessage(android.os.Message)
		 */
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == 100) {
				if ((Boolean) msg.obj == true) {
					int vercode = Config.getVerCode(getBaseContext());
					String vername = Config.getVerName(getBaseContext());
					if (newVerCode > vercode) {
						doNewVersionUpdate();
					} else {
						if (silence == 0)
							notNewVersionShow();
						else
							finish();
					}

				} else
					finish();
			} else
				finish();
		}

	}

	myHandler myhand = new myHandler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.app_update_act);
		this.setVisible(false);

		silence = this.getIntent().getIntExtra("silence", 0);
		Thread myThread = new Thread() {
			public void run() {
				Looper.prepare();
				boolean code = getServerVerCode();

				Message msg = new Message();
				msg.obj = code;
				msg.what = 100;
				myhand.sendMessage(msg);
				//Looper.loop();//thy-todo why?

			}
		};
		myThread.start();

	}

	/**
	 * @return
	 */
	private myHandler myHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	private boolean getServerVerCode() {
		String verjson="";
		try {
			 verjson = NetworkTool.getContent(Config.UPDATE_SERVER
					+ Config.UPDATE_VERJSON);
			JSONObject obj = new JSONObject(verjson);

			try {
				newVerCode = Integer.parseInt(obj.getString("versionCode"));
				newVerName = obj.getString("versionName");
			} catch (Exception e) {
				newVerCode = -1;
				newVerName = "";
				return false;
			}

		} catch (IOException e) {
			Toast.makeText(thisAct, "请检查升级网络连接："+Config.UPDATE_SERVER, Toast.LENGTH_LONG).show();
			newVerCode = -1;
			newVerName = "";
			// Log.e(TAG, e.getMessage());
			return false;
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			Toast.makeText(thisAct, "版本文件错误："+verjson==null?"":verjson, Toast.LENGTH_LONG).show();
			newVerCode = -1;
			newVerName = "";
		}
		return true;
	}

	private void notNewVersionShow() {
		int verCode = Config.getVerCode(this);
		String verName = Config.getVerName(this);
		StringBuffer sb = new StringBuffer();
		sb.append("当前版本:");
		sb.append(verName);
		sb.append(" Code:");
		sb.append(verCode);
		sb.append(",\n已是最新版,无需更新!");
		Dialog dialog = new AlertDialog.Builder(UpdateActivity.this)
				.setTitle("软件更新").setMessage(sb.toString())// 设置内容
				.setPositiveButton("确定",// 设置确定按钮
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								finish();
							}

						}).create();// 创建
		// 显示对话框
		dialog.show();
	}

	private void doNewVersionUpdate() {
		int verCode = Config.getVerCode(this);
		String verName = Config.getVerName(this);
		StringBuffer sb = new StringBuffer();
		sb.append("当前版本:");
		sb.append(verName);
		sb.append(" Code:");
		sb.append(verCode);
		sb.append(", 发现新版本:");
		sb.append(newVerName);
		sb.append(" Code:");
		sb.append(newVerCode);
		sb.append(", 是否更新?");
		Dialog dialog = new AlertDialog.Builder(UpdateActivity.this)
				.setTitle("软件更新")
				.setMessage(sb.toString())
				// 设置内容
				.setPositiveButton("更新",// 设置确定按钮
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								pBar = new ProgressDialog(UpdateActivity.this);
								pBar.setTitle("正在下载");
								pBar.setMessage("请稍候...");
								pBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
								downFile(Config.UPDATE_SERVER
										+ Config.UPDATE_APKNAME);
							}

						})
				.setNegativeButton("暂不更新",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// 点击"取消"按钮之后退出程序
								finish();
							}
						}).create();// 创建
		// 显示对话框
		dialog.show();
	}

	void downFile(final String url) {
		pBar.show();
		new Thread() {
			public void run() {
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(url);
				HttpResponse response;
				int count = 0;
				try {
					response = client.execute(get);
					HttpEntity entity = response.getEntity();
					long length = entity.getContentLength();
					InputStream is = entity.getContent();
					FileOutputStream fileOutputStream = null;
					if (is != null) {
						count = 0;
						File file = new File(
								// Environment.getDownloadCacheDirectory(),
								Environment.getExternalStorageDirectory(),
								Config.UPDATE_SAVENAME);
						fileOutputStream = new FileOutputStream(file);

						byte[] buf = new byte[1024];
						int ch = -1;

						while ((ch = is.read(buf)) != -1) {
							fileOutputStream.write(buf, 0, ch);
							count += ch;
							if (length > 0) {
							}
						}

					}
					fileOutputStream.flush();
					if (fileOutputStream != null) {
						fileOutputStream.close();
					}
					down();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}.start();

	}

	void down() {
		handler.post(new Runnable() {
			public void run() {
				pBar.cancel();
				update();
			}
		});

	}

	File getFileName() {

		File dir = getSaveDir();
		// Environment.getDownloadCacheDirectory(),
		File rfile = new File(dir, Config.UPDATE_SAVENAME);
		return rfile;
	}

	File getSaveDir() {
		File dir = Environment.getExternalStorageDirectory();
		return dir;
	}

	Uri getUri() {

		File dir = getSaveDir();
		// Environment.getDownloadCacheDirectory(),
		Uri r = Uri.parse("file://" + Environment.getExternalStorageDirectory()
				+ "/" + Config.UPDATE_SAVENAME);
		return r;
	}

	void update() {

		Intent intent = new Intent(Intent.ACTION_VIEW);

		Uri install = getUri();
		intent.setDataAndType(install,
				"application/vnd.android.package-archive");
		startActivity(intent);
		finish();
	}

}