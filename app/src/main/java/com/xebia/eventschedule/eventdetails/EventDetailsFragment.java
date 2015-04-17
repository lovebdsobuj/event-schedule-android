package com.xebia.eventschedule.eventdetails;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.xebia.eventschedule.R;
import com.xebia.eventschedule.model.Event;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class EventDetailsFragment extends Fragment {

    private static final String MAPS_ACTION = "geo:%f,%f?q=%s";
    private static final String DIALER_ACTION = "tel:+31355381921";
    private static final String ARG_EVENT = "event";
    private Event mEvent;

    public static EventDetailsFragment newInstance(final Event event) {
        EventDetailsFragment fragment = new EventDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_EVENT, event);
        fragment.setArguments(args);
        return fragment;
    }

    public EventDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEvent = (Event) getArguments().getSerializable(ARG_EVENT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.event_details, container, false);
        if (null != root) {
            ((TextView) root.findViewById(R.id.location_name)).setText(mEvent.getLocation().getName());
            ((TextView) root.findViewById(R.id.location_address)).setText(mEvent.getLocation().getAddress());
            ((TextView) root.findViewById(R.id.location_url)).setText(mEvent.getLocation().getUrl());
            root.findViewById(R.id.location_icon).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openMap();
                }
            });

            root.findViewById(R.id.contact_icon).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openDialer();
                }
            });
        }

        setRetainInstance(true);

        return root;
    }

    private void openDialer() {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(DIALER_ACTION));
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getActivity(), getString(R.string.activity_not_found), Toast.LENGTH_LONG).show();
        }
    }

    private void openMap() {
        double lat = mEvent.getLocation().getCoords().getLatitude();
        double lon = mEvent.getLocation().getCoords().getLatitude();
        String query;
        try {
            query = URLEncoder.encode(mEvent.getLocation().getName(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            query = "";
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(MAPS_ACTION, lat, lon, query)));
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            try {
                String geoUri = "http://maps.google.com/maps?q=loc:" + lat + "," + lon + " (" + mEvent.getLocation().getName() + ")";
                Intent webIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(geoUri));
                startActivity(webIntent);
            }catch (ActivityNotFoundException e2){
                Toast.makeText(getActivity(), getString(R.string.activity_not_found), Toast.LENGTH_LONG).show();
            }
        }
    }
}
