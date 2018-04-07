package giant.dynamic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * 采用代理的方式加载插件
 * <p>
 * Created by panda on 2018/3/30.
 */

public abstract class BaseProxyActivity extends BaseActivity {
    private Object pluginActivity;
    private Class<?> pluginClass;
    private String PLUGIN_ACTIVITY_NAME = "PLUGIN_ACTIVITY_NAME";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String pluginName = getIntent().getStringExtra(PLUGIN_ACTIVITY_NAME);
        if (TextUtils.isEmpty(pluginName)) {
            pluginName = pluginName();
        }
        instancePluginActivity(pluginName, savedInstanceState);
    }

    @Override
    public void startActivity(Intent intent) {
//        Intent targetIntent = new Intent(this, ProxyActivity.class);
//        targetIntent.putExtra(PLUGIN_ACTIVITY_NAME, intent.getComponent().getClassName());
//        super.startActivity(targetIntent);
        startActivityForResult(intent, -1);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        Intent targetIntent = new Intent(this, ProxyActivity.class);
        targetIntent.putExtra(PLUGIN_ACTIVITY_NAME, intent.getComponent().getClassName());
        super.startActivityForResult(targetIntent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            Method method = pluginClass.getDeclaredMethod("onActivityResult", int.class, int.class, Intent.class);
            method.setAccessible(true);
            method.invoke(pluginActivity, requestCode, resultCode, data);
            method.setAccessible(false);
        } catch (Exception e) {
            Log.i("demo", "run destroy error:" + Log.getStackTraceString(e));
        }
    }

    @Override
    public void onBackPressed() {
        try {
            Method method = pluginClass.getDeclaredMethod("onBackPressed");
            method.setAccessible(true);
            method.invoke(pluginActivity);
            method.setAccessible(false);
        } catch (Exception e) {
            Log.i("demo", "run destroy error:" + Log.getStackTraceString(e));
        }
    }

    /**
     * 插件activity的名称，从而完成实例化
     *
     * @return
     */
    public abstract String pluginName();

    private void instancePluginActivity(String plugin, @Nullable Bundle savedInstanceState) {
        try {
            // 将宿主的代理注入到插件中
            pluginClass = classLoader.loadClass(plugin);
            Constructor<?> localConstructor = pluginClass.getConstructor(new Class[]{});
            pluginActivity = localConstructor.newInstance();
            Method setProxy = pluginClass.getMethod("setProxy", Activity.class);
            setProxy.setAccessible(true);
            setProxy.invoke(pluginActivity, this);
            setProxy.setAccessible(false);

            // 触发插件的onCreate
            Method onCreate = pluginClass.getDeclaredMethod("onCreate", Bundle.class);
            onCreate.setAccessible(true);
            onCreate.invoke(pluginActivity, savedInstanceState);
            onCreate.setAccessible(false);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            Method method = pluginClass.getDeclaredMethod("onDestroy");
            method.setAccessible(true);
            method.invoke(pluginActivity);
            method.setAccessible(false);
        } catch (Exception e) {
            Log.i("demo", "run destroy error:" + Log.getStackTraceString(e));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            Method method = pluginClass.getMethod("onPause");
            method.setAccessible(true);
            method.invoke(pluginActivity);
            method.setAccessible(false);
        } catch (Exception e) {
            Log.i("demo", "run pause error:" + Log.getStackTraceString(e));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            Method method = pluginClass.getMethod("onResume");
            method.setAccessible(true);
            method.invoke(pluginActivity);
            method.setAccessible(false);
        } catch (Exception e) {
            Log.i("demo", "run resume error:" + Log.getStackTraceString(e));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            Method method = pluginClass.getMethod("onStart");
            method.setAccessible(true);
            method.invoke(pluginActivity);
            method.setAccessible(false);
        } catch (Exception e) {
            Log.i("demo", "run start error:" + Log.getStackTraceString(e));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            Method method = pluginClass.getMethod("onStop");
            method.setAccessible(true);
            method.invoke(pluginActivity);
            method.setAccessible(false);
        } catch (Exception e) {
            Log.i("demo", "run stop error:" + Log.getStackTraceString(e));
        }
    }
}
