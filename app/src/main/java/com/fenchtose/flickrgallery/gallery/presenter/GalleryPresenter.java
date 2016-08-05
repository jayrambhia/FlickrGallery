package com.fenchtose.flickrgallery.gallery.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.fenchtose.flickrgallery.gallery.models.FlickrFeed;

/**
 * Created by Jay Rambhia on 8/5/16.
 */
public interface GalleryPresenter {

    void attachView();
    void detachView();

    void onQueryEdited();
    void search(@NonNull String query);
    void reload();
    void loadMore();

    @Nullable FlickrFeed saveInstance();
    @Nullable String saveQuery();
    void reloadInstance(@NonNull FlickrFeed feed, @NonNull String query);

    void openImage(@Nullable View view, int position);
}
