package com.example.morldapp_demo01.mirror;

import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.morldapp_demo01.R;
import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.MqttGlobalPublishFilter;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.message.subscribe.Mqtt5Subscribe;

import java.nio.ByteBuffer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Client extends AppCompatActivity
{
	private Mqtt5BlockingClient mqtt5BlockingClient;
	private ScreenDecoder mScreenDecoder;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mirror_client);
		SurfaceView surfaceView = findViewById(R.id.sv_screen);
		surfaceView.getHolder().addCallback(
				new SurfaceHolder.Callback()
				{
					@Override
					public void surfaceCreated(@NonNull SurfaceHolder holder)
					{
						mScreenDecoder = new ScreenDecoder();
						mScreenDecoder.startDecode(holder.getSurface());
					}

					@Override
					public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height)
					{}

					@Override
					public void surfaceDestroyed(@NonNull SurfaceHolder holder) {}
				});

		try
		{
			final String host = "af2974435bf3431090de92906c641586.s1.eu.hivemq.cloud";
			final String username = "raritan";
			final String password = "sS_123456";

			// create an MQTT client
			mqtt5BlockingClient = MqttClient.builder()
					.useMqttVersion5()
					.serverHost(host)
					.serverPort(8883)
					.sslWithDefaultConfig()
					.buildBlocking();

			// connect to HiveMQ Cloud with TLS and username/pw
			mqtt5BlockingClient.connectWith()
					.simpleAuth()
					.username(username)
					.password(password.getBytes(UTF_8))
					.applySimpleAuth()
					.send();

			; mqtt5BlockingClient.subscribeWith()
				.topicFilter("morld")
				.send();

			mqtt5BlockingClient.toAsync().publishes(MqttGlobalPublishFilter.SUBSCRIBED, publish ->
			{
				ByteBuffer byteBuffer = publish.getPayload().get();
				byte[] b = new byte[byteBuffer.remaining()];
				byteBuffer.get(b, 0, b.length);
				mScreenDecoder.decodeData(b);
			});

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}