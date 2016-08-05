package com.fenchtose.flickrgallery.gallery.api;

import android.support.annotation.NonNull;

import com.fenchtose.flickrgallery.gallery.models.FlickrFeed;

import rx.Observable;

/**
 * Created by Jay Rambhia on 8/5/16.
 */
public interface PhotosProvider {

    Observable<FlickrFeed> getPhotos(@NonNull String query, int page, int limit);

}
