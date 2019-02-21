package com.example.flo.mirrornotes;

import android.os.AsyncTask;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkPost extends AsyncTask<String, Void, Void> {

    final String urlSet = "http://magicmirror:8080/AddCompleteNote";

    @Override
    protected Void doInBackground(String... strings) {

        String data = strings[0]; //data to post
        HttpURLConnection conn = null;

        try {
            URL url = new URL(urlSet);
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setConnectTimeout(2000);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            OutputStream out = new BufferedOutputStream(conn.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(data);
            writer.flush();
            writer.close();
            out.flush();
            out.close();

            conn.connect();
            conn.getResponseCode();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(conn != null)
            {
                conn.disconnect();
            }
        }

        return null;
    }
}
