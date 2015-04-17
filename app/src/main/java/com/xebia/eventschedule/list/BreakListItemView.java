package com.xebia.eventschedule.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xebia.eventschedule.R;
import com.xebia.eventschedule.model.Talk;

public class BreakListItemView extends RelativeLayout implements ScheduleListItemView {

    private final TalkListClickListener mListener;
    private TextView mTitleView;
    private Talk mTalk;

    public BreakListItemView(Context context, final TalkListClickListener listener) {
        super(context);
        mListener = listener;
        LayoutInflater.from(context).inflate(R.layout.list_item_break, this);
        onFinishInflate();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTitleView = (TextView) findViewById(R.id.title);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onTalkClick(mTalk);
                }
            }
        });
    }

    @Override
    public void showTalk(Talk talk) {
        mTalk = talk;
        mTitleView.setText(talk.getTitle());
    }

    @Override
    public void setHighlighted(boolean highlight) {
        // TODO
    }
}
