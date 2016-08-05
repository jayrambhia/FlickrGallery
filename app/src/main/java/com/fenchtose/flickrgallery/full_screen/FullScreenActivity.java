package com.fenchtose.flickrgallery.full_screen;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.fenchtose.flickrgallery.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jay Rambhia on 8/5/16.
 */
public class FullScreenActivity extends AppCompatActivity {

    public static Intent getIntent(@NonNull Context context, @NonNull String url) {
        Intent intent = new Intent(context, FullScreenActivity.class);
        // use constants
        intent.putExtra("key_url", url);
        return intent;
    }

    public static Bundle getOptions(@NonNull Activity activity, @Nullable View view) {
        if (view == null) {
            return null;
        }

        return ActivityOptionsCompat.makeSceneTransitionAnimation(activity, view, "image_transition").toBundle();
    }

    @BindView(R.id.progress_bar) ProgressBar progressBar;
    @BindView(R.id.image_view) ImageView imageView;

    private RequestManager glideRequestManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_screen_activity_layout);
        ButterKnife.bind(this);

        if (getIntent().getExtras() == null) {
            // throw error
            return;
        }

        String url = getIntent().getStringExtra("key_url");
        if (url == null || url.isEmpty()) {
            //
            return;
        }

        glideRequestManager = Glide.with(this);

        glideRequestManager.load(url).into(new SimpleTarget<GlideDrawable>() {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                progressBar.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageDrawable(resource);
            }
        });

        imageView.setVisibility(View.GONE);
    }
}
