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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

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
    private String zombieFilterStr = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainHandler = new Handler(getMainLooper());
        context = this;
        initViews();

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationDeserializer());
        listType = new TypeToken<List<ProcessInfo>>(){}.getType();
        gson = gsonBuilder.create();

        String jsonStr = readFile("test.json");
        ArrayList<ProcessInfo> psInfoList = null;
        psInfoList = parseJSON(jsonStr);

        try {
            JSONObject filterJson = new JSONObject();
            filterJson.put("state", "Z");
            zombieFilterStr = filterJson.toString();
        }
        catch (Exception e) {

        }

        psInfoRendererAdapter = new PSInfoRendererAdapter(this, psInfoList);
        psListView.setAdapter(psInfoRendererAdapter.getAdapter());

        Pubnub pubnub = new Pubnub(ProjectConfig.pubKey, ProjectConfig.subKey);
        try {
            pubnub.subscribe(ProjectConfig.channelName, pubnubCallback);
        }
        catch(Exception e) {
            Log.d(ProjectConfig.pubnubTag, "failed to subsrible channel");
        }
    }

    Callback pubnubCallback = new Callback() {

        @Override
        public void successCallback(String channel, Object message) {
            Log.d(ProjectConfig.pubnubTag,channel + " : " + message.toString());
            JSONObject jsonMsg = null;
            int eventCode = -1;
            try {
                jsonMsg = new JSONObject(message.toString());
                eventCode = jsonMsg.getInt("EventCode");
            }
            catch(Exception e) {
                Log.d(debug_tag, e.getMessage());
                return;
            }

            if(eventCode == ProjectConfig.SysEvent.ZombieReachThreshold.ordinal()) {
                int numZombies = 0;

                try {
                    numZombies = jsonMsg.getInt("#ZPS");
                } catch (Exception e) {
                    Log.d(debug_tag, e.getMessage());
                    return;
                }

                final int notifyID = 1; // 通知的識別號碼
                final int requestCode = notifyID; // PendingIntent的Request Code
                final Intent intent = getIntent(); // 目前Activity的Intent
                final int flags = PendingIntent.FLAG_CANCEL_CURRENT; // ONE_SHOT：PendingIntent只使用一次；CANCEL_CURRENT：PendingIntent執行前會先結束掉之前的；NO_CREATE：沿用先前的PendingIntent，不建立新的PendingIntent；UPDATE_CURRENT：更新先前PendingIntent所帶的額外資料，並繼續沿用
                final PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), requestCode, intent, flags); // 取得PendingIntent
                final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE); // 取得系統的通知服務
                final Notification notification = new Notification.Builder(getApplicationContext()).setSmallIcon(R.drawable.zombie).setContentTitle("殭屍潮").setContentText("警告：系統裡有" + numZombies + "隻殭屍").setContentIntent(pendingIntent).build(); // 建立通知
                notificationManager.notify(notifyID, notification); // 發送通知
            }
        }

        @Override
        public void errorCallback(String channel, PubnubError error) {
            Log.d(ProjectConfig.pubnubTag, "ERROR on channel " + channel
                    + " : " + error.toString());
        }

        @Override
        public void connectCallback(String channel, Object message) {
            super.connectCallback(channel, message);
            Log.d(ProjectConfig.pubnubTag,"connect to channel:" + channel);
        }

        @Override
        public void disconnectCallback(String channel, Object message) {
            super.disconnectCallback(channel, message);
            Log.d(ProjectConfig.pubnubTag, "disconnect from channel:" + channel);
        }

        @Override
        public void reconnectCallback(String channel, Object message) {
            super.reconnectCallback(channel, message);
            Log.d(ProjectConfig.pubnubTag,"reconnect from channel:" + channel);
        }

    };

    boolean successfullyUpdated = false;

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

                successfullyUpdated = false;

                PSRestClient.instance.get(ProjectConfig.allPSInfoRouteUncached, null, new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        super.onSuccess(statusCode, headers, response);
                        successfullyUpdated = true;
                        ArrayList<ProcessInfo> fetchedResult = parseJSON(response);
                        psInfoRendererAdapter.replaceAll(fetchedResult);
                        psInfoRendererAdapter.sort(previousSortingMetricIndex);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        Log.d(PSRestClient.debug_tag, throwable.getMessage());

                    }

                    @Override
                    public void onCancel() {
                        super.onCancel();

                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        if(!successfullyUpdated) {
                            Toast.makeText(context, "failed to update", Toast.LENGTH_LONG);
                        }
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

    private int previousSortingMetricIndex = 1;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int orderID = item.getOrder();
        if(orderID < 6) {
            previousSortingMetricIndex = orderID;
            psInfoRendererAdapter.sort(orderID);
        }
        else if(orderID == 6){
            psInfoRendererAdapter.changeOrdering();
            if(psInfoRendererAdapter.orderingCoeff == 1) {
                item.setTitle("descent");
            }
            else {
                item.setTitle("ascent");
            }

            psInfoRendererAdapter.sort(previousSortingMetricIndex);
        }
        else {
            psInfoRendererAdapter.getFilter().filter(zombieFilterStr);
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
