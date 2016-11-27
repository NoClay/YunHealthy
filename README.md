# YunHealthy
一个物联网应用，利用传感器随时随地进行身体的体检，采集用户身体状况，用于进行进一步拓展功能的实现。
# 使用的持久化

        SharedPreferences sharedPreferences = getSharedPreferences("LoginState", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userId", user.getObjectId());
        editor.putString("userName", user.getUserName());
        editor.putString("phoneNumber", user.getPhoneNumber());
        editor.putBoolean("isMan", user.getMan());
        editor.putInt("height", user.getHeight());
        editor.putInt("weight", user.getWeight());
        if (user.getUserImage() != null){
            editor.putString("userImage", user.getUserImage().getUrl());
        }else{
            editor.putString("userImage", "");
        }
        if (rememberLoginStateButton.isChecked()) {
            editor.putBoolean("loginRememberState", true);
        } else {
            editor.putBoolean("loginRememberState", false);
        }
        editor.commit();
