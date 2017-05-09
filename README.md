# YunHealthy
一个物联网应用，利用传感器随时随地进行身体的体检，采集用户身体状况，用于进行进一步拓展功能的实现。
# 贡献者
NoClay,flyings-sky
# 说明
在本地过去登录状态的时候，请使用以下代码
```java
/**
* 没有登录返回null
* 登录则返回SignUserData
**/
public static SignUserData getLoginUser() 
/**
* 修改登录状态、信息
* isLogin = true 则登录
**/
public static void editLoginState(SignUserData user, Boolean isLogin) 
/**
* 直接退出登录
**/
public static void exitLogin()
```
