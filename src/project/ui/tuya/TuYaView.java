package project.ui.tuya;

import project.util.MyLog;
import hardware.config.Handphone;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class TuYaView extends View {
	public final static int  INITVIEW=4,ONMOVE=1,GETIMAGE=2;
	private Paint paint = null;
	public Bitmap originalBitmap = null;
	private Bitmap new1Bitmap = null;
	private Bitmap new2Bitmap = null;
	private float clickX = 0, clickY = 0;
	private float startX = 0, startY = 0;
	private boolean isMove = true;
	private boolean isClear = false;
	private int color = Color.BLACK;
	private float strokeWidth = 10.0f;
	private Handler outHandle;
	static public int h = 800;
	static public int w = 800;

	public TuYaView(Context context, AttributeSet attrs, Handler hd) {
		super(context, attrs);
		this.outHandle = hd;
		w = Handphone.handphone.outSize.right;
		// initBmp(w, h);
		
		Log.i("RG", "new1Bitmap--->>>" + new1Bitmap);
	}

	public void initBmp(int wi, int hi) {
		if( originalBitmap==null)
			originalBitmap = Bitmap.createBitmap(wi, hi, Config.ARGB_8888);
		new1Bitmap = Bitmap.createBitmap(originalBitmap);

		setDrawingCacheEnabled(true);
	}

	public void clear() {
		isClear = true;
		new2Bitmap = Bitmap.createBitmap(originalBitmap);
		invalidate();
	}

	Bitmap saveImage = null;

	public Bitmap saveImage() {
		if (saveImage == null) {
			return null;
		}
		return saveImage;
	}

	public void clearImge() {
		saveImage = null;
	}

	public void setstyle(float strokeWidth) {
		this.strokeWidth = strokeWidth;
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawBitmap(HandWriting(new1Bitmap), 0, 0, null);

		

	}

	public Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			TuYaView tyv = null;
			int what = msg.what;
			if (msg.obj.getClass().equals(TuYaView.class))
				tyv = (TuYaView) msg.obj;
			switch (what) {
			case ONMOVE:
				MyLog.Assert(tyv != null, "");
				tyv.startX = tyv.clickX;
				tyv.startY = tyv.clickY;
				break;

			case GETIMAGE:
				MyLog.Assert(tyv != null, "");
				tyv.saveImage = Bitmap.createBitmap(tyv
						.HandWriting(tyv.new1Bitmap));
				// Message msg1 = new Message();
				// msg1 = Message.obtain(TuYaActivity.getHh(), 3);
				// TuYaActivity.getHh().sendMessage(msg1);
				sendMsgToActivity(3);

				break;
			case INITVIEW:
				w = (msg.arg1);

				h = (msg.arg2);
				tyv.initBmp(w, h);

				break;
			}

			super.handleMessage(msg);
		}

	};

	public void sendMsgToActivity(int msgid) {

		Message msg = new Message();
		// msg1 = Message.obtain(TuYaActivity.getHh(), 3);
		// TuYaActivity.getHh().sendMessage(msg1);
		
		msg = Message.obtain(outHandle, msgid);
		msg.sendToTarget();

	}

	public Bitmap HandWriting(Bitmap originalBitmap) {

		
		Canvas canvas = null;

		if (isClear) {
			canvas = new Canvas(new2Bitmap);
			Log.i("RG", "canvas ");
		} else {
			canvas = new Canvas(originalBitmap);
		}
		paint = new Paint();
		paint.setStyle(Style.STROKE);
		paint.setAntiAlias(true);
		paint.setColor(color);
		paint.setStrokeWidth(strokeWidth);
		Log.i("RG", "startX-->>>>" + startX);
		Log.i("RG", "startY-->>>>" + startY);
		if (isMove && !(startX == clickX && startY == clickY)) {
			canvas.drawLine(startX, startY, clickX, clickY, paint);

			// Message msg1 = new Message();
			// msg1 = Message.obtain(TuYaActivity.getHh(), 4);
			// TuYaActivity.getHh().sendMessage(msg1);

			sendMsgToActivity(4);
		}

		if (isClear) {
			return new2Bitmap;
		}

		return originalBitmap;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Message msg = new Message();
		msg = Message.obtain(handler, ONMOVE);
		msg.obj = this;

		handler.sendMessage(msg);
		clickX = event.getX();
		clickY = event.getY();
		if (event.getAction() == MotionEvent.ACTION_DOWN) {

			isMove = false;
			invalidate();
			return true;
		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {

			isMove = true;
			invalidate();
			return true;
		}

		return super.onTouchEvent(event);
	}

}
