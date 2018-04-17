package indi.noclay.cloudhealth.service;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Looper;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.List;

import indi.noclay.cloudhealth.R;
import indi.noclay.cloudhealth.database.MedicineDetail;
import indi.noclay.cloudhealth.fragment.DataMedicalFragment;
import indi.noclay.cloudhealth.util.UtilClass;

import static indi.noclay.cloudhealth.database.MedicineTableHelper.getMedicineDetail;


/**
 * Created by i-gaolonghai on 2017/7/31.
 */

public class UpdateService extends RemoteViewsService {
    private static final String TAG = "UpdateService";

    public List<MedicineDetail> getMedicines() {
        List<MedicineDetail> temp = getMedicineDetail(DataMedicalFragment.NOW_MEDICINE, null);
        return temp;
    }




    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.d(TAG, "onGetViewFactory: Here is stable!");
        return new ListRemoteViewsFactory(this.getApplicationContext(), intent);
    }

    class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

        private Context mContext;
        private List<MedicineDetail> medicineDetails;

        public ListRemoteViewsFactory(Context mContext, Intent intent) {
            this.mContext = mContext;
            if (Looper.myLooper() == null) {
                Looper.prepare();
            }
            if ((medicineDetails = getMedicines()) != null) {
                Log.d(TAG, "ListRemoteViewsFactory: 加载数据");
            }
        }

        @Override
        public void onCreate() {
            Log.d(TAG, "onCreate: 服务创建");
        }

        @Override
        public void onDataSetChanged() {
            Log.d(TAG, "onDataSetChanged: 设置数据");
        }

        @Override
        public void onDestroy() {
            medicineDetails.clear();
        }

        @Override
        public int getCount() {
            return medicineDetails.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            if (position < 0 || position >= medicineDetails.size()) {
                return null;
            }
            Log.d(TAG, "getViewAt: position = " + position);
            MedicineDetail medicineDetail = medicineDetails.get(position);
            Log.d(TAG, "getViewAt: size = " + medicineDetails.size());
            RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.item_medicine_widget);
            int nextTimePos = medicineDetail.getNextTime();
            remoteViews.setTextViewText(R.id.nextTime, medicineDetail.getTimes().get(nextTimePos));
            remoteViews.setTextViewText(R.id.showTag, medicineDetail.getTag());
            remoteViews.setTextViewText(R.id.showMedicineName, medicineDetail.getMedicineName());
            remoteViews.setTextViewText(R.id.showMedicineLength, "还需要服用" + medicineDetail.getDayLength() + "天");
            remoteViews.setTextViewText(R.id.showMedicineUseType, medicineDetail.getUseType()
                    + ""
                    + medicineDetail.getDoses().get(nextTimePos)
                    + medicineDetail.getUnit());
            Bitmap image = UtilClass.getBitmapFromGlide(mContext, medicineDetail.getMedicinePicture());
            if (image != null){
                remoteViews.setImageViewBitmap(R.id.showMedicineIcon, image);
                Log.d(TAG, "getViewAt: 这个操作耗时吗？");
            }
            return remoteViews;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
