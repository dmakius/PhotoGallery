package com.bignerdranch.android.photogallery;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FlickerFetchr {
    private static final String TAG = "FlickrFetchr";
    private static final String API_KEY = "36ee800a078e3aaaaa905a735082eee1";


    public byte[] getUrlBytes(String urlSpec) throws IOException{
        Log.i(TAG, "getURLBytes()!");
        Log.i(TAG, urlSpec);
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        Log.i(TAG, "Trying to Fetch Data");
        try{
            ByteArrayOutputStream out  = new ByteArrayOutputStream();
            Log.i(TAG, "Trying!");
            InputStream in = connection.getInputStream();
            Log.i(TAG, "Trying MORE!");
            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK){
                Log.i(TAG, "CRASH!");
                throw new IOException(connection.getResponseMessage() + ": with " + urlSpec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0){
                out.write(buffer, 0, bytesRead);
            }

            out.close();
            return out.toByteArray();

        }finally{
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws Exception{
        Log.i(TAG, "getUrlString()!");
        return new String(getUrlBytes(urlSpec));
    }

    public List<GalleryItem> fetchItems(){
        List<GalleryItem> items = new ArrayList<>();

        Log.i(TAG, "FetchingItems()!");
        try{
            String url = Uri.parse("https://api.flickr.com/services/rest/")
                    .buildUpon()
                    .appendQueryParameter("method", "flickr.photos.getRecent")
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("nojsoncallback", "1")
                    .appendQueryParameter("extras", "url_s")
                    .build().toString();
            String jsonString = getUrlString(url);
            Log.i(TAG,"Received JSON: " + jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseItems(items, jsonBody);
        }catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items " + ioe);
        }catch (JSONException je){
            Log.e(TAG, "Failed to parce JSON", je);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

    private void parseItems(List<GalleryItem> items, JSONObject jsonBody) throws IOException, JSONException {
        JSONObject photosJsonObject = jsonBody.getJSONObject("photos");
        JSONArray photosJsonArray = photosJsonObject.getJSONArray("photo");

        for(int i = 0; i < photosJsonArray.length(); i++){
            JSONObject photoJsonObject = photosJsonArray.getJSONObject(i);

            GalleryItem item = new GalleryItem();
            item.setId(photoJsonObject.getString("id"));
            item.setCaption(photoJsonObject.getString("title"));

            if(!photoJsonObject.has("url_s")){
                continue;
            }
            item.setUrl(photoJsonObject.getString("url_s"));
            items.add(item);
        }
    }
}
