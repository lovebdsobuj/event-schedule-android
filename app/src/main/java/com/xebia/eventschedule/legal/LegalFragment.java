package com.xebia.eventschedule.legal;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xebia.eventschedule.R;
import com.xebia.eventschedule.util.AnalyticsHelper;

public class LegalFragment extends Fragment {

    private static final String ARG_EVENT_ID = "eventId";

    public static LegalFragment newInstance(final String eventId) {
        LegalFragment fragment = new LegalFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String eventId = getArguments().getString(ARG_EVENT_ID);
        AnalyticsHelper.openedLegalActivity(eventId);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.legal, container, false);
    }
}
