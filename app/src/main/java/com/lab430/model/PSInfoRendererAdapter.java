package com.lab430.model;

import android.content.Context;
import android.view.LayoutInflater;

import com.lab430.utility.PSInfoRendererBuilder;
import com.pedrogomez.renderers.AdapteeCollection;
import com.pedrogomez.renderers.RendererAdapter;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by lab430 on 15/6/21.
 */
public class PSInfoRendererAdapter{

    RendererAdapter<ProcessInfo> rendererAdapter;
    AdapteeCollection<ProcessInfo> mPSInfoCollection = new AdapteeCollection<ProcessInfo>() {
        ArrayList<ProcessInfo> psInfoList = null;

        @Override
        public int size() {
            return psInfoList == null ? 0 : psInfoList.size();
        }

        @Override
        public ProcessInfo get(int pos) {
            return psInfoList.get(pos);
        }

        @Override
        public boolean add(ProcessInfo processInfo) {
            if(psInfoList == null) {
                psInfoList = new ArrayList<>();
            }
            return psInfoList.add(processInfo);
        }

        @Override
        public boolean remove(Object obj) {
            if(psInfoList != null) {
                return psInfoList.remove(obj);
            }
            else {
                return false;
            }
        }

        @Override
        public boolean addAll(Collection<? extends ProcessInfo> collection) {
            if(collection instanceof ArrayList) {
                psInfoList = (ArrayList)collection;
            }
            else {
                if(psInfoList == null) {
                    psInfoList = new ArrayList<>();
                }

                for(ProcessInfo psInfo : collection) {
                    psInfoList.add(psInfo);
                }
            }

            return true;
        }

        @Override
        public boolean removeAll(Collection<?> collection) {
            if(psInfoList != null) {
                boolean removeResult = true;
                for(Object obj : collection) {
                    if(!psInfoList.remove(obj)) {
                        removeResult = false;
                    }
                }
                return removeResult;

            }
            else {
                return false;
            }
        }

        @Override
        public void clear() {
            if(psInfoList != null) {
                psInfoList.clear();
            }
        }
    };

    public PSInfoRendererAdapter(Context context, ArrayList<ProcessInfo> psInfoList) {

        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        PSInfoRendererBuilder psInfoRendererBuilder = new PSInfoRendererBuilder(context);
        mPSInfoCollection.addAll(psInfoList);
        rendererAdapter = new RendererAdapter<ProcessInfo>(layoutInflater, psInfoRendererBuilder, mPSInfoCollection);

    }

    public RendererAdapter<ProcessInfo> getAdapter() {
        return rendererAdapter;
    }

}
