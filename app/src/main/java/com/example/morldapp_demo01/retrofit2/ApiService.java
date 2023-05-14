package com.example.morldapp_demo01.retrofit2;


import com.example.morldapp_demo01.pojo.LoginRequest;
import com.example.morldapp_demo01.pojo.RegisterRequest;
import com.example.morldapp_demo01.pojo.RegisterResponse;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService
{
	@POST("register")
	Observable<RegisterResponse> mm註冊(@Body RegisterRequest description);

	@POST("verifyLogin")
	Observable<RegisterResponse> mmd登入(@Body LoginRequest description);
}