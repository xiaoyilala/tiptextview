package com.ice.tiptext.util;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import java.lang.reflect.InvocationTargetException;

/**
 * 屏幕适配工具
 */
public class ApplicationUtils {
    private static Application application;

    /**
     * 获取Application
     * @return
     */
    public static Application getApplication() {
        if (application != null) return application;
        Application app = getApplicationByReflect();
        init(app);
        return app;
    }

    /**
     * 初始化，先跑它就对了
     * @param context
     */
    public static void init(final Context context) {
        if (context == null) {
            init(getApplicationByReflect());
            return;
        }
        init((Application) context.getApplicationContext());
    }

    /**
     * 初始化，先跑它就对了
     * @param app
     */
    public static void init(final Application app) {
        if (application == null) {
            if (app == null) {
                application = getApplicationByReflect();
            } else {
                application = app;
            }
        } else {
            if (app != null && app.getClass() != application.getClass()) {
                application = app;
            }
        }
    }

    /**
     * 获取Application
     * @return
     */
    private static Application getApplicationByReflect() {
        try {
            @SuppressLint("PrivateApi")
            Class<?> activityThread = Class.forName("android.app.ActivityThread");
            Object thread = activityThread.getMethod("currentActivityThread").invoke(null);
            Object app = activityThread.getMethod("getApplication").invoke(thread);
            if (app == null) {
                throw new NullPointerException("你能不能先初始化Application?");
            }
            return (Application) app;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        throw new NullPointerException("你能不能先初始化Application?");
    }

}
