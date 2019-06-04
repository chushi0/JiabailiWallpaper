package online.cszt0.jiabailiwallpaper;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public class Actor {
	private static final int FRAME = 120;
	private static final float START_ANGLE = 50;
	private static final float END_ANGLE = -50;
	private static final float ANGLE_PER_FRAME = (END_ANGLE - START_ANGLE) / FRAME;
	private static final float CENTER_X = 500;
	private static final float CENTER_Y = 1100;
	private static final float RADIUS = 700;

	private int startFrame;

	public Actor(int startFrame) {
		this.startFrame = startFrame;
	}

	/**
	 * 绘制角色帧
	 * 
	 * @param canvas
	 *            画布
	 * @param actorBitmap
	 *            角色帧图
	 * @param paint
	 *            画笔
	 * @param frame
	 *            当前帧
	 */
	public void draw(Canvas canvas, Bitmap actorBitmap, Paint paint, int frame) {
		canvas.save();
		int updateFrame = frame - startFrame;
		canvas.rotate(updateFrame * ANGLE_PER_FRAME + START_ANGLE, CENTER_X, CENTER_Y);
		int srcIndex = updateFrame % 10;
		Rect actorSrc = new Rect(srcIndex * 250, 0, srcIndex * 250 + 250, 400);
		RectF actorDst = new RectF(500 - 63, 300, 500 + 63, 500);
		canvas.drawBitmap(actorBitmap, actorSrc, actorDst, paint);
		canvas.restore();
	}

	public boolean isInScreen(int frame) {
		return frame - startFrame < FRAME;
	}
}
