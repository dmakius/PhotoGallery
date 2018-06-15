package com.bignerdranch.android.photogallery;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class FlickerFetchr {
    private static final String TAG = "PhotoGalleryFragment";
    public byte[] getUrlBytes(String urlSpec) throws IOException{
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
            Log.i(TAG, "Failed!");
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws Exception{
        return new String(getUrlBytes(urlSpec));
    }
}
