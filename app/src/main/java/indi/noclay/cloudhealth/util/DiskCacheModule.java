package indi.noclay.cloudhealth.util;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.module.GlideModule;

import java.io.File;

/**
 * Created by noclay on 2017/5/7.
 */

public class DiskCacheModule implements GlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
//        builder.setDiskCache(new InternalCacheDiskCacheFactory(
//                context, "glide_cache", 100 * 1024 * 1024));
        builder.setDiskCache(new DiskLruCacheFactory(
                new DiskLruCacheFactory.CacheDirectoryGetter() {
                    @Override
                    public File getCacheDirectory() {
                        return new File(ConstantsConfig.ROOT_PATH_IMAGE_DIR);
                    }
                }, 100 * 1024 * 1024))
                .setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
    }

    @Override
    public void registerComponents(Context context, Glide glide) {

    }
}
