package com.ice.tiptext.manager;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ActivityManager {

    private List<WeakReference<Activity>> activityRefs = new ArrayList<>();
    private List<FrontBackCallback> frontBackCallbacks = new ArrayList<>();
    private int activityStartCount = 0;
    private boolean front = true;

    public static ActivityManager getInstance(){
        return Inner.activityManager;
    }

    public void init(Application app){
        app.registerActivityLifecycleCallbacks(new InnerActivityLifecycleCallbacks());
    }

    public void addFrontBackCallback(FrontBackCallback callback){
        frontBackCallbacks.add(callback);
    }

    public void removeFrontBackCallback(FrontBackCallback callback){
        frontBackCallbacks.remove(callback);
    }

    public Activity getTopActivity(boolean alive){
        if(activityRefs.size()<=0){
            return null;
        }
        WeakReference<Activity> activityWeakReference = activityRefs.get(activityRefs.size() - 1);
        Activity activity = activityWeakReference.get();
        if(alive){
            if(activity==null || activity.isFinishing() ||
                    (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed())){
                activityRefs.remove(activityWeakReference);
                getTopActivity(true);
            }
        }
        return activity;
    }

    private class InnerActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
            activityRefs.add(new WeakReference<Activity>(activity));
        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {
            activityStartCount++;
            if(!front && activityStartCount>0){
                front = true;
                for(FrontBackCallback callback:frontBackCallbacks){
                    callback.onChanged(true);
                }
            }
        }

        @Override
        public void onActivityResumed(@NonNull Activity activity) {

        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {

        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {
            activityStartCount--;
            if(front && activityStartCount<=0){
                front = false;
                for(FrontBackCallback callback:frontBackCallbacks){
                    callback.onChanged(false);
                }
            }
        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {
            for(WeakReference<Activity> activityWeakReference: activityRefs){
                if(activityWeakReference!=null && activityWeakReference.get() == activity){
                    activityRefs.remove(activityWeakReference);
                    break;
                }
            }
        }
    }

    private ActivityManager(){}

    private static class Inner{
        private static ActivityManager activityManager = new ActivityManager();
    }

    public interface FrontBackCallback{
        void onChanged(boolean front);
    }
}
