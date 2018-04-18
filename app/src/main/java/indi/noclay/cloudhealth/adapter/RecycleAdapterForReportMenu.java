package indi.noclay.cloudhealth.adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import indi.noclay.cloudhealth.R;
import indi.noclay.cloudhealth.database.LocalDataBase;
import indi.noclay.cloudhealth.database.MenuInfo;
import indi.noclay.cloudhealth.util.ConstantsConfig;
import indi.noclay.cloudhealth.util.SharedPreferenceHelper;

import static indi.noclay.cloudhealth.database.ReportMenuTableHelper.getMenuData;
import static indi.noclay.cloudhealth.database.ReportMenuTableHelper.initMenuData;
import static indi.noclay.cloudhealth.database.ReportMenuTableHelper.updateMenuData;


/**
 * Created by 82661 on 2016/11/16.
 */

public class RecycleAdapterForReportMenu extends
        RecyclerView.Adapter<RecycleAdapterForReportMenu.ViewHolder> {

    private boolean isManage = false;
    private int layout;
    private OnItemClickListener onItemClickListener;
    private List<MenuInfo> menuInfoList;
    private Context context;
    private int type;
    private LocalDataBase dbHelper;
    private SQLiteDatabase db;
    private static final String TAG = "RecycleAdapterForReport";
    String userId;

    public interface OnItemClickListener {
        void onItemClick(View view, String title);
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public boolean isManage() {
        return isManage;
    }

    public void setManage(boolean manage) {
        isManage = manage;
    }

    public void toggleManage() {
        if (isManage) {
            isManage = false;
        } else {
            isManage = true;
        }
        this.notifyList();
        this.notifyDataSetChanged();
    }

    public RecycleAdapterForReportMenu(boolean isManage, int layout, Context context, int type) {
        this.isManage = isManage;
        this.layout = layout;
        this.type = type;
        this.context = context;
        //构造List
        initMenuData();
        menuInfoList = new ArrayList<>();
        notifyList();
    }

    private void notifyList() {
        //清除备份
        menuInfoList.clear();
        List<MenuInfo> temp = getMenuData(type);
        for (MenuInfo m : temp) {
            if (isManage || m.isChecked()) {
                menuInfoList.add(m);
            }
        }
        this.notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(layout, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: position" + position);
        Log.d(TAG, "onBindViewHolder: title" + menuInfoList.get(position).getTitle());
        if (isManage) {
            holder.checkBox.setChecked(menuInfoList.get(position).isChecked());
            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int i = 0;
                    if (holder.checkBox.isChecked()) {
                        i = 1;
                    }
                    //更新表单
                    updateMenuData(i, menuInfoList.get(position));
                }
            });
            holder.checkBox.setVisibility(View.VISIBLE);
        } else {
            holder.checkBox.setVisibility(View.GONE);
        }
        holder.labelText.setText(menuInfoList.get(position).getTitle());
        holder.labelImage.setImageDrawable(context.getResources()
                .getDrawable(menuInfoList.get(position).getImage(), null));
        //设置子项点击
        if (onItemClickListener != null && !isManage) {
            //如果子项点击事件不为空，且处于管理状态
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(v,
                            menuInfoList.get(position).getTitle());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return menuInfoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView labelImage;
        public TextView labelText;
        public CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            checkBox = (CheckBox) itemView.findViewById(R.id.report_label_check);
            labelImage = (ImageView) itemView.findViewById(R.id.report_label_view);
            labelText = (TextView) itemView.findViewById(R.id.report_label_title);
        }
    }

}
