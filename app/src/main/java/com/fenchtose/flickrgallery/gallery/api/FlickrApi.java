package com.fenchtose.flickrgallery.gallery.api;

import com.google.gson.JsonObject;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Jay Rambhia on 8/5/16.
 */
public interface FlickrApi {

    String BASE_URL = "https://api.flickr.com/services/";

    @GET("rest/?method=flickr.photos.search&format=json&nojsoncallback=1")
    Observable<JsonObject> getData(@Query("text") String query,
                                   @Query("api_key") String key,
                                   @Query("page") int page,
                                   @Query("per_page") int limit);
}
