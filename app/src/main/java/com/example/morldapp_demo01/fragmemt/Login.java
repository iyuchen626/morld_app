package com.example.morldapp_demo01.fragmemt;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.morldapp_demo01.Base;
import com.example.morldapp_demo01.Config;
import com.example.morldapp_demo01.Tools;
import com.example.morldapp_demo01.databinding.LoginBinding;
import com.example.morldapp_demo01.pojo.LoginRequest;
import com.example.morldapp_demo01.pojo.LoginResponse;
import com.example.morldapp_demo01.pojo.RegisterRequest;
import com.example.morldapp_demo01.pojo.RegisterResponse;
import com.example.morldapp_demo01.retrofit2.ApiStrategy;

import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class Login extends Base
{
	LoginBinding binding;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		binding = LoginBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		binding.btnSend.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				mm發送驗證碼();
			}
		});
		binding.btnOk.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				mm登入();
			}
		});
	}

	void mm發送驗證碼()
	{
		if (binding.inputUsername.getText().length() == 0)
		{
			Tools.showError(getActivity(), "請輸入信箱");
			return;
		}
		Tools.showProgress(getActivity(), "請稍後...");
		RegisterRequest model = new RegisterRequest();
		model.email = binding.inputUsername.getText().toString();
		Disposable disposable = ApiStrategy.getApiService().mm註冊(model).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<RegisterResponse>()
		{
			@Override
			public void accept(RegisterResponse res) throws Exception
			{
				Tools.hideProgress();
				if (res.success)
				{
					Tools.toastSuccess(getActivity(), res.message);
				}
				else
				{
					Tools.showError(getActivity(), res.message);
				}
			}
		}, new Consumer<Throwable>()
		{
			@Override
			public void accept(Throwable throwable) throws Exception
			{
				Tools.hideProgress();
				Tools.showError(getActivity(), throwable.getMessage());
			}
		});
	}

	void mm登入()
	{
		if (binding.inputUsername.getText().length() == 0)
		{
			Tools.showError(getActivity(), "請輸入信箱");
			return;
		}
		if (binding.inputCode.getText().length() != 6)
		{
			Tools.showError(getActivity(), "請輸入驗證碼");
			return;
		}
		Tools.showProgress(getActivity(), "請稍後...");
		LoginRequest model = new LoginRequest();
		model.email = binding.inputUsername.getText().toString();
		model.captcha = binding.inputCode.getText().toString();
		Disposable disposable = ApiStrategy.getApiService().mmd登入(model).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<LoginResponse>()
		{
			@Override
			public void accept(LoginResponse res) throws Exception
			{
				Tools.hideProgress();
				Log.i(Config.TAG, res.message);
				if (res.success)
				{
					Tools.mm儲存帳密(getApplicationContext(), Config.KEY_TOKEN, res.data.token);
					Tools.toastSuccess(getActivity(), res.message);
				}
				else
				{
					Tools.showError(getActivity(), res.message);
				}
			}
		}, new Consumer<Throwable>()
		{
			@Override
			public void accept(Throwable throwable) throws Exception
			{
				Tools.hideProgress();
				Tools.showError(getActivity(), throwable.getMessage());
			}
		});
	}
}


