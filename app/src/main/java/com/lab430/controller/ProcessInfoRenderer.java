package com.lab430.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lab430.psinfoandnotificationreceiver.R;
import com.lab430.model.ProcessInfo;
import com.pedrogomez.renderers.Renderer;

import java.text.SimpleDateFormat;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * Created by lab430 on 15/6/21.
 */
public class ProcessInfoRenderer extends Renderer<ProcessInfo> {

    private Context mContext;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("DD'days' HH:mm:ss");

    @InjectView(R.id.pid) TextView pid;

    @InjectView(R.id.pstate) TextView state;

    @InjectView(R.id.uname) TextView userName;

    @InjectView(R.id.cpuusage) TextView cpuusage;

    @InjectView(R.id.memusage) TextView memusage;

    @InjectView(R.id.elapsedtime) TextView elapsedTime;

    @InjectView(R.id.cmd) TextView issuedCommand;

    public ProcessInfoRenderer(Context context) {
        mContext = context;
    }

    @Override
    protected void setUpView(View view) {
        //set up view by findViewById
        //replaced by butterKnife tool
    }

    @Override
    protected void hookListeners(View view) {
        //set up ui listener
        //replaced by butterKnife tool
    }

    @Override
    protected View inflate(LayoutInflater layoutInflater, ViewGroup viewGroup) {
        View inflatedView = layoutInflater.inflate(R.layout.basic_psinfo_list_item, viewGroup, false);
        ButterKnife.inject(this, inflatedView);

        return inflatedView;
    }

    @Override
    public void render() {

        ProcessInfo psInfo = getContent();
        pid.setText("pid: " + String.valueOf(psInfo.pid));
        state.setText("state: " + psInfo.state);
        userName.setText("user: " + psInfo.userName);
        cpuusage.setText("cpu: " + String.valueOf(psInfo.cpuUsage) + "%");
        memusage.setText("mem: " + String.valueOf(psInfo.memUsage) + "%");
        elapsedTime.setText("duration: " + psInfo.elapsedTime.toString());
        issuedCommand.setText("command: " + psInfo.issuedCommand);

    }
}
