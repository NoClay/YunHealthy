# YunHealthy
云健康是一个配合硬件设备使用的软件，即项目分为多端软件和智能硬件，安卓端可以利用App将智能硬件收集到的数据加以处理，并及时保存数据到云端，还包括一些云端就医咨询的功能，用户可以利用客户端方便的添加自己的用药数据，利用手机闹钟提醒自己按时用药，为老年人专门添加了药物照片描述，一张照片即可添加用药。

# 贡献者

NoClay,flyings-sky
# 说明
在本地过去登录状态的时候，请使用以下代码
```java

public class SharedPreferenceHelper {
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
}
```
使用的SQLite：LocalStore.db

# 2017/5/11 修改布局说明：

修改了好友页面的布局，实现类似好友列表的布局思路如下：
1. 使用RecyclerView嵌套RecyclerView，在父RecyclerView的onBindView中计算出子RecyclerView的高度，这个可以采用如下设定：
  ```java
  	   childRecyclerView.getLayoutManager().setAutoMeasuredEnable(boolean flag)
  ```

2. 利用ExpandableListView控件


# 2017/5/20 修复bug说明：

修复了修改个人信息的bug

bug原因：未获取读取文件的权限

bug修复：使用Android动态权限申请

# 2017/8/4 修改桌面小部件布局

![桌面小部件.jpg](http://storage1.imgchr.com/Acbef.png)

