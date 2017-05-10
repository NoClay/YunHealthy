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

public class AdapterDoctors extends RecyclerView.Adapter<AdapterDoctors.ViewHolder>{

    private Context mContext;
    private int mResource;

    public AdapterDoctors(Context context, int resource) {
        mContext = context;
        mResource = resource;
    }

    public AdapterDoctors(Context context) {
        mContext = context;
        mResource = R.layout.item_doctors_section_detail;
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
        return 10;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        View view;
        CircleImageView mHeadImage;
        TextView mTalkTarget;
        TextView mLastTalkContent;

        ViewHolder(View view) {
            super(view);
            this.view = view;
            this.mHeadImage = (CircleImageView) view.findViewById(R.id.head_image);
            this.mTalkTarget = (TextView) view.findViewById(R.id.talkTarget);
            this.mLastTalkContent = (TextView) view.findViewById(R.id.lastTalkContent);
        }
    }
}
