package wang.fly.com.yunhealth.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import wang.fly.com.yunhealth.R;

/**
 * Created by noclay on 2017/5/10.
 */

public class AdapterTalk extends RecyclerView.Adapter<AdapterTalk.ViewHolder> {

    private Context mContext;
    private int mResource;

    public AdapterTalk(Context context) {
        mContext = context;
        mResource = R.layout.item_doctors_talk;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(mResource, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 50;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        CircleImageView mHeadImage;
        TextView mTalkTarget;
        TextView mLastTalkContent;
        TextView mLastTalkTime;
        TextView mNotedMessageCount;

        ViewHolder(View view) {
            super(view);
            this.view = view;
            this.mHeadImage = (CircleImageView) view.findViewById(R.id.head_image);
            this.mTalkTarget = (TextView) view.findViewById(R.id.talkTarget);
            this.mLastTalkContent = (TextView) view.findViewById(R.id.lastTalkContent);
            this.mLastTalkTime = (TextView) view.findViewById(R.id.lastTalkTime);
            this.mNotedMessageCount = (TextView) view.findViewById(R.id.notedMessageCount);
        }
    }
}
