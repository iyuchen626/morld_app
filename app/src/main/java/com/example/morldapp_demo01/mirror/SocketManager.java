package com.example.morldapp_demo01.mirror;

import android.media.projection.MediaProjection;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5PublishBuilder;

import org.jetbrains.annotations.NotNull;

import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import static com.hivemq.client.mqtt.MqttGlobalPublishFilter.ALL;
import static java.nio.charset.StandardCharsets.UTF_8;

public class SocketManager
{

	private static final String TAG = SocketManager.class.getSimpleName();
	private static final int SOCKET_PORT = 8884;
	private ScreenEncoder mScreenEncoder;
	private Mqtt5BlockingClient mqtt5BlockingClient;

	public void start(MediaProjection mediaProjection)
	{
//    mScreenSocketServer.start();
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
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		mScreenEncoder = new ScreenEncoder(this, mediaProjection);
		mScreenEncoder.startEncode();
	}

	public void close()
	{
		try
		{
			mqtt5BlockingClient.disconnect();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		if (mScreenEncoder != null)
		{
			mScreenEncoder.stopEncode();
		}
	}

	public void sendData(byte[] bytes)
	{
		mqtt5BlockingClient.publishWith().topic("morld").payload(bytes).send();
	}
}