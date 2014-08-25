package com.xebia.xebicon2014.details;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;
import com.xebia.xebicon2014.R;

/**
 * Created by ronaldwarners on 22-08-14.
 */
public class EventListAdapter extends ParseQueryAdapter {

    public EventListAdapter(Context context, String name){
        super(context, name);
    }

    @Override
    public View getItemView(ParseObject object, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.list_item_event, null);
        }

        super.getItemView(object, v, parent);

        // Add and download the image
        ParseImageView todoImage = (ParseImageView) v.findViewById(R.id.speakerimage);
        ParseFile imageFile = object.getParseFile("logo");
        if (imageFile != null) {
            todoImage.setParseFile(imageFile);
            todoImage.loadInBackground();
        }

        // Add the title view
        TextView titleTextView = (TextView) v.findViewById(R.id.title);
        titleTextView.setText(object.getString("name"));


        return v;
    }
}
