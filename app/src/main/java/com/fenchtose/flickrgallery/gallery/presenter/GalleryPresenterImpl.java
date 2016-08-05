package com.fenchtose.flickrgallery.gallery.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.fenchtose.flickrgallery.gallery.GalleryView;
import com.fenchtose.flickrgallery.gallery.api.PhotosProvider;
import com.fenchtose.flickrgallery.gallery.models.FlickrFeed;
import com.fenchtose.flickrgallery.gallery.models.FlickrImage;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Jay Rambhia on 8/5/16.
 */
public class GalleryPresenterImpl implements GalleryPresenter {

    private static final String TAG = "GalleryPresneter";
    private GalleryView galleryView;
    private PhotosProvider photosProvider;
    private FlickrFeed feed;

    private String currentQuery;

    private boolean isLoading = false;

    private CompositeSubscription _subscriptions;

    private static final int LIMIT = 15;

    public GalleryPresenterImpl(GalleryView galleryView, PhotosProvider photosProvider) {
        this.galleryView = galleryView;
        this.photosProvider = photosProvider;
    }

    @Override
    public void attachView() {
    }

    @Override
    public void detachView() {
        cancelApiCall();
    }

    @Override
    public void onQueryEdited() {
        cancelApiCall();
    }


    @Override
    public void search(@NonNull String query) {

        if (query.equals(currentQuery)) {
            // don't call
            return;
        }

        if (feed != null) {
            feed = null;
        }

        currentQuery = query;

        galleryView.showLoading(true);
        loadData(query, 1, LIMIT);
    }

    private void loadData(@NonNull String query, int page, int limit) {

        Log.d(TAG, "load data: " + query + ", " + page + ", " + limit);

        cancelApiCall();
        _subscriptions = new CompositeSubscription();

        isLoading = true;

        Subscription subscription = photosProvider.getPhotos(query, page, limit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<FlickrFeed>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        // TODO show error
                        galleryView.showLoading(false);
                        isLoading = false;
                    }

                    @Override
                    public void onNext(FlickrFeed flickrFeed) {
                        if (feed != null) {
                            feed.add(flickrFeed);
                        } else {
                            feed = flickrFeed;
                        }

                        galleryView.showLoading(false);
                        galleryView.showLoadingMore(false);
                        galleryView.setData(feed.getPhotos());

                        isLoading = false;
                    }
                });

        _subscriptions.add(subscription);
    }

    @Override
    public void reload() {
        // TODO implement this
    }

    @Override
    public void loadMore() {

        Log.d(TAG, "load more: " + currentQuery + ", " + isLoading);

        if (isLoading) {
            return;
        }

        if (feed == null) {
            if (currentQuery == null) {
                // TODO show error
                return;
            }

            search(currentQuery);
            return;
        }

        loadData(currentQuery, feed.getPage() + 1, LIMIT);
        galleryView.showLoadingMore(true);
    }

    @Nullable
    @Override
    public FlickrFeed saveInstance() {
        return feed;
    }

    @Nullable
    @Override
    public String saveQuery() {
        return currentQuery;
    }

    @Override
    public void reloadInstance(@NonNull FlickrFeed savedFeed, @NonNull String query) {
        feed = savedFeed;
        currentQuery = query;

        galleryView.showLoading(false);
        galleryView.showLoadingMore(false);
        galleryView.setData(feed.getPhotos());
    }

    @Override
    public void openImage(@Nullable View view, int position) {
        if (position < 0 || feed == null || feed.getPhotos() == null || feed.getPhotos().size() <= position) {
            return;
        }

        FlickrImage image = feed.getPhotos().get(position);
        galleryView.openImage(view, image.getUrl());
    }

    private void cancelApiCall() {
        if (_subscriptions != null) {
            _subscriptions.unsubscribe();
        }
    }
}
