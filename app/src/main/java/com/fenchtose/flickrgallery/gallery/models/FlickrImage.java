package com.fenchtose.flickrgallery.gallery.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonObject;

/**
 * Created by Jay Rambhia on 8/5/16.
 */

public class FlickrImage implements Parcelable {

    private String id;
    private String owner;
    private String secret;
    private String server;
    private int farm;
    private String title;
    private int isPublic;

    private String url;

    private FlickrImage() {

    }

    protected FlickrImage(Parcel in) {
        id = in.readString();
        owner = in.readString();
        secret = in.readString();
        server = in.readString();
        farm = in.readInt();
        title = in.readString();
        isPublic = in.readInt();
        url = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(owner);
        dest.writeString(secret);
        dest.writeString(server);
        dest.writeInt(farm);
        dest.writeString(title);
        dest.writeInt(isPublic);
        dest.writeString(url);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FlickrImage> CREATOR = new Creator<FlickrImage>() {
        @Override
        public FlickrImage createFromParcel(Parcel in) {
            return new FlickrImage(in);
        }

        @Override
        public FlickrImage[] newArray(int size) {
            return new FlickrImage[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public String getSecret() {
        return secret;
    }

    public String getServer() {
        return server;
    }

    public int getFarm() {
        return farm;
    }

    public String getTitle() {
        return title;
    }

    public int getIsPublic() {
        return isPublic;
    }

    public String getUrl() {
        return url;
    }

    public static FlickrImage parseJson(JsonObject jsonObject) {
        FlickrImage image = new FlickrImage();
        image.id = jsonObject.get("id").getAsString();
        image.owner = jsonObject.get("owner").getAsString();
        image.secret = jsonObject.get("secret").getAsString();
        image.server = jsonObject.get("server").getAsString();
        image.farm = jsonObject.get("farm").getAsInt();
        image.title = jsonObject.get("title").getAsString();
        image.url = "http://farm" + image.farm +  ".static.flickr.com/" + image.server +"/" + image.id + "_" + image.secret + ".jpg";

        return image;
    }
}
