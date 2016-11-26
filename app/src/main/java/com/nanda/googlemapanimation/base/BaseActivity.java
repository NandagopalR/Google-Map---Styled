package com.nanda.googlemapanimation.base;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;

public class BaseActivity extends AppCompatActivity {

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  protected void setStatusBarColor(int resId) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Window window = getWindow();
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      window.setStatusBarColor(ContextCompat.getColor(this, resId));
    }
  }

  protected void setUpToolBar(Toolbar toolBar) {
    setSupportActionBar(toolBar);
  }

  protected void setToolBarColor(Toolbar toolBarColor, int resId) {
    toolBarColor.setBackgroundResource(resId);
  }

  protected void setToolBarTitle(String title) {
    if (getSupportActionBar() != null) getSupportActionBar().setTitle(title);
  }

  protected void showBackButton(boolean show) {
    if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(show);
  }

  @Override public boolean onSupportNavigateUp() {
    onBackPressed();
    return true;
  }
}
