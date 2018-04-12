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

    public class NewsDetailHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    break;
                case 1: {
                    content = new WebView(NewDetailActivity.this);
                    mContentLayout.addView(content);
                    Elements element = document.select("body > div.wrapper > div.title_box > h1");
                    Log.d("test", "handleMessage: title = " + element.text());
                    mToolbarLayout.setTitle(element.text());
                    String time = "99健康网 " + document.select("body > div.wrapper > div.title_box > div > div.title_txt > span:nth-child(2)").text();
                    mTimeText.setText(time);
                    String html = document.select("body > div.wrapper > div.left_box > div.profile_box > p").text();
                    mGuideText.setText(Html.fromHtml(html));
                    content.setHorizontalScrollBarEnabled(false);
                    html = document.select("body > div.wrapper > div.left_box > div.new_cont.detail_con").html();
                    int pos = html.indexOf("<p align=\"right\">（");
                    if (pos != -1){
                        html = html.substring(0, pos);
                    }
                    html.replace("<img ", "<img style=\"width:100%\" ");
                    StringBuilder builder = new StringBuilder();
                    builder.append("<div class=\"wrap\" style=\"width:100%\">")
                            .append(html)
                            .append("</div>");
                    Log.d("content", "handleMessage: content = " + html);
                    content.loadDataWithBaseURL(null, html,
                            "text/html", "utf-8", null);
                    break;
                }
            }
        }
    }
    Handler handler = new NewsDetailHandler();

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
