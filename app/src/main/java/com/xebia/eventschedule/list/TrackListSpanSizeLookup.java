package com.xebia.eventschedule.list;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * A span size lookup that puts plenary sessions over all columns and anything else single column.
 *
 * @see TalkViewHolder#isPlenary()
 */
public class TrackListSpanSizeLookup extends GridLayoutManager.SpanSizeLookup {

    private final RecyclerView rv;
    private final int numColumns;

    public TrackListSpanSizeLookup(RecyclerView rv, int numColumns) {
        this.rv = rv;
        this.numColumns = numColumns;
    }

    @Override
    public int getSpanSize(int position) {
        TalkViewHolder vh = (TalkViewHolder) rv.findViewHolderForAdapterPosition(position);
        return vh != null && vh.isPlenary() ? numColumns : 1;
    }
}
