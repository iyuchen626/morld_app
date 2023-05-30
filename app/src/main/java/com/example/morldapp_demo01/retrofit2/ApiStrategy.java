package com.example.morldapp_demo01.retrofit2;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.example.morldapp_demo01.Config;
import com.example.morldapp_demo01.MyApplication;
import com.example.morldapp_demo01.Tools;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class ApiStrategy
{
	Gson gson;
	//读超时长，单位：毫秒
	public static final int READ_TIME_OUT = 5000;
	//连接时长，单位：毫秒
	public static final int CONNECT_TIME_OUT = 5000;
	private final static String CLIENT_PRI_KEY = "client-cert.bks";
	private final static String TRUSTSTORE_PUB_KEY = "server-cert.bks";
	private final static String CLIENT_BKS_PASSWORD = "123456";
	private final static String TRUSTSTORE_BKS_PASSWORD = "123456";
	private final static String KEYSTORE_TYPE = "BKS";
	private final static String PROTOCOL_TYPE = "TLS";
	private final static String CERTIFICATE_STANDARD = "X509";
	/**
	 * 设缓存有效期为两天
	 */
	private static final long CACHE_STALE_SEC = 60 * 60 * 24 * 2;
	public static ApiService apiService;
	static SSLSocketFactory sslSocketFactory;
	private static X509TrustManager x509TrustManager;
	private static String lastUrl = "";
	/**
	 * 云端响应头拦截器，用来配置缓存策略
	 * Dangerous interceptor that rewrites the server's cache-control header.
	 */
	private final Interceptor mRewriteCacheControlInterceptor = new Interceptor()
	{
		@Override
		public Response intercept(Chain chain) throws IOException
		{
			Request request = chain.request();
			String cacheControl = request.cacheControl().toString();
			if (!Tools.isNetworkConnected(MyApplication.getInstance()))
			{
				request = request.newBuilder()
						.cacheControl(TextUtils.isEmpty(cacheControl) ? CacheControl
								.FORCE_NETWORK : CacheControl.FORCE_CACHE)
						.build();
			}
			Response originalResponse = chain.proceed(request);
			if (!Tools.isNetworkConnected(MyApplication.getInstance()))
			{
				return originalResponse.newBuilder()
						.header("Cache-Control", cacheControl)
						.removeHeader("Pragma")
						.build();
			}
			else
			{
				return originalResponse.newBuilder()
						.header("Cache-Control", "public, only-if-cached, max-stale=" +
								CACHE_STALE_SEC)
						.removeHeader("Pragma")
						.build();
			}
		}
	};

	private ApiStrategy(String base_url)
	{
		String DATE_FORMAT_COMPLETE = "yyyy-MM-dd HH:mm:ss";
		gson = new GsonBuilder().setDateFormat(DATE_FORMAT_COMPLETE).create();
		HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();
		logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
		//缓存
		File cacheFile = new File(MyApplication.getInstance().getCacheDir(), "cache");
		Cache cache = new Cache(cacheFile, 1024 * 1024 * 10);

		//增加头部信息
		Interceptor headerInterceptor = new Interceptor()
		{
			@Override
			public Response intercept(Chain chain) throws IOException
			{
				Request request = chain.request();
				Response originalResponse = chain.proceed(request);
				return originalResponse.newBuilder().addHeader("Authorization", "Bearer 123")
						.build();
//				Response mainRes ponse = chain.proceed(chain.request());
//
//				Log.i(Config.TAG, "請求:" + chain.request().url().encodedPath());
//				String token = "000";
//				Request build = chain.request().newBuilder()
//						.addHeader("Authorization", "Bearer " + token)//设置允许请求json数据
//						.build();
//				return chain.proceed(build);
			}
		};

		//创建一个OkHttpClient并设置超时时间
		OkHttpClient client = new OkHttpClient.Builder()
				.readTimeout(READ_TIME_OUT, TimeUnit.MILLISECONDS)
				.connectTimeout(CONNECT_TIME_OUT, TimeUnit.MILLISECONDS)
				.addInterceptor(mRewriteCacheControlInterceptor)
				.addNetworkInterceptor(mRewriteCacheControlInterceptor)
				.addInterceptor(headerInterceptor)
				.cache(cache)
				.hostnameVerifier(new HostnameVerifier()
				{
					@Override
					public boolean verify(String hostname, SSLSession session)
					{
						return true;
					}
				})
				.build();
		if (base_url == null) base_url = Config.api_host;
		Retrofit retrofit = new Retrofit.Builder()
				.client(client)
				.baseUrl(base_url)
				.addConverterFactory(ScalarsConverterFactory.create())
				.addConverterFactory(GsonConverterFactory.create(gson))
				.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
				.build();
		apiService = retrofit.create(ApiService.class);
		lastUrl = base_url;
	}

	public static ApiService getApiService(String base_url)
	{
		synchronized (Api.class)
		{
			if (apiService == null || !lastUrl.equals(base_url))
			{
				new ApiStrategy(base_url);
			}
		}
		return apiService;
	}

	public static ApiService getApiService()
	{
		if (apiService == null || !lastUrl.equals(Config.api_host))
		{
			synchronized (Api.class)
			{
				if (apiService == null || !lastUrl.equals(Config.api_host))
				{
					new ApiStrategy(null);
				}
			}
		}
		return apiService;
	}
}