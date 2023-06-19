package com.example.morldapp_demo01.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FilmPOJO implements Serializable
{
	public String uuid;
	public String title;
	public String description;
	public String present_image_slug;
	public String video_slug;
	public String video_hls_slug;
	public String txt_slug;
	public String video_time;
	public String view_count;
	public String like_count;
	public float video_offset; //先隨便取名，後端應該要存的offset
	public List<KeyImageInfo> key_image_info = new ArrayList<>();
	public DetailInfo detail_info;
	public UserInfo user_info;

	public FilmPOJO() {
		video_offset = 0;
	}

	public class UserInfo implements Serializable
	{
		public String email;
		public String name;
		public String avatar_image_slug;
	}

	public class DetailInfo implements Serializable
	{
		public boolean publish;
		public boolean sell;
		public String film_review_status;
		public String process_status;
	}

	public class KeyImageInfo implements Serializable
	{
		public String key_time;
		public String key_image_slug;
	}
}
