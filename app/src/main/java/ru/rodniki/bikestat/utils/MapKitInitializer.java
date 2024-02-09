package ru.rodniki.bikestat.utils;

import android.content.Context;

import com.yandex.mapkit.MapKitFactory;

import ru.rodniki.bikestat.BuildConfig;

public class MapKitInitializer {
    public void initializeMapKit(Context context, Boolean initialized) {
        if (!initialized) {
            MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY);
            MapKitFactory.initialize(context);
        }
    }
}
