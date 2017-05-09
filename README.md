# YunHealthy
一个物联网应用，利用传感器随时随地进行身体的体检，采集用户身体状况，用于进行进一步拓展功能的实现。
# 使用的持久化
 /**
     *用于修改本地的是用户登陆信息
     * @param context
     * @param user
     * @param path
     * @param isRemember
     */
    public static void editLoginState(Context context,
                                      SignUserData user,
                                      String path,
                                      Boolean isRemember) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginState", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (user.getObjectId() != null){
            editor.putString("userId", user.getObjectId());
        }
        if (user.getUserName() != null){
            editor.putString("userName", user.getUserName());
        }
        if (user.getPhoneNumber() != null){
            editor.putString("phoneNumber", user.getPhoneNumber());
        }
        if (user.getMan() != null){
            editor.putBoolean("isMan", user.getMan());
        }
        if (user.getHeight() != null){
            editor.putInt("height", user.getHeight());
        }
        if (user.getWeight() != null){
            editor.putFloat("weight", user.getWeight());
        }
        if (user.getIdNumber() != null){
            editor.putString("idNumber", user.getIdNumber());
        }
        if (path != null){
            editor.putString("userImage", user.getUserImage().getUrl());
        }else{
            editor.putString("userImage", "");
        }
        if (isRemember != null){
            editor.putBoolean("loginRememberState", isRemember);
        }
        editor.commit();
    }