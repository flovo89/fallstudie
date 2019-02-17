package com.example.flo.mirrornotes;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringWriter;
import java.util.regex.Pattern;

public class Communication implements IReceptionNotifier {

    private NetworkGet networkGet;
    private NetworkPost networkPost;

    private IReceptionNotifier notifier;


    public Communication(IReceptionNotifier notifier)
    {
        this.networkGet = new NetworkGet(this);
        this.networkPost = new NetworkPost();

        this.notifier = notifier;
    }

    public void synchronize()
    {
        this.networkGet.execute();
    }

    /*public String getNotesWithTitle(String title) throws JSONException
    {
        JSONObject obj = new JSONObject(readData);
        JSONArray arr = new JSONArray(obj);

        StringWriter sw = new StringWriter();

        for(int i = 0; i < arr.length(); i++)
        {
            if(arr.getJSONObject(i).getString("memoTitle").equals(title))
            {
                sw.append(arr.getJSONObject(i).getString("item"));
                sw.append("\r\n");
            }
        }

        return sw.toString();
    }*/

    public void sendData(String data)
    {
        //TODO: Back to json
    }

    @Override
    public void receptionCallback(String res)
    {
        try {
            StringWriter sw = new StringWriter();

            String str = res.replace("[","").replace("]", "");

            String[] spl = str.split(Pattern.quote("},"));

            for(String x : spl)
            {
                //TODO: check if last char }, add if not
                JSONObject obj = new JSONObject(x);

                sw.append(obj.getString("memoTitle"));
                sw.append(",");
                sw.append(obj.getString("item"));
                sw.append("\r\n");
            }

            notifier.receptionCallback(sw.toString());
        } catch(JSONException e) {

        } catch(Exception e) {
            System.out.println(e.toString());
        }
    }
}
