package com.xebia.eventschedule.list;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xebia.eventschedule.R;
import com.xebia.eventschedule.model.Talk;

public class BreakListItemView extends RelativeLayout implements ScheduleListItemView {

    private TextView mTitleView;

    public BreakListItemView(Context context) {
        super(context);
        initialize();
    }

    public BreakListItemView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public BreakListItemView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BreakListItemView(final Context context, final AttributeSet attrs, final int defStyleAttr,
                             final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize();
    }

    private void initialize() {
        LayoutInflater.from(getContext()).inflate(R.layout.list_item_break, this);
        onFinishInflate();
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
}
