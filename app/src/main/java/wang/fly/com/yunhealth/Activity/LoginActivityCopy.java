package wang.fly.com.yunhealth.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;
import wang.fly.com.yunhealth.MVP.Bases.MVPBaseActivity;
import wang.fly.com.yunhealth.MVP.Presenters.LoginActivityPresenter;
import wang.fly.com.yunhealth.MVP.Views.LoginActivityInterface;
import wang.fly.com.yunhealth.MainActivityCopy;
import wang.fly.com.yunhealth.R;

public class LoginActivityCopy extends
        MVPBaseActivity<LoginActivityInterface, LoginActivityPresenter>
        implements View.OnClickListener, LoginActivityInterface,
        View.OnFocusChangeListener {

    /**
     * 手机
     */
    private EditText mUserName;
    /**
     * 密码
     */
    private EditText mUserPassWord;
    /**
     * 忘记密码?
     */
    private TextView mForgetPassWord;
    /**
     * 登  录
     */
    private Button mLogin;
    /**
     * 立即注册
     */
    private TextView mSignUser;
    public Context context = this;
    private CircleImageView mUserImageShow;
    private ImageView mClearName;
    private ImageView mClearPassword;
    public static final int SIGN = 0;
    public static final int CHANGE = 1;
    private long mExitTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_copy);
        initView();
        mPresenter.loginIfRemember();
    }

    @Override
    protected LoginActivityPresenter createPresenter() {
        LoginActivityPresenter temp = new LoginActivityPresenter(context);
        return temp;
    }

    private void initView() {
        mUserName = (EditText) findViewById(R.id.userName);
        mUserPassWord = (EditText) findViewById(R.id.userPassWord);
        mForgetPassWord = (TextView) findViewById(R.id.forgetPassWord);
        mForgetPassWord.setOnClickListener(this);
        mLogin = (Button) findViewById(R.id.login);
        mLogin.setOnClickListener(this);
        mSignUser = (TextView) findViewById(R.id.signUser);
        mSignUser.setOnClickListener(this);
        mUserImageShow = (CircleImageView) findViewById(R.id.userImageShow);
        mClearName = (ImageView) findViewById(R.id.clearName);
        mClearName.setOnClickListener(this);
        mClearPassword = (ImageView) findViewById(R.id.clearPassword);
        mClearPassword.setOnClickListener(this);
        mClearName.setVisibility(View.GONE);
        mClearPassword.setVisibility(View.GONE);
        mUserName.setOnFocusChangeListener(this);
        mUserPassWord.setOnFocusChangeListener(this);
        mUserPassWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                LoginActivityCopy.this.afterTextChanged(s.toString(), false);
            }
        });
        mUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                LoginActivityCopy.this.afterTextChanged(s.toString(), true);
            }
        });
    }

    public void afterTextChanged(String s, boolean isName){
        if (isName){
            if (s.length() == 0){
                mClearName.setVisibility(View.GONE);
            }else{
                mClearName.setVisibility(View.VISIBLE);
                mPresenter.loadImage(s.trim());
            }
        }else{
            if (s.length() == 0){
                mClearPassword.setVisibility(View.GONE);
            }else{
                mClearPassword.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.forgetPassWord:
                forgetPassWord();
                break;
            case R.id.login:
                mPresenter.login(mUserName.getText().toString(),
                        mUserPassWord.getText().toString());
                break;
            case R.id.signUser:
                signUser();
                break;
            case R.id.clearName:
                mUserName.setText("");
                break;
            case R.id.clearPassword:
                mUserPassWord.setText("");
                break;
        }
    }

    @Override
    public void loadImage(String path) {
        Glide.with(context)
                .load(path)
                .crossFade(200)
                .placeholder(R.drawable.timg)
                .error(R.drawable.timg)
                .fitCenter()
                .into(mUserImageShow);
    }

    @Override
    public void forgetPassWord() {
        Intent intent = new Intent(context, ChangePassWord.class);
        startActivityForResult(intent, CHANGE);
    }

    @Override
    public void signUser() {
        Intent intent = new Intent(context, SignActivity.class);
        startActivityForResult(intent, SIGN);
    }

    @Override
    public void loginSuccess() {
        Intent intent = new Intent(context, MainActivityCopy.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void loginFailed(String toast) {
        Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void badNet() {
        Toast.makeText(context, "网络异常，请查看网络", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        boolean isEmpty = ((EditText) v).getText().length() == 0 ? true : false;
        switch (v.getId()){
            case R.id.userName:{
                if (hasFocus && !isEmpty){
                    mClearName.setVisibility(View.VISIBLE);
                }else{
                    mClearName.setVisibility(View.GONE);
                }
                break;
            }
            case R.id.userPassWord:{
                if (hasFocus && !isEmpty){
                    mClearPassword.setVisibility(View.VISIBLE);
                }else{
                    mClearPassword.setVisibility(View.GONE);
                }
                break;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(context, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                setResult(RESULT_CANCELED);
                finish();
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case SIGN:{
                if (resultCode == RESULT_OK){
                    mUserName.setText(data.getStringExtra("phoneNumber"));
                }
                break;
            }
            case CHANGE:{
                if (resultCode == RESULT_OK){
                    mUserName.setText(data.getStringExtra("phoneNumber"));
                }
                break;
            }
        }
    }
}
