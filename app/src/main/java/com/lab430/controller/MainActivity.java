package com.lab430.controller;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lab430.model.Duration;
import com.lab430.model.PSInfoRendererAdapter;
import com.lab430.model.ProcessInfo;
import com.lab430.utility.DurationDeserializer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.example.lab430.psinfoandnotificationreceiver.R;
import com.pedrogomez.renderers.AdapteeCollection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity {

    public final static String debug_tag = "MainActivity";

    @InjectView(R.id.PSInfoList) ListView psListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = LayoutInflater.from(this);
        View inflatedView = inflater.inflate(R.layout.activity_main, null, false);
        setContentView(inflatedView);
        ButterKnife.inject(this, inflatedView);

        String jsonStr = readFile("test.json");
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationDeserializer());
        Type listType = new TypeToken<List<ProcessInfo>>(){}.getType();
        ArrayList<ProcessInfo> psInfoList = null;
        try {
            psInfoList = gsonBuilder.create().fromJson(jsonStr, listType);
        }
        catch(Exception e) {
            Log.d(debug_tag, e.getMessage());
        }

        PSInfoRendererAdapter psInfoRendererAdapter = new PSInfoRendererAdapter(this, psInfoList);
        psListView.setAdapter(psInfoRendererAdapter.getAdapter());

//        PSRestClient.get(ProjectConfig.allPSInfoRoute, null, new JsonHttpResponseHandler() {
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
//                super.onSuccess(statusCode, headers, response);
//
//
//
//
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                super.onFailure(statusCode, headers, responseString, throwable);
//                Log.d(PSRestClient.debug_tag, throwable.getMessage());
//            }
//        });


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

    public AdapteeCollection<ProcessInfo> genListAdapteePSCollection() {


        return null;
    }


    public String readFile(String filename) {
        StringBuilder b = new StringBuilder();
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(getAssets().open(filename)));
            String str;
            while ((str = in.readLine()) != null) {
                b.append(str);
            }
        } catch (IOException e) {
            Log.d(debug_tag, e.getMessage());
        } finally {
            try {
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return b.toString();

    }
}
