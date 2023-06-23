package com.example.morldapp_demo01.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.morldapp_demo01.Config;
import com.example.morldapp_demo01.R;
import com.example.morldapp_demo01.Tools;
import com.example.morldapp_demo01.adapter.MyVideoAdapter;
import com.example.morldapp_demo01.databinding.MyDownloadVideoBinding;
import com.example.morldapp_demo01.databinding.MyVideoBinding;
import com.example.morldapp_demo01.pojo.FilmListResponse;
import com.example.morldapp_demo01.pojo.FilmPOJO;
import com.example.morldapp_demo01.retrofit2.ApiStrategy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MyDownloadVideo extends Base
{
	MyDownloadVideoBinding binding;
	private MyVideoAdapter adapter;
	int page = 1;
	boolean isLoading = false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		binding = MyDownloadVideoBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		binding.back.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				finish();
			}
		});
		RecyclerView recyclerView = binding.recyclerView;
		DividerItemDecoration itemDecorator = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
		itemDecorator.setDrawable( ContextCompat.getDrawable(getActivity(), R.drawable.divider1));
		LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
		recyclerView.setLayoutManager(mLayoutManager);
		recyclerView.addItemDecoration(itemDecorator);
		List<FilmPOJO> fake = new ArrayList<>();
		adapter = new MyVideoAdapter((AppCompatActivity) getActivity(),fake);
		recyclerView.setAdapter(adapter);


		File ff  = getExternalFilesDir("videos");
		File[] files = ff.listFiles();
		for (int i = 0; i < files.length; i++)
		{
			Log.d("Files", "FileName:" + files[i].getName());
			String s = Tools.mmRead(getActivity(), "videos/"+files[i].getName());
			FilmPOJO data = Tools.getGson().fromJson(s, FilmPOJO.class);
			if(data == null) continue;
			adapter.mDataset.add(data);
		}
		adapter.notifyDataSetChanged();
	}

}


