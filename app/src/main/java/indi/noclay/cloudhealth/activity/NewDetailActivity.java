package indi.noclay.cloudhealth.activity;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import indi.noclay.cloudhealth.R;

/**
 * Created by no_clay on 2017/2/7.
 */

public class NewDetailActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout mToolbarLayout;
    @BindView(R.id.appBarLayout)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.timeText)
    TextView mTimeText;
    @BindView(R.id.guideText)
    TextView mGuideText;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.activity_news)
    CoordinatorLayout mActivityNews;
    @BindView(R.id.contentLayout)
    LinearLayout mContentLayout;
    WebView content;


    private String url;
    private Document document;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        ButterKnife.bind(this);
        initView();
        getData();
    }

    private void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (url != null) {
                    try {
                        document = Jsoup.connect(url).get();
                        Message message = Message.obtain();
                        message.what = (document == null ? 0 : 1);
                        handler.sendMessage(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void initView() {
        mToolbarLayout.setTitle("健康资讯");
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Bundle data = getIntent().getBundleExtra("data");
        url = data.getString("url");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                System.exit(0);
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    Handler handler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    break;
                case 1: {
                    content = new WebView(NewDetailActivity.this);
                    mContentLayout.addView(content);

                    Elements element = document.select("#Page > div.con_left > " +
                            "div.art_con.cc > div > div.title > h1");
                    Log.d("test", "handleMessage: title = " + element.text());
                    mToolbarLayout.setTitle(element.text());
                    String time = "99健康网 " + document.select("#Page > div.con_left " +
                            "> div.art_con.cc > div > div.title > div.time_share > " +
                            "div.l_time > span:nth-child(1)").text();
                    mTimeText.setText(time);
                    String html = document.select("#Page > div.con_left > " +
                            "div.art_con.cc > div > div.article_con > div.art_intro > p").text();
                    mGuideText.setText(Html.fromHtml(html));
                    html = document.select("#Page > div.con_left > div.art_con.cc > " +
                            "div > div.article_con > div.detail_con").html();
                    Log.d("content", "handleMessage: content = " + html);
                    content.loadDataWithBaseURL(null, html,
                            "text/html", "utf-8", null);
                    break;
                }
            }
        }
    };

    @Override
    public void onBackPressed() {
        System.exit(0);
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        System.exit(0);
        super.onDestroy();
    }

    @Override
    public void overridePendingTransition(int enterAnim, int exitAnim) {
        super.overridePendingTransition(enterAnim, exitAnim);
    }
}
