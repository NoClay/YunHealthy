package wang.fly.com.yunhealth.MyViewPackage;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;

import wang.fly.com.yunhealth.Adapter.ResultListViewAdapter;
import wang.fly.com.yunhealth.R;
import wang.fly.com.yunhealth.util.UtilClass;

/**
 * Created by 82661 on 2016/11/6.
 */

public class ResultDialog extends PopupWindow{

    private View mResultView;
    private ImageView closeButton;
    private View.OnClickListener listener;
    private ListView resultView;
    private Button lookAdvice;

    public ResultDialog(Context context, View.OnClickListener listener, ResultListViewAdapter adapter) {
        super(context);
        this.listener = listener;
        LayoutInflater layoutInflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mResultView = layoutInflater.inflate(R.layout.dialog_result, null);
        this.setContentView(mResultView);
        initView();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int screenHeight = windowManager.getDefaultDisplay().getHeight();
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(screenHeight / 2 + UtilClass.Dp2Px(context, 15));
        ColorDrawable dw = new ColorDrawable(Color.WHITE);
        this.setBackgroundDrawable(dw);
        this.setAnimationStyle(R.style.PopupAnimation);
        this.resultView.setAdapter(adapter);
    }

    @Override
    public void setHeight(int height) {
        super.setHeight(height);
    }

    private void initView() {
        closeButton = (ImageView) mResultView.findViewById(R.id.close_result_button);
        closeButton.setOnClickListener(listener);
        resultView = (ListView) mResultView.findViewById(R.id.showResultListView);
        lookAdvice = (Button) mResultView.findViewById(R.id.lookAdvice);
        lookAdvice.setOnClickListener(listener);
    }
}
