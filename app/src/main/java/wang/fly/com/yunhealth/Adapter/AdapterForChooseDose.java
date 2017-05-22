package wang.fly.com.yunhealth.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import wang.fly.com.yunhealth.MyViewPackage.Dialogs.InputTimeAndDoseDialog;
import wang.fly.com.yunhealth.R;
import wang.fly.com.yunhealth.util.MyConstants;
import wang.fly.com.yunhealth.util.UtilClass;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by noclay on 2017/5/20.
 */

public class AdapterForChooseDose extends RecyclerView.Adapter<AdapterForChooseDose.ViewHolder> {

    Context mContext;
    int mResource;
    List<InputTimeAndDoseDialog.Dose> mDatas;
    private static final String TAG = "AdapterChooseDose";



    public AdapterForChooseDose(Context context,
                             List<InputTimeAndDoseDialog.Dose> datas,
                             int resource) {
        mContext = context;
        mResource = resource;
        mDatas = datas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(mResource, parent, false);
        return new ViewHolder(view);
    }

    public void clearCheck(ViewHolder holder, boolean checked) {
        holder.mCheckbox.setVisibility(GONE);
        holder.mChooseDoseLayout.setVisibility(GONE);
        if (checked) {
            holder.mChooseDoseLayout.setVisibility(VISIBLE);
            holder.mCheckbox.setVisibility(VISIBLE);
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final InputTimeAndDoseDialog.Dose data = mDatas.get(position);
        holder.mTime.setText(MyConstants.TIMES[position]);
        holder.mDose.setText(data.getValue().toString());
        clearCheck(holder, data.getChecked());
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.incButton:{
                        float temp = Float.valueOf(holder.mDose.getText().toString());
                        temp += MyConstants.ADD_LENGTH;
                        holder.mDose.setText(temp + "");
                        data.setValue(temp);
                        break;
                    }
                    case R.id.decButton:{
                        float temp = Float.valueOf(holder.mDose.getText().toString());
                        if (temp - MyConstants.ADD_LENGTH < 0){
                            temp = 0f;
                        }else{
                            temp -= MyConstants.ADD_LENGTH;
                        }
                        holder.mDose.setText(temp + "");
                        data.setValue(temp);
                        break;
                    }
                }
            }
        };
        holder.mDose.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "afterTextChanged: " + s.toString());
                String input = s.toString();
                if (UtilClass.isFloatOfString(input)){
                    float temp = Float.valueOf(input);
                    data.setValue(temp);
                }else{
//                    data.setValue(0.0f);
                }
            }
        });
        holder.mDose.setOnClickListener(listener);
        holder.mIncButton.setOnClickListener(listener);
        holder.mDecButton.setOnClickListener(listener);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.toggle();
                clearCheck(holder, data.getChecked());
            }
        });
    }


    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        View view;
        ImageView mCheckbox;
        TextView mTime;
        ImageView mIncButton;
        EditText mDose;
        ImageView mDecButton;
        LinearLayout mChooseDoseLayout;

        ViewHolder(View view) {
            super(view);
            this.view = view;
            this.mCheckbox = (ImageView) view.findViewById(R.id.checkbox);
            this.mTime = (TextView) view.findViewById(R.id.time);
            this.mIncButton = (ImageView) view.findViewById(R.id.incButton);
            this.mDose = (EditText) view.findViewById(R.id.dose);
            this.mDecButton = (ImageView) view.findViewById(R.id.decButton);
            this.mChooseDoseLayout = (LinearLayout) view.findViewById(R.id.chooseDoseLayout);
        }
    }
}
