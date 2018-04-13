package indi.noclay.cloudhealth.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import indi.noclay.cloudhealth.R;
import indi.noclay.cloudhealth.adapter.LoadItemAdapterForNews;
import indi.noclay.cloudhealth.database.NewsData;
import indi.noclay.cloudhealth.myview.AutoLoadMoreRecyclerView;
import indi.noclay.cloudhealth.myview.FullLinearLayoutManager;
import indi.noclay.cloudhealth.util.YunHealthyLoading;


public class NewsActivity extends AppCompatActivity
        implements LoadItemAdapterForNews.OnItemClickListener, View.OnClickListener {


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
    //    @BindView(R.id.nextPageBt)
//    View mNextPageLoadBt;
    @BindView(R.id.nestedScrollView)
    NestedScrollView mNestedScrollView;
    LoadItemAdapterForNews loadItemAdapterForNews;
    LinkedList<NewsData> newsList;
    List<NewsData> temp;
    boolean mIsFirstIn = true;
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
        YunHealthyLoading.show(this);
    }

    private void initView() {
        mToolbarLayout.setTitle("健康资讯");
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        newsList = new LinkedList<>();
        loadItemAdapterForNews = new LoadItemAdapterForNews(newsList);
        loadItemAdapterForNews.setNewsDatas(newsList);
        LinearLayoutManager fLayout = new LinearLayoutManager(NewsActivity.this,
                AutoLoadMoreRecyclerView.VERTICAL, false);
        fLayout.setSmoothScrollbarEnabled(true);
        fLayout.setAutoMeasureEnabled(true);
        mNewsList.setLayoutManager(fLayout);
        mNewsList.setAdapter(loadItemAdapterForNews);
//        mNewsList.setAutoLoadMoreEnable(true);
        mNewsList.setHasFixedSize(true);
        mNewsList.setNestedScrollingEnabled(false);
        loadItemAdapterForNews.setOnItemClickListener(this);
//        mNewsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                Log.d(TAG, "onScrollStateChanged: ");
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//            }
//        });
        mNestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
                    // 向下滑动
                }

                if (scrollY < oldScrollY) {
                    // 向上滑动
                }

                if (scrollY == 0) {
                    // 顶部
                }

                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    // 底部
                    getData();
                }
            }
        });
//        mNextPageLoadBt.setOnClickListener(this);
    }


    public void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    temp = new ArrayList<NewsData>();
                    document = Jsoup.connect("http://news.99.com.cn/jiankang/" + (nextPage == null ? "" : nextPage)).get();
                    Elements elements = document.select("div.DlistWfc");
                    for (Element e : elements) {
                        NewsData newsData = new NewsData();
                        Elements date = e.select("div.fenghk");
                        StringBuilder stringBuilder = new StringBuilder();
                        for (Element i : date) {
                            stringBuilder.append(i.text().substring(0, i.text().indexOf("201")));
                        }
                        newsData.setDate(stringBuilder.toString().replace(" ", "\n"));
                        newsData.setContent(e.select("p.fengP2").text());
                        newsData.setTitle(e.select("h2 > a").text());
                        newsData.setUrl(e.select("h2 > a").attr("href"));
                        temp.add(newsData);
                    }
                    nextPage = document.select("#page > div.list_con.blue.cc > div.list_left > div.list_left_con > div.list_page > span:nth-child(9) > a").attr("href");
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


    public class NewsHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0: {
                    //成功加载了数据
                    if (temp.size() != 0) {
                        Log.d(TAG, "handleMessage: size " + temp.size());
                        mNewsList.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                for (int i = 0; i < temp.size(); i++) {
                                    newsList.addLast(temp.get(i));
                                }
//                                newsList.addAll(temp);
                                if (!mIsFirstIn) {
                                    loadItemAdapterForNews.notifyItemRangeInserted(newsList.size() - temp.size() + 1, temp.size());
                                } else {
                                    loadItemAdapterForNews.notifyDataSetChanged();
                                }
                                YunHealthyLoading.dismiss();
                            }
                        }, 100);

                        StringBuilder builder = new StringBuilder();
                        for (int i = 0; i < newsList.size(); i++) {
                            builder.append(newsList.get(i).getDate());
                        }
                        Log.d(TAG, "handleMessage: " + builder.toString());
                    }
                    break;
                }
                default:YunHealthyLoading.dismiss();break;

            }
        }
    }
    Handler handler = new NewsHandler();

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.nextPageBt:{
//                getData();
//                break;
//            }
        }
    }
}
