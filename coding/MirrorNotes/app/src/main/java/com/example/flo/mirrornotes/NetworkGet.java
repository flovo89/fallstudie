package com.example.flo.mirrornotes;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class NetworkGet extends AsyncTask<Void, Void, String> {

    final String urlGet = "http://magicmirror:8080/GetCompleteNote";
    private IReceptionNotifier notifier;

    NetworkGet(IReceptionNotifier notifier)
    {
        this.notifier = notifier;
    }

    @Override
    protected String doInBackground(Void... voids) {

        URL url;
        StringBuffer response = new StringBuffer();
        try {
            url = new URL(urlGet);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url");
        }

        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(2000);

            // handle the response
            int status = conn.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                throw new IOException("Post failed with error code " + status);
            } else {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return response.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        notifier.receptionCallback(result);
    }
}
