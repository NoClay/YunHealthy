package wang.fly.com.yunhealth.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import wang.fly.com.yunhealth.DataBasePackage.MyDataBase;
import wang.fly.com.yunhealth.R;
import wang.fly.com.yunhealth.util.MyConstants;

/**
 * Created by 82661 on 2016/11/16.
 */

public class RecycleAdapterForReportMenu extends
        RecyclerView.Adapter<RecycleAdapterForReportMenu.ViewHolder>{

    private boolean isManage = false;
    private int layout;
    private OnItemClickListener onItemClickListener;
    private List<MenuInfo> menuInfoList;
    private Context context;
    private int type;
    private MyDataBase dbHelper;
    private SQLiteDatabase db;
    private static final String TAG = "RecycleAdapterForReport";
    String userId;
    public interface OnItemClickListener{
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

    public void toggleManage(){
        if (isManage){
            isManage = false;
        }else{
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
        dbHelper = new MyDataBase(context,
                "LocalStore.db", null, MyConstants.DATABASE_VERSION);
        String userId = context.getSharedPreferences("LoginState",
                Context.MODE_PRIVATE).getString("userId", null);
        dbHelper.initMenuData(userId);
        menuInfoList = new ArrayList<>();
        notifyList();
    }

    private void notifyList() {
        //清除备份
        menuInfoList.clear();
        userId = context.getSharedPreferences("LoginState",
                Context.MODE_PRIVATE).getString("userId", null);
        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from report_menu " +
                "where type = " + type + " and userId = '" + userId + "'", null);
        if (cursor.moveToFirst()){
            do {
                MenuInfo m = new MenuInfo();
                m.setType(type);
                m.setTitle(cursor.getString(cursor.getColumnIndex("content")));
                m.setImage(cursor.getInt(cursor.getColumnIndex("image")));
                int flag = cursor.getInt(cursor.getColumnIndex("checked"));
                if (flag == 0){
                    m.setChecked(false);
                }else{
                    m.setChecked(true);
                }
                if (isManage || m.isChecked()){
                    //管理页面或者已经添加了的菜单
                    menuInfoList.add(m);
                }
            }while (cursor.moveToNext());
            this.notifyDataSetChanged();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: position" + position);
        Log.d(TAG, "onBindViewHolder: title" + menuInfoList.get(position).getTitle());
        if (isManage){
            holder.checkBox.setChecked(menuInfoList.get(position).isChecked());
            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int i = 0;
                    if (holder.checkBox.isChecked()){
                        i = 1;
                    }else{
                        i = 0;
                    }
                    //更新表单
                    db.execSQL("update report_menu " +
                            "set checked = " + i +
                            " where content = '" + menuInfoList.get(position).getTitle() + "'" +
                            " and userId = '" + userId + "'");
                }
            });
            holder.checkBox.setVisibility(View.VISIBLE);
        }else {
            holder.checkBox.setVisibility(View.GONE);
        }
        holder.labelText.setText(menuInfoList.get(position).getTitle());
        holder.labelImage.setImageDrawable(context.getResources()
                .getDrawable(menuInfoList.get(position).getImage()));
        //设置子项点击
        if (onItemClickListener != null && !isManage){
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

    public class ViewHolder extends RecyclerView.ViewHolder{
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

    public static class MenuInfo{
        private int type;
        private String title;
        private int image;
        private boolean checked;



        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getImage() {
            return image;
        }

        public void setImage(int image) {
            this.image = image;
        }

        public boolean isChecked() {
            return checked;
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
        }
    }
}
