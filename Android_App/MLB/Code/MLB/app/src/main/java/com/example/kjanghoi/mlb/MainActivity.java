package com.example.kjanghoi.mlb;

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
import android.os.SystemClock;
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

import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.kjanghoi.mlb.R;

public class MainActivity extends ActionBarActivity {

    //이전 키를 2번 누르면 앱을 종료
    private long mExitModeTime = 0L;
    @Override
    public  void onBackPressed() {
        if(mExitModeTime != 0 && SystemClock.uptimeMillis() - mExitModeTime < 3000) {
            finish();
        }else {
            android.widget.Toast.makeText(this, "이전키를 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
            mExitModeTime = SystemClock.uptimeMillis();
        }
    }

    //야구관련 변수 선언
    private ListView mListView = null;
    private ListViewAdapter mAdapter = null;

    String[] team_name = new String[50];
    String[] game_status = new String[50];
    String[] ball_counts = new String[50];
    String[] strike_counts = new String[50];
    String[] out_counts = new String[50];
    String[] inning = new String[50];
    String[] inning_State = new String[50];
    String[] game_score = new String[50];
    String[] batter_last = new String[50];
    String[] batter_first = new String[50];
    String[] pitcher_last = new String[50];
    String[] pitcher_first = new String[50];
    String[] player = new String[50];
    Drawable[] team_logo = new Drawable[50];
    String[] base_status = new String[50];
    Drawable[] base_image = new Drawable[50];
    GetMLBScoreboard task = null;
    String[] us_time = new String[50];
    String[] kr_time = new String[50];
    String[] tmp_1 = new String[50];
    Integer[] temp_1 = new Integer[50];
    String[] tmp_2 = new String[50];
    Integer[] temp_2 = new Integer[50];
    String[] tmp_3 = new String[50];
    TextView current_Date = null;
    long date = 0;
    SimpleDateFormat sdf = null;
    String dataString = null;
    String year = null;
    String month = null;
    String us_day = null;
    String day_current = null;
    String day_before = null;
    String day_after = null;
    String xml_day = null;
    String uri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startActivity(new Intent(this, SplashActivity.class));

        //날짜 , 시간을 처리하는 부분
        current_Date = (TextView) findViewById(R.id.date);
        date = System.currentTimeMillis();
        sdf = new SimpleDateFormat("yyyy/MM/dd");
        dataString = sdf.format(date);
        current_Date.setText(dataString);

        year = dataString.substring(0, 4);
        month = dataString.substring(5, 7);
        day_current = dataString.substring(8, 10);

        int tmp = Integer.parseInt(day_current);
        tmp--;

        if(tmp < 10) {
            us_day = Integer.toString(tmp);
            us_day = "0" + us_day;
        }
        else {
            us_day = Integer.toString(tmp);
        }

        //객체 생성 , url 변수
        task = new GetMLBScoreboard();
        uri = "http://gd2.mlb.com/components/game/mlb/year_" + year + "/month_" + month + "/day_" + us_day + "/scoreboard_android.xml";
        task.execute("http://gd2.mlb.com/components/game/mlb/year_" + year + "/month_" + month + "/day_" + us_day + "/scoreboard_android.xml");
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //액션바 클릭시
    public boolean onOptionsItemSelected(MenuItem item) {
        //ActionBar 메뉴 클릭에 대한 이벤트 처리
        String txt = null;
        int id = item.getItemId();
        switch (id) {
            case R.id.item1:
                txt = "이전날짜";

                int tmp_1 = Integer.parseInt(day_current);
                tmp_1--;
                if(tmp_1 < 10) {
                    day_before = Integer.toString(tmp_1);
                    day_before = "0" + day_before;
                }
                else {
                    day_before = Integer.toString(tmp_1);
                }

                dataString = year + "/" + month + "/" + day_before;
                current_Date.setText(dataString);

                int tmp_3 = Integer.parseInt(day_before);
                tmp_3--;

                if(tmp_3 < 10) {
                    xml_day = Integer.toString(tmp_3);
                    xml_day = "0" + xml_day;
                }
                else {
                    xml_day = Integer.toString(tmp_3);
                }

                task = new GetMLBScoreboard();
                uri = "http://gd2.mlb.com/components/game/mlb/year_" + year + "/month_" + month + "/day_" + xml_day + "/scoreboard_android.xml";
                task.execute("http://gd2.mlb.com/components/game/mlb/year_" + year + "/month_" + month + "/day_" + xml_day + "/scoreboard_android.xml");

                day_current = day_before;
                break;
            case R.id.item2:
                txt = "새로고침";

                current_Date = (TextView) findViewById(R.id.date);
                date = System.currentTimeMillis();
                sdf = new SimpleDateFormat("yyyy/MM/dd");
                dataString = sdf.format(date);
                day_current = dataString.substring(8, 10);
                current_Date.setText(dataString);

                task = new GetMLBScoreboard();
                uri = "http://gd2.mlb.com/components/game/mlb/year_" + year + "/month_" + month + "/day_" + us_day + "/scoreboard_android.xml";
                task.execute("http://gd2.mlb.com/components/game/mlb/year_" + year + "/month_" + month + "/day_" + us_day + "/scoreboard_android.xml");
                break;
            case R.id.item3:
                txt = "다음날짜";

                int tmp_2 = Integer.parseInt(day_current);
                tmp_2++;
                if(tmp_2 < 10) {
                    day_after = Integer.toString(tmp_2);
                    day_after = "0" + day_after;
                }
                else {
                    day_after = Integer.toString(tmp_2);
                }

                dataString = year + "/" + month + "/" + day_after;
                current_Date.setText(dataString);

                int tmp_4 = Integer.parseInt(day_after);
                tmp_4--;

                if(tmp_4 < 10) {
                    xml_day = Integer.toString(tmp_4);
                    xml_day = "0" + xml_day;
                }
                else {
                    xml_day = Integer.toString(tmp_4);
                }

                task = new GetMLBScoreboard();
                uri = "http://gd2.mlb.com/components/game/mlb/year_" + year + "/month_" + month + "/day_" + xml_day + "/scoreboard_android.xml";
                task.execute("http://gd2.mlb.com/components/game/mlb/year_" + year + "/month_" + month + "/day_" + xml_day + "/scoreboard_android.xml");
                day_current = day_after;
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //ViewHolder
    private class ViewHolder {
        public ImageView f_logo;
        public ImageView s_logo;
        public ImageView sit;

        public TextView f_team;
        public TextView f_player;
        public TextView f_score;

        public TextView s_team;
        public TextView s_player;
        public TextView s_score;

        public TextView inning;
        public TextView b_count;
        public TextView s_count;
        public TextView o_count;
    }

    //AsyncTask와 Generics 같이 설명
    private class GetMLBScoreboard extends AsyncTask<String, Void, Elements> {
        @Override

        protected Elements doInBackground(String... strings) {
            try {
                Document doc = Jsoup.parse(new URL(strings[0]).openStream(), "utf-8", strings[0]);

                Elements elements = doc.select("game");

                for (int i = 0; i < elements.size(); i++) {

                    team_name[2 * i] = elements.get(i).attr("home_name_abbrev");
                    team_name[2 * i + 1] = elements.get(i).attr("away_name_abbrev");

                    game_status[i] = elements.get(i).select("status").attr("status");
                    ball_counts[i] = elements.get(i).select("status").attr("b");
                    strike_counts[i] = elements.get(i).select("status").attr("s");
                    out_counts[i] = elements.get(i).select("status").attr("o");

                    inning[i] = elements.get(i).select("status").attr("inning");
                    inning_State[i] = elements.get(i).select("status").attr("inning_state");

                    game_score[2 * i] = elements.get(i).select("linescore").select("r").attr("home");
                    game_score[2 * i + 1] = elements.get(i).select("linescore").select("r").attr("away");

                    batter_last[i] = elements.get(i).select("batter").attr("last");
                    batter_first[i] = elements.get(i).select("batter").attr("first");
                    pitcher_last[i] = elements.get(i).select("pitcher").attr("last");
                    pitcher_first[i] = elements.get(i).select("pitcher").attr("first");

                    base_status[i] = elements.get(i).select("runners_on_base").attr("status");
                    us_time[i] = elements.get(i).attr("time");

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
            mListView = (ListView) findViewById(R.id.mList);
            mAdapter = new ListViewAdapter(getApplicationContext());
            mListView.setAdapter(mAdapter);

            mListView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
                    intent.putExtra("position", position);
                    intent.putExtra("URL", uri);
                    startActivity(intent);
                }
            });

            //경기 상황에 대한 분류
            for (int i = 0; i < elements.size(); i++) {

                if (game_status[i].equals("Preview")) {
                    tmp_1[i] = us_time[i].substring(0, 1);
                    tmp_3[i] = us_time[i].substring(2, 3);
                    temp_1[i] = Integer.parseInt(tmp_1[i]);

                    if (tmp_3[i].equals(":")) {
                        tmp_2[i] = us_time[i].substring(0, 2);
                        temp_2[i] = Integer.parseInt(tmp_2[i]);
                        temp_2[i]++;

                        kr_time[i] = Integer.toString(temp_2[i]) + ":" + us_time[i].substring(3, 5);
                        game_status[i] = kr_time[i];

                        ball_counts[i] = null;
                        strike_counts[i] = null;
                        out_counts[i] = null;
                        base_image[i] = getResources().getDrawable(R.drawable.base_0);
                        player[2 * i] = null;
                        player[2 * i + 1] = null;
                    } else {
                        temp_1[i]++;

                        kr_time[i] = Integer.toString(temp_1[i]) + ":" + us_time[i].substring(2, 4);
                        game_status[i] = kr_time[i];
                        ball_counts[i] = null;
                        strike_counts[i] = null;
                        out_counts[i] = null;
                        base_image[i] = getResources().getDrawable(R.drawable.base_0);
                        player[2 * i] = null;
                        player[2 * i + 1] = null;
                    }
                } else if (game_status[i].equals("Pre-Game") || game_status[i].equals("Warmup")) {
                    game_status[i] = "곧 시작";

                    ball_counts[i] = null;
                    strike_counts[i] = null;
                    out_counts[i] = null;
                    base_image[i] = getResources().getDrawable(R.drawable.base_0);
                    player[2 * i] = null;
                    player[2 * i + 1] = null;

                } else if (game_status[i].equals("In Progress")) {
                    if (game_status[i].equals("ManagerChallenge")) {
                        game_status[i] = "챌린지";
                        break;
                    }
                    if (game_status[i].equals("Delayed") || game_status[i].equals("Delayed Start")) {
                        game_status[i] = "지연";
                        break;
                    }
                    switch (Integer.parseInt(base_status[i])) {
                        case 0: {
                            base_image[i] = getResources().getDrawable(R.drawable.base_0);
                            break;
                        }
                        case 1: {
                            base_image[i] = getResources().getDrawable(R.drawable.base_1);
                            break;
                        }
                        case 2: {
                            base_image[i] = getResources().getDrawable(R.drawable.base_2);
                            break;
                        }
                        case 3: {
                            base_image[i] = getResources().getDrawable(R.drawable.base_3);
                            break;
                        }
                        case 4: {
                            base_image[i] = getResources().getDrawable(R.drawable.base_4);
                            break;
                        }
                        case 5: {
                            base_image[i] = getResources().getDrawable(R.drawable.base_5);
                            break;
                        }
                        case 6: {
                            base_image[i] = getResources().getDrawable(R.drawable.base_6);
                            break;
                        }
                        case 7: {
                            base_image[i] = getResources().getDrawable(R.drawable.base_0);
                            break;
                        }
                        default: {
                            base_image[i] = getResources().getDrawable(R.drawable.base_0);
                            break;
                        }
                    }

                    if (inning_State[i].equals("Top")) {
                        inning_State[i] = "초";
                        player[2 * i + 1] = batter_first[i] + " " + batter_last[i];
                        player[2 * i] = pitcher_first[i] + " " + pitcher_last[i];
                    }
                    if (inning_State[i].equals("Middle")) {
                        inning_State[i] = "초 종료";
                    }
                    if (inning_State[i].equals("Bottom")) {
                        inning_State[i] = "말";
                        player[2 * i] = batter_first[i] + " " + batter_last[i];
                        player[2 * i + 1] = pitcher_first[i] + " " + pitcher_last[i];
                    }
                    if (inning_State[i].equals("End")) {
                        inning_State[i] = "말 종료";
                    }
                    game_status[i] = inning[i] + "회" + inning_State[i];
                } else if (game_status[i].equals("Final") || game_status[i].equals("Game Over")) {
                    game_status[i] = "경기 종료";
                    ball_counts[i] = null;
                    strike_counts[i] = null;
                    out_counts[i] = null;
                    base_image[i] = getResources().getDrawable(R.drawable.base_0);
                    player[2 * i] = null;
                    player[2 * i + 1] = null;
                }
            }

            //팀에 대한 분류
            for (int i = 0; i < 2 * elements.size(); i++) {
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

            for (int i = 0; i < elements.size(); i++) {
                mAdapter.addItem(team_logo[2 * i + 1],
                        team_name[2 * i + 1],
                        player[2 * i + 1],
                        base_image[i],
                        ball_counts[i],
                        strike_counts[i],
                        out_counts[i],
                        game_score[2 * i + 1],
                        game_status[i],
                        game_score[2 * i],
                        team_logo[2 * i],
                        team_name[2 * i],
                        player[2 * i]);
            }
        }
    }

    private class ListViewAdapter extends BaseAdapter {
        private Context mContext = null;
        private ArrayList<com.example.kjanghoi.mlb.ListData> mListData = new ArrayList<com.example.kjanghoi.mlb.ListData>();

        public ListViewAdapter(Context mContext) {
            super();
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return mListData.size();
        }

        @Override
        public Object getItem(int position) {
            return mListData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void addItem(Drawable f_logo, String f_team, String f_player, Drawable sit, String b_count, String s_count, String o_count, String f_score, String inning, String s_score, Drawable s_logo, String s_team, String s_player) {
            com.example.kjanghoi.mlb.ListData addInfo = null;
            addInfo = new com.example.kjanghoi.mlb.ListData();
            addInfo.f_logo = f_logo;
            addInfo.f_player = f_player;
            addInfo.f_team = f_team;
            addInfo.f_score = f_score;

            addInfo.sit = sit;
            addInfo.inning = inning;
            addInfo.b_count = b_count;
            addInfo.s_count = s_count;
            addInfo.o_count = o_count;


            addInfo.s_logo = s_logo;
            addInfo.s_player = s_player;
            addInfo.s_team = s_team;
            addInfo.s_score = s_score;

            mListData.add(addInfo);
        }

        public void remove(int position) {
            mListData.remove(position);
            dataChange();
        }

        public void sort() {
            Collections.sort(mListData, com.example.kjanghoi.mlb.ListData.ALPHA_COMPARATOR);
            dataChange();
        }

        public void dataChange() {
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.listview_item, null);

                holder.f_logo = (ImageView) convertView.findViewById(R.id.f_logo);
                holder.f_team = (TextView) convertView.findViewById(R.id.f_Team);
                holder.f_player = (TextView) convertView.findViewById(R.id.f_player);
                holder.f_score = (TextView) convertView.findViewById(R.id.f_score);


                holder.sit = (ImageView) convertView.findViewById(R.id.sit);
                holder.inning = (TextView) convertView.findViewById(R.id.inning);
                holder.b_count = (TextView) convertView.findViewById(R.id.b_count);
                holder.s_count = (TextView) convertView.findViewById(R.id.s_count);
                holder.o_count = (TextView) convertView.findViewById(R.id.o_count);

                holder.s_logo = (ImageView) convertView.findViewById(R.id.s_logo);
                holder.s_team = (TextView) convertView.findViewById(R.id.s_team);
                holder.s_player = (TextView) convertView.findViewById(R.id.s_player);
                holder.s_score = (TextView) convertView.findViewById(R.id.s_score);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            com.example.kjanghoi.mlb.ListData mData = mListData.get(position);

            if (mData.f_logo != null) {
                holder.f_logo.setVisibility(View.VISIBLE);
                holder.f_logo.setImageDrawable(mData.f_logo);
            } else {
                holder.f_logo.setVisibility(View.GONE);
            }

            if (mData.sit != null) {
                holder.sit.setVisibility(View.VISIBLE);
                holder.sit.setImageDrawable(mData.sit);
            } else {
                holder.sit.setVisibility(View.GONE);
            }

            if (mData.s_logo != null) {
                holder.s_logo.setVisibility(View.VISIBLE);
                holder.s_logo.setImageDrawable(mData.s_logo);
            } else {
                holder.s_logo.setVisibility(View.GONE);
            }

            holder.f_team.setText(mData.f_team);
            holder.f_player.setText(mData.f_player);
            holder.f_score.setText(mData.f_score);

            holder.inning.setText(mData.inning);
            holder.b_count.setText(mData.b_count);
            holder.s_count.setText(mData.s_count);
            holder.o_count.setText(mData.o_count);

            holder.s_team.setText(mData.s_team);
            holder.s_player.setText(mData.s_player);
            holder.s_score.setText(mData.s_score);

            return convertView;
        }
    }
}

