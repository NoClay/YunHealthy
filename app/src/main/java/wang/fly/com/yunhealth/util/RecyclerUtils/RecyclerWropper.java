package wang.fly.com.yunhealth.util.RecyclerUtils;

import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by noclay on 2017/4/20.
 */

public class RecyclerWropper<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int BASE_ITEM_TYPE_HEADER = 100000;
    public static final int BASE_ITEM_TYPE_FOOTER = 200000;

    private SparseArrayCompat<View> mHeaderViews = new SparseArrayCompat<>();
    private SparseArrayCompat<View> mFooterViews = new SparseArrayCompat<>();

    private RecyclerView.Adapter mInnerAdapter;

    public RecyclerWropper(RecyclerView.Adapter innerAdapter) {
        mInnerAdapter = innerAdapter;
    }

    private boolean isHeaderPos(int position) {
        return position < getHeadersCount();
    }

    private boolean isFooterPos(int position) {
        return position >= getHeadersCount() + getRealItemCount();
    }

    public void addHeaderView(View view) {
        mHeaderViews.put(mHeaderViews.size() + BASE_ITEM_TYPE_HEADER, view);
    }

    public void addFootView(View view) {
        mFooterViews.put(mFooterViews.size() + BASE_ITEM_TYPE_FOOTER, view);
    }

    public int getFootersCount() {
        return mFooterViews.size();
    }


    public int getHeadersCount() {
        return mHeaderViews.size();
    }

    public int getRealItemCount() {
        return mInnerAdapter.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderPos(position)) {
            return mHeaderViews.keyAt(position);
        } else if (isFooterPos(position)) {
            return mFooterViews.keyAt(position - getHeadersCount() - getRealItemCount());
        }
        return mInnerAdapter.getItemViewType(position - getHeadersCount());
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mHeaderViews.get(viewType) != null) {

            ViewHolder holder = ViewHolder.createViewHolder(
                    parent.getContext(), mHeaderViews.get(viewType)
            );
            return holder;
        } else if (mFooterViews.get(viewType) != null) {
            ViewHolder holder = ViewHolder.createViewHolder(
                    parent.getContext(), mFooterViews.get(viewType)
            );
            return holder;
        }
        return mInnerAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isHeaderPos(position)){
            return;
        }
        if (isFooterPos(position)){
            return;
        }
        mInnerAdapter.onBindViewHolder(holder, position - getHeadersCount());
    }

    @Override
    public int getItemCount() {
        return getHeadersCount() + getRealItemCount() + getFootersCount();
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView)
    {
        WrapperUtil.onAttachedToRecyclerView(mInnerAdapter, recyclerView, new WrapperUtil.SpanSizeCallback()
        {
            @Override
            public int getSpanSize(GridLayoutManager layoutManager, GridLayoutManager.SpanSizeLookup oldLookup, int position)
            {
                int viewType = getItemViewType(position);
                if (mHeaderViews.get(viewType) != null)
                {
                    return layoutManager.getSpanCount();
                } else if (mFooterViews.get(viewType) != null)
                {
                    return layoutManager.getSpanCount();
                }
                if (oldLookup != null)
                    return oldLookup.getSpanSize(position);
                return 1;
            }
        });
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder)
    {
        mInnerAdapter.onViewAttachedToWindow(holder);
        int position = holder.getLayoutPosition();
        if (isHeaderPos(position) || isFooterPos(position))
        {
            WrapperUtil.setFullSpan(holder);
        }
    }
}
