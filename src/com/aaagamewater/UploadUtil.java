package com.aaagamewater;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import android.os.Environment;

public class UploadUtil {
	public static String url="http://10.40.7.171:8080/UploadTest/MyUploadServlet";
	public static void uploadFile(String userName,File file,AjaxCallBack<Object> callback){
		AjaxParams p=new AjaxParams();
		p.put("user", userName);//设置上传文件的用户名
		try {
			p.put("file", file);//设置上传的文件数据
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FinalHttp http=new FinalHttp();
		http.post(url, p, callback);
	}
	public static File createFile(String data,String fileName){
		File file=new File(Environment.getExternalStorageDirectory(),fileName);
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				return null;
			}
		}
		FileOutputStream fos=null;
		try {
			fos=new FileOutputStream(file);
			fos.write(data.getBytes());
		} catch (FileNotFoundException e) {
			file=null;
			e.printStackTrace();
		} catch (IOException e) {
			file=null;
			e.printStackTrace();
		}finally{
			try {
				fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return file;
	}

}
