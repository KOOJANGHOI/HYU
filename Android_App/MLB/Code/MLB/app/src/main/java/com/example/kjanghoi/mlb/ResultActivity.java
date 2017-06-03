package com.example.kjanghoi.mlb;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.StringTokenizer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by kjanghoi on 2016-08-21.
 */
public class ResultActivity extends Activity {

    String[] team_name = new String[2];
    Drawable[] team_logo = new Drawable[2];

    String batter_first = null;
    String batter_last = null;
    String batter_avg = null;
    String batter_hr = null;

    String pitcher_first = null;
    String pitcher_last = null;
    String pitcher_wins = null;
    String pitcher_losses = null;
    String pitcher_era = null;

    String game_status = null;
    String inning_State = null;

    String winning_pitcher_first = null;
    String winning_pitcher_last = null;
    String winning_pitcher_wins = null;
    String winning_pitcher_losses = null;
    String winning_pitcher_era = null;

    String losing_pitcher_first = null;
    String losing_pitcher_last = null;
    String losing_pitcher_wins = null;
    String losing_pitcher_losses = null;
    String losing_pitcher_era = null;

    String saving_pitcher_first = null;
    String saving_pitcher_last = null;
    String saving_pitcher_saves = null;
    String saving_pitcher_era = null;

    String home_pitcher_first = null;
    String home_pitcher_last = null;
    String home_pitcher_wins = null;
    String home_pitcher_losses = null;
    String home_pitcher_era = null;

    String away_pitcher_first = null;
    String away_pitcher_last = null;
    String away_pitcher_wins = null;
    String away_pitcher_losses = null;
    String away_pitcher_era = null;

    String away_r = null;
    String away_h = null;
    String away_e = null;

    String home_r = null;
    String home_h = null;
    String home_e = null;

    int away_score = 0;
    int home_score = 0;

    String[] away_inning_score = new String[10];
    String[] home_inning_score = new String[10];

    GetMLBScoreboard task = null;

