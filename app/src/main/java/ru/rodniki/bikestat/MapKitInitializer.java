package ru.rodniki.bikestat;

import android.content.Context;

import com.yandex.mapkit.MapKitFactory;

public class MapKitInitializer {
    public void initializeMapKit(Context context, Boolean initialized) {
        if (!initialized) {
            MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY);
            MapKitFactory.initialize(context);
        }
    }
}
