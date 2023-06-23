package com.example.morldapp_demo01.retrofit2;


import com.example.morldapp_demo01.pojo.FilmListResponse;
import com.example.morldapp_demo01.pojo.FilmPOJO;
import com.example.morldapp_demo01.pojo.LoginRequest;
import com.example.morldapp_demo01.pojo.LoginResponse;
import com.example.morldapp_demo01.pojo.RegisterRequest;
import com.example.morldapp_demo01.pojo.RegisterResponse;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiService
{
	@POST("register")
	Observable<RegisterResponse> mm註冊(@Body RegisterRequest description);

	@POST("verifyLogin")
	Observable<LoginResponse> mmd登入(@Body LoginRequest description);

	@GET("film")
	Observable<FilmListResponse> mm影片清單(@Query("title") String title, @Query("page") int page);

	@GET("film_p")
	Observable<FilmListResponse> mm個人影片清單(@Query("title") String title, @Query("page") int page);

	@HTTP(method = "DELETE", path = "film", hasBody = true)
	Observable<FilmListResponse> mm刪除個人影片(@Body FilmPOJO uuid);

	@Multipart
	@POST("film")
	Observable<String> mm上傳個人影片(@Part MultipartBody.Part f1, @Part MultipartBody.Part f2, @Part MultipartBody.Part f3,@Part("title") RequestBody title, @Part("description") RequestBody description, @Part("film_type_id") RequestBody film_type_id, @Part("sell") RequestBody sell, @Part("publish") RequestBody publish);
}