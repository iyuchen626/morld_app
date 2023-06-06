package com.example.morldapp_demo01.mirror;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.IBinder;

import com.example.morldapp_demo01.R;
import com.example.morldapp_demo01.activity.MainActivity;

import static android.app.PendingIntent.FLAG_MUTABLE;

public class ScreenService extends Service
{

	private MediaProjectionManager mMediaProjectionManager;
	private SocketManager mSocketManager;

	@Override
	public void onCreate()
	{
		super.onCreate();
		mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
		createNotificationChannel();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		int resultCode = intent.getIntExtra("code", -1);
		Intent resultData = intent.getParcelableExtra("data");
		startProject(resultCode, resultData);
		return super.onStartCommand(intent, flags, startId);
	}

	// 录屏开始后进行编码推流
	private void startProject(int resultCode, Intent data)
	{
		MediaProjection mediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
		if (mediaProjection == null)
		{
			return;
		}
		// 初始化服务器端
		mSocketManager = new SocketManager();
		mSocketManager.start(mediaProjection);
	}

	private void createNotificationChannel()
	{
		Notification.Builder builder = new Notification.Builder(this.getApplicationContext());
		Intent nfIntent = new Intent(this, MainActivity.class);

		builder.setContentIntent(PendingIntent.getActivity(this, 0, nfIntent, FLAG_MUTABLE))
				.setLargeIcon(
						BitmapFactory.decodeResource(
								this.getResources(), R.mipmap.ic_launcher)) // 设置下拉列表中的图标(大图标)
				.setSmallIcon(R.mipmap.ic_launcher) // 设置状态栏内的小图标
				.setContentText("is running......") // 设置上下文内容
				.setWhen(System.currentTimeMillis()); // 设置该通知发生的时间

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
		{
			builder.setChannelId("notification_id");
			// 前台服务notification适配
			NotificationManager notificationManager =
					(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			NotificationChannel channel =
					new NotificationChannel(
							"notification_id", "notification_name", NotificationManager.IMPORTANCE_LOW);
			notificationManager.createNotificationChannel(channel);
		}

		Notification notification = builder.build();
		notification.defaults = Notification.DEFAULT_SOUND; // 设置为默认通知音
		startForeground(110, notification);
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	@Override
	public void onDestroy()
	{
		mSocketManager.close();
		super.onDestroy();
	}
}