package com.example.flo.mirrornotes;

import android.os.AsyncTask;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkPost extends AsyncTask<String, Void, Void> {

    final String urlSet = "http://magicmirror:8080/AddCompleteNote";

    @Override
    protected Void doInBackground(String... strings) {

        String data = strings[0]; //data to post
        OutputStream out;
        HttpURLConnection conn = null;

        try {
            URL url = new URL(urlSet);
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setConnectTimeout(2000);

            out = new BufferedOutputStream(conn.getOutputStream());

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            writer.write(data);
            writer.flush();
            writer.close();
            out.close();

            //conn.connect();
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
