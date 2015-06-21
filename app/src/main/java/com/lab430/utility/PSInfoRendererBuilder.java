package com.lab430.utility;

import android.content.Context;

import com.lab430.controller.ProcessInfoRenderer;
import com.lab430.model.ProcessInfo;
import com.pedrogomez.renderers.Renderer;
import com.pedrogomez.renderers.RendererBuilder;

import java.util.ArrayList;

/**
 * Created by lab430 on 15/6/21.
 */
public class PSInfoRendererBuilder extends RendererBuilder<ProcessInfo>{

    public PSInfoRendererBuilder(Context context) {
        ArrayList<Renderer<ProcessInfo>> prototypes = getPrototypes(context);
        setPrototypes(prototypes);

    }

    private ArrayList<Renderer<ProcessInfo>> getPrototypes(Context context) {
        ArrayList<Renderer<ProcessInfo>> prototypes = new ArrayList<>();
        prototypes.add(new ProcessInfoRenderer(context));
        return prototypes;
    }


    @Override
    protected Class getPrototypeClass(ProcessInfo processInfo) {
        return ProcessInfoRenderer.class;
    }




}
