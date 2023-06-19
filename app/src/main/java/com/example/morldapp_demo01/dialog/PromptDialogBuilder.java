package com.example.morldapp_demo01.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class PromptDialogBuilder extends AlertDialog.Builder
{
	AppCompatActivity context;
	private AlertDialog dialog;
	private InternalListener listener;
	private CharSequence positiveText;
	private Integer positiveResourceId = android.R.string.ok;
	private CharSequence negativeText;
	private Integer negativeResourceId = android.R.string.cancel;
	private static Integer padding;
	private static final float PADDING = 10f;

	public interface PromptListener
	{
		public void onInputProvided(String input);

		public void onCancel();

		public class Impl implements PromptListener
		{

			@Override
			public void onInputProvided(String input)
			{
			}

			@Override
			public void onCancel()
			{
			}
		}

	}

	private Integer getPadding()
	{
		if (padding == null)
		{
			Display display = context.getWindowManager().getDefaultDisplay();
			DisplayMetrics metrics = new DisplayMetrics();
			display.getMetrics(metrics);
			padding = (int) (PADDING * metrics.density);
		}
		return padding;
	}

	@Override
	public AlertDialog create()
	{
		if (positiveText != null)
		{
			setPositiveButton(positiveText, listener);
		}
		else
		{
			setPositiveButton(positiveResourceId, listener);
		}
		if (negativeText != null)
		{
			setNegativeButton(negativeText, listener);
		}
		else
		{
			setNegativeButton(negativeResourceId, listener);
		}
		dialog = super.create();
		listener.input = new EditText(getContext());
		int pad = getPadding();
		listener.input.setOnKeyListener(listener);
		dialog.setView(listener.input, pad, 0, pad, pad);
		return dialog;
	}

	public PromptDialogBuilder(AppCompatActivity context)
	{
		super(context);
		this.context = context;
		listener = new InternalListener(new PromptListener.Impl());
	}

	public PromptDialogBuilder setPromptListener(PromptListener listener)
	{
		this.listener = new InternalListener(listener);
		return this;
	}

	public PromptDialogBuilder setPositiveButton(CharSequence text)
	{
		positiveText = text;
		return this;
	}

	public PromptDialogBuilder setPositiveButton(int textId)
	{
		positiveResourceId = textId;
		return this;
	}

	public PromptDialogBuilder setNegativeButton(CharSequence text)
	{
		negativeText = text;
		return this;
	}

	public PromptDialogBuilder setNegativeButton(int textId)
	{
		negativeResourceId = textId;
		return this;
	}

	private class InternalListener implements OnClickListener, OnKeyListener
	{

		private final PromptListener listener;
		private EditText input;

		private InternalListener(PromptListener listener)
		{
			super();
			if (listener == null)
			{
				throw new NullPointerException("Listener can't be null");
			}
			this.listener = listener;
		}

		@Override
		public void onClick(DialogInterface dialog, int which)
		{
			switch (which)
			{
				case AlertDialog.BUTTON_POSITIVE:
					final CharSequence email = input.getText();
				case AlertDialog.BUTTON_NEGATIVE:
				default:
					dialog.dismiss();
					break;
			}
		}

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event)
		{
			if (keyCode == KeyEvent.KEYCODE_ENTER)
			{
				onClick(dialog, AlertDialog.BUTTON_POSITIVE);
				return true;
			}
			return false;
		}
	}

}