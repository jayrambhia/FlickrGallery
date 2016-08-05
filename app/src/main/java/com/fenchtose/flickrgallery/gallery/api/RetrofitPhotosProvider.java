package com.fenchtose.flickrgallery.gallery.api;

import android.support.annotation.NonNull;

import com.fenchtose.flickrgallery.gallery.models.FlickrFeed;
import com.google.gson.JsonObject;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Jay Rambhia on 8/5/16.
 */
public class RetrofitPhotosProvider implements PhotosProvider {

    private final String apiKey;
    private Retrofit retrofit;
    private FlickrApi flickrApi;

    public RetrofitPhotosProvider(String apiKey) {
        this.apiKey = apiKey;
        retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(FlickrApi.BASE_URL)
                .build();

        flickrApi = retrofit.create(FlickrApi.class);
    }

    @Override
    public Observable<FlickrFeed> getPhotos(@NonNull String query, int page, int limit) {
        return flickrApi.getData(query, apiKey, page, limit)
                .flatMap(new Func1<JsonObject, Observable<FlickrFeed>>() {
                    @Override
                    public Observable<FlickrFeed> call(JsonObject jsonObject) {
                        return Observable.just(FlickrFeed.parseJson(jsonObject.get("photos").getAsJsonObject()));
                    }
                });
    }
}
