package com.fenchtose.flickrgallery.gallery;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.fenchtose.flickrgallery.R;
import com.fenchtose.flickrgallery.full_screen.FullScreenActivity;
import com.fenchtose.flickrgallery.gallery.api.RetrofitPhotosProvider;
import com.fenchtose.flickrgallery.gallery.models.FlickrFeed;
import com.fenchtose.flickrgallery.gallery.models.FlickrImage;
import com.fenchtose.flickrgallery.gallery.presenter.GalleryPresenter;
import com.fenchtose.flickrgallery.gallery.presenter.GalleryPresenterImpl;
import com.fenchtose.flickrgallery.utils.RecyclerViewScrollListener;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

public class GalleryActivity extends AppCompatActivity implements GalleryView {

    private static final String TAG = "GalleryActivity";
    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.progress_bar) ProgressBar progressBar;
    @BindView(R.id.query_view) EditText queryView;

    private RequestManager glideRequestManager;
    private PhotosAdapter adapter;

    private GalleryPresenter presenter;

    private PublishSubject<String> querySubject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        ButterKnife.bind(this);

        progressBar.setVisibility(View.GONE);

        glideRequestManager = Glide.with(this);
        adapter = new PhotosAdapter(this, glideRequestManager);
        adapter.setHasStableIds(true);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (adapter.isPositionLoader(position)) {
                    return 3;
                }

                return 1;
            }
        });

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        adapter.setCallback(new PhotosAdapter.Callback() {
            @Override
            public void onImageClicked(@Nullable View view, int position) {
                presenter.openImage(view, position);
            }
        });

        recyclerView.addOnScrollListener(new RecyclerViewScrollListener() {
            @Override
            public void onScrollUp() {

            }

            @Override
            public void onScrollDown() {

            }

            @Override
            public void onLoadMore() {
                Log.d(TAG, "load more called");
                presenter.loadMore();
            }
        });

        presenter = new GalleryPresenterImpl(this, new RetrofitPhotosProvider("3e7cc266ae2b0e0d78e279ce8e361736"));
        presenter.attachView();

        querySubject = PublishSubject.create();
        queryView.addTextChangedListener(queryWatcher);
        querySubject
                .doOnNext(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        presenter.onQueryEdited();
                    }
                })
                .debounce(800, TimeUnit.MILLISECONDS)
                .filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String s) {
                        return s.length() > 3;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        presenter.search(s);
                    }
                });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        FlickrFeed feed = presenter.saveInstance();
        String currentQuery = presenter.saveQuery();

        if (feed == null || currentQuery == null) {
            return;
        }

        // TODO use constants for keys
        outState.putParcelable("feed", feed);
        outState.putString("query", currentQuery);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            FlickrFeed feed = savedInstanceState.getParcelable("feed");
            String currentQuery = savedInstanceState.getString("query");

            if (feed != null && currentQuery != null) {
                presenter.reloadInstance(feed, currentQuery);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
        queryView.removeTextChangedListener(queryWatcher);
        querySubject.onCompleted();
    }

    @Override
    public void showLoading(boolean show) {
        if (show) {
            recyclerView.setVisibility(View.GONE);
        }

        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setData(@NonNull List<FlickrImage> photos) {
        adapter.setPhotos(photos);
        adapter.notifyDataSetChanged();
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showLoadingMore(boolean show) {
        adapter.setShowLoading(show);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void openImage(@Nullable View view, @NonNull String url) {
        Intent intent = FullScreenActivity.getIntent(this, url);
        Bundle options = FullScreenActivity.getOptions(this, view);
        startActivity(intent, options);
    }

    private TextWatcher queryWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            querySubject.onNext(s.toString());
        }
    };
}
