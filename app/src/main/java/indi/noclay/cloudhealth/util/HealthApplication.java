package indi.noclay.cloudhealth.util;

import android.Manifest;
import android.app.Application;
import android.content.Context;

import java.io.File;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConfig;
import cn.smssdk.SMSSDK;

/**
 * Created by noclay on 2017/4/15.
 */

public class HealthApplication extends Application {

    private static Context sContext;
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化一些资源
        sContext = getApplicationContext();
        initDirs();
        initDependencies();
    }


    /**
     * 初始化文件夹
     */
    private void initDirs() {
        File file = new File(ConstantsConfig.APPLICATION_DIR);
        if (!file.exists() || file.isFile()) {
            file.delete();
            file.mkdirs();
        }
        file = new File(ConstantsConfig.ROOT_PATH_IMAGE_DIR);
        if (!file.exists() || file.isFile()){
            file.delete();
            file.mkdirs();
        }
        file = new File(ConstantsConfig.CACHE_DATA_DIR);
        if (!file.exists() || file.isFile()){
            file.delete();
            file.mkdirs();
        }
    }




    public static Context getContext() {
        return sContext;
    }

    /**
     * 初始化依赖第三方：
     * Bmob
     * Mob
     */
    public void initDependencies() {
        BmobConfig config = new BmobConfig.Builder(this)
                .setApplicationId("d2e2a48caabc1e5c399b20b2adea85eb")
                .setConnectTimeout(15)
                .setUploadBlockSize(1024 * 1024)
                .setFileExpiration(2500)
                .build();
        Bmob.initialize(config);
        //初始化Mob
        SMSSDK.initSDK(this, "195be1e7755e2", "5bdd8a14d2e2f5734797443c982b0db4");
    }

}
