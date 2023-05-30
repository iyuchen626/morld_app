package com.example.morldapp_demo01.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.morldapp_demo01.Tools;
import com.example.morldapp_demo01.databinding.ListHome2Binding;
import com.example.morldapp_demo01.pojo.FilmPOJO;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class Home2Adapter extends RecyclerView.Adapter<Home2Adapter.ViewHolder>
{
	public List<FilmPOJO> mDataset;

	public  class ViewHolder extends RecyclerView.ViewHolder
	{
		public ListHome2Binding binding;

		public ViewHolder(ListHome2Binding view)
		{
			super(view.getRoot());
			binding = view;
		}
	}

	public Home2Adapter(List<FilmPOJO> myDataset)
	{
		mDataset = myDataset;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
		ListHome2Binding binding = ListHome2Binding.inflate(layoutInflater, parent, false);
		return new ViewHolder(binding);
	}

	@Override
	public void onBindViewHolder(ViewHolder viewHolder, int position)
	{
		//把對應index的資料找出來放到entity
		final FilmPOJO entity = mDataset.get(position);
		Tools.loadImg(viewHolder.binding.cover, entity.present_image_slug);
		viewHolder.binding.title.setText(entity.title);
	}

	@Override
	public int getItemCount()
	{
		return mDataset.size();
	}


}
