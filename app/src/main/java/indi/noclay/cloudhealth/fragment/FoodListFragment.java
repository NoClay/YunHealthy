package indi.noclay.cloudhealth.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import indi.noclay.cloudhealth.R;

/**
 * Created by clay on 2018/4/17.
 */

public class FoodListFragment extends Fragment {

    View mView;
    String mTitle;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleArguments();
    }

    private void handleArguments() {
        if (getArguments() != null){
            mTitle = getArguments().getString("title");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_food_list, container, false);
        ((TextView) mView.findViewById(R.id.test)).setText(mTitle);
        return mView;
    }

}
