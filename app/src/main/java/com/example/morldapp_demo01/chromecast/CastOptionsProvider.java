package com.example.morldapp_demo01.chromecast;

import android.content.Context;

import com.example.morldapp_demo01.R;
import com.google.android.gms.cast.LaunchOptions;
import com.google.android.gms.cast.framework.CastOptions;
import com.google.android.gms.cast.framework.OptionsProvider;
import com.google.android.gms.cast.framework.SessionProvider;
import com.google.android.gms.cast.framework.media.CastMediaOptions;
import com.google.android.gms.cast.framework.media.MediaIntentReceiver;
import com.google.android.gms.cast.framework.media.NotificationOptions;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CastOptionsProvider implements OptionsProvider {

    @Override
    public CastOptions getCastOptions(Context context) {
        NotificationOptions notificationOptions = new NotificationOptions.Builder()
                .setActions(Arrays.asList(MediaIntentReceiver.ACTION_SKIP_NEXT,
                        MediaIntentReceiver.ACTION_TOGGLE_PLAYBACK,
                        MediaIntentReceiver.ACTION_STOP_CASTING), new int[]{1, 2})
                .setTargetActivityClassName(ExpandedControlsActivity.class.getName())
                .build();
        CastMediaOptions mediaOptions = new CastMediaOptions.Builder()
//                .setImagePicker(new ImagePickerImpl())
                .setNotificationOptions(notificationOptions)
                .setExpandedControllerActivityClassName(ExpandedControlsActivity.class.getName())
                .build();
        /** Following lines enable Cast Connect */
        LaunchOptions launchOptions = new LaunchOptions.Builder()
                .setAndroidReceiverCompatible(true)
                .build();
        return new CastOptions.Builder()
                .setLaunchOptions(launchOptions)
                .setReceiverApplicationId(context.getString(R.string.app_id))
                .setCastMediaOptions(mediaOptions)
                .build();
    }

    @Nullable
    @Override
    public List<SessionProvider> getAdditionalSessionProviders(@NonNull Context context)
    {
        return null;
    }

//    override fun getAdditionalSessionProviders(context: Context): List<SessionProvider>? {
//        return null
//    }
}
