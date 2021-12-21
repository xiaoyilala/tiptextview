package com.ice.tiptext.permisson;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


import com.ice.tiptext.R;
import com.ice.tiptext.util.ListUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 系统权限工具类
 */
public class PermissionUtils {
    private Activity mActivity;
    private List<String> mPermissions = new ArrayList<>();

    private PermissionUtils(Activity activity) {
        mActivity = activity;
    }

    /**
     * 设置请求的对象
     */
    public static PermissionUtils with(Activity activity) {
        return new PermissionUtils(activity);
    }

    /**
     * 设置权限组
     */
    public PermissionUtils permission(String... permissions) {
        mPermissions.addAll(Arrays.asList(permissions));
        return this;
    }

    /**
     * 设置权限组
     */
    public PermissionUtils permission(String[]... permissions) {
        for (String[] group : permissions) {
            mPermissions.addAll(Arrays.asList(group));
        }
        return this;
    }

    /**
     * 设置权限组
     */
    public PermissionUtils permission(List<String> permissions) {
        mPermissions.addAll(permissions);
        return this;
    }

    /**
     * 请求权限
     */
    public void request(IOnPermissionCallback call) {
        //如果没有指定请求的权限，就使用清单注册的权限进行请求
        if (mPermissions == null || mPermissions.size() == 0) mPermissions = getManifestPermissions(mActivity);
        if (mPermissions == null || mPermissions.size() == 0) throw new IllegalArgumentException("The requested permission cannot be empty");
        //使用isFinishing方法Activity在熄屏状态下会导致崩溃
        //if (mActivity == null || mActivity.isFinishing()) throw new IllegalArgumentException("Illegal Activity was passed in");
        if (mActivity == null) throw new IllegalArgumentException("The activity is empty");
        if (call == null) throw new IllegalArgumentException("The permission request callback interface must be implemented");

        checkTargetSdkVersion(mActivity, mPermissions);

        ArrayList<String> failPermissions = getFailPermissions(mActivity, mPermissions);

        if (failPermissions == null || failPermissions.size() == 0) {
            //证明权限已经全部授予过
            call.hasPermission(mPermissions, true);
        } else {
            //申请没有授予过的权限
            PermissionFragment.newInstant(failPermissions).prepareRequest(mActivity, call);
        }
    }

    /**
     * 跳转到应用权限设置页面
     *
     * @param context 上下文对象
     */
    public static void gotoPermissionSettings(Context context) {
        PermissionSettingPage.start(context, false);
    }

    /**
     * 跳转到应用权限设置页面
     *
     * @param context 上下文对象
     * @param newTask 是否使用新的任务栈启动
     */
    public static void gotoPermissionSettings(Context context, boolean newTask) {
        PermissionSettingPage.start(context, newTask);
    }

    /**
     * 跳转到应用详情页面
     *
     * @param context 上下文对象
     */
    public static void gotoDetailSettings(Context context) {
        PermissionSettingPage.startGoogle(context);
    }

