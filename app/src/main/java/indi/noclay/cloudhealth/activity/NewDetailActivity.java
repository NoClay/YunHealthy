package indi.noclay.cloudhealth.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import indi.noclay.cloudhealth.R;
import indi.noclay.cloudhealth.database.NewsData;
import indi.noclay.cloudhealth.myview.YunHealthyErrorView;
import indi.noclay.cloudhealth.util.ConstantsConfig;
import indi.noclay.cloudhealth.util.SharedPreferenceHelper;
import indi.noclay.cloudhealth.util.ViewUtils;
import indi.noclay.cloudhealth.util.YunHealthyLoading;

import static indi.noclay.cloudhealth.util.ViewUtils.hideView;
import static indi.noclay.cloudhealth.util.ViewUtils.showView;
import static pers.noclay.utiltool.ShareUtils.shareText;

/**
 * Created by no_clay on 2017/2/7.
 */

public class NewDetailActivity extends AppCompatActivity implements View.OnClickListener {
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
    @BindView(R.id.errorView)
    YunHealthyErrorView errorView;
    WebView content;


    private String url;
    private Document document;
    private boolean isTop;
    private String title;
    private NewsData mNewsData;
    private static final String TAG = "NewDetailActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        ButterKnife.bind(this);
        initView();
        getData();
        YunHealthyLoading.show(this);
    }

    private void getData() {
        hideView(errorView);
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
        isTop = getIntent().getBooleanExtra(ConstantsConfig.PARAMS_IS_TOP, false);
        url = getIntent().getStringExtra(ConstantsConfig.PARAMS_URL);
        title = getIntent().getStringExtra(ConstantsConfig.PARAMS_TITLE);
        if (!isTop){
            mNewsData = (NewsData) getIntent().getSerializableExtra(ConstantsConfig.PARAMS_OBJECT);
        }
        mToolbarLayout.setTitle(title);
        mFab.setOnClickListener(this);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        hideView(errorView);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                System.exit(0);
                finish();
                return true;
            }
            case R.id.star:{
                item.setVisible(false);
                Toast.makeText(this, "请到我的资讯查看", Toast.LENGTH_SHORT).show();
                if (mNewsData != null){
                    mNewsData.setOwner(SharedPreferenceHelper.getLoginUser());
                    mNewsData.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e == null){
                                Log.d(TAG, "done: collection success");
                            }else{
                                if (!e.toString().contains("unique index cannot has duplicate value")){
                                    Log.e(TAG, "done: " + e.toString(), e);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(NewDetailActivity.this, "添加失败，请重试", Toast.LENGTH_SHORT).show();
                                            item.setVisible(true);
                                        }
                                    });
                                }
                            }
                        }
                    });
                }
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isTop){
            return false;
        }
        getMenuInflater().inflate(R.menu.menu_collection, menu);
        return true;
    }



    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab) {
            if (isTop) {
                shareText(this, title, "我正在看【" + title + "】，\n查看详情：" + url);
            } else {
                //健康资讯的分享
                shareText(this, title, "我正在看【" + title + "】:\n" + mGuideText.getText() + "\n查看详情：" + url);
            }
        }
    }

    public class NewsDetailHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    YunHealthyLoading.dismiss();
                    showView(errorView);
                    break;
                case 1: {
                    content = new WebView(NewDetailActivity.this);
                    mContentLayout.addView(content);
                    if (isTop) {
                        handleIfIsTop();
                    } else {
                        handleIfNotTop();
                    }
                    YunHealthyLoading.dismiss();
                    break;
                }
                default:
            }
        }
    }

    private void handleIfNotTop() {
        Elements element;
        String time = "99健康网 " + document.select("body > div.wrapper > div.title_box > div > div.title_txt > span:nth-child(2)").text();
        mTimeText.setText(time);
        String html = document.select("body > div.wrapper > div.left_box > div.profile_box > p").text();
        mGuideText.setText(Html.fromHtml(html));
        content.setHorizontalScrollBarEnabled(false);
        html = document.select("body > div.wrapper > div.left_box > div.new_cont.detail_con").html();
        int pos = html.indexOf("<p align=\"right\">（");
        if (pos != -1) {
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
    }

    private void handleIfIsTop() {
        ViewUtils.hideView(mGuideText);
        Elements element;
        String time = document.select("body > div.container.wrapper.clearfix > div.main.fl > div.article > div.newsinfo").text();
        mTimeText.setText(time);
        String html;
        content.setHorizontalScrollBarEnabled(false);
        html = document.select("body > div.container.wrapper.clearfix > div.main.fl > div.article > div.newstext").html();
        html.replace("<img ", "<img style=\"width:100%\" ");
        StringBuilder builder = new StringBuilder();
        builder.append("<div class=\"wrap\" style=\"width:100%\">")
                .append(html)
                .append("</div>");
        content.loadDataWithBaseURL(null, html,
                "text/html", "utf-8", null);
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
