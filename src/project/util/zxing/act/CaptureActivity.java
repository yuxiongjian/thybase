package project.util.zxing.act;

import java.io.IOException;
import java.util.Vector;



import project.ui.ProjectActivity;
import project.util.zxing.camera.CameraManager;
import project.util.zxing.decoding.CaptureActivityHandler;
import project.util.zxing.decoding.InactivityTimer;
import project.util.zxing.view.ViewfinderView;
import com.thybase.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

public class CaptureActivity extends ProjectActivity implements Callback {

	/**
	 * 
	 */
	public static final long serialVersionUID = CaptureActivity.class
			.hashCode();
	private CaptureActivityHandler handler;

	/*
	 * (non-Javadoc)
	 * 
	 * @see project.ui.ProjectActivity#onLongPress(android.view.MotionEvent)
	 */
	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		super.onLongPress(e);
		Message msg = null;
		if( handler!=null ) {
			msg = handler.obtainMessage(R.id.get_preview);
			msg.sendToTarget();
		}
	}

	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;
	private CaptureActivity thisAct;
	private String testbarcode = "";
	private Intent mIntent;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.capture_barcode);
		/*
		 * if( CameraManager.isLandscape )
		 * setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		 * else
		 * setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		 */
		thisAct = this;
		mIntent = this.getIntent();
		CameraManager.init(getApplication());
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		hasSurface = false;

		inactivityTimer = new InactivityTimer(this);

	}

	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
		setResult(Activity.RESULT_CANCELED, getIntent());
		// thisAct.finish();
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (Exception ioe) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats,
					characterSet);
		}
		/*
		 * if ( Handphone.isVMDebug()){ testbarcode =
		 * getIntent().getStringExtra("testbarcode"); //Drawable bt =
		 * mResource.getDrawable(R.drawable.camera); Result r = new
		 * Result(testbarcode,null,null,null); Bitmap icon =
		 * BitmapFactory.decodeResource(mResource,R.drawable.camera);
		 * 
		 * //Message mg = handler.obtainMessage(); Message mg =
		 * Message.obtain(getHandler(), R.id.decode_succeeded, r);
		 * 
		 * Bundle bundle = new Bundle(); mg.obj = r;
		 * mg.what=R.id.decode_succeeded;
		 * bundle.putParcelable(DecodeThread.BARCODE_BITMAP, icon);
		 * mg.setData(bundle); //handler.sendMessage(mg); mg.sendToTarget();
		 * 
		 * // Bundle bundle = message.getData(); // Bitmap barcode = bundle ==
		 * null ? null : // (Bitmap)
		 * bundle.getParcelable(DecodeThread.BARCODE_BITMAP); //
		 * activity.handleDecode((Result) message.obj, barcode);
		 * //this.handleDecode(r, icon); }
		 */

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

		//CameraManager.setCameraDisplayOrientation(thisAct);

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();

	}

	public void handleViewBmp(final String obj, Bitmap picture) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.showbmp,
				(ViewGroup) findViewById(R.id.dialog));
		ImageView bpmv = (ImageView)layout.findViewById(R.id.showbmp);
		
		AlertDialog.Builder dialog =new AlertDialog.Builder(this)
				.setView(layout);//.setNegativeButton("取消", null)	;

		
		//Drawable drawable = new BitmapDrawable(picture);
		bpmv.setImageBitmap(picture);
		//bpmv.setBackground(drawable);
		dialog.setTitle("拍照");
		dialog.setMessage(obj);

		dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = mIntent;
				intent.putExtra("barcode", obj.toString());
				setResult(Activity.RESULT_OK, intent);
				thisAct.finish();
			}
		});
		dialog.setOnCancelListener(new OnCancelListener() {  
			  
            

			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				Message message = Message.obtain(getHandler(),
    					R.id.decode_failed);
    			message.sendToTarget();
               
				
			}  
        });  
		
		dialog.create().show();
	}

	public void handleDecode(final String obj, Bitmap barcode) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		if (barcode == null) {
			dialog.setIcon(null);
		} else {

			Drawable drawable = new BitmapDrawable(barcode);
			dialog.setIcon(drawable);
		}
		dialog.setTitle("机器条码");
		dialog.setMessage(obj);

		dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = mIntent;
				intent.putExtra("barcode", obj.toString());
				setResult(Activity.RESULT_OK, intent);
				thisAct.finish();
			}
		});
		
		dialog.setNegativeButton("返回", new DialogInterface.OnClickListener() {  
			  
            @Override  
            public void onClick(DialogInterface dialog, int which) {  
            	Message message = Message.obtain(getHandler(),
    					R.id.decode_failed);
    			message.sendToTarget();
                dialog.cancel();  
  
            }  
        });  
		dialog.create().show();
	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

}