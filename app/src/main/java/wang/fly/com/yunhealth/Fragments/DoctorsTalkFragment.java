package wang.fly.com.yunhealth.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import wang.fly.com.yunhealth.Adapter.AdapterTalk;
import wang.fly.com.yunhealth.R;

/**
 * Created by noclay on 2017/5/10.
 */

public class DoctorsTalkFragment extends Fragment {
    private View view;
    private RecyclerView mTalkRecyclerView;
    private AdapterTalk mAdapterTalk;
    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_doctors_talks, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mContext = getContext();
        mTalkRecyclerView = (RecyclerView) view.findViewById(R.id.talkRecyclerView);
        mAdapterTalk = new AdapterTalk(mContext);
        mTalkRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mTalkRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, RecyclerView.VERTICAL));
        mTalkRecyclerView.setHasFixedSize(false);
        mTalkRecyclerView.setAdapter(mAdapterTalk);

    }
}
