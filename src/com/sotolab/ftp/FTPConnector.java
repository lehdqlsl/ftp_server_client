package com.sotolab.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import android.util.Log;

public class FTPConnector {

	private static FTPConnector ftpconnector;

	private static String SERVER = ""; // FTP ȣ��Ʈ �ּ�
	private static int port; // ��Ʈ��ȣ
	private static String ID = ""; // ���� ���̵�
	private static String PASS = ""; // ���� �н�����
	private static String mEncodingSet = ""; // �ɸ��� ��
	private static String mDefaultWorkDirectory = "/"; // �⺻ �۾� ���丮

	static FTPClient ftp = null;
	static FileInputStream fis = null;

	/**
	 * FTPConnector의 객체를 생성합니다.<br/>
	 * 
	 * @param server
	 *            서버 호스트 주소입니다.
	 * @param id
	 *            로그인 아이디입니다.
	 * @param pass
	 *            로그인 패스워드입니다.
	 * @param encodingSet
	 *            인코딩 종류를 지정합니다. UTF-8 or EUC-KR
	 * @param workDirctory
	 *            작업을 진행할 FTP 서버의 디렉터리를 지정합니다.<br/>
	 * 
	 */

	public static synchronized FTPConnector getInstance() {
		if (ftpconnector == null)
			ftpconnector = new FTPConnector(SERVER, port, ID, PASS, mEncodingSet, mDefaultWorkDirectory);
		return ftpconnector;
	}

	public FTPConnector(String server, int port, String id, String pass, String encodingSet, String workDirctory) {
		// TODO Auto-generated constructor stub
		FTPConnector.SERVER = server;
		FTPConnector.port = port;
		FTPConnector.ID = id;
		FTPConnector.PASS = pass;
		FTPConnector.mEncodingSet = encodingSet;
		FTPConnector.mDefaultWorkDirectory = workDirctory;

		ftp = new FTPClient();
		Log.i("FPT_LOGIN_OK", FTPConnector.SERVER + FTPConnector.port + FTPConnector.ID + FTPConnector.PASS
				+ FTPConnector.mEncodingSet + FTPConnector.mDefaultWorkDirectory);
	}

	/**
	 * FTP서버로 접속을 시도합니다.<br/>
	 * 
	 * @return 서버 접속 성공 여부를 리턴합니다.
	 */

	public boolean login() {
		boolean loginResult = false;
		try {
			ftp.setControlEncoding(mEncodingSet);
			ftp.connect(SERVER, port);
			loginResult = ftp.login(ID, PASS);
			ftp.enterLocalPassiveMode(); // PassiveMode 접속
			ftp.makeDirectory(mDefaultWorkDirectory);
			ftp.changeWorkingDirectory(mDefaultWorkDirectory);
		} catch (IOException e) {
			Log.e("FTP_LOGIN_ERR", e.toString());
		}

		if (!loginResult) {
			Log.e("FTP_LOGIN_ERR", "로그인 실패");
			return false;
		} else {
			Log.i("FPT_LOGIN_OK", "로그인 성공");
			return true;
		}
	}

	/**
	 * FTP서버로 파일을 전송합니<br/>
	 * 
	 * @param file
	 *            전송할 파일의 객체를 필요로 합니다.
	 * @return 파일전송 성공 실패 여부를 리턴합니다.
	 */

	public static boolean uploadFile(File file) {
		if (!ftp.isConnected()) {
			Log.e("UPLOAD_ERR", "현재 FTP 서버에 접속되어 잇지 않습니다.");
			return false;
		}

		boolean uploadResult = false;
		try {
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
			fis = new FileInputStream(file);
			uploadResult = ftp.storeFile(file.getName(), fis);
			if (!uploadResult) {
				Log.e("FTP_SEND_ERR", "파일 전송을 실패하였습니다.");
				uploadResult = false;
			}
		} catch (Exception e) {
			Log.e("FTP_SEND_ERR", "파일전송에 문제가 생겼습니다." + e.toString());
			uploadResult = false;
		}
		return uploadResult;
	}

	public static FTPFile[] list() {
		try {
			return ftp.listFiles();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
