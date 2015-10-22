package com.heat.and.longdistance;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.View;
import android.view.WindowManager;

import java.util.Random;

public class ScaleGestureView extends View {
	private ScaleGestureDetector _scaleGestureDetector;
	private float _dpi;
	private int _span;
	private float _focusX, _focusY;

	Paint _paint = new Paint();
	SmoothColor _smoothColor = new SmoothColor();

	public ScaleGestureView(Context context) {
		super(context);
		_scaleGestureDetector = new ScaleGestureDetector(context, new MySimpleOnScaleGestureListener());

		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(metrics);
		_dpi = metrics.ydpi;
		Log.v("xdpi（x軸の1インチあたりのピクセル数）:", String.valueOf(metrics.xdpi));
		Log.v("ydpi（y軸の1インチあたりのピクセル数）:", String.valueOf(metrics.ydpi));
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		_scaleGestureDetector.onTouchEvent(e);
		return true;
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		_paint.setAntiAlias(true);
		_paint.setTextSize(40);

		_smoothColor.setNextColor(_paint);
		canvas.drawCircle(_focusX, _focusY, _span * 5, _paint);
		_paint.setColor(Color.BLACK);
		canvas.drawText("<- " + _span + "mm" + " ->", _focusX, _focusY, _paint);
	}

	private class MySimpleOnScaleGestureListener extends SimpleOnScaleGestureListener {

		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			final float INCH_TO_MILIMETOR = 25.4f;

			Log.v("onScale", "CurrentSpan(px): " + String.valueOf(detector.getCurrentSpan()));
			Log.v("onScale", "CurrentSpan(mm): " + String.valueOf(detector.getCurrentSpan() / _dpi * INCH_TO_MILIMETOR));
			_span = (int) (detector.getCurrentSpan() / _dpi * INCH_TO_MILIMETOR);
			_focusX = detector.getFocusX();
			_focusY = detector.getFocusY();
			invalidate();
			return true;
		}

		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector) {
			return true;
		}

		@Override
		public void onScaleEnd(ScaleGestureDetector detector) {
		}
	}

	class SmoothColor {
		private int _r, _g, _b;
		private boolean _isRedIncrement;
		private boolean _isGreenIncrement;
		private boolean _isBlueIncrement;

		public SmoothColor() {
			Random rand = new Random();
			_r = rand.nextInt(255);
			_g = rand.nextInt(255);
			_b = rand.nextInt(255);
			_isRedIncrement = rand.nextBoolean();
			_isGreenIncrement = rand.nextBoolean();
			_isBlueIncrement = rand.nextBoolean();
		}

		public void setNextColor(Paint paint) {
			int newR = (_isRedIncrement) ? _r + 1 : _r - 1;
			int newG = (_isGreenIncrement) ? _g + 1 : _g - 1;
			int newB = (_isBlueIncrement) ? _b + 1 : _b - 1;

			if (isInColorRange(newR))
				_r = newR;
			else
				_isRedIncrement = !_isRedIncrement;

			if (isInColorRange(newG))
				_g = newG;
			else
				_isGreenIncrement = !_isGreenIncrement;

			if (isInColorRange(newB))
				_b = newB;
			else
				_isBlueIncrement = !_isBlueIncrement;

			paint.setColor(Color.rgb(_r, _g, _b));
		}

		private boolean isInColorRange(int color) {
			return color <= 255 && color >= 0;
		}
	}
}
