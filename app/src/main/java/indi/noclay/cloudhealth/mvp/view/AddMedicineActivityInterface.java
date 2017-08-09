package indi.noclay.cloudhealth.mvp.view;


import indi.noclay.cloudhealth.database.MedicineDetail;

/**
 * Created by noclay on 2017/5/7.
 */

public interface AddMedicineActivityInterface {
    void startSaveData();
    void saveSuccess();
    void saveFailed();
    void editImage();
    void showImage(String url);
    void initView(MedicineDetail medicineDetail);
    void toast(String content);
    void startLoadImage();
    void loadSuccess();
    void loadFailed();
    void inputDayLength();
    void inputTimeAndDose();
    MedicineDetail getMedicineDetail();
}
