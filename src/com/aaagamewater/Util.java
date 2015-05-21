package com.aaagamewater;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import net.tsz.afinal.FinalDb;
import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.Log;

public class Util {
	
	public static String getOs(){	
		return "android"+Build.VERSION.SDK_INT;
	}
	public static String getCpu(){
		String str1 = "/proc/cpuinfo";
        String str2 = "";
        String[] cpuInfo = { "", "" }; // 1-cpu型号 //2-cpu频率
        String[] arrayOfString;
        try {
                FileReader fr = new FileReader(str1);
                BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
                str2 = localBufferedReader.readLine();
                arrayOfString = str2.split("\\s+");
                for (int i = 2; i < arrayOfString.length; i++) {
                        cpuInfo[0] = cpuInfo[0] + arrayOfString + " ";
                }
                str2 = localBufferedReader.readLine();
                arrayOfString = str2.split("\\s+");
                cpuInfo[1] += arrayOfString[2];
                localBufferedReader.close();
        } catch (IOException e) {
        }
		return "cpu型号:" + cpuInfo[0] + "n" + "cpu频率:" + cpuInfo[1];
	}
	public static String getProcessorSum(){
		Runtime r=Runtime.getRuntime();		
		return ""+r.availableProcessors();
	}
	public static String getPhoneNumber(Context mContext) {
		TelephonyManager tm=(TelephonyManager) mContext.getSystemService(Service.TELEPHONY_SERVICE);
		return tm.getLine1Number();
	}

	public static String getIp() {
		 try {
             for (Enumeration<NetworkInterface> en = NetworkInterface
                             .getNetworkInterfaces(); en.hasMoreElements();) {
                     NetworkInterface intf = en.nextElement();
                     for (Enumeration<InetAddress> enumIpAddr = intf
                                     .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                             InetAddress inetAddress = enumIpAddr.nextElement();
                             if (!inetAddress.isLoopbackAddress()) {
                                     return inetAddress.getHostAddress().toString();
                             }
                     }
             }
		 } catch (SocketException ex) {
             ex.printStackTrace();
		 }
     return "";
	}
	public static String getTotalMemory(Context mContext){
		
		 String str1 = "/proc/meminfo";// 系统内存信息文件
         String str2;
         String[] arrayOfString;
         long initial_memory = 0;
         try {
                 FileReader localFileReader = new FileReader(str1);
                 BufferedReader localBufferedReader = new BufferedReader(
                                 localFileReader, 8192);
                 str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小

                 arrayOfString = str2.split("\\s+");
                 for (String num : arrayOfString) {
                         Log.i(str2, num + "t");
                 }

                 initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte
                 localBufferedReader.close();

         } catch (IOException e) {
         }
         return Formatter.formatFileSize(mContext, initial_memory);// Byte转换为KB或者MB，内存大小规格化
	}
	
	
    public static boolean isNetConnecting(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
               return false;
        } else {
               return true;
        }

    }
    public static String createFileName(Context mContext){
    	String fileName="";
    	fileName=getPhoneNumber(mContext)+"__";
    	long time=SystemClock.currentThreadTimeMillis();
    	
    	SimpleDateFormat sdf=new SimpleDateFormat("yyyy_MM_dd",Locale.CHINA);//这里对文件名的长度有限制，太长会出现异常   Locale.CHINA 将UTC时间转换成中国北京的时间
//    	fileName+=sdf.format(new Date(time));
//    	fileName+=time;
    	//使用日历得到的不是UTC时间，得到的是系统的当前时间
    	Calendar calendar=Calendar.getInstance();
    	int year=calendar.get(Calendar.YEAR);
    	int month=calendar.get(Calendar.MONTH)+1;//calendar.get(Calendar.MONTH)得到的月份是0-11，所以真正的月份要+1
    	int day=calendar.get(Calendar.DAY_OF_MONTH);
    	int hour=calendar.get(Calendar.HOUR_OF_DAY);
    	int minute=calendar.get(Calendar.MINUTE);
    	String timeFormat=year+"_"+month+"_"+day+"_"+hour+"_"+minute;
    	fileName+=timeFormat;
    	fileName+=".txt";
    	return fileName;
    }
    public static UserData getLocalUserData(Context mContext){
    	UserData userData=new UserData();
		userData.setCpu(Util.getCpu());
		userData.setOs(Util.getOs());
		userData.setIp(Util.getIp());
		userData.setPhoneNumber(Util.getPhoneNumber(mContext));
		userData.setProcessorSum(Util.getProcessorSum());
		userData.setRawMemory(Util.getTotalMemory(mContext));
		return userData;
    }
    public static boolean hasDataFromDb(Context mContext){
    	FinalDb finalDb=FinalDb.create(mContext);
		List<UserData> users=finalDb.findAll(UserData.class);
		if(users.isEmpty()){
			return false;
		}
		return true;
    }
    public static void  saveGameLevel(Context mContext,int level){
    	SharedPreferences spf=mContext.getSharedPreferences("game_level", Context.MODE_PRIVATE);
    	spf.edit().putInt("level", level).commit();
    }
    public static int getGameLevel(Context mContext){
    	SharedPreferences spf=mContext.getSharedPreferences("game_level", Context.MODE_PRIVATE);
    	return  spf.getInt("level", 1);
    }
}
