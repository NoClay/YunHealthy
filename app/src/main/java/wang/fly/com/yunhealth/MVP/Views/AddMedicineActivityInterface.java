package wang.fly.com.yunhealth.MVP.Views;

import wang.fly.com.yunhealth.DataBasePackage.MedicineDetail;
import wang.fly.com.yunhealth.DataBasePackage.SignUserData;

/**
 * Created by noclay on 2017/5/7.
 */

public interface AddMedicineActivityInterface {
    void startSaveData();
    void saveSuccess(SignUserData userData);
    void saveFailed();
    void editImage();
    void showImage(String url);
    void initView(MedicineDetail medicineDetail);
    void toast(String content);
    void startLoadImage();
    void loadSuccess();
    void loadFailed();
    void inputDayLength();
    MedicineDetail getMedicineDetail();
}
