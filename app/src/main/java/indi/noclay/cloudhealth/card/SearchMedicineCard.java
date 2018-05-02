package indi.noclay.cloudhealth.card;

import android.app.Activity;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import indi.noclay.cloudhealth.R;
import indi.noclay.cloudhealth.carddata.MedicineRetData;

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

    public SearchMedicineCard(View itemView, Handler mHandler, Activity mActivity, Fragment mFragment) {
        super(itemView, mHandler, mActivity, mFragment);
    }

    @Override
    public void initData(Object object) {
        if (object instanceof MedicineRetData.Drug){
            mDrug = (MedicineRetData.Drug) object;
            name.setText(mDrug.getDrugName());
            Glide.with(getContext()).load(mDrug.getImg()).error(R.drawable.icon_load_failed).crossFade().into(new SimpleTarget<GlideDrawable>() {
                @Override
                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                    img.setImageDrawable(resource);
                }
            });
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
