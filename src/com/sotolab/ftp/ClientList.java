package com.sotolab.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTP;

import com.sotolab.ftp.R;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ClientList extends ListFragment {

	ListView lv;
	private List<String> item = null;
	private List<String> path = null;
	private String root = "/";
	private TextView mPath;
	DialogCreate dialogCreate;
	DialogDismiss dialogDismis;
	ProgressDialog progressDialog;
	FailMsg failmsg;
	FileInputStream fis = null;
	Handler handler;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.client_list, container, false);
		lv = (ListView) v.findViewById(android.R.id.list);
		mPath = (TextView) v.findViewById(R.id.path);

		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		dialogCreate = new DialogCreate();
		dialogDismis = new DialogDismiss();
		failmsg = new FailMsg();
		handler = new Handler();
		getDir(root);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub

		super.onStart();
	}

	public void getDir(String dirPath) {

		mPath.setText("Location: " + dirPath);

		item = new ArrayList<String>();
		path = new ArrayList<String>();
		File f = new File(dirPath);
		File[] files = f.listFiles();

		if (!dirPath.equals(root)) {
			item.add(root);
			path.add(root);
			item.add("../");
			path.add(f.getParent());
		}

		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file.isDirectory()) {
				if (!file.isHidden() && file.canRead()) {
					item.add(file.getName() + "/");
					path.add(file.getPath());
				}
			} else if (file.isFile()) {
				if (!file.isHidden() && file.canRead()) {
					item.add(file.getName());
					path.add(file.getPath());
				}
			}
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_activated_1, item);
		setListAdapter(adapter);
		adapter.addAll(item);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		// super.onListItemClick(l, v, position, id);

		final File file = new File(path.get(position));

		if (file.isDirectory()) {
			if (file.canRead())
				getDir(path.get(position));
			else {
				new AlertDialog.Builder(getActivity()).setIcon(R.drawable.ic_launcher)
						.setTitle("[" + file.getName() + "] folder can't be read!")
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
							}
						}).show();
			}
		} else {
			AlertDialog.Builder alert_confirm = new AlertDialog.Builder(getActivity());
			alert_confirm.setTitle("[" + file.getName() + "]").setMessage("FTP서버로 업로드").setCancelable(false)
					.setPositiveButton("확인", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							threadList(file);
						}
					}).setNegativeButton("취소", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// 'No'
							return;
						}
					});
			AlertDialog alert = alert_confirm.create();
			alert.show();
		}
	}

	public void threadList(final File file) {
		Thread Connect = new Thread(new Runnable() {
			public void run() {
				handler.post(dialogCreate);
				uploadfile(file);
				handler.post(dialogDismis);
			}
		});
		Connect.start();
	}

	public class DialogCreate implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			progressDialog = new ProgressDialog(getActivity());
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage("업로드 중 . . .");
			progressDialog.setCancelable(false);
			progressDialog.show();

		}

	}

	public class DialogDismiss implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			progressDialog.dismiss();
			Toast.makeText(getActivity(), "업로드 완료", Toast.LENGTH_SHORT).show();
		}

	}

	public class FailMsg implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Toast.makeText(getActivity(), "업로드 실패", Toast.LENGTH_SHORT).show();
		}

	}

	public void uploadfile(File file) {
		boolean uploadResult;
		try {
			FTPConnector.ftp.setFileType(FTP.BINARY_FILE_TYPE);
			fis = new FileInputStream(file);
			uploadResult = FTPConnector.ftp.storeFile(file.getName(), fis);
			if (!uploadResult) {
				Log.e("FTP_SEND_ERR", "파일 전송을 실패하였습니다.");
				uploadResult = false;
				handler.post(failmsg);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("FTP_SEND_ERR", "파일전송에 문제가 생겼습니다." + e.toString());
			uploadResult = false;
			handler.post(failmsg);
		}
	}

}
