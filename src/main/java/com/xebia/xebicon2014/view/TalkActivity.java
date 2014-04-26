package com.xebia.xebicon2014.view;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.xebia.xebicon2014.R;
import com.xebia.xebicon2014.model.Talk;

/**
 * An Activity to display information about a particular talk.
 */
public class TalkActivity extends CalligraphyActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk);

        // Fetch the data about this talk from Parse.
        String talkId = Talk.getTalkId(getIntent().getData());
        Talk.getInBackground(talkId, new GetCallback<Talk>() {
            @Override
            public void done(final Talk talk, ParseException e) {

                // if we cannot get the data right now, the best we can do is show a toast.
                if (e != null) {
                    Toast.makeText(TalkActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }

                if (talk == null) {
                    throw new RuntimeException("Somehow the talk was null.");
                }

                TalkDetailsFragment fragment = (TalkDetailsFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.talk_details);
                if (null != fragment) {
                    fragment.setTalk(talk);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // By interpreting the home tab as a back press, we get the platform-specific "previous
            // activity" transition instead of the "next activity" one.
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
