package indi.noclay.cloudhealth.util;

import android.view.View;
import android.widget.TextView;

/**
 * Created by clay on 2018/4/16.
 */

public class ViewUtils {

    /**
     * 延时显示View
     * @param target
     * @param delays
     */
    public static void showViewDelayed(final View target, int delays) {
        target.postDelayed(new Runnable() {
            @Override
            public void run() {
                target.setVisibility(View.VISIBLE);
            }
        }, delays);
    }

    /**
     * 计算TextView的宽度
     * @param text
     * @param textView
     * @return
     */
    public static float getTextWidth(String text, TextView textView){
        return textView.getPaint().measureText(text);
    }

    public static void setViewVisibility(int visibility, View... views){
        try {
            if (views == null || views.length == 0){
                return;
            }

            for (View view : views){
                if (view != null){
                    view.setVisibility(visibility);
                }
            }
        }catch (Throwable t){
            t.printStackTrace();
        }
    }
    /**
     * 显示View
     * @param views
     */
    public static void showView(View... views){
        try {
            if (views == null || views.length == 0){
                return;
            }

            for (View view : views){
                if (view != null){
                    view.setVisibility(View.VISIBLE);
                }
            }
        }catch (Throwable t){
            t.printStackTrace();
        }
    }

    /**
     * 隐藏view
     * @param views
     */
    public static void hideView(View... views){
        try {
            if (views == null || views.length == 0){
                return;
            }

            for (View view : views){
                if (view != null){
                    view.setVisibility(View.GONE);
                }
            }
        }catch (Throwable t){
            t.printStackTrace();
        }
    }
}
