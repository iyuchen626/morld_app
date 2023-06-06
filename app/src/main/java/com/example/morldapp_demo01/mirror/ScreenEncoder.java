package com.example.morldapp_demo01.mirror;

import android.hardware.display.DisplayManager;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.projection.MediaProjection;
import android.nfc.Tag;
import android.util.Log;
import android.view.Surface;

import com.example.morldapp_demo01.Config;
import com.example.morldapp_demo01.Tools;

import java.io.IOException;
import java.nio.ByteBuffer;

public class ScreenEncoder extends Thread
{

	//不同手机支持的编码最大分辨率不同
	private static final int VIDEO_WIDTH = 720;
	private static final int VIDEO_HEIGHT = 1280;
	private static final int SCREEN_FRAME_RATE = 15;
	private static final int SCREEN_FRAME_INTERVAL = 1;
	private static final long SOCKET_TIME_OUT = 10000;
	// I帧
	private static final int TYPE_FRAME_INTERVAL = 19;
	// vps帧
	private static final int TYPE_FRAME_VPS = 32;
	private final MediaProjection mMediaProjection;
	private final SocketManager mSocketManager;
	private MediaCodec mMediaCodec;
	private boolean mPlaying = true;
	// 记录vps pps sps
	private byte[] vps_pps_sps;

	public ScreenEncoder(SocketManager socketManager, MediaProjection mediaProjection)
	{
		mSocketManager = socketManager;
		mMediaProjection = mediaProjection;
	}

	public void startEncode()
	{
		MediaFormat mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_HEVC, VIDEO_WIDTH, VIDEO_HEIGHT);
		mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
		// 比特率（比特/秒）
		mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, VIDEO_WIDTH * VIDEO_HEIGHT);
		// 帧率
		mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, SCREEN_FRAME_RATE);
		// I帧的频率
		mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, SCREEN_FRAME_INTERVAL);
		try
		{
			mMediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_HEVC);
			mMediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
			Surface surface = mMediaCodec.createInputSurface();
			mMediaProjection.createVirtualDisplay(
					"screen",
					VIDEO_WIDTH,
					VIDEO_HEIGHT,
					1,
					DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,
					surface,
					null,
					null);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		start();
	}

	@Override
	public void run()
	{
		mMediaCodec.start();
		MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
		while (mPlaying)
		{
			int outPutBufferId = mMediaCodec.dequeueOutputBuffer(bufferInfo, SOCKET_TIME_OUT);
			if (outPutBufferId >= 0)
			{
				ByteBuffer byteBuffer = mMediaCodec.getOutputBuffer(outPutBufferId);
				encodeData(byteBuffer, bufferInfo);
				mMediaCodec.releaseOutputBuffer(outPutBufferId, false);
			}
		}
	}

	private void encodeData(ByteBuffer byteBuffer, MediaCodec.BufferInfo bufferInfo)
	{
		int offSet = 4;
		if (byteBuffer.get(2) == 0x01)
		{
			offSet = 3;
		}
		int type = (byteBuffer.get(offSet) & 0x7E) >> 1;
		if (type == TYPE_FRAME_VPS)
		{
			vps_pps_sps = new byte[bufferInfo.size];
			byteBuffer.get(vps_pps_sps);
		}
		else if (type == TYPE_FRAME_INTERVAL)
		{
			final byte[] bytes = new byte[bufferInfo.size];
			byteBuffer.get(bytes);
			byte[] newBytes = new byte[vps_pps_sps.length + bytes.length];
			System.arraycopy(vps_pps_sps, 0, newBytes, 0, vps_pps_sps.length);
			System.arraycopy(bytes, 0, newBytes, vps_pps_sps.length, bytes.length);
			String ds = Tools.bytesToHex(newBytes);
			Log.d(Config.TAG, ds);
			mSocketManager.sendData(newBytes);
		}
		else
		{
			byte[] bytes = new byte[bufferInfo.size];
			byteBuffer.get(bytes);
			mSocketManager.sendData(bytes);
		}
	}

	public void stopEncode()
	{
		mPlaying = false;
		if (mMediaCodec != null)
		{
			mMediaCodec.release();
		}
		if (mMediaProjection != null)
		{
			mMediaProjection.stop();
		}
	}
}