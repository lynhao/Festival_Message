package com.demo.linhao.festival_sms.sceane;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.demo.linhao.festival_sms.R;

public class SplashScreenActivity extends AppCompatActivity{

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //thread for splash screen running
        Thread screenDisplayTimer = new Thread(){
        	public void run(){
        		try {
					sleep(2000);
				} catch (InterruptedException e) {
				}finally{
					startActivity(new Intent(SplashScreenActivity.this, IntroScreenActivity.class));
					overridePendingTransition(R.anim.fade, R.anim.hold);

				}
        		finish();
        	}
        };
        screenDisplayTimer.start();
	}


}
