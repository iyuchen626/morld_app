package com.example.morldapp_demo01.dialog;


import android.content.DialogInterface;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import io.reactivex.disposables.Disposable;


public class DBase extends DialogFragment
{
	protected List<Disposable> disposableList = new ArrayList<>();
	protected boolean isLoading;

	protected void mm取消Disposable()
	{
		for (Disposable d : disposableList)
		{
			if (!d.isDisposed()) d.dispose();
		}
	}

	@Override
	public void show(@NonNull FragmentManager manager, @Nullable String tag)
	{
		FragmentTransaction ft = manager.beginTransaction();
		ft.add(this, tag);
		ft.commitAllowingStateLoss();
	}

	@Override
	public void onDismiss(@NonNull DialogInterface dialog)
	{
		super.onDismiss(dialog);
		mm取消Disposable();
	}


}
