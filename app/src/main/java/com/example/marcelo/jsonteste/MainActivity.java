package com.example.marcelo.jsonteste;

import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;


public class MainActivity extends ActionBarActivity {

    private String uri = "http://api.wipmania.com/json";
    private Map retorno;
    final CountDownLatch latch = new CountDownLatch(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tv = (TextView) findViewById(R.id.saida);

        //getJson(uri);
        sendJson("marcelo@gmail.com","123");
        //Map interno = (Map) retorno.get("address");

       //tv.setText(interno.keySet().toString() + interno.values().toString());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getJson(final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //Object retorno = null;
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpGet request = new HttpGet();
                    request.setURI(new URI(url));
                    HttpResponse response = httpClient.execute(request);
                    InputStream content = response.getEntity().getContent();
                    Reader reader = new InputStreamReader(content);
                    Gson gson =  new Gson();
                    retorno = gson.fromJson(reader, HashMap.class);
                    content.close();
                    latch.countDown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void sendJson(final String email, final String pwd) {
        final String URL = "http://validate.jsontest.com/";
        Thread t = new Thread() {

            public void run() {
                Looper.prepare(); //For Preparing Message Pool for the child Thread
                HttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
                HttpResponse response;
                JSONObject json = new JSONObject();

                try {
                    HttpPost post = new HttpPost(URL);
                    json.put("email", email);
                    json.put("password", pwd);
                    ArrayList parameters = new ArrayList(2);
                    parameters.add(new BasicNameValuePair("json",json.toString()));
                    post.setEntity(new UrlEncodedFormEntity(parameters));

                    //StringEntity se = new StringEntity( json.toString());
                    //se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    response = client.execute(post);

                    /*Checking response */
                    if(response!=null){
                        InputStream in = response.getEntity().getContent(); //Get the data in the entity
                        Log.v("retorno", in.toString());
                    }

                } catch(Exception e) {
                    e.printStackTrace();
                }

                Looper.loop(); //Loop in the message queue
            }
        };

        t.start();
    }
}
