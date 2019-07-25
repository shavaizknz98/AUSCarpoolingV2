package com.example.auscarpoolingv2;

import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RideListAdapter extends RecyclerView.Adapter<RideListAdapter.MyViewHolder> {

    public ArrayList<RideList> rideList;
    public TextView rideDetails;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public MyViewHolder(View v) {
            super(v);
            textView = itemView.findViewById(R.id.rideDetails);
        }
    }

    public RideListAdapter(ArrayList<RideList> myDataset) {
        rideList = myDataset;
    }

    @Override
    public RideListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.riders_recycler_item, parent, false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.textView.setText(rideList.get(position).getAllDetails());
        Log.d("HELLOHELLO", rideList.get(position).getName());
        Linkify.addLinks(holder.textView,Linkify.ALL);

    }

    @Override
    public int getItemCount() {
        return rideList.size();
    }

}