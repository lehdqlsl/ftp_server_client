package com.sotolab.ftp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class select extends Activity {

	Button _btn_server;
	Button _btn_client;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select);

		_btn_server = (Button) findViewById(R.id.btn_server);
		_btn_client = (Button) findViewById(R.id.btn_client);

		_btn_server.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "Server",
						Toast.LENGTH_SHORT).show();
				Intent MainActivity = new Intent(select.this,
						MainActivity.class);
				startActivity(MainActivity);
			}
		});

		_btn_client.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "Client",
						Toast.LENGTH_SHORT).show();
				Intent ClientActivity = new Intent(select.this,
						ClientActivity.class);
				startActivity(ClientActivity);
			}
		});

	}
}
