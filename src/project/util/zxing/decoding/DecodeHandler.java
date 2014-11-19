/*
 * Copyright (C) 2010 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package project.util.zxing.decoding;

import com.thybase.R;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.util.Hashtable;

import project.util.MyLog;
import project.util.zxing.act.CaptureActivity;

import project.util.zxing.camera.CameraManager;
import project.util.zxing.camera.PlanarYUVLuminanceSource;

final class DecodeHandler extends Handler {

	private static final String TAG = DecodeHandler.class.getSimpleName();
	private BinaryBitmap bitmap;
	PlanarYUVLuminanceSource source;
	private final CaptureActivity activity;
	private final MultiFormatReader multiFormatReader;
	Result rawResult = null;
	private byte[] data;
	private Bitmap sbitmap;

	DecodeHandler(CaptureActivity activity,
			Hashtable<DecodeHintType, Object> hints) {
		multiFormatReader = new MultiFormatReader();
		multiFormatReader.setHints(hints);
		this.activity = activity;
	}

	@Override
	public void handleMessage(Message message) {
		if (message.what == R.id.get_preview) {
			if (source == null) {
				Toast.makeText(activity, "尚未DECODE", Toast.LENGTH_LONG).show();
				return;
			}
			sbitmap = source.renderCroppedGreyscaleBitmapAll();
			// sbitmap = source.renderCroppedGreyscaleBitmap();
			Message msg = Message.obtain(activity.getHandler(),
					R.id.get_preview_ret, "preview");
			Bundle bundle = new Bundle();
			bundle.putParcelable(DecodeThread.BARCODE_BITMAP, sbitmap);
			msg.setData(bundle);
			// Log.d(TAG, "Sending decode succeeded message...");
			msg.sendToTarget();
			source = null;
		} else if (message.what == R.id.decode) {
			// Log.d(TAG, "Got decode message");
			// data = (byte[]) message.obj;
			decode((byte[]) message.obj, message.arg1, message.arg2);
		} else if (message.what == R.id.quit) {
			Looper.myLooper().quit();
		}
	}

	/**
	 * Decode the data within the viewfinder rectangle, and time how long it
	 * took. For efficiency, reuse the same reader objects from one decode to
	 * the next.
	 * 
	 * @param data
	 *            The YUV preview frame.
	 * @param width
	 *            The width of the preview frame.
	 * @param height
	 *            The height of the preview frame.
	 */
	private void decode(byte[] data, int width, int height) {
		long start = System.currentTimeMillis();

		if (!CameraManager.isLandscape ) {
			byte[] rotatedData = new byte[data.length];

			MyLog.Assert(width>height, "decode width<height");
			int dw = width ;
			int dh = height;

			for (int y = 0; y < dh; y++) {

				for (int x = 0; x < dw; x++)
					rotatedData[x * dh + dh - y - 1] = data[x + y * dw];
			}
			MyLog.Log("rotated");
			source = CameraManager.get().buildLuminanceSource(rotatedData, height,
					width);
			// data = rotatedData;

		} else
			source = CameraManager.get().buildLuminanceSource(data, width,
					height);

		bitmap = new BinaryBitmap(new HybridBinarizer(source));
		
		try {
			rawResult = multiFormatReader.decodeWithState(bitmap);
		} catch (ReaderException re) {
			// continue
		} finally {
			multiFormatReader.reset();
		}

		if (rawResult != null) {
			long end = System.currentTimeMillis();
			Log.d(TAG, "Found barcode (" + (end - start) + " ms):\n"
					+ rawResult.toString());
			Message message = Message.obtain(activity.getHandler(),
					R.id.decode_succeeded, rawResult);
			Bundle bundle = new Bundle();
			bundle.putParcelable(DecodeThread.BARCODE_BITMAP,
					source.renderCroppedGreyscaleBitmap());
			message.setData(bundle);
			// Log.d(TAG, "Sending decode succeeded message...");
			message.sendToTarget();
		} else {
			Message message = Message.obtain(activity.getHandler(),
					R.id.decode_failed);
			message.sendToTarget();
		}
	}

}
