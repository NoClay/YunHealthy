package indi.noclay.cloudhealth.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.List;

import indi.noclay.cloudhealth.R;
import indi.noclay.cloudhealth.database.MedicineDetail;
import indi.noclay.cloudhealth.database.MedicineTableHelper;

import static indi.noclay.cloudhealth.database.MedicineTableHelper.CLOCK_CLOSE;
import static indi.noclay.cloudhealth.database.MedicineTableHelper.CLOCK_OPEN;


/**
 * Created by noclay on 2017/4/23.
 */

public class MedicineListAdapter extends RecyclerView.Adapter<MedicineListAdapter.ViewHolder> {

    List<MedicineDetail> medicineList;
    Context mContext;
    int resourceId;
    private static final String TAG = "MedicineListAdapter";

    public MedicineListAdapter(List<MedicineDetail> medicineList, Context context, int resourceId) {
        this.medicineList = medicineList;
        mContext = context;
        this.resourceId = resourceId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(resourceId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final MedicineDetail temp = medicineList.get(position);
        Integer nextTimePos = temp.getNextTime();
        Log.d(TAG, "onBindViewHolder: size = " + medicineList.size());
        if (nextTimePos != null) {
            Log.d(TAG, "onBindViewHolder: time = " + temp.getTimes().get(nextTimePos));
            holder.mNextTime.setText(temp.getTimes().get(nextTimePos) + "");
            holder.mShowMedicineUseType.setText(temp.getUseType() +
                    temp.getDoses().get(nextTimePos)
                    + temp.getUnit());
        }
        holder.mShowTag.setText(temp.getTag() + "");
        Integer isOpen = temp.getIsOpen();
        if (isOpen == null || isOpen == CLOCK_OPEN) {
            holder.mOpenOrClose.setChecked(true);
        } else {
            holder.mOpenOrClose.setChecked(false);
        }
        holder.mOpenOrClose.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                temp.setIsOpen(isChecked ? CLOCK_OPEN : CLOCK_CLOSE);
                MedicineTableHelper.updateMedicineDetail(temp);
                temp.update();
            }
        });
        Glide.with(mContext).load(temp.getMedicinePicture())
                .placeholder(R.drawable.medicine)
                .error(R.drawable.medicine)
                .crossFade().into(new SimpleTarget<GlideDrawable>() {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                holder.mShowMedicineIcon.setImageDrawable(resource);
            }
        });
        holder.mShowMedicineName.setText(temp.getMedicineName() + "");
        holder.mShowMedicineLength.setText("还需要服用" + temp.getDayLength() + "天");
    }

    @Override
    public int getItemCount() {
        return medicineList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        TextView mNextTime;
        TextView mShowTag;
        Switch mOpenOrClose;
        ImageView mShowMedicineIcon;
        TextView mShowMedicineName;
        TextView mShowMedicineUseType;
        TextView mShowMedicineLength;

        ViewHolder(View view) {
            super(view);
            this.view = view;
            this.mNextTime = (TextView) view.findViewById(R.id.nextTime);
            this.mShowTag = (TextView) view.findViewById(R.id.showTag);
            this.mOpenOrClose = (Switch) view.findViewById(R.id.openOrClose);
            this.mShowMedicineIcon = (ImageView) view.findViewById(R.id.showMedicineIcon);
            this.mShowMedicineName = (TextView) view.findViewById(R.id.showMedicineName);
            this.mShowMedicineUseType = (TextView) view.findViewById(R.id.showMedicineUseType);
            this.mShowMedicineLength = (TextView) view.findViewById(R.id.showMedicineLength);
        }
    }
}
