package indi.noclay.cloudhealth.myview.dialog;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import indi.noclay.cloudhealth.R;


/**
 * Created by 82661 on 2016/11/30.
 */

public class ChooseImageDialog extends PopupWindow{
    private View chooseWay;
    private TextView takePhoto, pickPhoto;
    private TextView cancleButton;

    public ChooseImageDialog(Context context, View.OnClickListener itemOnClick) {
        super(context);
        LayoutInflater layoutInflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        chooseWay = layoutInflater.inflate(R.layout.dialog_choose_image, null);
        this.setContentView(chooseWay);

        takePhoto = (TextView) chooseWay.findViewById(R.id.takePhotoBtn);
        pickPhoto = (TextView) chooseWay.findViewById(R.id.pickPhotoBtn);
        cancleButton = (TextView) chooseWay.findViewById(R.id.cancelBtn);
        takePhoto.setOnClickListener(itemOnClick);
        pickPhoto.setOnClickListener(itemOnClick);
        cancleButton.setOnClickListener(itemOnClick);

        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setFocusable(true);
//        this.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.dialog_window_background));
//        this.getBackground().setAlpha(200);
        ColorDrawable dw = new ColorDrawable(0x88000000);
        this.setBackgroundDrawable(dw);
        this.setAnimationStyle(R.style.PopupAnimation);

    }
}
