package com.example.morldapp_demo01.adapter;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.morldapp_demo01.Tools;
import com.example.morldapp_demo01.activity.VideoLandscape;
import com.example.morldapp_demo01.databinding.ListHome1Binding;
import com.example.morldapp_demo01.pojo.FilmPOJO;

import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class Home1Adapter extends RecyclerView.Adapter<Home1Adapter.ViewHolder>
{
	public List<FilmPOJO> mDataset;
	AppCompatActivity activity;

	public class ViewHolder extends RecyclerView.ViewHolder
	{
		public ListHome1Binding binding;

		public ViewHolder(ListHome1Binding view)
		{
			super(view.getRoot());
			binding = view;
		}
	}

	public Home1Adapter(AppCompatActivity a, List<FilmPOJO> myDataset)
	{
		activity = a;
		mDataset = myDataset;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
		ListHome1Binding binding = ListHome1Binding.inflate(layoutInflater, parent, false);
		return new ViewHolder(binding);
	}

	@Override
	public void onBindViewHolder(ViewHolder viewHolder, int position)
	{
		//把對應index的資料找出來放到entity
		final FilmPOJO entity = mDataset.get(position);
		Tools.loadImg(viewHolder.binding.cover, entity.present_image_slug);
		viewHolder.binding.title.setText(entity.title);
		viewHolder.itemView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Bundle bundle = new Bundle();
				bundle.putSerializable("data", entity);
				AlertDialog.Builder builder = new AlertDialog.Builder(activity);
				builder.setTitle("選擇類型");
				String[] animals = {"播檔案", "播串流"};
				builder.setItems(animals, new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						switch (which)
						{
							case 0:
								bundle.putSerializable("MEDIA_TYPE", "file");
								activity.startActivity(new Intent(activity, VideoLandscape.class).putExtras(bundle));
								break;
							case 1:
								bundle.putSerializable("MEDIA_TYPE", "stream");
								activity.startActivity(new Intent(activity, VideoLandscape.class).putExtras(bundle));
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
