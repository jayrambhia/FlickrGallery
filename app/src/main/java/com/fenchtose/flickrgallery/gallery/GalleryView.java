package com.fenchtose.flickrgallery.gallery;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.fenchtose.flickrgallery.gallery.models.FlickrImage;

import java.util.List;

/**
 * Created by Jay Rambhia on 8/5/16.
 */
public interface GalleryView {

    void showLoading(boolean show);
    void setData(@NonNull List<FlickrImage> photos);
    void showLoadingMore(boolean show);

    void openImage(@Nullable View view, @NonNull String url);
}
