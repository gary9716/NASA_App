package com.lab430.model;

/**
 * Created by lab430 on 15/6/21.
 */
public class Duration {

    public final static int numVol = 4;
    public int[] volume = new int[numVol];
    // 0 : sec, 1 : min, 2 : hour, 3 : day
    private StringBuffer strBuf = new StringBuffer();

    @Override
    public String toString() {
        strBuf.setLength(0);
        strBuf.append(volume[3]);
        strBuf.append("days ");
        strBuf.append(volume[2]);
        strBuf.append(':');
        strBuf.append(volume[1]);
        strBuf.append(':');
        strBuf.append(volume[0]);

        return strBuf.toString();
    }
}
