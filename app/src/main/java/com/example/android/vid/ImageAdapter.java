package com.example.android.vid;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    static ArrayList<String> posters;
    static String API_KEY = "292045ea04fcdc083fb7908532fbd563";
    static boolean sortbypop;

    public ImageAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return 20;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {

            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Picasso.with(mContext).load("http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=c20129fdf73b5df3ab44548ad7f73586").into(imageView);
        } else {
            imageView = (ImageView) convertView;
        }

        return imageView;
    }

    public String[] getPathsFromAPI(boolean sortbypop) {

        while (true) {
            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;
            String JSONResults;

            try {
                String urlString = null;

                if (sortbypop) {
                    urlString = "https://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=" + API_KEY;
                } else {
                    urlString = "https://api.themoviedb.org/3/discover/movie?sort_by=vote_average.desc&vote_count.gte=500&api_key=" + API_KEY;
                }

                URL url = new URL(urlString);

                httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if (inputStream == null) {
                    return null;
                }

                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                JSONResults = buffer.toString();

                try {
                    return getPathsFromJSON(JSONResults);
                } catch (JSONException e) {
                    return null;
                }

            } catch (Exception e) {
                continue;
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (final IOException e) {
                        return null;
                    }
                }
            }
        }
    }

    public String[] getPathsFromJSON(String JSONStringParam) throws JSONException {

        JSONObject JSONString = new JSONObject(JSONStringParam);

        JSONArray movieArray = JSONString.getJSONArray("results");
        String[] results = new String[movieArray.length()];

        for (int i = 0; i < movieArray.length(); i++) {
            JSONObject movie = movieArray.getJSONObject(i);
            String moviePath = movie.getString("poster_path");
            results[i] = moviePath;
        }
        return results;
    }

}
