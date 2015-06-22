package com.lab430.model;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.util.Date;

/**
 * Created by lab430 on 15/6/20.
 */
public class ProcessInfo {

    @SerializedName("S")
    public String state;

    @SerializedName("PID")
    public Integer pid;

    @SerializedName("USER")
    public String userName;

    @SerializedName("%CPU")
    public Float cpuUsage;

    @SerializedName("%MEM")
    public Float memUsage;

    @SerializedName("ELAPSED")
    public Duration elapsedTime;

    @SerializedName("COMMAND")
    public String issuedCommand;

    public boolean areConditionsFulfilled(JSONObject filterObj) {
        return true;
        //wait for implemented
    }


}
