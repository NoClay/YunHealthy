package indi.noclay.cloudhealth.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static indi.noclay.cloudhealth.util.ConstantsConfig.CACHE_DATA_DIR;
import static indi.noclay.cloudhealth.util.ConstantsConfig.MEASURE_TYPE_XINDIAN;

/**
 * Created by NoClay on 2018/5/6.
 */

public class BinFileHelper {

    public static String getCacheFileName(int type) {
        String cacheDir = CACHE_DATA_DIR + SharedPreferenceHelper.getLoginUserId() + "/";
        if (type == MEASURE_TYPE_XINDIAN) {
            cacheDir = cacheDir + "xindian/";
        }
        File dir = new File(cacheDir);
        if (!dir.exists() || dir.isFile()) {
            dir.delete();
            dir.mkdirs();
        }
        return cacheDir + UtilClass.getTimeStamp() + ".bin";
    }


    public static List<Integer> readFromFile(String fileName){
        List<Integer> integers = new ArrayList<>();
        File file = new File(fileName);
        if (file.exists() && file.isFile()){
            try {
                DataInputStream in = new DataInputStream(new FileInputStream(fileName));
                byte[] bytes = new byte[2];
                while (in.read(bytes) != -1){
                    int value = 0;
                    value += (bytes[0] & 0xFF) * 256;
                    value += (bytes[1] & 0xFF);
                    integers.add(value);
                }
                in.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return integers;
    }


    public static void appendToFile(String fileName, int... values) {
        File file = new File(fileName);
        try {
            if (!file.exists() || file.isDirectory()) {
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
}
