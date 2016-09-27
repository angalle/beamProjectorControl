package com.example.smartremotecontrol;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

public class Splash extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
        
		 Handler handler = new Handler();
	        handler.postDelayed(new Runnable() {
	         
	         @Override
	         public void run() {
	          finish();
	         }
	     }, 3000); // 3초동안 로딩이미지가 있는 activity가 실행됨.	      
	}	
}
