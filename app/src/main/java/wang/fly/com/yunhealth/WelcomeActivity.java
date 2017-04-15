package wang.fly.com.yunhealth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import wang.fly.com.yunhealth.Activity.LoginActivityCopy;

public class WelcomeActivity extends AppCompatActivity implements Animation.AnimationListener{

    private ImageView mImage;
    /**
     * 云健康，守护您的健康
     */
    private TextView mText;
    private RelativeLayout mActivityWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        initView();
        startAnimation();
    }

    private void initView() {
        mImage = (ImageView) findViewById(R.id.image);
        mText = (TextView) findViewById(R.id.text);
        mActivityWelcome = (RelativeLayout) findViewById(R.id.activity_welcome);
    }

    private void startAnimation(){
        Animation animation = new AlphaAnimation(0.1f, 1.0f);
        animation.setDuration(500);
        mImage.setAnimation(animation);
        animation.setAnimationListener(this);
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        Intent intent = new Intent(WelcomeActivity.this, LoginActivityCopy.class);
        startActivity(intent);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

}
