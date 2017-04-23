package wang.fly.com.yunhealth.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import wang.fly.com.yunhealth.DataBasePackage.MedicineDetial;
import wang.fly.com.yunhealth.R;

/**
 * Created by noclay on 2017/4/23.
 */

public class MedicineListAdapter extends RecyclerView.Adapter<MedicineListAdapter.ViewHolder> {

    List<MedicineDetial> medicineList;
    Context mContext;
    int resourceId;

    public MedicineListAdapter(List<MedicineDetial> medicineList, Context context, int resourceId) {
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
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return medicineList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
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
