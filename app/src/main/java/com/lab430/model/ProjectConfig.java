package com.lab430.model;

/**
 * Created by lab430 on 15/6/20.
 */
public class ProjectConfig {
    public final static String serverIP = "140.112.30.37";
    public final static int serverPort = 8000;
    public final static String serverURL = "http://" + serverIP + ":" + serverPort;

    public final static String allPSInfoRouteUncached = "/parsedPSUncached";
    public final static String allPSInfoRoute = "/parsedPS";

    //Pubnub
    public final static String pubKey = "pub-c-071abdd4-bbd4-4387-98e1-aab8abf4744a";
    public final static String subKey = "sub-c-0caf38e0-1403-11e5-af43-0619f8945a4f";
    public final static String channelName = "PSInfo";
    public final static String pubnubTag = "pubnubDebug";

}
