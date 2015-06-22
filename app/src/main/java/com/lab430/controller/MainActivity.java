package com.lab430.controller;

import com.baoyz.widget.PullRefreshLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lab430.model.Duration;
import com.lab430.model.PSInfoRendererAdapter;
import com.lab430.model.PSRestClient;
import com.lab430.model.ProcessInfo;
import com.lab430.model.ProjectConfig;
import com.lab430.utility.DurationDeserializer;

import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.lab430.psinfoandnotificationreceiver.R;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;

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
    @InjectView(R.id.swipeRefreshLayout) PullRefreshLayout refreshLayout;

    private Context context;
    private Handler mainHandler;
    private Gson gson;
    private Type listType;
    private PSInfoRendererAdapter psInfoRendererAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainHandler = new Handler(getMainLooper());
        context = this;
        initViews();

        String jsonStr = readFile("test.json");

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationDeserializer());
        listType = new TypeToken<List<ProcessInfo>>(){}.getType();
        gson = gsonBuilder.create();

        ArrayList<ProcessInfo> psInfoList = null;
        psInfoList = parseJSON(jsonStr);

        psInfoRendererAdapter = new PSInfoRendererAdapter(this, psInfoList);
        psListView.setAdapter(psInfoRendererAdapter.getAdapter());



    }

    private void initViews() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View inflatedView = inflater.inflate(R.layout.activity_main, null, false);
        setContentView(inflatedView);
        ButterKnife.inject(this, inflatedView);
        refreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_MATERIAL);
        refreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // start refresh
//                mainHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        refreshLayout.setRefreshing(false);
//                    }
//                },3000);

                PSRestClient.get(ProjectConfig.allPSInfoRoute, null, new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        super.onSuccess(statusCode, headers, response);
                        //parseJSON(response);

                        refreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        Log.d(PSRestClient.debug_tag, throwable.getMessage());
                        Toast.makeText(context, "failed to update, status code:" + statusCode, Toast.LENGTH_LONG);

                        refreshLayout.setRefreshing(false);
                    }

                });


            }
        });
    }

    private ArrayList<ProcessInfo> parseJSON(JSONArray jsonArray) {
        return parseJSON(jsonArray.toString());
    }


    private ArrayList<ProcessInfo> parseJSON(String jsonStr) {
        try {
            return gson.fromJson(jsonStr, listType);
        }
        catch(Exception e) {
            Log.d(debug_tag, e.getMessage());
            return null;
        }
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
        int orderID = item.getOrder();
        if(orderID < 6) {
            psInfoRendererAdapter.sort(orderID);
        }
        else {
            psInfoRendererAdapter.changeOrdering();
            if(psInfoRendererAdapter.orderingCoeff == 1) {
                item.setTitle("descent");
            }
            else {
                item.setTitle("ascent");
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        psListView.setSelection(0);

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
