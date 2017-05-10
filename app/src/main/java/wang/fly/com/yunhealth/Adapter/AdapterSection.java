package wang.fly.com.yunhealth.Adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import wang.fly.com.yunhealth.R;
import wang.fly.com.yunhealth.util.MyConstants;
import wang.fly.com.yunhealth.util.RecyclerUtils.MyRecyclerViewDivider;
import wang.fly.com.yunhealth.util.UtilClass;

/**
 * Created by noclay on 2017/5/10.
 */

public class AdapterSection extends RecyclerView.Adapter<AdapterSection.ViewHolder>{

    private Context mContext;
    private int mResource;
    private boolean []isOpen = new boolean[MyConstants.SECTIONS.length];
    private List<AdapterDoctors> mAdapterDoctors;
    public static int sItem_Height;

    public AdapterSection(Context context, int resource) {
        mContext = context;
        mResource = resource;
        initAdapter();
    }

    public AdapterSection(Context context) {
        mContext = context;
        mResource = R.layout.item_doctors_sections;
        initAdapter();
    }
    public void initAdapter(){
        mAdapterDoctors = new ArrayList<>();
        sItem_Height = UtilClass.Dp2Px(mContext, 51);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(mResource, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mSectionName.setText(MyConstants.SECTIONS[position]);
        holder.mDoctorRecyclerViewInner.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapterDoctors.add(new AdapterDoctors(mContext));
        if (isOpen[position]){
            //已经打开了
            holder.mDoctorRecyclerViewInner.setLayoutManager(new LinearLayoutManager(mContext));
            holder.mDoctorRecyclerViewInner.setAdapter(mAdapterDoctors.get(position));
            ViewGroup.LayoutParams layoutParams = holder.mDoctorRecyclerViewInner.getLayoutParams();
//            LinearLayout.LayoutParams parent = (LinearLayout.LayoutParams) holder.itemView.getLayoutParams();
//            layoutParams.setMargins(0, parent.topMargin + sItem_Height, 0, 0);
            holder.mDoctorRecyclerViewInner.getLayoutManager().setAutoMeasureEnabled(true);
//            layoutParams.height = mAdapterDoctors.get(position).getItemCount() * (sItem_Height + 1);
//            holder.mDoctorRecyclerViewInner.setLayoutParams(layoutParams);
            holder.mDoctorRecyclerViewInner.addItemDecoration(
                    new MyRecyclerViewDivider(mContext));
            holder.mDoctorRecyclerViewInner.setVisibility(View.VISIBLE);
        }else{
            holder.mDoctorRecyclerViewInner.setVisibility(View.GONE);
        }
        holder.mToggleAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOpen[position]){
                    //已经打开了
                    toggleButtonAnimation(holder, false);
                    isOpen[position] = false;
                    holder.mDoctorRecyclerViewInner.setVisibility(View.GONE);
                }else{
                    toggleButtonAnimation(holder, true);
                    isOpen[position] = true;
                    holder.mDoctorRecyclerViewInner.setLayoutManager(new LinearLayoutManager(mContext));
                    holder.mDoctorRecyclerViewInner.setAdapter(mAdapterDoctors.get(position));
                    holder.mDoctorRecyclerViewInner.addItemDecoration(new MyRecyclerViewDivider(mContext));
//                    ViewGroup.LayoutParams layoutParams = holder.mDoctorRecyclerViewInner.getLayoutParams();
                    holder.mDoctorRecyclerViewInner.getLayoutManager().setAutoMeasureEnabled(true);
//                    holder.mDoctorRecyclerViewInner.getLayoutManager().layoutDecoratedWithMargins();
//                    LinearLayout.LayoutParams parent = (LinearLayout.LayoutParams) holder.itemView.getLayoutParams();
//                    layoutParams.setMargins(0, parent.topMargin + sItem_Height, 0, 0);
//                    layoutParams.height = mAdapterDoctors.get(position).getItemCount() * (sItem_Height + 1);
//                    holder.mDoctorRecyclerViewInner.setLayoutParams(layoutParams);
                    holder.mDoctorRecyclerViewInner.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void toggleButtonAnimation(ViewHolder holder, boolean isOpen){
        if (isOpen){
            ObjectAnimator animator = ObjectAnimator.ofFloat(
                    holder.mToggleButton,
                    "rotation",
                    0, 90
            );
            animator.setDuration(MyConstants.ANIMATION_DURATION);
            animator.start();
        }else{
            ObjectAnimator animator = ObjectAnimator.ofFloat(
                    holder.mToggleButton,
                    "rotation",
                    90, 0
            );
            animator.setDuration(MyConstants.ANIMATION_DURATION);
            animator.start();
        }
    }

    @Override
    public int getItemCount() {
        return MyConstants.SECTIONS.length;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        View view;
        ImageView mToggleButton;
        TextView mSectionName;
        TextView mSectionDoctorsCount;
        RelativeLayout mToggleAction;
        RecyclerView mDoctorRecyclerViewInner;

        ViewHolder(View view) {
            super(view);
            this.view = view;
            this.mToggleButton = (ImageView) view.findViewById(R.id.toggleButton);
            this.mSectionName = (TextView) view.findViewById(R.id.sectionName);
            this.mSectionDoctorsCount = (TextView) view.findViewById(R.id.sectionDoctorsCount);
            this.mToggleAction = (RelativeLayout) view.findViewById(R.id.toggleAction);
            this.mDoctorRecyclerViewInner = (RecyclerView) view.findViewById(R.id.doctorRecyclerViewInner);
        }


    }
}
