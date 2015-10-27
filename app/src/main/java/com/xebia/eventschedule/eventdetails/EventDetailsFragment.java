package com.xebia.eventschedule.eventdetails;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.xebia.eventschedule.R;
import com.xebia.eventschedule.model.Event;
import com.xebia.eventschedule.model.Location;
import com.xebia.eventschedule.util.AnalyticsHelper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

public class EventDetailsFragment extends Fragment {

    private static final String MAPS_ACTION = "geo:%f,%f?q=%s";
    private static final String GEO_URI = "http://maps.google.com/maps?q=loc:%f,%f (%s)";
    private static final String DIALER_ACTION = "tel:+31355381921";
    private static final String ARG_EVENT_ID = "eventId";

    private TextView mLocationName;
    private TextView mLocationAddress;
    private TextView mLocationUrl;

    private String mEventId;
    private Event mEvent;

    public static EventDetailsFragment newInstance(final String eventId) {
        EventDetailsFragment fragment = new EventDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEventId = getArguments().getString(ARG_EVENT_ID);
        AnalyticsHelper.openedEventDetailsActivity(mEventId);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Fetch the data about this event from Parse
        Event.getInBackground(mEventId, new GetCallback<Event>() {
            @Override
            public void done(final Event event, ParseException e) {

                if (null == getActivity()) {
                    return;
                }

                // if we cannot get the data right now, the best we can do is show a toast.
                if (e != null) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }

                if (event == null) {
                    throw new RuntimeException("Somehow the event was null.");
                }

                onEventLoaded(event);
            }
        });
    }

    private void onEventLoaded(final Event event) {
        mEvent = event;
        if (null != event.getLocation()) {
            mLocationName.setText(event.getLocation().getName());
            mLocationAddress.setText(event.getLocation().getAddress());
            mLocationUrl.setText(event.getLocation().getUrl());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.event_details, container, false);
        if (null != root) {
            mLocationName = ((TextView) root.findViewById(R.id.location_name));
            mLocationAddress = ((TextView) root.findViewById(R.id.location_address));
            mLocationUrl = ((TextView) root.findViewById(R.id.location_url));

            ImageButton locationIcon = (ImageButton) root.findViewById(R.id.location_icon);
            DrawableCompat.setTint(locationIcon.getDrawable(), ContextCompat.getColor(getContext(), R.color.accent));
            locationIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openMap();
                }
            });

            ImageButton contactIcon = (ImageButton) root.findViewById(R.id.contact_icon);
            DrawableCompat.setTint(contactIcon.getDrawable(), ContextCompat.getColor(getContext(), R.color.accent));
            contactIcon.setOnClickListener(new View.OnClickListener() {
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
        final Location location = mEvent.getLocation();
        if (null != location && null != location.getCoords()) {
            double lat = location.getCoords().getLatitude();
            double lon = location.getCoords().getLatitude();
            String query;
            try {
                query = URLEncoder.encode(location.getName() != null ? location.getName() : "", "UTF-8");
            } catch (UnsupportedEncodingException e) {
                query = "";
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(Locale.US, MAPS_ACTION, lat, lon, query)));
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                try {
                    String geoUri = String.format(Locale.US, GEO_URI, lat, lon, location.getName());
                    Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                    startActivity(webIntent);
                } catch (ActivityNotFoundException e2) {
                    Toast.makeText(getActivity(), getString(R.string.activity_not_found), Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
