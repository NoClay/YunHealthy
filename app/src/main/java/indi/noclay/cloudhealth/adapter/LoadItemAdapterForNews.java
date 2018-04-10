package indi.noclay.cloudhealth.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import indi.noclay.cloudhealth.R;
import indi.noclay.cloudhealth.database.NewsData;


/**
 * Created by no_clay on 2017/2/7.
 */

public class LoadItemAdapterForNews
        extends RecyclerView.Adapter<LoadItemAdapterForNews.ViewHolder> {

    private List<NewsData> mNewsDatas;
    OnItemClickListener mOnItemClickListener;
    private int mNowBindingPos = 0;

    public LoadItemAdapterForNews(List<NewsData> newsDatas) {
        mNewsDatas = newsDatas;
    }

    public List<NewsData> getNewsDatas() {
        return mNewsDatas;
    }

    public void setNewsDatas(List<NewsData> newsDatas) {
        mNewsDatas = newsDatas;
    }

    public OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_news, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        mNowBindingPos = position;
        NewsData newsData = mNewsDatas.get(position);
        holder.mDateShow.setText(newsData.getDate());
        holder.mTitleShow.setText(newsData.getTitle());
        holder.mContentShow.setText(newsData.getContent());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(v, holder.getLayoutPosition());
            }
        });
    }

    public int getmNowBindingPos() {
        return mNowBindingPos;
    }

    public boolean isLastItemBinding(){
        return mNowBindingPos == getItemCount() - 1;
    }

    @Override
    public int getItemCount() {
        return mNewsDatas.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.dateShow)
        TextView mDateShow;
        @BindView(R.id.titleShow)
        TextView mTitleShow;
        @BindView(R.id.contentShow)
        TextView mContentShow;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
