package com.example.morldapp_demo01.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.morldapp_demo01.Config;
import com.example.morldapp_demo01.R;
import com.example.morldapp_demo01.Tools;
import com.example.morldapp_demo01.adapter.Home1Adapter;
import com.example.morldapp_demo01.adapter.MyVideoAdapter;
import com.example.morldapp_demo01.databinding.LoginBinding;
import com.example.morldapp_demo01.databinding.MyVideoBinding;
import com.example.morldapp_demo01.pojo.FilmListResponse;
import com.example.morldapp_demo01.pojo.FilmPOJO;
import com.example.morldapp_demo01.pojo.LoginRequest;
import com.example.morldapp_demo01.pojo.LoginResponse;
import com.example.morldapp_demo01.pojo.RegisterRequest;
import com.example.morldapp_demo01.pojo.RegisterResponse;
import com.example.morldapp_demo01.pojo.User;
import com.example.morldapp_demo01.retrofit2.ApiStrategy;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MyVideo extends Base
{
	MyVideoBinding binding;
	private MyVideoAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		binding = MyVideoBinding.inflate(getLayoutInflater());
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
		reload("");
	}

	void reload(String title)
	{
		Tools.showProgress((AppCompatActivity) getActivity(), "請稍後...");
		Disposable disposable = ApiStrategy.getApiService().mm個人影片清單(title).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<FilmListResponse>()
		{
			@Override
			public void accept(FilmListResponse res) throws Exception
			{
				Tools.hideProgress((AppCompatActivity) getActivity());
				Log.i(Config.TAG, res.error);
				if (res.error.equals(""))
				{
					adapter.mDataset.clear();
					adapter.mDataset.addAll(res.data);
					adapter.notifyDataSetChanged();
				}
				else
				{
					Tools.showError((AppCompatActivity) getActivity(), res.error);
				}
			}
		}, new Consumer<Throwable>()
		{
			@Override
			public void accept(Throwable throwable) throws Exception
			{
				Tools.hideProgress((AppCompatActivity) getActivity());
				Tools.showError((AppCompatActivity) getActivity(), throwable.getMessage());
			}
		});
	}

}


