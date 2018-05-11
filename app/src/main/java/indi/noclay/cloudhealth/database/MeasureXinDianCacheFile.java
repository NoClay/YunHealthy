package indi.noclay.cloudhealth.database;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobPointer;

/**
 * Created by NoClay on 2018/5/9.
 */

public class MeasureXinDianCacheFile extends BmobObject{
    private String fileName;
    private String fileUrl;
    private BmobPointer owner;
    private String fileLength;

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
