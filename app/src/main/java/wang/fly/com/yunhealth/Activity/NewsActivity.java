package wang.fly.com.yunhealth.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import wang.fly.com.yunhealth.Adapter.LoadItemAdapterForNews;
import wang.fly.com.yunhealth.DataBasePackage.NewsData;
import wang.fly.com.yunhealth.MyViewPackage.AutoLoadMoreRecyclerView;
import wang.fly.com.yunhealth.MyViewPackage.FullLinearLayoutManager;
import wang.fly.com.yunhealth.R;

public class NewsActivity extends AppCompatActivity
        implements LoadItemAdapterForNews.OnItemClickListener{


    Document document;
    private static final String TAG = "NewsActivity";
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout mToolbarLayout;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.news_list)
    RecyclerView mNewsList;
    LoadItemAdapterForNews loadItemAdapterForNews;
    List<NewsData> newsList;
    List<NewsData> temp;
    String nextPage;
    public static final int LOAD_SUCCESS = 0;
    public static final int LOAD_EMPITY = 1;
    public static final int LOAD_ERROR = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        ButterKnife.bind(this);
        initView();
        getData();
    }

    private void initView() {
        mToolbarLayout.setTitle("健康资讯");
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        newsList = new ArrayList<>();
        loadItemAdapterForNews = new LoadItemAdapterForNews(newsList);
        loadItemAdapterForNews.setNewsDatas(newsList);
        FullLinearLayoutManager fLayout = new FullLinearLayoutManager(NewsActivity.this,
                AutoLoadMoreRecyclerView.VERTICAL, true);
        fLayout.setSmoothScrollbarEnabled(true);
        mNewsList.setAdapter(loadItemAdapterForNews);
        mNewsList.setLayoutManager(fLayout);
//        mNewsList.setAutoLoadMoreEnable(true);
        mNewsList.setHasFixedSize(true);
        mNewsList.setNestedScrollingEnabled(false);
        loadItemAdapterForNews.setOnItemClickListener(this);
    }


    public void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    temp = new ArrayList<NewsData>();
                    document = Jsoup.connect("http://news.99.com.cn/jiankang/").get();
                    Elements elements = document.select("div.DlistWfc");
                    for (Element e :
                            elements) {
                        NewsData newsData = new NewsData();
                        Elements date = e.select("div.fenghk");
                        StringBuilder stringBuilder = new StringBuilder();
                        for (Element i :
                                date) {
                            stringBuilder.append(i.text());
                            stringBuilder.append("\n");
                        }
                        newsData.setDate(stringBuilder.toString());
                        newsData.setContent(e.select("p.fengP2").text());
                        newsData.setTitle(e.select("h2 > a").text());
                        newsData.setUrl(e.select("h2 > a").attr("href"));
                        temp.add(newsData);
                    }
                    Message message = new Message();
                    message.what = 0;
                    message.arg1 = LOAD_SUCCESS;
                    handler.sendMessage(message);
                } catch (IOException e) {
                    Message message = new Message();
                    message.what = 0;
                    message.arg1 = LOAD_ERROR;
                    handler.sendMessage(message);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0: {
                    //成功加载了数据
                    if (temp.size() != 0){
                        Log.d(TAG, "handleMessage: size " + temp.size());
                        mNewsList.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                for (int i = temp.size(); i > 0; i --) {
                                    newsList.add(temp.get(i - 1));
                                }
//                                newsList.addAll(temp);
                                loadItemAdapterForNews.setNewsDatas(newsList);
                                loadItemAdapterForNews.notifyDataSetChanged();
                            }
                        }, 1000);

                    }
                    break;
                }
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(this, NewDetailActivity.class);
        //利用Bundle传输信息
        Bundle data = new Bundle();
        data.putString("url", newsList.get(position).getUrl());
        intent.putExtra("data", data);
        startActivity(intent);
    }
}
