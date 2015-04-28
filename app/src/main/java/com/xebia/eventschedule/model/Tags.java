package com.xebia.eventschedule.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.xebia.eventschedule.R;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Helper class for managing Talk tags. Maintains a sorted list of tag labels and corresponding colors.
 *
 * Take care to call {@link #init(Context, List)} to initialize the list of tags before calling {@link #get()}.
 *
 * Created by steven on 4/28/15.
 */
public class Tags {

    private static Tags sInstance;
    private final List<String> mTagLabels = new ArrayList<>();
    private final List<Integer> mTagColors = new ArrayList<>();

    private Tags(final Context context) {
        mTagColors.add(context.getResources().getColor(R.color.red_500));
        mTagColors.add(context.getResources().getColor(R.color.deep_purple_500));
        mTagColors.add(context.getResources().getColor(R.color.light_blue_500));
        mTagColors.add(context.getResources().getColor(R.color.green_500));
        mTagColors.add(context.getResources().getColor(R.color.yellow_500));
        mTagColors.add(context.getResources().getColor(R.color.deep_orange_500));
        mTagColors.add(context.getResources().getColor(R.color.blue_grey_500));
        mTagColors.add(context.getResources().getColor(R.color.pink_500));
        mTagColors.add(context.getResources().getColor(R.color.indigo_500));
        mTagColors.add(context.getResources().getColor(R.color.cyan_500));
        mTagColors.add(context.getResources().getColor(R.color.light_green_500));
        mTagColors.add(context.getResources().getColor(R.color.amber_500));
        mTagColors.add(context.getResources().getColor(R.color.brown_500));
        mTagColors.add(context.getResources().getColor(R.color.purple_500));
        mTagColors.add(context.getResources().getColor(R.color.blue_500));
        mTagColors.add(context.getResources().getColor(R.color.lime_500));
        mTagColors.add(context.getResources().getColor(R.color.orange_500));
        mTagColors.add(context.getResources().getColor(R.color.grey_500));
    }

    public static Tags init(final Context context, final List<Talk> talks) {
        if (null == sInstance) {
            sInstance = new Tags(context);
        }
        sInstance.setTagLabels(getUniqueTalkTagsSorted(talks));
        return sInstance;
    }

    public static Tags get() {
        if (null == sInstance) {
            throw new RuntimeException("Tags singleton not initialized yet");
        }
        return sInstance;
    }

    /**
     * Obtains the unique tags of the given Talks and returns them in lexical ordering by the default locale.
     *
     * @param talks the talks from which to obtain the tags.
     * @return the unique tags in lexical order. This list may be the immutable.
     */
    @NonNull
    private static List<String> getUniqueTalkTagsSorted(@NonNull List<Talk> talks) {
        final Set<String> tagsUnique = new HashSet<>();
        for (Talk talk : talks) {
            tagsUnique.addAll(talk.getTags());
        }
        if (tagsUnique.isEmpty()) {
            Log.d("Tags", "There were no tags on any talk");
            return Collections.emptyList();
        }
        final List<String> tagsOrdered = new ArrayList<>(tagsUnique);
        Collections.sort(tagsOrdered, Collator.getInstance());
        return tagsOrdered;
    }

    private void setTagLabels(final List<String> tagLabels) {
        mTagLabels.clear();
        mTagLabels.addAll(tagLabels);
    }

    public int getTagColor(final String tag) {
        int index = mTagLabels.indexOf(tag);
        if (index >= 0) {
            return mTagColors.get(index % mTagColors.size());
        } else {
            return mTagColors.get(mTagColors.size() - 1);
        }
    }

    public List<String> getTagLabels() {
        return mTagLabels;
    }
}
