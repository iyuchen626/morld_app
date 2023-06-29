package com.example.morldapp_demo01.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.morldapp_demo01.databinding.ListControlMenuBinding;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class ControlMenuAdapter extends RecyclerView.Adapter<ControlMenuAdapter.ViewHolder>
{
	public List<String> mDataset;
	AppCompatActivity activity;

	public class ViewHolder extends RecyclerView.ViewHolder
	{
		public ListControlMenuBinding binding;

		public ViewHolder(ListControlMenuBinding view)
		{
			super(view.getRoot());
			binding = view;
		}
	}

	public ControlMenuAdapter(AppCompatActivity a, List<String> myDataset)
	{
		activity = a;
		mDataset = myDataset;
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
	{
		LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
		ListControlMenuBinding binding = ListControlMenuBinding.inflate(layoutInflater, parent, false);
		return new ViewHolder(binding);
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position)
	{
		ViewHolder viewHolder = (ViewHolder) holder;
		final String entity = mDataset.get(position);
		viewHolder.binding.title.setText(entity);
	}

	@Override
	public int getItemCount()
	{
		return mDataset.size();
	}


}
