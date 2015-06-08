package com.example.fuseaway;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class DrawingView extends View {

	private final PorterDuffXfermode clearMode = new PorterDuffXfermode(
			PorterDuff.Mode.CLEAR);

	private ArrayList<Path> drawPaths;
	private LinkedList<PointF> pathPoints;
	private HashMap<Path, Paint> paintMap;
	private HashMap<PointF, Long> pointTimes;
	private Path drawPath;
	private Paint drawPaint, canvasPaint, pastPaint;
	private int paintColor = Color.BLACK;
	private Canvas drawCanvas, lastCanvas;
	private Bitmap canvasBitmap, lastBitmap;
	private boolean isColorSelected = true;
	private boolean isErasing = false;
	private PointF lastPoint = null;
	private boolean dontMove = false;

	public DrawingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (android.os.Build.VERSION.SDK_INT >= 11) {
			setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
		setupDrawing();
	}

	private void setupDrawing() {
		drawPaths = new ArrayList<Path>();
		pathPoints = new LinkedList<PointF>();
		paintMap = new HashMap<Path, Paint>();
		pointTimes = new HashMap<PointF, Long>();
		drawPath = new Path();
		drawPaint = new Paint();
		drawPaint.setColor(paintColor);
		drawPaint.setAntiAlias(true);
		drawPaint.setStrokeWidth(20);
		drawPaint.setStyle(Paint.Style.STROKE);
		drawPaint.setStrokeJoin(Paint.Join.ROUND);
		drawPaint.setStrokeCap(Paint.Cap.ROUND);
		pastPaint = drawPaint;
		canvasPaint = new Paint(Paint.DITHER_FLAG);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// view given size
		super.onSizeChanged(w, h, oldw, oldh);
		// canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		// drawCanvas = new Canvas(canvasBitmap);
		lastBitmap = Bitmap.createScaledBitmap(canvasBitmap, w, h, true);
		drawCanvas = new Canvas(lastBitmap);
	}

	public void drawOnBackground(Bitmap bitmap) {
		Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), bitmap.getConfig());
		Canvas canvas = new Canvas(bitmap);
		draw(canvas);
		// Paint p = new Paint();
		// p.setAlpha(255);
		// canvas.drawBitmap(lastBitmap, 0f, 0f, p);
		/*
		 * for (Path path : drawPaths) { pastPaint = (paintMap.get(path)); if
		 * (paintMap.get(path) == Color.WHITE) {
		 * pastPaint.setXfermode(clearMode); // pastPaint.setXfermode(null); //
		 * pastPaint.setColor(1); } else { pastPaint.setXfermode(null); }
		 * canvas.drawPath(path, pastPaint); // path.reset(); }
		 */

	}

	public void setBitmap(Bitmap bitmap) {
		canvasBitmap = bitmap;
	}

	public Bitmap getBitmap() {
		return canvasBitmap;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// draw view
		// lastCanvas = canvas;
		long currTime = System.currentTimeMillis();
		canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
		// canvas.drawPath(drawPath, drawPaint);
		PointF headPoint = pathPoints.peekFirst();
		if (headPoint != null) {
			drawPath.reset();
			drawPath.moveTo(headPoint.x, headPoint.y);
			Iterator<PointF> it = pathPoints.iterator();

			while (it.hasNext()) {
				PointF point = it.next();
				Long pointTime = pointTimes.get(point);
				if (pointTime != null && currTime - pointTime > 2000) {
					pointTimes.remove(point);
					it.remove();
				} else {
					drawPath.lineTo(point.x, point.y);
				}
			}
		}
		for (Path path : drawPaths) {
			pastPaint = (paintMap.get(path));
			/*
			 * if (paintMap.get(path) == Color.WHITE) {
			 * pastPaint.setXfermode(clearMode); // pastPaint.setXfermode(null);
			 * // pastPaint.setColor(1); } else { pastPaint.setXfermode(null); }
			 */
			canvas.drawPath(path, pastPaint);
			// path.reset();
		}
		if (!isErasing) {
			// drawPaint.setColor(paintColor);
			// drawPaint.setXfermode(null);
			canvas.drawPath(drawPath, drawPaint);
			// drawPath.reset();
		} else {
			// drawPaint.setColor(paintColor);
			// drawPaint.setXfermode(clearMode);
			// drawPaint.setXfermode(null);
			canvas.drawPath(drawPath, drawPaint);
			// drawPath.reset();
		}
		postInvalidate();
	}

	public Canvas getCanvas() {
		return drawCanvas;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (isColorSelected) {
			long currTime = System.currentTimeMillis();
			// detect user touch
			float touchX = event.getX();
			float touchY = event.getY();
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				// lastCanvas.drawBitmap(canvasBitmap, 0, 0, null);
				// drawPath.reset();
				// pathPoints.clear();
				float distance = 0;
				if (lastPoint != null) {
					distance = calcDistSquared(lastPoint, (new PointF(touchX,
							touchY)));
				}
				Log.d("DIST", "" + distance);
				if (distance < 4000) {
					dontMove = false;
					pathPoints.addLast(new PointF(touchX, touchY));
					pointTimes.put(new PointF(touchX, touchY), currTime);
					lastPoint = new PointF(touchX, touchY);
				}
				// drawPath.moveTo(touchX, touchY);
				break;
			case MotionEvent.ACTION_MOVE:
				if (!dontMove) {
					// drawPath.lineTo(touchX, touchY);
					pathPoints.addLast(new PointF(touchX, touchY));
					pointTimes.put(new PointF(touchX, touchY), currTime);
					lastPoint = new PointF(touchX, touchY);
					if (pathPoints.size() > 50) {
						PointF removedPoint = pathPoints.remove();
						pointTimes.remove(removedPoint);
					}
				}
				break;
			case MotionEvent.ACTION_UP:
				dontMove = true;
				if (isErasing) {
					drawPaint.setXfermode(clearMode);
				} else {
					drawPaint.setXfermode(null);
				}
				// drawCanvas.drawPath(drawPath, drawPaint);
				// drawPath.reset();
				// drawPaths.add(drawPath);
				// paintMap.put(drawPath, drawPaint);

				// drawPath.reset();
				// invalidate();
				// drawPath = new Path();
				Paint tempPaint = drawPaint;
				drawPaint = new Paint(tempPaint);
				break;
			default:
				return false;
			}
			invalidate();
		}
		return true;
	}

	public float calcDistSquared(PointF a, PointF b) {
		float xDist = a.x - b.x;
		float yDist = a.y - b.y;
		return (xDist * xDist + yDist * yDist);
	}

	public void setColor(String newColor) {
		invalidate();
		isErasing = false;
		isColorSelected = true;
		drawPaint.setXfermode(null);
		paintColor = Color.parseColor(newColor);
		drawPaint.setColor(paintColor);
	}

	public void setErase() {
		invalidate();
		isErasing = true;
		isColorSelected = true;
		paintColor = Color.WHITE;
		drawPaint.setXfermode(clearMode);
	}

	public void clearCanvas() {
		drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
		drawPaths = new ArrayList<Path>();
		pathPoints = new LinkedList<PointF>();
		invalidate();
	}

	public void noColor() {
		isColorSelected = false;
		isErasing = false;
	}

	public void undo() {
		if (drawPaths.size() > 0) {
			drawPaths.remove(drawPaths.size() - 1);
			invalidate();
		}
		// canvasBitmap = lastBitmap;
	}
}