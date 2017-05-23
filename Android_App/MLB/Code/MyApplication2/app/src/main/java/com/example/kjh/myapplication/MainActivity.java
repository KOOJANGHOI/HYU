package com.example.kjh.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    /*GetMLBScoreboard task = null;
    String uri = null;
    String year = "2017";
    String month = "05";
    String us_day = "17";

    String team_name = null;
    String game_status = null;
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //task = new GetMLBScoreboard();
        //uri = "http://gd2.mlb.com/components/game/mlb/year_" + year + "/month_" + month + "/day_" + us_day + "/scoreboard_android.xml";
        //task.execute("http://gd2.mlb.com/components/game/mlb/year_" + year + "/month_" + month + "/day_" + us_day + "/scoreboard_android.xml");

        //TextView text1 = (TextView)findViewById(R.id.text1);
        //TextView text2 = (TextView)findViewById(R.id.text2);


    }
    /*
    private class GetMLBScoreboard extends AsyncTask<String, Void, Elements> {
        @Override

        protected Elements doInBackground(String... strings) {
            try {
                Document doc = Jsoup.parse(new URL(strings[0]).openStream(), "utf-8", strings[0]);

                Elements elements = doc.select("game");

                //for (int i = 0; i < elements.size(); i++) {

                    team_name = elements.get(0).attr("home_name_abbrev");
                    //team_name[2 * i + 1] = elements.get(i).attr("away_name_abbrev");

                    game_status = elements.get(0).select("status").attr("status");
                    //ball_counts[i] = elements.get(i).select("status").attr("b");
                    //strike_counts[i] = elements.get(i).select("status").attr("s");
                    //out_counts[i] = elements.get(i).select("status").attr("o");

                    //inning[i] = elements.get(i).select("status").attr("inning");
                    //inning_State[i] = elements.get(i).select("status").attr("inning_state");

                    //game_score[2 * i] = elements.get(i).select("linescore").select("r").attr("home");
                    //game_score[2 * i + 1] = elements.get(i).select("linescore").select("r").attr("away");

                    //batter_last[i] = elements.get(i).select("batter").attr("last");
                    //batter_first[i] = elements.get(i).select("batter").attr("first");
                    //pitcher_last[i] = elements.get(i).select("pitcher").attr("last");
                    //pitcher_first[i] = elements.get(i).select("pitcher").attr("first");

                    //base_status[i] = elements.get(i).select("runners_on_base").attr("status");
                    //us_time[i] = elements.get(i).attr("time");

               // }
                return elements;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


    }
    */
}
