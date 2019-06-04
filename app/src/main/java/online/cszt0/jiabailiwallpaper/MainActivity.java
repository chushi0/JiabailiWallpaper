package online.cszt0.jiabailiwallpaper;

import java.util.Objects;

import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

	TextView textView;
	Button button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		textView = findViewById(R.id.text);
		button = findViewById(R.id.button);
		button.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(Intent.ACTION_SET_WALLPAPER);
		startActivity(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();
		WallpaperManager wallpaperManager = (WallpaperManager) Objects
				.requireNonNull(getSystemService(WALLPAPER_SERVICE));
		WallpaperInfo info = wallpaperManager.getWallpaperInfo();
		if (info != null && info.getPackageName().equals(getPackageName())) {
			textView.setText(R.string.ui_success);
			textView.setTextColor(getColor(R.color.colorGreen));
			button.setVisibility(View.GONE);
		} else {
			textView.setText(R.string.ui_fail);
			textView.setTextColor(getColor(R.color.colorRed));
			button.setVisibility(View.VISIBLE);
		}
	}
}
