package com.xebia.eventschedule.list;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.xebia.eventschedule.R;
import com.xebia.eventschedule.model.Talk;

public class BreakListItemView extends FrameLayout implements ScheduleListItemView {

    private TextView mTitleView;

    public BreakListItemView(Context context) {
        super(context);
    }

    public BreakListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BreakListItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressLint("NewApi")
    public BreakListItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTitleView = (TextView) findViewById(R.id.title);
    }

    @Override
    public void showTalk(Talk talk) {
        mTitleView.setText(talk.getTitle());
    }

    @Override
    public void setHighlighted(boolean highlight) {
        // TODO
    }
}
