package indi.noclay.cloudhealth.myview.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by clay on 2018/4/18.
 */

public abstract class ShowAtDialog extends Dialog{
    public ShowAtDialog(@NonNull Context context) {
        super(context);
    }

    public ShowAtDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected ShowAtDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public void showAtLocation(View parent, int gravity, int width, int height){
        initWindow(false, gravity, width, height);
        this.show();
    }

    public void showFullParent(View parent){
        initWindow(parent);
        this.show();
    }

    public void showFullScreen(){
        initWindow(true, 0, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        this.show();
    }

    private void initWindow(boolean isFull, int gravity, int width, int height){
        Window window = this.getWindow();
        if (!isFull){
            window.setGravity(gravity);
        }
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = width;
        lp.height = height;
        window.setAttributes(lp);
    }

    public void initWindow(View parent){
        Window window = this.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = parent.getLayoutParams().width;
        lp.height = parent.getLayoutParams().height;
        lp.x = (int) parent.getX();
        lp.y = (int) parent.getY();
        window.setAttributes(lp);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);
    }
}
