package indi.noclay.cloudhealth.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static indi.noclay.cloudhealth.util.ConstantsConfig.CACHE_DATA_DIR;

/**
 * Created by clay on 2018/3/28.
 */

public class FileCacheUtil {

    public static String getCacheFileName(){
        return getCacheFileName(UtilClass.getTimeStamp());
    }

    /**
     * 根据时间戳获取缓存文件名
     * @param timeStamp
     * @return
     */
    public static String getCacheFileName(String timeStamp){
        String cacheDir = CACHE_DATA_DIR
                + SharedPreferenceHelper.getLoginUserId()
                + "/xindian/";
        File dir = new File(cacheDir);
        if (!dir.exists() || dir.isFile()){
            dir.delete();
            dir.mkdirs();
        }
        return cacheDir + timeStamp + ".bin";
    }

    public static void appendToFile(int... values){
        String fileName = getCacheFileName();
        File file = new File(fileName);
        try {
            if (!file.exists() || file.isDirectory()){
                file.delete();
                file.createNewFile();
            }
            DataOutputStream out = new DataOutputStream(new FileOutputStream(fileName, true));
            for (int value : values) {
                byte[] bytes = new byte[2];
                bytes[0] = (byte) (value / 256);
                bytes[1] = (byte) (value % 256);
                out.write(bytes);
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static List<Integer> readFromFile(Calendar calendar){
        return readFromFile(getCacheFileName(UtilClass.getTimeStamp(calendar)));
    }
    public static List<Integer> readFromFile(int year, int month, int day, int hour, int minute){
        return readFromFile(getCacheFileName(UtilClass.getTimeStamp(year, month, day, hour, minute)));
    }

    public static List<Integer> readFromFile(String fileName){
        List<Integer> integers = new ArrayList<>();
        File file = new File(fileName);
        if (file.exists() && file.isFile()){
            try {
                DataInputStream in = new DataInputStream(new FileInputStream(fileName));
                byte[] bytes = new byte[2];
                int count = 0;
                while (in.read(bytes) != -1){
                    int value = 0;
                    value += (bytes[0] & 0xFF) * 256;
                    value += (bytes[1] & 0xFF);
                    integers.add(value);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return integers;
    }
}