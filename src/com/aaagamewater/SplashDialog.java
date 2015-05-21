package com.aaagamewater;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

public class SplashDialog extends Dialog {
	public SplashDialog(Context context) {
		super(context, R.style.Dialog_Fullscreen);
	}

	ImageView imageView;
	Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		imageView = (ImageView) findViewById(R.id.iv_adimg);
		AlphaAnimation alphaAnimation2 = new AlphaAnimation((float) 1,
				(float) 0);
		alphaAnimation2.setDuration(1500);// 设定动画时间
		alphaAnimation2.setAnimationListener(new AnimationListener() {
			// 实现抽象方法
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) { // 动画结束的动作，跳转
				handler.sendEmptyMessage(0);
			}
		});
		imageView.setAnimation(alphaAnimation2); // 启动动画
		imageView.setVisibility(View.VISIBLE);
		
		
		
		handler = new Handler(new Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				imageView.setVisibility(View.INVISIBLE);
				dismiss();
				return false;
			}
		});
	}
	
}