    String passed_url = null;
    int position = 0;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    TextView away_1, away_2, away_3, away_4, away_5, away_6, away_7, away_8, away_9, away_R, away_H, away_E = null;
    TextView home_1, home_2, home_3, home_4, home_5, home_6, home_7, home_8, home_9, home_R, home_H, home_E = null;
    ImageView away_logo = null;
    ImageView home_logo = null;
    TextView hometeam_name = null;
    TextView awayteam_name = null;
    TextView away_info = null;
    TextView home_info = null;
    TextView game_result = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_result);
        Intent intent = getIntent();

        passed_url = intent.getExtras().getString("URL");
        position = intent.getExtras().getInt("position");

        task = new GetMLBScoreboard();
        task.execute(passed_url);

        game_result = (TextView) findViewById(R.id.game_result);
        hometeam_name = (TextView) findViewById(R.id.hometeam_name);
        awayteam_name = (TextView) findViewById(R.id.awayteam_name);
        away_1 = (TextView) findViewById(R.id.away_1);
        away_2 = (TextView) findViewById(R.id.away_2);
        away_3 = (TextView) findViewById(R.id.away_3);
        away_4 = (TextView) findViewById(R.id.away_4);
        away_5 = (TextView) findViewById(R.id.away_5);
        away_6 = (TextView) findViewById(R.id.away_6);
        away_7 = (TextView) findViewById(R.id.away_7);
        away_8 = (TextView) findViewById(R.id.away_8);
        away_9 = (TextView) findViewById(R.id.away_9);
        away_R = (TextView) findViewById(R.id.away_R);
        away_H = (TextView) findViewById(R.id.away_H);
        away_E = (TextView) findViewById(R.id.away_E);
        home_1 = (TextView) findViewById(R.id.home_1);
        home_2 = (TextView) findViewById(R.id.home_2);
        home_3 = (TextView) findViewById(R.id.home_3);
        home_4 = (TextView) findViewById(R.id.home_4);
        home_5 = (TextView) findViewById(R.id.home_5);
        home_6 = (TextView) findViewById(R.id.home_6);
        home_7 = (TextView) findViewById(R.id.home_7);
        home_8 = (TextView) findViewById(R.id.home_8);
        home_9 = (TextView) findViewById(R.id.home_9);
        home_R = (TextView) findViewById(R.id.home_R);
        home_H = (TextView) findViewById(R.id.home_H);
        home_E = (TextView) findViewById(R.id.home_E);
        away_logo = (ImageView) findViewById(R.id.away_logo);
        home_logo = (ImageView) findViewById(R.id.home_logo);
        away_info = (TextView) findViewById(R.id.away_info);
        home_info = (TextView) findViewById(R.id.home_info);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Result Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.kjanghoi.mlb/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Result Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.kjanghoi.mlb/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    private class GetMLBScoreboard extends AsyncTask<String, Void, Elements> {
        @Override
        protected Elements doInBackground(String... strings) {
            try {
                Document doc = Jsoup.parse(new URL(strings[0]).openStream(), "utf-8", strings[0]);

                Elements elements = doc.select("game");

                team_name[0] = elements.get(position).attr("away_name_abbrev");
                team_name[1] = elements.get(position).attr("home_name_abbrev");
                game_status = elements.get(position).select("status").attr("status");
                inning_State = elements.get(position).select("status").attr("inning_state");

                batter_first = elements.get(position).select("batter").attr("first");
                batter_last = elements.get(position).select("batter").attr("last");
                batter_avg = elements.get(position).select("batter").attr("avg");
                batter_hr = elements.get(position).select("batter").attr("hr");

                pitcher_first = elements.get(position).select("pitcher").attr("first");
                pitcher_last = elements.get(position).select("pitcher").attr("last");
                pitcher_wins = elements.get(position).select("pitcher").attr("wins");
                pitcher_losses = elements.get(position).select("pitcher").attr("losses");
                pitcher_era = elements.get(position).select("pitcher").attr("era");

                winning_pitcher_first = elements.get(position).select("winning_pitcher").attr("first");
                winning_pitcher_last = elements.get(position).select("winning_pitcher").attr("last");
                winning_pitcher_wins = elements.get(position).select("winning_pitcher").attr("wins");
                winning_pitcher_losses = elements.get(position).select("winning_pitcher").attr("losses");
                winning_pitcher_era = elements.get(position).select("winning_pitcher").attr("era");

                losing_pitcher_first = elements.get(position).select("losing_pitcher").attr("first");
                losing_pitcher_last = elements.get(position).select("losing_pitcher").attr("last");
                losing_pitcher_wins = elements.get(position).select("losing_pitcher").attr("wins");
                losing_pitcher_losses = elements.get(position).select("losing_pitcher").attr("losses");
                losing_pitcher_era = elements.get(position).select("losing_pitcher").attr("era");

                saving_pitcher_first = elements.get(position).select("save_pitcher").attr("first");
                saving_pitcher_last = elements.get(position).select("save_pitcher").attr("last");
                saving_pitcher_saves = elements.get(position).select("save_pitcher").attr("saves");
                saving_pitcher_era = elements.get(position).select("save_pitcher").attr("era");

                home_pitcher_first = elements.get(position).select("home_probable_pitcher").attr("first");
                home_pitcher_last = elements.get(position).select("home_probable_pitcher").attr("last");
                home_pitcher_wins = elements.get(position).select("home_probable_pitcher").attr("s_wins");
                home_pitcher_losses = elements.get(position).select("home_probable_pitcher").attr("s_losses");
                home_pitcher_era = elements.get(position).select("home_probable_pitcher").attr("s_era");

                away_pitcher_first = elements.get(position).select("away_probable_pitcher").attr("first");
                away_pitcher_last = elements.get(position).select("away_probable_pitcher").attr("last");
                away_pitcher_wins = elements.get(position).select("away_probable_pitcher").attr("s_wins");
                away_pitcher_losses = elements.get(position).select("away_probable_pitcher").attr("s_losses");
                away_pitcher_era = elements.get(position).select("away_probable_pitcher").attr("s_era");

                away_r = elements.get(position).select("linescore").select("r").attr("away");
                away_h = elements.get(position).select("linescore").select("h").attr("away");
                away_e = elements.get(position).select("linescore").select("e").attr("away");

                home_r = elements.get(position).select("linescore").select("r").attr("home");
                home_h = elements.get(position).select("linescore").select("h").attr("home");
                home_e = elements.get(position).select("linescore").select("e").attr("home");
                Log.d("FUCK", "1");
                if (game_status.equals("In Progress") || game_status.equals("Delayed") || game_status.equals("Delayed Start") || game_status.equals("Manager Challenge") || game_status.equals("Final") || game_status.equals("Game Over")) {
                    for (int i = 0; i < 9; i++) {
                        Log.d("FUCK", "2");
                        away_inning_score[i] = elements.get(position).select("linescore").select("inning").get(i).attr("away");
                        home_inning_score[i] = elements.get(position).select("linescore").select("inning").get(i).attr("home");
                        if (away_inning_score[i] == "" || home_inning_score[i] == "") {
                            break;
                        }
                    }
                } else {
                    for (int i = 0; i < 9; i++) {
                        away_inning_score[i] = "";
                        home_inning_score[i] = "";
                    }
                }
                return elements;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Elements elements) {
            super.onPostExecute(elements);
            for (int i = 0; i < 2; i++) {
                if (team_name[i].equals("CHC")) {
                    team_name[i] = "시카고컵스";
                    team_logo[i] = getResources().getDrawable(R.drawable.chc);
                }
                if (team_name[i].equals("WSH")) {
                    team_name[i] = "워싱턴";
                    team_logo[i] = getResources().getDrawable(R.drawable.wsh);
                }
                if (team_name[i].equals("PHI")) {
                    team_name[i] = "필라델피아";
                    team_logo[i] = getResources().getDrawable(R.drawable.phi);
                }
                if (team_name[i].equals("NYY")) {
                    team_name[i] = "뉴욕양키스";
                    team_logo[i] = getResources().getDrawable(R.drawable.nyy);
                }
                if (team_name[i].equals("TOR")) {
                    team_name[i] = "토론토";
                    team_logo[i] = getResources().getDrawable(R.drawable.tor);
                }
                if (team_name[i].equals("BOS")) {
                    team_name[i] = "보스턴";
                    team_logo[i] = getResources().getDrawable(R.drawable.bos);
                }
                if (team_name[i].equals("MIA")) {
                    team_name[i] = "마이애미";
                    team_logo[i] = getResources().getDrawable(R.drawable.mia);
                }
                if (team_name[i].equals("CLE")) {
                    team_name[i] = "클리블랜드";
                    team_logo[i] = getResources().getDrawable(R.drawable.cle);
                }
                if (team_name[i].equals("NYM")) {
                    team_name[i] = "뉴욕메츠";
                    team_logo[i] = getResources().getDrawable(R.drawable.nym);
                }
                if (team_name[i].equals("TEX")) {
                    team_name[i] = "텍사스";
                    team_logo[i] = getResources().getDrawable(R.drawable.tex);
                }
                if (team_name[i].equals("MIL")) {
                    team_name[i] = "밀워키";
                    team_logo[i] = getResources().getDrawable(R.drawable.mil);
                }
                if (team_name[i].equals("MIN")) {
                    team_name[i] = "미네소타";
                    team_logo[i] = getResources().getDrawable(R.drawable.min);
                }
                if (team_name[i].equals("OAK")) {
                    team_name[i] = "오클랜드";
                    team_logo[i] = getResources().getDrawable(R.drawable.oak);
                }
                if (team_name[i].equals("LAD")) {
                    team_name[i] = "LA다져스";
                    team_logo[i] = getResources().getDrawable(R.drawable.lad);
                }
                if (team_name[i].equals("SF")) {
                    team_name[i] = "샌프란시스코";
                    team_logo[i] = getResources().getDrawable(R.drawable.sf);
                }
                if (team_name[i].equals("BAL")) {
                    team_name[i] = "볼티모어";
                    team_logo[i] = getResources().getDrawable(R.drawable.bal);
                }
                if (team_name[i].equals("STL")) {
                    team_name[i] = "세인트루이스";
                    team_logo[i] = getResources().getDrawable(R.drawable.stl);
                }
                if (team_name[i].equals("ATL")) {
                    team_name[i] = "애틀란타";
                    team_logo[i] = getResources().getDrawable(R.drawable.atl);
                }
                if (team_name[i].equals("COL")) {
                    team_name[i] = "콜로라도";
                    team_logo[i] = getResources().getDrawable(R.drawable.col);
                }
                if (team_name[i].equals("TB")) {
                    team_name[i] = "템파베이";
                    team_logo[i] = getResources().getDrawable(R.drawable.tb);
                }
                if (team_name[i].equals("HOU")) {
                    team_name[i] = "휴스턴";
                    team_logo[i] = getResources().getDrawable(R.drawable.hou);
                }
                if (team_name[i].equals("ARI")) {
                    team_name[i] = "애리조나";
                    team_logo[i] = getResources().getDrawable(R.drawable.ari);
                }
                if (team_name[i].equals("CWS")) {
                    team_name[i] = "시카고W";
                    team_logo[i] = getResources().getDrawable(R.drawable.cws);
                }
                if (team_name[i].equals("SD")) {
                    team_name[i] = "샌디에이고";
                    team_logo[i] = getResources().getDrawable(R.drawable.sd);
                }
                if (team_name[i].equals("DET")) {
                    team_name[i] = "디트로이트";
                    team_logo[i] = getResources().getDrawable(R.drawable.det);
                }
                if (team_name[i].equals("CIN")) {
                    team_name[i] = "신시내티";
                    team_logo[i] = getResources().getDrawable(R.drawable.cin);
                }
                if (team_name[i].equals("KC")) {
                    team_name[i] = "캔자스시티";
                    team_logo[i] = getResources().getDrawable(R.drawable.kc);
                }
                if (team_name[i].equals("SEA")) {
                    team_name[i] = "시애틀";
                    team_logo[i] = getResources().getDrawable(R.drawable.sea);
                }
                if (team_name[i].equals("PIT")) {
                    team_name[i] = "피츠버그";
                    team_logo[i] = getResources().getDrawable(R.drawable.pit);
                }
                if (team_name[i].equals("LAA")) {
                    team_name[i] = "LA에인절스";
                    team_logo[i] = getResources().getDrawable(R.drawable.laa);
                }
            }
            if (game_status.equals("Pre-Game") || game_status.equals("Warmup")) {
                game_status = "경기 시작 임박";
                away_info.setText("선발\n" + away_pitcher_first + " " + away_pitcher_last + "\n" + away_pitcher_wins + "승 " + away_pitcher_losses + "패  era " + away_pitcher_era);
                home_info.setText("선발\n" + home_pitcher_first + " " + home_pitcher_last + "\n" + home_pitcher_wins + "승 " + home_pitcher_losses + "패  era " + home_pitcher_era);
            }
            if (game_status.equals("Preview")) {
                game_status = "경기 이전";
                away_info.setText("선발\n" + away_pitcher_first + " " + away_pitcher_last + "\n" + away_pitcher_wins + "승 " + away_pitcher_losses + "패  era " + away_pitcher_era);
                home_info.setText("선발\n" + home_pitcher_first + " " + home_pitcher_last + "\n" + home_pitcher_wins + "승 " + home_pitcher_losses + "패  era " + home_pitcher_era);
            }
            if (game_status.equals("In Progress")) {
                game_status = "경기 중";
                Log.d("FUCK", "경기중");
                if (game_status.equals("ManagerChallenge")) {
                    game_status = "챌린지";
                }
                if (game_status.equals("Delayed") || game_status.equals("Delayed Start")) {
                    game_status = "지연";
                }
                if (inning_State.equals("Top") || inning_State.equals("Middle")) {
                    away_info.setText("타자\n" + batter_first + " " + batter_last + "\n" + batter_avg + "  " + batter_hr + "홈런");
                    home_info.setText("투수\n" + pitcher_first + " " + pitcher_last + "\n" + pitcher_wins + "승 " + pitcher_losses + "패  era " + pitcher_era);
                    Log.d("FUCK", "초");
                }
                if (inning_State.equals("Bottom") || inning_State.equals("End")) {
                    home_info.setText("타자\n" + batter_first + " " + batter_last + "\n" + batter_avg + "  " + batter_hr + "홈런");
                    away_info.setText("투수\n" + pitcher_first + " " + pitcher_last + "\n" + pitcher_wins + "승 " + pitcher_losses + "패  era " + pitcher_era);
                    Log.d("FUCK", "말");
                }
            }
            if (game_status.equals("Final") || game_status.equals("Game Over")) {
                game_status = "경기 종료";
                away_score = Integer.parseInt(away_r);
                home_score = Integer.parseInt(home_r);

                if (saving_pitcher_first == "") {
                    if (away_score > home_score) {
                        away_info.setText("승리\n" + winning_pitcher_first + " " + winning_pitcher_last + "\n" + winning_pitcher_wins + "승 " + winning_pitcher_losses + "패  era " + winning_pitcher_era);
                        home_info.setText("패배\n" + losing_pitcher_first + " " + losing_pitcher_last + "\n" + losing_pitcher_wins + "승 " + losing_pitcher_losses + "패  era " + losing_pitcher_era);
                    }
                    if (away_score < home_score) {
                        home_info.setText("승리\n" + winning_pitcher_first + " " + winning_pitcher_last + "\n" + winning_pitcher_wins + "승 " + winning_pitcher_losses + "패  era " + winning_pitcher_era);
                        away_info.setText("패배\n" + losing_pitcher_first + " " + losing_pitcher_last + "\n" + losing_pitcher_wins + "승 " + losing_pitcher_losses + "패  era " + losing_pitcher_era);
                    }
                } else {
                    if (away_score > home_score) {
                        away_info.setText("승리\n" + winning_pitcher_first + " " + winning_pitcher_last + "\n" + winning_pitcher_wins + "승 " + winning_pitcher_losses + "패  era " + winning_pitcher_era + "\n\n세이브\n" + saving_pitcher_first + " " + saving_pitcher_last + "\n" + saving_pitcher_saves + "세  era " + saving_pitcher_era);
                        home_info.setText("패배\n" + losing_pitcher_first + " " + losing_pitcher_last + "\n" + losing_pitcher_wins + "승 " + losing_pitcher_losses + "패  era " + losing_pitcher_era);
                    }
                    if (away_score < home_score) {
                        home_info.setText("승리\n" + winning_pitcher_first + " " + winning_pitcher_last + "\n" + winning_pitcher_wins + "승 " + winning_pitcher_losses + "패  era " + winning_pitcher_era + "\n\n세이브\n" + saving_pitcher_first + " " + saving_pitcher_last + "\n" + saving_pitcher_saves + "세  era " + saving_pitcher_era);
                        away_info.setText("패배\n" + losing_pitcher_first + " " + losing_pitcher_last + "\n" + losing_pitcher_wins + "승 " + losing_pitcher_losses + "패  era " + losing_pitcher_era);
                    }
                }

            }

            game_result.setText(game_status);
            away_logo.setImageDrawable(team_logo[0]);
            home_logo.setImageDrawable(team_logo[1]);
            awayteam_name.setText(team_name[0]);
            hometeam_name.setText(team_name[1]);

            away_1.setText(away_inning_score[0]);
            away_2.setText(away_inning_score[1]);
            away_3.setText(away_inning_score[2]);
            away_4.setText(away_inning_score[3]);
            away_5.setText(away_inning_score[4]);
            away_6.setText(away_inning_score[5]);
            away_7.setText(away_inning_score[6]);
            away_8.setText(away_inning_score[7]);
            away_9.setText(away_inning_score[8]);
            away_R.setText(away_r);
            away_H.setText(away_h);
            away_E.setText(away_e);

            home_1.setText(home_inning_score[0]);
            home_2.setText(home_inning_score[1]);
            home_3.setText(home_inning_score[2]);
            home_4.setText(home_inning_score[3]);
            home_5.setText(home_inning_score[4]);
            home_6.setText(home_inning_score[5]);
            home_7.setText(home_inning_score[6]);
            home_8.setText(home_inning_score[7]);
            home_9.setText(home_inning_score[8]);
            home_R.setText(home_r);
            home_H.setText(home_h);
            home_E.setText(home_e);

        }
    }
}















