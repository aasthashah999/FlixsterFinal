package com.example.flicks.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Config {
    //base URL for images
    String imageBaseUrl;
    //the poster size
    String posterSize;

    //the backdrop size to use when fetching images
    String backdropSize;

    public Config(JSONObject object) throws JSONException {
        JSONObject images = object.getJSONObject("images");
        imageBaseUrl = images.getString("secure_base_url");
        JSONArray posterSizeOptions = images.getJSONArray("poster_sizes");
        posterSize = posterSizeOptions.optString(3,"w342");
        //parse the backdrop sizes and use the option at the index 1 or 780 as fallback
        JSONArray backdropSizeOptions = images.getJSONArray("backdrop_sizes");
        backdropSize = backdropSizeOptions.optString(1,"w780");
    }

    //helper method to create the URL
    public String getImageUrl(String size, String path){
        return String.format("%s%s%s", imageBaseUrl,size, path);
    }

    public String getImageBaseUrl() {
        return imageBaseUrl;
    }

    public String getPosterSize() {
        return posterSize;
    }

    public String getBackdropSize() {
        return backdropSize;
    }
}