    /**
     * 返回应用程序在清单文件中注册的权限
     */
    public static List<String> getManifestPermissions(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            return Arrays.asList(pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_PERMISSIONS).requestedPermissions);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 是否是6.0以上版本
     */
    static boolean isOverMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * 是否是8.0以上版本
     */
    static boolean isOverOreo() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }

    /**
     * 是否有安装权限
     */
    static boolean isHasInstallPermission(Context context) {
        if (isOverOreo()) {
            return context.getPackageManager().canRequestPackageInstalls();
        }
        return true;
    }

    /**
     * 是否有悬浮窗权限
     */
    public static boolean isHasOverlaysPermission(Context context) {
        if (isOverMarshmallow()) {
            return Settings.canDrawOverlays(context);
        }
        return true;
    }

    /**
     * 获取没有授予的权限
     *
     * @param context               上下文对象
     * @param permissions           需要请求的权限组
     */
    static ArrayList<String> getFailPermissions(Context context, List<String> permissions) {

        //如果是安卓6.0以下版本就返回null
        if (!PermissionUtils.isOverMarshmallow()) {
            return null;
        }

        List<String> manifestPermissionList = getManifestPermissions(context);

        ArrayList<String> failPermissions = null;

        for (int i = permissions.size() - 1; i >= 0; i--) {
            String permission = permissions.get(i);
            if (!ListUtils.isEmpty(manifestPermissionList) && !manifestPermissionList.contains(permission)) {
                permissions.remove(i);
                continue;
            }
            //检测安装权限
            if (permission.equals(Permission.REQUEST_INSTALL_PACKAGES)) {

                if (!isHasInstallPermission(context)) {
                    if (failPermissions == null) failPermissions = new ArrayList<>();
                    failPermissions.add(permission);
                }
                continue;
            }

            //检测悬浮窗权限
            if (permission.equals(Permission.SYSTEM_ALERT_WINDOW)) {

                if (!isHasOverlaysPermission(context)) {
                    if (failPermissions == null) failPermissions = new ArrayList<>();
                    failPermissions.add(permission);
                }
                continue;
            }

            //检测8.0的两个新权限
            if (permission.equals(Permission.ANSWER_PHONE_CALLS) || permission.equals(Permission.READ_PHONE_NUMBERS)) {

                //检查当前的安卓版本是否符合要求
                if (!isOverOreo()) {
                    continue;
                }
            }

            //把没有授予过的权限加入到集合中
            if (context.checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED) {
                if (failPermissions == null) failPermissions = new ArrayList<>();
                failPermissions.add(permission);
            }
        }

        return failPermissions;
    }

    /**
     * 是否还能继续申请没有授予的权限
     *
     * @param activity              Activity对象
     * @param failPermissions       失败的权限
     */
    static boolean isRequestDeniedPermission(Activity activity, List<String> failPermissions) {
        for (String permission : failPermissions) {
            //检查是否还有权限还能继续申请的（这里指没有被授予的权限但是也没有被永久拒绝的）
            if (!checkSinglePermissionPermanentDenied(activity, permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 在权限组中检查是否有某个权限是否被永久拒绝
     *
     * @param activity              Activity对象
     * @param permissions            请求的权限
     */
    static boolean checkMorePermissionPermanentDenied(Activity activity, List<String> permissions) {

        for (String permission : permissions) {

            if (checkSinglePermissionPermanentDenied(activity, permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查某个权限是否被永久拒绝
     *
     * @param activity              Activity对象
     * @param permission            请求的权限
     */
    static boolean checkSinglePermissionPermanentDenied(Activity activity, String permission) {

        //安装权限和浮窗权限不算，本身申请方式和危险权限申请方式不同，因为没有永久拒绝的选项，所以这里返回false
        if (permission.equals(Permission.REQUEST_INSTALL_PACKAGES) || permission.equals(Permission.SYSTEM_ALERT_WINDOW)) {
            return false;
        }

        //检测8.0的两个新权限
        if (permission.equals(Permission.ANSWER_PHONE_CALLS) || permission.equals(Permission.READ_PHONE_NUMBERS)) {

            //检查当前的安卓版本是否符合要求
            if (!isOverOreo()) {
                return false;
            }
        }

        if (PermissionUtils.isOverMarshmallow()) {
            if (activity.checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED  &&
                    !activity.shouldShowRequestPermissionRationale(permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取没有授予的权限
     *
     * @param permissions           需要请求的权限组
     * @param grantResults          允许结果组
     */
    static List<String> getFailPermissions(String[] permissions, int[] grantResults) {
        List<String> failPermissions = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {

            //把没有授予过的权限加入到集合中，-1表示没有授予，0表示已经授予
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                failPermissions.add(permissions[i]);
            }
        }
        return failPermissions;
    }

    /**
     * 获取已授予的权限
     *
     * @param permissions       需要请求的权限组
     * @param grantResults      允许结果组
     */
    static List<String> getSucceedPermissions(String[] permissions, int[] grantResults) {

        List<String> succeedPermissions = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {

            //把授予过的权限加入到集合中，-1表示没有授予，0表示已经授予
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                succeedPermissions.add(permissions[i]);
            }
        }
        return succeedPermissions;
    }

    /**
     * 检测权限有没有在清单文件中注册
     *
     * @param activity              Activity对象
     * @param requestPermissions    请求的权限组
     */
    static void checkPermissions(Activity activity, List<String> requestPermissions) {
        List<String> manifest = PermissionUtils.getManifestPermissions(activity);
        if (manifest != null && manifest.size() != 0) {
            for (String permission : requestPermissions) {
                if (!manifest.contains(permission)) {
                    throw new ManifestRegisterException(permission);
                }
            }
        } else {
            throw new ManifestRegisterException(null);
        }
    }

    /**
     * 检查targetSdkVersion是否符合要求
     *
     * @param context                   上下文对象
     * @param requestPermissions       请求的权限组
     */
    static void checkTargetSdkVersion(Context context, List<String> requestPermissions) {
        //检查是否包含了8.0的权限
        if (requestPermissions.contains(Permission.REQUEST_INSTALL_PACKAGES)
                || requestPermissions.contains(Permission.ANSWER_PHONE_CALLS)
                || requestPermissions.contains(Permission.READ_PHONE_NUMBERS)) {
            //必须设置 targetSdkVersion >= 26 才能正常检测权限
            if (context.getApplicationInfo().targetSdkVersion < Build.VERSION_CODES.O) {
                throw new RuntimeException("The targetSdkVersion SDK must be 26 or more");
            }
        }else {
            //必须设置 targetSdkVersion >= 23 才能正常检测权限
            if (context.getApplicationInfo().targetSdkVersion < Build.VERSION_CODES.M) {
                throw new RuntimeException("The targetSdkVersion SDK must be 23 or more");
            }
        }
    }

    /**
     * 检查某些权限是否全部授予了
     *
     * @param context     上下文对象
     * @param permissions 需要请求的权限组
     */
    public static boolean isHasPermission(Context context, String... permissions) {
        ArrayList<String> failPermissions = PermissionUtils.getFailPermissions(context, new ArrayList<>(Arrays.asList(permissions)));
        return failPermissions == null || failPermissions.size() == 0;
    }

    /**
     * 检查某些权限是否全部授予了
     *
     * @param context     上下文对象
     * @param permissions 需要请求的权限组
     */
    public static boolean isHasPermission(Context context, String[]... permissions) {
        List<String> permissionList = new ArrayList<>();
        for (String[] group : permissions) {
            permissionList.addAll(Arrays.asList(group));
        }
        ArrayList<String> failPermissions = PermissionUtils.getFailPermissions(context, permissionList);
        return failPermissions == null || failPermissions.size() == 0;
    }
}
