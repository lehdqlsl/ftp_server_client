package com.sotolab.ftp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPFile;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ServerList extends ListFragment {

	private List<String> item = null;
	private pList plist = new pList();
	Handler handler = new Handler();
	private FTPFile[] list;
	private String root = "/";
	ListView lv;
	OutputStream outputstream;
	DialogCreate dialogCreate;
	DialogDismiss dialogDismis;
	ProgressDialog progressDialog;
	FailMsg failmsg;
	TextView tv;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.server_list, container, false);
		lv = (ListView) v.findViewById(android.R.id.list);
		tv = (TextView) v.findViewById(R.id.textView1);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		dialogCreate = new DialogCreate();
		dialogDismis = new DialogDismiss();
		failmsg = new FailMsg();
		handler = new Handler();

		threadList();
	}

	public void threadList() {

		Thread Connect = new Thread(new Runnable() {
			public void run() {

				getDir(root);
				handler.post(plist);

			}
		});
		Connect.start();
	}

	protected void getDir(String dirPath) {
		// TODO Auto-generated method stub
		list = FTPConnector.list();
		item = new ArrayList<String>();

		if (!dirPath.equals(root)) {
			item.add(root);
			item.add("../");

		}

		for (int i = 0; i < list.length; i++) {
			FTPFile file = list[i];
			if (file.isDirectory())
				item.add(file.getName() + "/");
			else
				item.add(file.getName());
		}
	}

	public class pList implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_activated_1, item);
			setListAdapter(adapter);
			adapter.addAll(item);
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		// super.onListItemClick(l, v, position, id);
		FTPFile file = list[position];
		final File file1 = new File("/" + file.getName());

		if (list[position].isFile()) {
			AlertDialog.Builder alert_confirm = new AlertDialog.Builder(getActivity());
			alert_confirm.setTitle("[" + list[position].getName() + "]").setMessage("다운로드").setCancelable(false)
					.setPositiveButton("확인", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// 'YES'
							threadList(file1);
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
		} else {

		}
	}

	public void threadList(final File file) {
		Thread Connect = new Thread(new Runnable() {
			public void run() {
				handler.post(dialogCreate);
				downloadfile(file);
				handler.post(dialogDismis);
			}
		});
		Connect.start();
	}

	public void downloadfile(File file) {
		boolean uploadResult;
		File getfile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
				file.getName());
		;
		Log.e("FTP", "파일정보 : " + getfile.getPath() + "경로 : " + getfile.getAbsolutePath());
		try {
			outputstream = new FileOutputStream(getfile);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			Log.e("FTP_SEND_ERR", "outputstream 생성 실패" + e1.toString());
			e1.printStackTrace();
		}
		try {
			FTPConnector.ftp.setFileType(FTP.BINARY_FILE_TYPE);
			uploadResult = FTPConnector.ftp.retrieveFile(file.getName(), outputstream);
			if (!uploadResult) {
				Log.e("FTP_SEND_ERR", "파일 전송을 실패하였습니다.");
				uploadResult = false;
				handler.post(failmsg);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("FTP_SEND_ERR", "파일 전송에 문제가 생겼습니다." + e.toString());
			uploadResult = false;
			handler.post(failmsg);
		}
	}

	public class DialogCreate implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			progressDialog = new ProgressDialog(getActivity());
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage("다운로드 중. . .");
			progressDialog.setCancelable(false);
			progressDialog.show();

		}

	}

	public class DialogDismiss implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			progressDialog.dismiss();
			Toast.makeText(getActivity(), "다운 완료", Toast.LENGTH_SHORT).show();
		}

	}

	public class FailMsg implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Toast.makeText(getActivity(), "다운 실패", Toast.LENGTH_SHORT).show();
		}

	}

}