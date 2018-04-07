package giant.dynamic;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import dalvik.system.PathClassLoader;

/**
 * Created by panda on 2018/3/30.
 */
public abstract class BasePerfectActivity extends BaseActivity {
    private static final String EXTRA_TARGET_INTENT = "extra_target_intent";
    protected Object originGDefault;
    protected Handler originmH;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        hookActivityManagerNative();
        hookActivityThreadHandler();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (originGDefault != null) {
            unHookActivityManagerNative();
        }
        if (originmH != null) {
            unHookActivityThreadHandler();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isInjected()) {
            inject(classLoader);
        }
    }

    /**
     * 判断plugin是否已经注入
     *
     * @return
     */
    private boolean isInjected() {
        PathClassLoader pathLoader = (PathClassLoader) getClassLoader();
        try {
            Object elements = getDexElements(getPathList(pathLoader));
            int i = Array.getLength(elements);
            for (int j = 0; j < i; j++) {
                Object classPath = Array.get(elements, j);
                if (classPath.toString().contains("plugin")) {
                    return true;
                }
            }
            return false;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 在启动插件的Activity之前，hook住AMS，将目标Activity替换成桩Activity从而绕过xml中的申明
     */
    private void hookActivityManagerNative() {
        try {
            Field gDefaultField = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Class<?> activityManager = Class.forName("android.app.ActivityManager");
                gDefaultField = activityManager.getDeclaredField("IActivityManagerSingleton");
            } else {
                Class<?> activityManagerNativeClass = Class.forName("android.app.ActivityManagerNative");
                gDefaultField = activityManagerNativeClass.getDeclaredField("gDefault");
            }
            gDefaultField.setAccessible(true);
            Object gDefault = gDefaultField.get(null);
            gDefaultField.setAccessible(false);
            Class<?> singleton = Class.forName("android.util.Singleton");
            Field mInstanceField = singleton.getDeclaredField("mInstance");
            mInstanceField.setAccessible(true);
            originGDefault = mInstanceField.get(gDefault);
            Class<?> iActivityManagerInterface = Class.forName("android.app.IActivityManager");
            Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                    new Class<?>[] { iActivityManagerInterface }, new IActivityManagerHandler(originGDefault));
            mInstanceField.set(gDefault, proxy);
            mInstanceField.setAccessible(false);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 在退出的情况下，需要将hook拦截给去掉，否则会影响正常的逻辑
     */
    private void unHookActivityManagerNative() {
        try {
            Field gDefaultField = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Class<?> activityManager = Class.forName("android.app.ActivityManager");
                gDefaultField = activityManager.getDeclaredField("IActivityManagerSingleton");
            } else {
                Class<?> activityManagerNativeClass = Class.forName("android.app.ActivityManagerNative");
                gDefaultField = activityManagerNativeClass.getDeclaredField("gDefault");
            }
            gDefaultField.setAccessible(true);
            Object gDefault = gDefaultField.get(null);
            gDefaultField.setAccessible(false);
            Class<?> singleton = Class.forName("android.util.Singleton");
            Field mInstanceField = singleton.getDeclaredField("mInstance");
            mInstanceField.setAccessible(true);
            mInstanceField.set(gDefault, originGDefault);
            mInstanceField.setAccessible(false);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将桩activity恢复成我们真实想要跳转的activity
     */
    private void hookActivityThreadHandler() {
        try {
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Field currentActivityThreadField = activityThreadClass.getDeclaredField("sCurrentActivityThread");
            currentActivityThreadField.setAccessible(true);
            Object currentActivityThread = currentActivityThreadField.get(null);
            currentActivityThreadField.setAccessible(false);

            Field mHField = activityThreadClass.getDeclaredField("mH");
            mHField.setAccessible(true);
            originmH = (Handler) mHField.get(currentActivityThread);

            Field mCallBackField = Handler.class.getDeclaredField("mCallback");
            mCallBackField.setAccessible(true);

            mCallBackField.set(originmH, new ActivityThreadHandlerCallback(originmH));
            mCallBackField.setAccessible(false);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 在退出的情况下，需要将hook拦截给去掉，否则会影响正常的逻辑
     */
    private void unHookActivityThreadHandler() {
        try {
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Field currentActivityThreadField = activityThreadClass.getDeclaredField("sCurrentActivityThread");
            currentActivityThreadField.setAccessible(true);
            Object currentActivityThread = currentActivityThreadField.get(null);
            currentActivityThreadField.setAccessible(false);

            Field mHField = activityThreadClass.getDeclaredField("mH");
            mHField.setAccessible(true);
            Handler mH = (Handler) mHField.get(currentActivityThread);

            Field mCallBackField = Handler.class.getDeclaredField("mCallback");
            mCallBackField.setAccessible(true);
            mCallBackField.set(mH, originmH);
            mCallBackField.setAccessible(false);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 对IActivityManager接口的动态代理
     */
    private class IActivityManagerHandler implements InvocationHandler {
        Object impl;

        public IActivityManagerHandler(Object impl) {
            this.impl = impl;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if ("startActivityForResult".equals(method.getName()) || "startActivity".equals(method.getName())) {
                Intent raw;
                int index = 0;

                for (int i = 0; i < args.length; i++) {
                    if (args[i] instanceof Intent) {
                        index = i;
                        break;
                    }
                }
                raw = (Intent) args[index];
                Intent newIntent = new Intent();
                String stubPackage = "giant.dynamic";
                ComponentName componentName = new ComponentName(stubPackage, StubActivity.class.getName());
                newIntent.setComponent(componentName);
                newIntent.putExtra(EXTRA_TARGET_INTENT, raw);

                // 替换掉Intent, 达到欺骗AMS的目的
                args[index] = newIntent;
                return method.invoke(impl, args);
            }

            // 其他还是正常的调用
            return method.invoke(impl, args);
        }
    }

    private class ActivityThreadHandlerCallback implements Handler.Callback {
        Handler mBase;

        public ActivityThreadHandlerCallback(Handler base) {
            mBase = base;
        }

        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what) {
                case 100:
                    handleLaunchActivity(msg);
                    break;
            }

            mBase.handleMessage(msg);
            return true;
        }

        private void handleLaunchActivity(Message msg) {
            Object obj = msg.obj;

            try {
                // 把替身恢复成真身
                Field intent = obj.getClass().getDeclaredField("intent");
                intent.setAccessible(true);
                Intent raw = (Intent) intent.get(obj);

                Intent target = raw.getParcelableExtra(EXTRA_TARGET_INTENT);
                raw.setComponent(target.getComponent());
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
