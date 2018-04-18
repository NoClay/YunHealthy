package indi.noclay.cloudhealth.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import org.w3c.dom.Text;

import java.util.logging.Logger;

import indi.noclay.cloudhealth.R;

/**
 * Created by clay on 2018/4/12.
 */

public class YunHealthyLoading {
    private static LoadingDialog loadingDialog;
    public static final long MIN_LOADING_TIME_IN_MILLSECONDS = 1000;
    public static final int DIALOG_STYLE_NO_FULLSCREEN = R.style.AppTheme_DialogNoFullScreen;
    public static final int DIALOG_STYLE_FULLSCREEN = R.style.AppTheme_DialogFullScreen;
    public static Context sContext;
    private static long sShowTime;
    private static final String TAG = "YunHealthyLoading";

    public static boolean isShowing() {
        return loadingDialog != null && loadingDialog.isShowing();
    }


    public static void show(Context context, View parent){
        if (!isShowing() && context != null) {
            if (!(context instanceof Activity) || UtilClass.isActivityContextVaild((Activity) context)) {
                loadingDialog = new YunHealthyLoading.LoadingDialog(context, DIALOG_STYLE_NO_FULLSCREEN);
                loadingDialog.setCanceledOnTouchOutside(false);
                initWindow(parent);
                loadingDialog.show();
                sShowTime = System.currentTimeMillis();
                sContext = context;
            }
        }
    }
    public static void show(Context context, int gravity, int width, int height) {
        if (!isShowing() && context != null) {
            if (!(context instanceof Activity) || UtilClass.isActivityContextVaild((Activity) context)) {
                loadingDialog = new YunHealthyLoading.LoadingDialog(context, DIALOG_STYLE_NO_FULLSCREEN);
                loadingDialog.setCanceledOnTouchOutside(false);
                initWindow(gravity, width, height);
                loadingDialog.show();
                sShowTime = System.currentTimeMillis();
                sContext = context;
            }
        }
    }

    /**
     * 全屏展示
     * @param context
     */
    public static void show(Context context) {
        if (!isShowing() && context != null) {
            if (!(context instanceof Activity) || UtilClass.isActivityContextVaild((Activity) context)) {
                loadingDialog = new YunHealthyLoading.LoadingDialog(context, DIALOG_STYLE_NO_FULLSCREEN);
                loadingDialog.setCanceledOnTouchOutside(false);
                initWindow();
                loadingDialog.show();
                sShowTime = System.currentTimeMillis();
                sContext = context;
            }
        }
    }

    public static void initWindow(View parent){
        Window window = loadingDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = parent.getMeasuredWidth();
        lp.height = parent.getMeasuredHeight();
        lp.x = (int) parent.getX();
        lp.y = (int) parent.getY();
        window.setAttributes(lp);
    }

    public static void initWindow(int gravity, int width, int height){
        initWindow(false, gravity, width, height);
    }
    public static void initWindow(){
        initWindow(true, 0, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }

    public static void initWindow(boolean isFull, int gravity, int width, int height){
        Window window = loadingDialog.getWindow();
        if (!isFull){
            window.setGravity(gravity);
        }
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = width;
        lp.height = height;
        window.setAttributes(lp);
    }

    public static void dismiss() {
        if (loadingDialog != null && loadingDialog.isShowing() && null != loadingDialog.getWindow()) {
            WindowManager.LayoutParams lp = loadingDialog.getWindow().getAttributes();
            Log.d(TAG, "dismiss: width = " + lp.width + ", height = " + lp.height + ", x = " + lp.x + ", y = " + lp.y);
            loadingDialog.dismiss();
        }
        loadingDialog = null;
    }

    private static class LoadingDialog extends Dialog {
        private LottieAnimationView loading;
        private TextView hint;
//        private AnimationDrawable loadingDrawable;

        public LoadingDialog(Context context, int themeResId) {
            super(context, themeResId);
        }

        public LoadingDialog(Context context) {
            super(context);
        }

        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.setContentView(R.layout.dialog_loading);
            this.loading = (LottieAnimationView) this.findViewById(R.id.iv_loading);
//            this.loadingDrawable = (AnimationDrawable) this.loading.getDrawable();
            this.hint = (TextView) this.findViewById(R.id.iv_hint);
        }

        public void dismiss() {
            try {
                super.dismiss();
            } catch (Exception var2) {
                var2.printStackTrace();
            }
//            this.loadingDrawable.stop();
        }

        public void show() {
            super.show();
//            this.loadingDrawable.start();
        }
    }
}
