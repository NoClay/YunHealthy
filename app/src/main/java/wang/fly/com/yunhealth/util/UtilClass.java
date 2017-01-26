package wang.fly.com.yunhealth.util;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by 82661 on 2016/11/6.
 */

public class UtilClass {

    /**
     * 比较两个double类型变量的大小，相等返回0，大于返回1，小于返回-1
     * @param d1
     * @param d2
     * @return
     */
    public static int compareDouble(double d1, double d2){
        if (Math.abs(d1 - d2) < 1e-6){
            return 0;
        }else if ((d1 - d2) > 0){
            return 1;
        }else {
            return -1;
        }
    }
    public static int compareDouble(double d1, double d2, double d3){
        if (Math.abs(d1 - d2) < d3){
            return 0;
        }else if ((d1 - d2) > 0){
            return 1;
        }else {
            return -1;
        }
    }
    /**
     * 检查网络状态
     *
     * @param context
     */
    public static boolean checkNetwork(Context context) {
        ConnectivityManager conMan = (ConnectivityManager) context.
                getSystemService(Context.CONNECTIVITY_SERVICE);

//mobile 3G Data Network
        NetworkInfo.State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
//wifi
        NetworkInfo.State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();

//如果3G网络和wifi网络都未连接，且不是处于正在连接状态 则进入Network Setting界面 由用户配置网络连接
        if (mobile == NetworkInfo.State.CONNECTED || mobile == NetworkInfo.State.CONNECTING)
            return true;
        if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING)
            return true;
//        context.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
        //进入无线网络配置界面
//startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)); //进入手机中的wifi网络设置界面
        return false;
    }

    /**
     * 将Calendar转转为时间字符串
     */
    public static String valueOfCalendar(Calendar calendar) {
        if (calendar == null) {
            return null;
        }
        return calendar.get(Calendar.YEAR) + "-"
                + (calendar.get(Calendar.MONTH) + 1) + "-"
                + calendar.get(Calendar.DAY_OF_MONTH) + " "
                + calendar.get(Calendar.HOUR_OF_DAY) + ":"
                + calendar.get(Calendar.MINUTE) + ":"
                + calendar.get(Calendar.SECOND) + ":"
                + calendar.get(Calendar.MILLISECOND);
    }

    /**
     * 获取资源转换为bitmap，不存在设置为默认图片
     *
     * @param context
     * @param id
     * @return
     */
    public static Bitmap resToBitmap(Context context, int id) {
        return BitmapFactory.decodeResource(context.getResources(), id);
    }

    /**
     * 将整型转换为布尔型
     *
     * @param integer
     * @return
     */
    public static boolean booleanValueOfInteger(Integer integer) {
        if (integer == null) {
            return false;
        }
        if (integer == 0) {
            return false;
        }
        return true;
    }

    /**
     * 将Date转换为字符串
     */
    public static String valueOfDate(Date date, String timeFormat) {
        if (date == null) {
            return null;
        }
        if (timeFormat == null) {
            timeFormat = "yyyy-MM-dd HH:mm:00";
        }
        SimpleDateFormat format = new SimpleDateFormat(timeFormat);
        return format.format(date);
    }

    /**
     * 解析形如 20XX-XX-XX XX:XX:XX
     * 标志位 -, - ,  , :, :
     *
     * @param bmobDate
     * @return
     */
    public static Date resolveBmobDate(String bmobDate, String timeFormat) {
        if (bmobDate == null) {
            return null;
        }
        if (timeFormat == null) {
            timeFormat = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat format = new SimpleDateFormat(timeFormat);
        try {
            return format.parse(bmobDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 判断网络链接状态
     *
     * @param context
     * @return
     */
    public static boolean isOpenNetWork(Context context) {
        ConnectivityManager connect = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connect.getActiveNetworkInfo() != null) {
            return connect.getActiveNetworkInfo().isAvailable();
        }
        return false;
    }

    /**
     * dp到px转换
     *
     * @param context
     * @param dp
     * @return
     */
    public static int Dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    /**
     * px到dp的转换
     *
     * @param context
     * @param px
     * @return
     */
    public static int Px2Dp(Context context, float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    /**
     * 保留两位小数，并返回字符串
     *
     * @param value
     * @return
     */
    public static String getTwoShortValue(float value) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
        return decimalFormat.format(value);//format 返回的是字符串
    }

    /**
     * 字节流转换为十六进制字符串
     *
     * @param src
     * @return
     */
    public static String valueOfBytes(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * 十六进制字符串转换为bytes
     *
     * @param hexString
     * @return
     */
    public static byte[] valueOfString(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase().replace(" ", "");
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static boolean checkHexString(String src) {
        for (char c : src.toCharArray()) {
            if ("0123456789AaBbCcDdEeFf".indexOf(c) == -1) {
                return false;
            }
        }
        return true;
    }


    /**
     * 将一个十六进制字符串转换为十进制整型
     */
    public static int valueOfHexString(String data) {
        int result = Integer.valueOf(data, 16).intValue();
        String value = Integer.toBinaryString(result);
        if (value.length() == data.length() * 4 && value.startsWith("1")) {
            result--;
            value = Integer.toBinaryString(result);
            StringBuilder string = new StringBuilder(value.length());
            for (int i = 0; i < value.length(); i++) {
                if (i == 0) {
                    string.append(1);
                } else if (value.charAt(i) == '1') {
                    string.append(0);
                } else {
                    string.append(1);
                }
            }
            result = -Integer.valueOf(string.toString(), 2);
        }
        return result;
    }

    /**
     * 判断一个字符串可能是手机号码
     */
    public static boolean isMobileNum(String mobiles) {
        String regex = "1[3|5|7|8|][0-9]{9}";
        return mobiles.matches(regex);
    }

    /**
     * 安卓4.4从uri获取图片文件
     *
     * @param context
     * @param uri
     * @return
     */
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }

                    // TODO handle non-primary volumes
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {

                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    return getDataColumn(context, contentUri, null, null);
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{
                            split[1]
                    };

                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
            // MediaStore (and general)
            else if ("content".equalsIgnoreCase(uri.getScheme())) {

                // Return the remote address
                if (isGooglePhotosUri(uri))
                    return uri.getLastPathSegment();

                return getDataColumn(context, uri, null, null);
            }
            // File
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }


    public static void requestPermission(final Activity activity,
                                         String permission) {
        if (activity == null) {
            return;
        }
        int check = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            check = activity.checkSelfPermission(permission);
        } else {
            check = activity.checkCallingOrSelfPermission(permission);
        }
        if (check != PackageManager.PERMISSION_GRANTED) {
            //没有获取该权限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.requestPermissions(new String[]{
                        permission
                }, 0);
            }
        }
    }

    /**
     * Toast
     */
    public static void toToast(Context context, String content) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }

    public static boolean isAllNumber(String data) {
        for (int i = 0; i < data.length(); i++) {
            char c = data.charAt(i);
            if (c < '0' && c > '9') {
                return false;
            }
        }
        return true;
    }
}
