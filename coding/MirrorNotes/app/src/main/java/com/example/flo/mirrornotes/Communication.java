package com.example.flo.mirrornotes;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class Communication implements IReceptionNotifier {

    private IReceptionNotifier notifier;


    public Communication(IReceptionNotifier notifier)
    {
        this.notifier = notifier;
    }

    public void synchronize()
    {
        NetworkGet networkGet = new NetworkGet(this);
        networkGet.execute();
    }

    public void sendData(String data)
    {
        NetworkPost networkPost = new NetworkPost();

        String[] str = data.split("\n");
        JSONArray arr = new JSONArray();

        for(String x : str)
        {
            JSONObject o = new JSONObject();
            String[] a = x.split(",");
            try {
                o.put("memoTitle", a[0]);
                o.put("level", "INFO");
                o.put("item", a[1]);
                o.put("timestamp", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(new Date()));
                arr.put(o);
            } catch (JSONException e) {
                System.out.println(e.toString());
            }
        }

        networkPost.execute(arr.toString());
    }

    @Override
    public void receptionCallback(String res)
    {
        try {
            StringWriter sw = new StringWriter();

            JSONArray arr = new JSONArray(res);

            for(int i = 0; i < arr.length(); i++)
            {
                JSONObject o = arr.getJSONObject(i);
                sw.append(o.getString("memoTitle"));
                sw.append(",");
                sw.append(o.getString("item"));
                sw.append("\n");
            }

            notifier.receptionCallback(sw.toString());
        } catch(JSONException e) {
            System.out.println(e.toString());
        } catch(Exception e) {
            System.out.println(e.toString());
        }
    }
}
