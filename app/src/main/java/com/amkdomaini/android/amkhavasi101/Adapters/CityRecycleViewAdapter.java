package com.amkdomaini.android.amkhavasi101.Adapters;


import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.amkdomaini.android.amkhavasi101.Interfaces.ItemTouchHelperAdapter;
import com.amkdomaini.android.amkhavasi101.Interfaces.ItemTouchHelperViewHolder;
import com.amkdomaini.android.amkhavasi101.Interfaces.OnStartDragListener;
import com.amkdomaini.android.amkhavasi101.Interfaces.RecyclerViewCityClick;
import com.amkdomaini.android.amkhavasi101.Modules.City;

import java.util.ArrayList;
import java.util.Collections;


public class CityRecycleViewAdapter extends RecyclerView.Adapter<CityRecycleViewAdapter.MyViewHolder> implements ItemTouchHelperAdapter {

    public RecyclerViewCityClick delegate;
    private ArrayList<City> dataModelArrayList;
    private Context mContext;
    private OnStartDragListener mDragStartListener;

    public CityRecycleViewAdapter(Context context, ArrayList<City> dataModelArrayList, OnStartDragListener mDragStartListener) {
        this.mContext = context;
        this.dataModelArrayList = dataModelArrayList;
        this.mDragStartListener = mDragStartListener;
    }

    public ArrayList<City> getDataModelArrayList() {
        return dataModelArrayList;
    }

    @Override
    @NonNull
    public CityRecycleViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.city_card_layout, parent, false);

        return new CityRecycleViewAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        //Ignore Lint Error, not a problem here.
        final City dataModel = dataModelArrayList.get(position);

        holder.mName.setText(dataModel.getFullName());


        holder.mButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onItemDismiss(position);
                notifyItemRangeChanged(position, dataModelArrayList.size());
            }
        });


        holder.mMainCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delegate.onItemClicked(String.valueOf(dataModel.getCityName()));
            }
        });


        holder.mMainCardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mDragStartListener.onStartDrag(holder);

                return false;
            }
        });
    }

    @Override
    public void onItemMove(int fromIndex, int toIndex) {

        if (fromIndex < dataModelArrayList.size() && toIndex < dataModelArrayList.size()) {
            if (fromIndex < toIndex) {
                for (int i = fromIndex; i < toIndex; i++) {
                    Collections.swap(dataModelArrayList, i, i + 1);
                }
            } else {
                for (int i = fromIndex; i > toIndex; i--) {
                    Collections.swap(dataModelArrayList, i, i - 1);
                }
            }
            notifyItemMoved(fromIndex, toIndex);
        }
    }

    @Override
    public void onItemDismiss(int position) {

        if (dataModelArrayList.size() > position) {
            dataModelArrayList.remove(position);
            notifyItemRemoved(position);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        if (dataModelArrayList != null)
            return dataModelArrayList.size();
        else return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
        TextView mName;

        ImageButton mButton;

        CardView mMainCardView;

        private MyViewHolder(View view) {
            super(view);
            mButton = view.findViewById(R.id.delete_button);
            mName = view.findViewById(R.id.name);
            mMainCardView = view.findViewById(R.id.cardView);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }

    }
}