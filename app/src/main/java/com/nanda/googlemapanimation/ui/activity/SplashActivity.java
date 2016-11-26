package com.nanda.googlemapanimation.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import com.nanda.googlemapanimation.R;
import com.nanda.googlemapanimation.base.BaseActivity;

/**
 * Created by nandagopal on 11/26/16.
 */
public class SplashActivity extends BaseActivity {

  private Handler handler;
  private Runnable runnable = new Runnable() {
    @Override public void run() {
      startActivity(new Intent(SplashActivity.this, MapActivity.class));
    }
  };

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);
    handler = new Handler();

    handler.postDelayed(runnable, 2000);
  }
}
