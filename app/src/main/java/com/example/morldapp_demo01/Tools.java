package com.example.morldapp_demo01;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.morldapp_demo01.dialog.Normal;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.hjq.toast.ToastParams;
import com.hjq.toast.Toaster;
import com.hjq.toast.style.CustomToastStyle;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.scottyab.aescrypt.AESCrypt;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import taimoor.sultani.sweetalert2.Sweetalert;

/**
 * Created by 123 on 2017/9/18.
 */

public class Tools
{
	public static final String TAG = "Morld";
	static boolean progressDone = false;
	static  Sweetalert sweetalert;
	private static Gson gson;

	private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = HEX_ARRAY[v >>> 4];
			hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
		}
		return new String(hexChars);
	}

	public static void showProgress(AppCompatActivity a, String msg)
	{
		if (a == null || a.isFinishing()) return;
		if(sweetalert != null)
		{
			sweetalert.setContentText(msg);
		}
		else
		{
			sweetalert = new Sweetalert(a, Sweetalert.PROGRESS_TYPE)
					.setContentText(msg);
			sweetalert.setCancelable(false);
			sweetalert.show();
		}
	}

	public static void hideProgress(AppCompatActivity a)
	{
		if (a == null || a.isFinishing()) return;
		if(sweetalert != null)
		{
			sweetalert.dismissWithAnimation();
			sweetalert = null;
		}
	}
	public static void showError(AppCompatActivity a, String title)
	{
		if (a == null || a.isFinishing()) return;
		new Sweetalert(a, Sweetalert.ERROR_TYPE)
				.setTitleText("錯誤")
				.setContentText(title)
				.show();
	}
	public static void showInfo(AppCompatActivity a, String title, String s)
	{
		if (a == null || a.isFinishing()) return;
		Normal editNameDialog = new Normal();
		editNameDialog.setTitle(title);
		editNameDialog.setContent(s);
		editNameDialog.setOK("關閉", new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
			}
		});
		editNameDialog.show(a.getSupportFragmentManager(), "EditNameDialog");
	}

	public static void toast(Context context, String msg)
	{
		ToastParams params = new ToastParams();
		params.text = msg;
		params.style = new CustomToastStyle(R.layout.toast_info);
		Toaster.show(params);
	}

	public static void toastSuccess(Context context, String msg)
	{
		ToastParams params = new ToastParams();
		params.text = msg;
		params.style = new CustomToastStyle(R.layout.toast_success);
		Toaster.show(params);
	}

	public static float getDensity(Context context)
	{
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		return metrics.density;
	}

	public static float convertDpToPixel(float dp, Context context)
	{
		float px = dp * getDensity(context);
		return px;
	}

	public static void buildAlertMessageNoGps(Activity a)
	{
		final AlertDialog.Builder builder = new AlertDialog.Builder(a);
		builder.setMessage("定位需要開啟才能使用地圖，是否開啟?")
				.setCancelable(false)
				.setPositiveButton("是", new DialogInterface.OnClickListener()
				{
					public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id)
					{
						a.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
					}
				})
				.setNegativeButton("否", new DialogInterface.OnClickListener()
				{
					public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id)
					{
						dialog.cancel();
					}
				});
		final AlertDialog alert = builder.create();
		alert.show();
	}

	public static void mm請求所有必要權限(Activity a, final OnPermissionListener cc)
	{
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P)
		{
			Dexter.withActivity(a)
					.withPermissions(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)
					.withListener(new MultiplePermissionsListener()
					{
						@Override
						public void onPermissionsChecked(MultiplePermissionsReport report)
						{
							if (report.areAllPermissionsGranted())
							{
								cc.onGranted();
							}
							else
							{
								cc.onDenied();
							}
							Log.i(TAG, report.areAllPermissionsGranted() + "");
						}

						@Override
						public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token)
						{
							Log.i(TAG, token.toString());
							token.continuePermissionRequest();
						}
					}).check();
		}
		else
		{
			List<String> ssd = new ArrayList<>();
			ssd.add(Manifest.permission.CAMERA);
			ssd.add(Manifest.permission.RECORD_AUDIO);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2)
			{
				ssd.add(Manifest.permission.READ_EXTERNAL_STORAGE);
			}
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
			{
				ssd.add(Manifest.permission.READ_MEDIA_VIDEO);
			}
			Dexter.withActivity(a).withPermissions(ssd.toArray(new String[0]))
					.withListener(new MultiplePermissionsListener()
					{
						@Override
						public void onPermissionsChecked(MultiplePermissionsReport report)
						{
							if (report.areAllPermissionsGranted())
							{
								cc.onGranted();
							}
							else
							{
								cc.onDenied();
							}
							Log.i(TAG, report.areAllPermissionsGranted() + "");
						}

						@Override
						public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token)
						{
							Log.i(TAG, token.toString());
							token.continuePermissionRequest();
						}
					}).check();
		}
	}
	public static void mm請求位置(Activity a, final OnPermissionListener cc)
	{
		Dexter.withActivity(a)
				.withPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
				.withListener(new MultiplePermissionsListener()
				{
					@Override
					public void onPermissionsChecked(MultiplePermissionsReport report)
					{
						if (report.areAllPermissionsGranted())
						{
							cc.onGranted();
						}
						else
						{
							cc.onDenied();
						}
						Log.i(TAG, report.areAllPermissionsGranted() + "");
					}

					@Override
					public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token)
					{
						Log.i(TAG, token.toString());
						token.continuePermissionRequest();
					}
				}).check();
	}

	public static void mm請求記憶卡寫入(Activity a, final OnPermissionListener cc)
	{
		Dexter.withActivity(a)
				.withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
				.withListener(new MultiplePermissionsListener()
				{
					@Override
					public void onPermissionsChecked(MultiplePermissionsReport report)
					{
						if (report.areAllPermissionsGranted())
						{
							cc.onGranted();
						}
						else
						{
							cc.onDenied();
						}
						Log.i(TAG, report.areAllPermissionsGranted() + "");
					}

					@Override
					public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token)
					{
						Log.i(TAG, token.toString());
						token.continuePermissionRequest();
					}
				}).check();
	}

	public static void mm儲存帳密(Context context, String key, String p)
	{
		try
		{
			p = AESCrypt.encrypt(Tools.getUniqueID(context), p);
			Tools.saveData(context, key, p);
		}
		catch (GeneralSecurityException e)
		{
			//handle error
		}
	}

	public static String getUniqueID(Context context)
	{
		StringBuilder uniqueID = new StringBuilder();
		// String tmDevice="", tmSerial="", androidId="";
		//  final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		// uniqueID.append(tm.getDeviceId());
		//uniqueID.append(tm.getSimSerialNumber());
		uniqueID.append(Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
		String m_szDevIDShort = "35" + (Build.BOARD.length() % 10) + (Build.BRAND.length() % 10) + (Build.CPU_ABI.length() % 10) + (Build.DEVICE.length() % 10) + (Build.MANUFACTURER.length() % 10) + (Build.MODEL.length() % 10) + (Build.PRODUCT.length() % 10);
		// Thanks to @Roman SL!
		// http://stackoverflow.com/a/4789483/950427
		// Only devices with API >= 9 have android.os.Build.SERIAL
		// http://developer.android.com/reference/android/os/Build.html#SERIAL
		// If a user upgrades software or roots their device, there will be a duplicate entry
		String serial = "";
		try
		{
			serial = Build.class.getField("SERIAL").get(null).toString();
			// Go ahead and return the serial for api => 9
			uniqueID.append(m_szDevIDShort.hashCode());
			uniqueID.append(serial.hashCode());
		}
		catch (Exception exception)
		{
		}
		return md5(uniqueID.toString());
	}

	public static final String md5(final String s)
	{
		final String MD5 = "MD5";
		try
		{
			// Create MD5 Hash
			MessageDigest digest = MessageDigest.getInstance(MD5);
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuilder hexString = new StringBuilder();
			for (byte aMessageDigest : messageDigest)
			{
				String h = Integer.toHexString(0xFF & aMessageDigest);
				while (h.length() < 2)
				{
					h = "0" + h;
				}
				hexString.append(h);
			}
			return hexString.toString();

		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		return "";
	}

	public static String mm時間補0(int t)
	{
		if (t <= 9)
		{
			return "0" + t;
		}
		else
		{
			return "" + t;
		}
	}

	public static String mm時間補0(String t)
	{
		if (t.length() < 2)
		{
			return "0" + t;
		}
		else
		{
			return "" + t;
		}
	}

	public static void mm請求相機(Activity a, final OnPermissionListener cc)
	{
		Dexter.withActivity(a)
				.withPermissions(Manifest.permission.CAMERA)
				.withListener(new MultiplePermissionsListener()
				{
					@Override
					public void onPermissionsChecked(MultiplePermissionsReport report)
					{
						if (report.areAllPermissionsGranted())
						{
							cc.onGranted();
						}
						else
						{
							cc.onDenied();
						}
						Log.i(TAG, report.areAllPermissionsGranted() + "");
					}

					@Override
					public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token)
					{
						Log.i(TAG, token.toString());
						token.continuePermissionRequest();
					}
				}).check();
	}

	public static int mm讀點(Context a)
	{
		SharedPreferences settings = a.getSharedPreferences(a.getPackageName(), Context.MODE_PRIVATE);
		return settings.getInt("ddd", 0);
	}

	public static void mm扣點(Context a, int point, TextView text)
	{
		mm扣點(a, point);
		text.setText(String.valueOf(mm讀點(a)));
	}

	public static void mm扣點(Context a, int point)
	{
		SharedPreferences sharedPref = a.getSharedPreferences(a.getPackageName(), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putInt("ddd", mm讀點(a) - point < 0 ? 0 : mm讀點(a) - point);
		editor.commit();
	}

	public static String mmRead(Context a, String key)
	{
		SharedPreferences sharedPref = a.getSharedPreferences(a.getPackageName(), Context.MODE_PRIVATE);
		return sharedPref.getString(key, "");
	}

	public static void mmSave(Context a, String key, String value)
	{
		SharedPreferences sharedPref = a.getSharedPreferences(a.getPackageName(), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static void mm寫點(Context a, int point)
	{
		SharedPreferences sharedPref = a.getSharedPreferences(a.getPackageName(), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putInt("ddd", point);
		editor.commit();
	}

	public static void mm加點(Context a, int point)
	{
		SharedPreferences sharedPref = a.getSharedPreferences(a.getPackageName(), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putInt("ddd", mm讀點(a) + point);
		editor.commit();
	}

	public static void loadImg(ImageView a, String point)
	{
		Picasso.get().load(point).into(a);
	}

	public static String mm轉成時間(Date date)
	{
		if (date == null)
		{
			return "";
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String s = simpleDateFormat.format(date);
		return s;
	}

	public static Date mm字串轉日期時間(String s)
	{
		Date d = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try
		{
			d = simpleDateFormat.parse(s);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		return d;
	}

	public static Date mm字串轉日期(String s)
	{
		Date d = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		try
		{
			d = simpleDateFormat.parse(s);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		return d;
	}

	public static Gson getGson()
	{
		if (gson == null)
		{
			String DATE_FORMAT_COMPLETE = "yyyy-MM-dd HH:mm:ss";
			Gson g = new GsonBuilder()
					.setDateFormat(DATE_FORMAT_COMPLETE)
					.create();
			TypeAdapter<Date> dateTypeAdapter = g.getAdapter(Date.class);
			TypeAdapter<Date> safeDateTypeAdapter = dateTypeAdapter.nullSafe();
			gson = new GsonBuilder()
					.setDateFormat(DATE_FORMAT_COMPLETE)
					.registerTypeAdapter(Date.class, safeDateTypeAdapter)
					.create();
		}
		return gson;
	}

	public static String getWeekOfDate(Date dt)
	{
		String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (w < 0)
		{
			w = 0;
		}
		return weekDays[w];
	}

	public static Bitmap getBitmapFromURL(String src)
	{
		try
		{
			java.net.URL url = new java.net.URL(src);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap myBitmap = BitmapFactory.decodeStream(input);
			return myBitmap;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static String mm局時間(Date date)
	{
		if (date == null)
		{
			return "";
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd HH:mm");
		String s = simpleDateFormat.format(date);
		s = getWeekOfDate(date) + " " + s;
		return s;
	}

	public static byte[] bitmapToBytes(Bitmap bmp)
	{
		int bytes = bmp.getByteCount();
		ByteBuffer buf = ByteBuffer.allocate(bytes);
		bmp.copyPixelsToBuffer(buf);
		byte[] byteArray = buf.array();
		return byteArray;
	}

	public static String mm日期轉成字串(Date date)
	{
		if (date == null)
		{
			return "";
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String s = simpleDateFormat.format(date);
		return s;
	}

	public static void saveData(Context context, String key, String value)
	{
		SharedPreferences sharedPref = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(key, value);
		editor.apply();
	}

	public static String readData(Context context, String key)
	{
		SharedPreferences settings = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
		return settings.getString(key, "");
	}

	public static boolean check餘額(Context context, int point)
	{
		int currentPoint = Tools.mm讀點(context);
		return currentPoint >= point;
	}

	public static byte[] hexStringToByteArray(String s)
	{
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2)
		{
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
					+ Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	public static int toInt(EditText e)
	{
		if (e.length() == 0) return 0;
		try
		{
			int ss = Integer.parseInt(e.getText().toString());
			return ss;
		}
		catch (Exception xe)
		{
		}
		return 0;
	}

	public static String getVersionName(Context context)
	{
		PackageManager packageManager = context.getPackageManager();
		PackageInfo packageInfo;
		String versionName = "";
		try
		{
			packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			versionName = packageInfo.versionName;
		}
		catch (PackageManager.NameNotFoundException e)
		{
			e.printStackTrace();
		}
		return versionName;
	}

	public static boolean isNetworkConnected(Context context)
	{
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		return cm.getActiveNetworkInfo() != null;
	}

	public interface OnPermissionListener
	{
		void onGranted();
		void onDenied();
	}

	public interface OnProgressTimeout
	{
		void onTimeout();
	}
}
