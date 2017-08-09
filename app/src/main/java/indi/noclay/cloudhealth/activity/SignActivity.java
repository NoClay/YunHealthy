package indi.noclay.cloudhealth.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.smssdk.EventHandler;
import cn.smssdk.OnSendMessageHandler;
import cn.smssdk.SMSSDK;
import indi.noclay.cloudhealth.R;
import indi.noclay.cloudhealth.database.SignUserData;

public class SignActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "SignFirstStep";
    private ImageView cancelButton;
    private EditText signUserName;
    private RadioButton manChecked, womanChecked;
    private EditText signUserPassWord, signUserPassWordAgain;
    private EditText signUserPhoneNumber;
    private Button sendMessage;
    private EditText checkNumberForMessage;
//    private TextView accessText;
    private Button completeSignButton;
    private Context context = this;
    private int cur = 1;
    private int i = 30;
    private static final int MSG_WHAT_FOR_THREAD = 0;
    private static final int MSG_WHAT_FOR_THREAD_DEATH = 2;
    private static final int MSG_WHAT_FOT_SHORT_MESSAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        findView();
        EventHandler eh = new EventHandler() {
            @Override
            public void afterEvent(int i, int i1, Object o) {
                Message msg = new Message();
                msg.arg1 = i;
                msg.arg2 = i1;
                msg.what = MSG_WHAT_FOT_SHORT_MESSAGE;
                handler.sendMessage(msg);
            }
        };
        SMSSDK.registerEventHandler(eh);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    public static boolean isMobileNum(String mobiles) {
        String regex = "1[3|5|7|8|][0-9]{9}";
        return mobiles.matches(regex);
    }

    private void findView() {
        cancelButton = (ImageView) findViewById(R.id.back);
        signUserName = (EditText) findViewById(R.id.name);
        manChecked = (RadioButton) findViewById(R.id.man_check);
        womanChecked = (RadioButton) findViewById(R.id.woman_check);
        signUserPassWord = (EditText) findViewById(R.id.loadPassWord);
        signUserPassWordAgain = (EditText) findViewById(R.id.loadPassWordAgain);
        signUserPhoneNumber = (EditText) findViewById(R.id.loadPhoneNumber);
        sendMessage = (Button) findViewById(R.id.send_message);
        checkNumberForMessage = (EditText) findViewById(R.id.checkNumber);
//        accessText = (TextView) findViewById(R.id.accessText);
        completeSignButton = (Button) findViewById(R.id.completeSign);
        cancelButton.setOnClickListener(this);
        manChecked.setOnClickListener(this);
        womanChecked.setOnClickListener(this);
        sendMessage.setOnClickListener(this);
//        accessText.setOnClickListener(this);
        completeSignButton.setOnClickListener(this);
        signUserName.requestFocus();
        TextView title = (TextView) findViewById(R.id.info_title);
        title.setText("注册");
        manChecked.setChecked(true);
        signUserPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (isMobileNum(signUserPhoneNumber.getText().toString())) {
                    //发送短信
                    sendMessage.setClickable(true);
                    sendMessage.setBackground(getResources().getDrawable(R.drawable.circle_button_def,
                            getTheme()));
                } else {
                    sendMessage.setClickable(false);
                    sendMessage.setBackground(getResources().getDrawable(R.drawable.circle_button_2,
                            getTheme()));

                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back: {
                setResultBack(false);
                break;
            }
            case R.id.man_check: {
                cur = 1;
                break;//是男的则为1
            }
            case R.id.woman_check: {
                cur = -1;
                break;
            }
            case R.id.send_message:{
                BmobQuery<SignUserData> query = new BmobQuery<>();
                query.addWhereEqualTo("phoneNumber", signUserPhoneNumber.getText().toString());
                query.findObjects(new FindListener<SignUserData>() {
                    @Override
                    public void done(List<SignUserData> list, BmobException e) {
                        if (e != null){
                            Toast.makeText(SignActivity.this, "数据库错误", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (list.isEmpty()){
                            //获取验证码
                            SMSSDK.getVerificationCode("86", signUserPhoneNumber.getText().
                                    toString(), new OnSendMessageHandler() {
                                @Override
                                public boolean onSendMessage(String s, String s1) {
                                    return false;
                                }
                            });
                        }else{
                            Toast.makeText(SignActivity.this, "该手机号已经注册",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            }
            case R.id.completeSign: {
                if (signUserName.getText().toString().length() > 20
                        || signUserName.getText().toString().length() <= 0) {
                    Toast.makeText(context, "姓名过长或过短", Toast.LENGTH_SHORT).show();
                } else if (signUserPassWord.getText().toString().length() > 16 || signUserPassWord.
                        getText().toString().length() < 6) {
                    Toast.makeText(context, "密码过长或过短", Toast.LENGTH_SHORT).show();
                } else if (!signUserPassWord.getText().toString().equals(signUserPassWordAgain.getText().toString())) {
                    Toast.makeText(context, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(checkNumberForMessage.getText().toString())) {
                    Toast.makeText(context, "验证码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    //验证码
                    SMSSDK.submitVerificationCode("86", signUserPhoneNumber.getText().toString(),
                            checkNumberForMessage.getText().toString());
                }
                break;
            }
        }
    }

    private void setResultBack(boolean b) {
        Intent intent = new Intent();
        if (b) {
            intent.putExtra("phoneNumber", signUserPhoneNumber.getText().toString());
            setResult(RESULT_OK, intent);
        } else {
            intent.putExtra("phoneNumber", "");
            setResult(RESULT_CANCELED, intent);
        }
        finish();
    }

    Handler handler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_WHAT_FOR_THREAD: {
                    sendMessage.setClickable(false);
                    sendMessage.setText(msg.arg1 + "秒后可获取验证码");
                    sendMessage.setBackground(getResources().getDrawable(R.drawable.circle_button_2, getTheme()));
                    break;
                }
                case MSG_WHAT_FOR_THREAD_DEATH: {
//                    Log.d(TAG, "handleMessage() called with: " + "线程死亡");
                    sendMessage.setClickable(true);
                    sendMessage.setText("获取验证码");
                    sendMessage.setBackground(getResources().getDrawable(R.drawable.circle_button_def, getTheme()));
                    break;
                }
                case MSG_WHAT_FOT_SHORT_MESSAGE: {
                    int event = msg.arg1;
                    int result = msg.arg2;
                    switch (event) {
                        case SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE: {
                            if (result == SMSSDK.RESULT_COMPLETE) {
                                signUser();//将注册信息上传到服务器端
                                //验证成功
                            } else {
                                Toast.makeText(context, "验证码错误", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        }
                        case SMSSDK.EVENT_GET_VERIFICATION_CODE: {
                            if (result == SMSSDK.RESULT_COMPLETE) {
                                Toast.makeText(context, "验证码发送成功，请等待",
                                        Toast.LENGTH_SHORT).show();
                                i = 30;
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        while (i > 0) {
                                            i--;
                                            try {
                                                Thread.sleep(1000);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            Message message = new Message();
                                            if (i == 0) {
                                                message.what = SignActivity.MSG_WHAT_FOR_THREAD_DEATH;
                                            } else {
                                                message.what = SignActivity.MSG_WHAT_FOR_THREAD;
                                            }
                                            message.arg1 = i;
                                            handler.sendMessage(message);
                                        }
                                    }
                                }).start();
                            } else {
                                Toast.makeText(context, "验证码发送失败", Toast.LENGTH_SHORT).show();
                                //获取验证码失败
                            }
                        }
                        break;
                    }
                }
            }
        }
    };

    private void signUser() {
        //设置注册按钮不可点击
        completeSignButton.setClickable(false);
        SignUserData sign = new SignUserData();
        sign.setPhoneNumber(signUserPhoneNumber.getText().toString());//设置用户名
        sign.setPassWord(signUserPassWord.getText().toString());//设置密码
        sign.setUserName(signUserName.getText().toString());//设置昵称
        if (cur == 1) {//设置性别
            sign.setMan(true);
        } else if (cur == -1) {
            sign.setMan(false);
        }
        sign.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    Toast.makeText(context, "注册成功，即将跳转到登录界面", Toast.LENGTH_SHORT).show();
                    setResultBack(true);
                } else {
                    Toast.makeText(context, "注册失败，请稍后重试", Toast.LENGTH_SHORT).show();
                    completeSignButton.setClickable(true);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResultBack(false);
        super.onBackPressed();
    }
}
