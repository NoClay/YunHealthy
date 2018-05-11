package indi.noclay.cloudhealth.database;

import java.util.Calendar;
import java.util.Date;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobPointer;

/**
 * Created by NoClay on 2018/5/9.
 */

public class MeasureXinDian extends BmobObject{
    private String fileName;
    private String fileUrl;
    private BmobPointer owner;
    private String fileLength;
    private BmobDate createDate;

    public BmobDate getCreateDate() {
        return createDate;
    }

    public void setCreateDate(BmobDate createDate) {
        this.createDate = createDate;
    }

    public String getFileLength() {
        return fileLength;
    }

    public void setFileLength(String fileLength) {
        this.fileLength = fileLength;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
        String data = fileName.substring(0, fileName.length() - 4);
        String[] numbers = data.split("_");
        if (numbers.length == 5){
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, Integer.valueOf(numbers[0]));
            calendar.set(Calendar.MONTH, Integer.valueOf(numbers[1]) - 1);
            calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(numbers[2]));
            calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(numbers[3]));
            calendar.set(Calendar.MINUTE, Integer.valueOf(numbers[4]));
            Date date = new Date();
            date.setTime(calendar.getTimeInMillis());
            setCreateDate(new BmobDate(date));
        }
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public BmobPointer getOwner() {
        return owner;
    }

    public void setOwner(BmobPointer owner) {
        this.owner = owner;
    }
}
