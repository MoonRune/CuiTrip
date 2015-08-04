package com.lab.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

import com.commonsware.cwac.endless.EndlessAdapter;
import com.loopj.android.http.SyncHttpClient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class PagedEndlessAdapter<T> extends EndlessAdapter {

    public static class IncomingData<E> {
        public List<E> itemList;
        public boolean reachEnd;
        public boolean error = false;
    }

    private IncomingData<T> mReceivedData;

    private int mCurrentPage = 0;

    public PagedEndlessAdapter(ListAdapter wrapped) {
        super(wrapped);
    }

    public PagedEndlessAdapter(ListAdapter wrapped, boolean keepOnAppending) {
        super(wrapped, keepOnAppending);
    }

    public PagedEndlessAdapter(Context context, ListAdapter wrapped, int pendingResource) {
        super(context, wrapped, pendingResource);
    }

    public PagedEndlessAdapter(Context context, ListAdapter wrapped, int pendingResource, boolean keepOnAppending) {
        super(context, wrapped, pendingResource, keepOnAppending);
    }

    private SyncHttpClient createClient() {
        return new SyncHttpClient();
    }

    private synchronized void setReceivedData(IncomingData<T> items) {
        mReceivedData = items;
    }

    private synchronized IncomingData<T> getReceivedData() {
        return mReceivedData;
    }

    public void initStartPage(int startPage) {
        mCurrentPage = startPage;
    }

    public int getPageSize() {
        return 10;
    }

    @Override
    protected boolean cacheInBackground() throws Exception {
        IncomingData<T> incoming = workForNewItems(mCurrentPage + 1);
        if (incoming != null && !incoming.error && !incoming.reachEnd) {
            mCurrentPage += 1;
        }
        setReceivedData(incoming);
        return incoming != null && !incoming.error ? !incoming.reachEnd : false;
    }

    protected abstract IncomingData<T> workForNewItems(int requestPageNum) throws Exception;

    @Override
    protected void appendCachedData() {
        IncomingData<T> incoming = getReceivedData();
        setReceivedData(null);

        if (incoming != null && incoming.itemList != null) {
            ListAdapter adapter = getWrappedAdapter();
            if (adapter != null && adapter instanceof WrappedAdapter) {
                ((WrappedAdapter) adapter).addItems(incoming.itemList);
            }
        }
    }

    public abstract static class WrappedAdapter<E> extends BaseAdapter {
        List<E> mItems;
        Context mContext;

        public WrappedAdapter(Context context, List<E> initItems) {
            mContext = context;
            mItems = initItems;
//            if (initItems != null)
//                mItems = new ArrayList<E>(initItems);
        }

        public void addItem(E item) {
            if (mItems == null) mItems = new ArrayList<E>();
            mItems.add(item);
        }

        public void addItems(Collection<E> items) {
            if (mItems == null) mItems = new ArrayList<E>();
            mItems.addAll(items);
        }

        public Context getContext() {
            return mContext;
        }

        @Override
        public int getCount() {
            if (mItems != null) return mItems.size();
            return 0;
        }

        @Override
        public E getItem(int position) {
            if (mItems != null && mItems.size() > position && position >= 0) {
                return mItems.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return convertView;
        }
    }

}