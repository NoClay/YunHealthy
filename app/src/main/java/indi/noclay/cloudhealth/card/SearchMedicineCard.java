package indi.noclay.cloudhealth.card;

import android.app.Activity;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import indi.noclay.cloudhealth.R;
import indi.noclay.cloudhealth.carddata.MedicineRetData;
import indi.noclay.cloudhealth.util.ViewUtils;

/**
 * Created by NoClay on 2018/5/2.
 */

public class SearchMedicineCard extends BaseCard {
    private TextView name;
    private ImageView img;
    private TextView price;
    private TextView pzwh;
    private TextView zxbz;
    private TextView manu;
    private MedicineRetData.Drug mDrug;

    public TextView getName() {
        return name;
    }

    public void setName(TextView name) {
        this.name = name;
    }

    public ImageView getImg() {
        return img;
    }

    public void setImg(ImageView img) {
        this.img = img;
    }

    public TextView getPrice() {
        return price;
    }

    public void setPrice(TextView price) {
        this.price = price;
    }

    public TextView getPzwh() {
        return pzwh;
    }

    public void setPzwh(TextView pzwh) {
        this.pzwh = pzwh;
    }

    public TextView getZxbz() {
        return zxbz;
    }

    public void setZxbz(TextView zxbz) {
        this.zxbz = zxbz;
    }

    public TextView getManu() {
        return manu;
    }

    public void setManu(TextView manu) {
        this.manu = manu;
    }

    public MedicineRetData.Drug getDrug() {
        return mDrug;
    }

    public void setDrug(MedicineRetData.Drug drug) {
        mDrug = drug;
    }

    public SearchMedicineCard(View itemView, Handler mHandler, Activity mActivity, Fragment mFragment) {
        super(itemView, mHandler, mActivity, mFragment);
    }

    @Override
    public void initData(Object object) {
        if (object instanceof MedicineRetData.Drug){
            mDrug = (MedicineRetData.Drug) object;
            name.setText(mDrug.getDrugName());
            if(TextUtils.isEmpty(mDrug.getImg())){
                ViewUtils.hideView(img);
            }else{
                ViewUtils.showView(img);
                Glide.with(getContext()).load(mDrug.getImg()).error(R.drawable.icon_load_failed).crossFade().into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        img.setImageDrawable(resource);
                    }
                });
            }
            price.setText(mDrug.getPrice());
            pzwh.setText(mDrug.getPzwh());
            zxbz.setText(mDrug.getZxbz());
            manu.setText(mDrug.getManu());
        }
    }

    @Override
    public void initView(View itemView) {
        name = (TextView) itemView.findViewById(R.id.name);
        img = (ImageView) itemView.findViewById(R.id.img);
        price = (TextView) itemView.findViewById(R.id.price);
        pzwh = (TextView) itemView.findViewById(R.id.pzwh);
        zxbz = (TextView) itemView.findViewById(R.id.zxbz);
        manu = (TextView) itemView.findViewById(R.id.manu);
    }
}
