package com.fenchtose.flickrgallery.gallery;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.bumptech.glide.RequestManager;
import com.fenchtose.flickrgallery.R;
import com.fenchtose.flickrgallery.gallery.models.FlickrImage;
import com.fenchtose.flickrgallery.widgets.SquareImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jay Rambhia on 8/5/16.
 */
public class PhotosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_PHOTO = 1;
    private static final int VIEW_TYPE_LOADER = 2;

    private static final int ITEM_ID_LOADER = -1;

    private Context context;
    private LayoutInflater inflater;

    private List<FlickrImage> photos;

    // TODO create a differet interface for image caching
    private RequestManager imageProvider;

    private boolean showLoading = false;

    private Callback callback;

    public PhotosAdapter(Context context, RequestManager imageProvider) {
        this.context = context;
        this.imageProvider = imageProvider;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_LOADER) {
            return new LoaderViewHolder(inflater.inflate(R.layout.item_loader_layout, parent, false));
        } else if (viewType == VIEW_TYPE_PHOTO) {
            return new ImageViewHolder(inflater.inflate(R.layout.item_photo_layout, parent, false));
        }

        throw new RuntimeException("Invalid view type: " + viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isPositionLoader(position)) {
            LoaderViewHolder viewHolder = (LoaderViewHolder) holder;
            viewHolder.progressBar.setVisibility(showLoading ? View.VISIBLE : View.GONE);
            return;
        }

        ImageViewHolder viewHolder = (ImageViewHolder)holder;
        FlickrImage image = photos.get(position);
        imageProvider.load(image.getUrl()).into(viewHolder.imageView);
    }

    @Override
    public int getItemCount() {
        if (photos == null || photos.isEmpty()) {
            return 0;
        }

        int size = photos.size();
        if (showLoading) {
            size++;
        }

        return size;
    }

    public boolean isPositionLoader(int position) {
        return showLoading && position == photos.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionLoader(position)) {
            return VIEW_TYPE_LOADER;
        }

        return VIEW_TYPE_PHOTO;
    }

    @Override
    public long getItemId(int position) {
        if (isPositionLoader(position)) {
            return ITEM_ID_LOADER;
        }

        return photos.get(position).getId().hashCode();
    }

    public void setShowLoading(boolean showLoading) {
        this.showLoading = showLoading;
    }

    public void setPhotos(List<FlickrImage> photos) {
        this.photos = photos;
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        private SquareImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = (SquareImageView)itemView;
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callback != null) {
                        callback.onImageClicked(imageView, getAdapterPosition());
                    }
                }
            });
        }
    }

    public class LoaderViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.progress_bar) ProgressBar progressBar;

        public LoaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        void onImageClicked(@Nullable View view, int position);
    }
}
