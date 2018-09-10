package com.ceri.android.ucounter.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ceri.android.ucounter.R;
import com.ceri.android.ucounter.ui.CounterInfo;

import java.util.ArrayList;
import java.util.List;

///**************************
//* Drawer ListView Adapter
//**************************/
public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.CounterViewHolder>{

    /** Context of parent activity */
    private Context mContext;

    /** List of data */
    private List<CounterInfo> mList = new ArrayList<>();

    public DrawerAdapter(Context context, List<CounterInfo> list) {
        mContext = context;
        mList = list;
    }

    /** Creates View Holders */
    @NonNull
    @Override
    public DrawerAdapter.CounterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.counter_drawer_item, parent, false);
        CounterViewHolder counterViewHolder = new CounterViewHolder(view);
        Log.e("DrawerAdapter", "Called");
        return counterViewHolder;
    }

    /** Binds View Holder */
    @Override
    public void onBindViewHolder(@NonNull DrawerAdapter.CounterViewHolder holder, int position,
                                 @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    /** Gets info from the CounterInfo list and assigns them to the item */
    @Override
    public void onBindViewHolder(@NonNull CounterViewHolder holder, int position) {
        holder.mName.setText(mList.get(position).getName());
        holder.mValue.setText(String.valueOf(mList.get(position).getValue()));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class CounterViewHolder extends RecyclerView.ViewHolder{
        TextView mName;
        TextView mValue;

        CounterViewHolder(View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.counter_drawer_item_name);
            mValue = itemView.findViewById(R.id.counter_drawer_item_value);
        }
    }

}