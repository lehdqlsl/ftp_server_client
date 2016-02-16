package com.sotolab.ftp;

import java.net.InetAddress;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ClientActivity extends Activity {
	EditText InputIP;
	EditText InputPort;
	Button btn_connect;
	InetAddress ipAddress = null;
	TextViewRunnable runnable;
	next nextrun;
	Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		InputIP = (EditText) findViewById(R.id.editIP);
		btn_connect = (Button) findViewById(R.id.connect);
		runnable = new TextViewRunnable();
		nextrun = new next();
		handler = new Handler();

		btn_connect.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Thread Connect = new Thread(new Runnable() {
					public void run() {

						String str = InputIP.getText().toString();

						final FTPConnector ftpc = new FTPConnector(str, 2121,
								"wmlab", "wmlab", "UTF-8", "/");

						if (true == ftpc.login()) {
							handler.post(nextrun);
							Intent DisplayActivity = new Intent(
									ClientActivity.this, DisplayActivity.class);

							startActivity(DisplayActivity);
						} else {
							handler.post(runnable);
						}
					}
				});
				Connect.start();
			}
		});
	}
	public class TextViewRunnable implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Toast.makeText(getApplicationContext(), "연결 실패", Toast.LENGTH_LONG)
					.show();
		}

	}

	public class next implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Toast.makeText(getApplicationContext(), "연결 성공", Toast.LENGTH_LONG)
					.show();
		}
	}
}
