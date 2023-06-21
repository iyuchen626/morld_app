package com.example.morldapp_demo01.adapter;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.morldapp_demo01.Tools;
import com.example.morldapp_demo01.activity.VideoLandscape;
import com.example.morldapp_demo01.databinding.ListMyVideoBinding;
import com.example.morldapp_demo01.databinding.ListMyVideoBinding;
import com.example.morldapp_demo01.mirror.Client;
import com.example.morldapp_demo01.pojo.FilmListResponse;
import com.example.morldapp_demo01.pojo.FilmPOJO;
import com.example.morldapp_demo01.retrofit2.ApiStrategy;

import java.io.File;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MyVideoAdapter extends RecyclerView.Adapter<MyVideoAdapter.ViewHolder>
{
	public List<FilmPOJO> mDataset;
	AppCompatActivity activity;

	public class ViewHolder extends RecyclerView.ViewHolder
	{
		public ListMyVideoBinding binding;

		public ViewHolder(ListMyVideoBinding view)
		{
			super(view.getRoot());
			binding = view;
		}
	}

	public MyVideoAdapter(AppCompatActivity a, List<FilmPOJO> myDataset)
	{
		activity = a;
		mDataset = myDataset;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
		ListMyVideoBinding binding = ListMyVideoBinding.inflate(layoutInflater, parent, false);
		return new ViewHolder(binding);
	}

	@Override
	public void onBindViewHolder(ViewHolder viewHolder, int position)
	{
		final FilmPOJO entity = mDataset.get(position);
		Tools.loadImg(viewHolder.binding.cover, entity.present_image_slug);
		viewHolder.binding.title.setText(entity.title);
		viewHolder.binding.time.setText("上傳時間: 2023/06/21");
		viewHolder.binding.likes.setText("按讚人數"+entity.like_count+"人");
		viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(activity);
				builder.setTitle("選擇");
				String[] animals = {"播放", "刪除"};
				builder.setItems(animals, new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						switch (which)
						{
							case 0:
								Bundle bundle = new Bundle();
								bundle.putSerializable("data", entity);
								bundle.putSerializable("MEDIA_TYPE", "file");
								activity.startActivity(new Intent(activity, VideoLandscape.class).putExtras(bundle));
								break;
							case 1:
								Tools.showQuestion(activity, "訊息", "確認刪除影片?", "是", "否", new View.OnClickListener()
								{
									@Override
									public void onClick(View v)
									{
										ApiStrategy.getApiService().mm刪除個人影片(entity).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<FilmListResponse>()
										{
											@Override
											public void accept(FilmListResponse filmListResponse) throws Exception
											{
												mDataset.remove(position);
												notifyDataSetChanged();
											}
										}, new Consumer<Throwable>() {
											@Override
											public void accept(Throwable throwable) throws Exception
											{
												Tools.showError(activity, "API跳錯:"+throwable.getMessage());
											}
										});
									}
								}, new View.OnClickListener() {
									@Override
									public void onClick(View v)
									{

									}
								});
								break;
						}
					}
				});
				AlertDialog dialog = builder.create();
				dialog.show();
			}
		});
	}

	@Override
	public int getItemCount()
	{
		return mDataset.size();
	}


}
