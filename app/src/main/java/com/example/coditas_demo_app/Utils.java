package com.example.coditas_demo_app;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    private static final String TAG = "Utils";

    public static List<Profile> loadProfiles(String jsonurl){
        List<Profile> profileList = new ArrayList<>();
        // Making a request to url and getting response
        String jsonStr = makeServiceCall(jsonurl);
        if (jsonStr != null) {
            try {
                JSONObject jsonObject = new JSONObject(jsonStr);
                // Getting JSON Array node
                JSONArray jsonArray = jsonObject.getJSONArray("results");

                // looping through All Contacts
                for (int i = 0; i < jsonArray.length(); i++) {
                    //getting the json object of the particular index inside the array
                    JSONObject nameObject = jsonArray.getJSONObject(i).getJSONObject("user").getJSONObject("name");
                    String name = nameObject.getString("title") +" "+nameObject.getString("first") + " " + nameObject.getString("last");
                    JSONObject locationObject = jsonArray.getJSONObject(i).getJSONObject("user").getJSONObject("location");
                    String location = locationObject.getString("street") +","+locationObject.getString("city") + "," + locationObject.getString("state") + "," + locationObject.getString("zip");
                    String imageUrl = jsonArray.getJSONObject(i).getJSONObject("user").getString("picture");
                    String dob = jsonArray.getJSONObject(i).getJSONObject("user").getString("dob");
                    Log.d(TAG, "name :" + name);
                    Log.d(TAG, "location :" + location);
                    Log.d(TAG, "imageUrl" + imageUrl);
                    Log.d(TAG, "dob" + dob);
                    Profile profile = new Profile(name, imageUrl, dob,location);
                    profileList.add(profile);
                }
            } catch (final JSONException e) {
                Log.e(TAG, "Json parsing error: " + e.getMessage());
            }

        } else {
            Log.e(TAG, "Couldn't get json from server.");
        }
        return profileList;
    }

    public static String makeServiceCall(String reqUrl) {
        String response = null;
        try {
            URL url = new URL(reqUrl);
           // HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            URLConnection conn = url.openConnection();
           // Log.e("test","conn.getResponseMessage() : "+conn.g());
           // conn.setRequestMethod("GET");
            // read the response
            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = convertStreamToString(in);
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        } catch (ProtocolException e) {
            Log.e(TAG, "ProtocolException: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        return response;
    }

    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }
}
