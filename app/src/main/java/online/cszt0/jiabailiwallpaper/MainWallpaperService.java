package online.cszt0.jiabailiwallpaper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.SystemClock;
import android.service.wallpaper.WallpaperService;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.WindowManager;

public final class MainWallpaperService extends WallpaperService {
	@Override
	public Engine onCreateEngine() {
		return new WallpaperEngine();
	}
	class WallpaperEngine extends Engine implements Runnable {
		private Handler handler;
		private boolean run;
		private Paint paint;
		private float offset;
		private Bitmap background;
		private Bitmap actor;
		private long startTime;
		private ArrayList<Actor> actors;
		private int lastAddActorFrame;

		@Override
		public void onCreate(SurfaceHolder surfaceHolder) {
			super.onCreate(surfaceHolder);
			handler = new Handler();
			paint = new Paint();
			paint.setAntiAlias(true);
			actors = new ArrayList<>();
			AssetManager assetManager = getAssets();
			try {
				background = BitmapFactory.decodeStream(assetManager.open("background.png"));
				actor = BitmapFactory.decodeStream(assetManager.open("actor.png"));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public void onVisibilityChanged(boolean visible) {
			super.onVisibilityChanged(visible);
			run = visible;
			if (visible) {
				startTime = SystemClock.currentThreadTimeMillis();
				handler.post(this);
				actors.add(new Actor(0));
			} else {
				actors.clear();
				lastAddActorFrame = 0;
			}
		}

		@Override
		public void run() {
			if (!run) {
				return;
			}
			handler.post(this);
			SurfaceHolder surfaceHolder = getSurfaceHolder();
			// 当前帧
			int updateFrameNumber = (int) ((SystemClock.currentThreadTimeMillis() - startTime) / 20);
			// 刷新角色信息
			if (updateFrameNumber - lastAddActorFrame > 30) {
				actors.add(new Actor(updateFrameNumber));
				lastAddActorFrame = updateFrameNumber;
			}
			Iterator<Actor> actorIterator = actors.iterator();
			while (actorIterator.hasNext()) {
				Actor actor = actorIterator.next();
				if (!actor.isInScreen(updateFrameNumber)) {
					actorIterator.remove();
				}
			}
			// 屏幕宽高
			DisplayMetrics displayMetrics = new DisplayMetrics();
			((WindowManager) Objects.requireNonNull(getSystemService(WINDOW_SERVICE))).getDefaultDisplay()
					.getRealMetrics(displayMetrics);
			int width = displayMetrics.widthPixels;
			int height = displayMetrics.heightPixels;
			// 背景图宽高
			int backgroundWidth = background.getWidth();
			int backgroundHeight = background.getHeight();
			// 按照高度缩放背景图大小
			float scale = (float) height / (float) backgroundHeight;
			// 缩放后图片宽度
			float scaledWidth = backgroundWidth * scale;
			Canvas canvas = surfaceHolder.lockCanvas();
			// 根据滑动位置平移画布及缩放大小
			canvas.translate(-(scaledWidth - width) * offset, 0);
			canvas.scale(scale, scale);
			// 绘制背景
			Rect src = new Rect(0, 0, backgroundWidth, backgroundHeight);
			RectF dst = new RectF(0, 0, backgroundWidth, backgroundHeight);
			canvas.drawBitmap(background, src, dst, paint);
			// 绘制角色
			for (Actor actor : actors) {
				actor.draw(canvas, this.actor, paint, updateFrameNumber);
			}
			// 提交画布内容
			surfaceHolder.unlockCanvasAndPost(canvas);
		}

		@Override
		public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep,
				int xPixelOffset, int yPixelOffset) {
			super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep, xPixelOffset, yPixelOffset);
			offset = xOffset;
		}

		@Override
		public void onDestroy() {
			super.onDestroy();
			background.recycle();
			actor.recycle();
		}
	}
}
