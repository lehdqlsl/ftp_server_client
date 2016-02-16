package com.sotolab.ftp;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.TextView;

public class Slash extends Activity {

	private TextView _Version;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// overridePendingTransition(R.anim.slide_out_left,R.anim.fade);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_slash);

		Handler handler = new Handler();
		handler.postDelayed(new splashHandler(), 1350);
		_Version = (TextView) findViewById(R.id.txt_version);

		PackageInfo pi = null;

		try {
			pi = getPackageManager().getPackageInfo(getPackageName(), 0);
			String ver = pi.versionName;
			_Version.setText("Ver : " + ver);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private class splashHandler implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			startActivity(new Intent(getApplication(), FTPmain.class));
			overridePendingTransition(R.anim.slide_in_right, R.anim.fade);
			Slash.this.finish();
		}
	}
}
