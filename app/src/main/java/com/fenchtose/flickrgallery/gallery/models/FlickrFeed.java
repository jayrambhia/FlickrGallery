package com.fenchtose.flickrgallery.gallery.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jay Rambhia on 8/5/16.
 */

public class FlickrFeed implements Parcelable {
    private int page;
    private int totalPages;
    private int perPage;
    private int total;
    private List<FlickrImage> photos;

    private FlickrFeed() {

    }

    protected FlickrFeed(Parcel in) {
        page = in.readInt();
        totalPages = in.readInt();
        perPage = in.readInt();
        total = in.readInt();
        photos = in.createTypedArrayList(FlickrImage.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(page);
        dest.writeInt(totalPages);
        dest.writeInt(perPage);
        dest.writeInt(total);
        dest.writeTypedList(photos);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FlickrFeed> CREATOR = new Creator<FlickrFeed>() {
        @Override
        public FlickrFeed createFromParcel(Parcel in) {
            return new FlickrFeed(in);
        }

        @Override
        public FlickrFeed[] newArray(int size) {
            return new FlickrFeed[size];
        }
    };

    public void add(FlickrFeed feed) {
        // assuming total, perpage and totalPages will be constant
        page = feed.page;
        photos.addAll(feed.getPhotos());
    }

    public int getPage() {
        return page;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getPerPage() {
        return perPage;
    }

    public int getTotal() {
        return total;
    }

    public List<FlickrImage> getPhotos() {
        return photos;
    }

    public static FlickrFeed parseJson(JsonObject jsonObject) {
        // get page and other details
        FlickrFeed feed = new FlickrFeed();
        feed.page = jsonObject.get("page").getAsInt();
        feed.totalPages = jsonObject.get("pages").getAsInt();
        feed.perPage = jsonObject.get("perpage").getAsInt();
        feed.total = jsonObject.get("total").getAsInt();

        feed.photos = new ArrayList<>();

        JsonArray photosArray = jsonObject.getAsJsonArray("photo");
        for (int i=0; i < photosArray.size(); i++) {
            feed.photos.add(FlickrImage.parseJson(photosArray.get(i).getAsJsonObject()));
        }

        return feed;
    }
}
