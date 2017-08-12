package com.example.hello.maps1.requestEngines;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Ivan on 12.03.2017.
 */

public class PostClass extends AsyncTask<String, Void, Void> {
    private final Context context;

    public PostClass(Context c){

        this.context = c;
//            this.error = status;
//            this.type = t;
    }

    protected void onPreExecute(){
    }

    @Override
    protected Void doInBackground(String... params) {
        try {
            URL url = new URL("http://192.168.0.106");

            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            String urlParameters = "action=getOneObject";
            connection.setRequestMethod("POST");
            connection.setRequestProperty("USER-AGENT", "Mozilla/5.0");
            connection.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");
            connection.setDoOutput(true);
            DataOutputStream dStream = new DataOutputStream(connection.getOutputStream());
            dStream.writeBytes(urlParameters);
            dStream.flush();
            dStream.close();

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = "";
            StringBuilder responseOutput = new StringBuilder();
            while((line = br.readLine()) != null ) {
                responseOutput.append(line);
            }
            br.close();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute() {
    }
}
