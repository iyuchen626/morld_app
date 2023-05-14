package com.example.morldapp_demo01.retrofit2;


import com.example.morldapp_demo01.Config;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class Api
{


	public static ApiService apiService;

	private Api()
	{
		Gson gson = new GsonBuilder()
				.setDateFormat("yyyy-MM-dd HH:mm:ss")
				.create();
		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl(Config.api_host)
				.addConverterFactory(GsonConverterFactory.create(gson))
//                .addConverterFactory(GsonConverterFactory.create())//请求的结果转为实体类
				//适配RxJava2.0,RxJava1.x则为RxJavaCallAdapterFactory.create()
				.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
				.build();
		apiService = retrofit.create(ApiService.class);
	}

	//单例
	public static ApiService getApiService()
	{
		if (apiService == null)
		{
			synchronized (Api.class)
			{
				if (apiService == null)
				{
					new Api();
				}
			}
		}
		return apiService;
	}

}
