package com.lab430.model;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Filter;
import android.widget.Filterable;

import com.lab430.utility.PSInfoRendererBuilder;
import com.pedrogomez.renderers.AdapteeCollection;
import com.pedrogomez.renderers.RendererAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by lab430 on 15/6/21.
 */
public class PSInfoRendererAdapter implements Filterable{

    public static final String debug_tag = "psInfoAdapter";
    public int orderingCoeff = 1;

    RendererAdapter<ProcessInfo> rendererAdapter;
    ArrayList<ProcessInfo> allData = null;
    ArrayList<ProcessInfo> filteredData = null;
    ItemFilter itemFilter = new ItemFilter();

    public void changeOrdering() {
        orderingCoeff *= -1;
    }

    public PSInfoRendererAdapter(Context context, ArrayList<ProcessInfo> psInfoList) {
        allData = psInfoList;
        filteredData = psInfoList;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        PSInfoRendererBuilder psInfoRendererBuilder = new PSInfoRendererBuilder(context);
        mPSInfoCollection.addAll(psInfoList);
        rendererAdapter = new RendererAdapter<ProcessInfo>(layoutInflater, psInfoRendererBuilder, mPSInfoCollection);

    }


    public void sort(int metricIndex) {
        Collections.sort(filteredData, comparators[metricIndex]);
        rendererAdapter.notifyDataSetChanged();
    }

    abstract class PSInfoComparator implements Comparator<ProcessInfo> {
    }

    private PSInfoComparator[] comparators = new PSInfoComparator[]{
            new PSInfoComparator() {
                @Override
                public int compare(ProcessInfo p1, ProcessInfo p2) {
                    return orderingCoeff * p1.pid.compareTo(p2.pid);
                }
            },
            new PSInfoComparator() {
                @Override
                public int compare(ProcessInfo p1, ProcessInfo p2) {
                    return orderingCoeff * p1.userName.compareTo(p2.userName);
                }
            },
            new PSInfoComparator() {
                @Override
                public int compare(ProcessInfo p1, ProcessInfo p2) {
                    return orderingCoeff * p1.state.compareTo(p2.userName);
                }
            },
            new PSInfoComparator() {
                @Override
                public int compare(ProcessInfo p1, ProcessInfo p2) {
                    return orderingCoeff * p1.cpuUsage.compareTo(p2.cpuUsage);
                }
            },
            new PSInfoComparator() {
                @Override
                public int compare(ProcessInfo p1, ProcessInfo p2) {
                    return orderingCoeff * p1.memUsage.compareTo(p2.memUsage);
                }
            },
            new PSInfoComparator() {
                @Override
                public int compare(ProcessInfo p1, ProcessInfo p2) {
                    return orderingCoeff * p1.elapsedTime.compareTo(p2.elapsedTime);
                }
            }
    };


    @Override
    public Filter getFilter() {
        return itemFilter;
    }

    public RendererAdapter<ProcessInfo> getAdapter() {
        return rendererAdapter;
    }

    private class ItemFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();

            String filterString = constraint.toString().toLowerCase();
            JSONObject jsonFilter = null;
            try {
                jsonFilter = new JSONObject(filterString);
            }
            catch(JSONException e) {
                Log.d(debug_tag, e.getMessage());
                filterResults.count = allData.size();
                filterResults.values = allData;
                return filterResults;
            }

            final List<ProcessInfo> dataSource = allData;
            int totalCount = dataSource.size();

            final ArrayList<ProcessInfo> resultList = new ArrayList<>();

            for(int i = 0;i < totalCount;i++) {
                ProcessInfo psInfo = dataSource.get(i);
                if(psInfo.areConditionsFulfilled(jsonFilter)) {
                    resultList.add(psInfo);
                }
            }

            filterResults.count = resultList.size();
            filterResults.values = resultList;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults filterResults) {

            rendererAdapter.clear();
            rendererAdapter.addAll((ArrayList<ProcessInfo>)filterResults.values);
            rendererAdapter.notifyDataSetChanged();

        }

    }

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


}
