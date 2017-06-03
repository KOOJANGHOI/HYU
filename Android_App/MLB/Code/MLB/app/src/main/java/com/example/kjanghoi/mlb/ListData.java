package com.example.kjanghoi.mlb;

import java.text.Collator;
import java.util.Comparator;

import android.graphics.drawable.Drawable;

/**
 * Created by kjanghoi on 2016-07-28.
 */
public class ListData {
    public Drawable f_logo;
    public Drawable s_logo;
    public Drawable sit;

    public String f_team;
    public String f_player;
    public String f_score;

    public String s_team;
    public String s_player;
    public String s_score;

    public String inning;
    public String b_count;
    public String s_count;
    public String o_count;

    public static final Comparator<ListData> ALPHA_COMPARATOR = new Comparator<ListData>() {
        public final Collator sCollator = Collator.getInstance();

        @Override
        public int compare(ListData mListDate_1, ListData mListDate_2) {
            return sCollator.compare(mListDate_1.f_team, mListDate_2.f_team);
        }
    };

}
