package com.amkdomaini.android.amkhavasi101.Controllers;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.amkdomaini.android.amkhavasi101.Adapters.CityRecycleViewAdapter;


public class CityCardsViewTouchHelperCallbacks extends ItemTouchHelper.Callback {

    private final CityRecycleViewAdapter mAdapter;

    public CityCardsViewTouchHelperCallbacks(CityRecycleViewAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }
    //WHEN FALSE, CANT DELETE CITIES BY SWIPING


    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }
    //WHEN TRUE, CAN CHANGE ORDER OF CITIES

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
    }
}

