package com.example.morldapp_demo01.dialog;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.morldapp_demo01.databinding.DialogNormalBinding;


public class Normal extends DBase
{
	DialogNormalBinding binding;
	TextView cancel;
	TextView ok;
	LinearLayout linearLayout5;
	TextView title;
	TextView content;
	String textContent = "";
	String textTitle = "";
	String textOK = "";
	String textCancel = "";
	View.OnClickListener okonClickListener;
	View.OnClickListener cancelonClickListener;

	public void setContent(String s)
	{
		textContent = s;
	}

	public void setTitle(String s)
	{
		textTitle = s;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		binding = DialogNormalBinding.inflate(getLayoutInflater());
		cancel = binding.cancel;
		ok = binding.ok;
		linearLayout5 = binding.linearLayout5;
		title = binding.title;
		content = binding.content;
		title.setText(textTitle);
		content.setText(textContent);
		ok.setText(textOK);
		ok.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				dismiss();
				okonClickListener.onClick(view);
			}
		});

		if (!textCancel.equals(""))
		{
			cancel.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View view)
				{
					dismiss();
					cancelonClickListener.onClick(view);
				}
			});
			cancel.setText(textCancel);
			cancel.setVisibility(View.VISIBLE);
			linearLayout5.setVisibility(View.VISIBLE);
		}
		if (textTitle.equals(""))
		{
			title.setVisibility(View.GONE);
		}
		if (textContent.equals("")) content.setVisibility(View.GONE);
		return binding.getRoot();
	}

	public void setOK(String s, View.OnClickListener cc)
	{
		textOK = s;
		okonClickListener = cc;
	}

	public void setCancel(String s, View.OnClickListener cc)
	{
		textCancel = s;
		cancelonClickListener = cc;
	}
}
