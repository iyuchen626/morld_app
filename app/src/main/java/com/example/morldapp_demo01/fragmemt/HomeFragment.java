package com.example.morldapp_demo01.fragmemt;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.morldapp_demo01.Config;
import com.example.morldapp_demo01.R;
import com.example.morldapp_demo01.Tools;
import com.example.morldapp_demo01.adapter.Home1Adapter;
import com.example.morldapp_demo01.adapter.Home2Adapter;
import com.example.morldapp_demo01.databinding.FragmentHomeBinding;
import com.example.morldapp_demo01.event.SearchVideoEvent;
import com.example.morldapp_demo01.pojo.FilmListResponse;
import com.example.morldapp_demo01.pojo.FilmPOJO;
import com.example.morldapp_demo01.retrofit2.ApiStrategy;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class HomeFragment extends Fragment{
    FragmentHomeBinding binding;
    private Home1Adapter adapter1;
    private Home2Adapter adapter2;

    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater,container,false);

        List<SlideModel> res = new ArrayList<>();
        res.add(new SlideModel("https://picsum.photos/id/237/1000/1000", ScaleTypes.CENTER_CROP));
        res.add(new SlideModel("https://picsum.photos/id/137/1000/1000",ScaleTypes.CENTER_CROP));
        res.add(new SlideModel("https://picsum.photos/id/239/1000/1000",ScaleTypes.CENTER_CROP));
        binding.imageSlider.setImageList(res);

        RecyclerView recyclerView = binding.recyclerView1;
        DividerItemDecoration itemDecorator = new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL);
        itemDecorator.setDrawable( ContextCompat.getDrawable(getActivity(),R.drawable.divider1));
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(itemDecorator);
        List<FilmPOJO> fake = new ArrayList<>();
        adapter1 = new Home1Adapter((AppCompatActivity) getActivity(),fake);
        recyclerView.setAdapter(adapter1);


        recyclerView = binding.recyclerView2;
        itemDecorator = new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider1));
        mLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(itemDecorator);
        fake = new ArrayList<>();
//        for(int i =1; i<= 100; i++) fake.add(new Home1POJO());
        adapter2 = new Home2Adapter(fake);
        recyclerView.setAdapter(adapter2);
        return binding.getRoot();
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
        reload("");
    }

    void reload(String title)
    {
        Tools.showProgress((AppCompatActivity) getActivity(), "請稍後...");
        Disposable disposable = ApiStrategy.getApiService().mm影片清單(title).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<FilmListResponse>()
        {
            @Override
            public void accept(FilmListResponse res) throws Exception
            {
                Tools.hideProgress((AppCompatActivity) getActivity());
                Log.i(Config.TAG, res.error);
                if (res.error.equals(""))
                {
                    adapter1.mDataset.clear();
                    adapter1.mDataset.addAll(res.data);
                    adapter1.notifyDataSetChanged();
                    adapter2.mDataset.clear();
                    adapter2.mDataset.addAll(res.data);
                    adapter2.notifyDataSetChanged();
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

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SearchVideoEvent event) {
        reload(event.like);
    }
}


