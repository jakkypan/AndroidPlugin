package giant.dynamic;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.ArrayMap;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

/**
 * Created by panda on 2018/3/29.
 */
public abstract class BaseActivity extends AppCompatActivity {
    public AssetManager mAssetManager;//资源管理器
    public Resources mResources;//资源
    public Resources.Theme mTheme;//主题
    public String apkPath = "";
    public String dexOutputDirs;
    public DexClassLoader classLoader = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dexOutputDirs = getFilesDir() + File.separator + "dex";
        File dexOutput = new File(dexOutputDirs);
        if (!dexOutput.exists()) {
            dexOutput.mkdirs();
        }
        try {
            InputStream is = getResources().getAssets().open("plugin.apk");
            apkPath = getFilesDir() + File.separator + "plugin.apk";
            FileOutputStream out = new FileOutputStream(apkPath);
            byte[] b = new byte[1024];
            int n = 0;
            while ((n = is.read(b)) != -1) {
                out.write(b, 0, n);
            }
            is.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        classLoader = new DexClassLoader(apkPath, dexOutputDirs, null, getClassLoader());
    }

    /**
     * 反射方式加载插件
     *
     * 通过替换LoadedApk中的mClassLoader让加载的Activity有启动流程和生命周期
     *
     * @param dLoader
     */
    protected void loadApkClassLoader(DexClassLoader dLoader) {
        Object currentActivityThread = RefInvoke.invokeStaticMethod(
                "android.app.ActivityThread", "currentActivityThread",
                new Class[] {}, new Object[] {});
        String packageName = this.getPackageName();
        ArrayMap mPackages = (ArrayMap) RefInvoke.getFieldOjbect(
                "android.app.ActivityThread", currentActivityThread,
                "mPackages");
        WeakReference wr = (WeakReference) mPackages.get(packageName);
        RefInvoke.setFieldOjbect("android.app.LoadedApk", "mClassLoader", wr.get(), dLoader);
    }

    /**
     * 反射方式加载插件
     *
     * 通过合并PathClassLoader和DexClassLoader中的dexElements数组的方式
     *
     * @param loader
     */
    protected void inject(DexClassLoader loader){
        PathClassLoader pathLoader = (PathClassLoader) getClassLoader();

        try {
            Object dexElements = combineArray(
                    getDexElements(getPathList(pathLoader)),
                    getDexElements(getPathList(loader)));
            Object pathList = getPathList(pathLoader);
            setField(pathList, pathList.getClass(), "dexElements", dexElements);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected Object getPathList(Object baseDexClassLoader)
            throws IllegalArgumentException, NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
        return getField(baseDexClassLoader, Class.forName("dalvik.system.BaseDexClassLoader"), "pathList");
    }

    protected Object getField(Object obj, Class<?> cl, String field)
            throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field localField = cl.getDeclaredField(field);
        localField.setAccessible(true);
        return localField.get(obj);
    }

    protected Object getDexElements(Object paramObject)
            throws IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
        return getField(paramObject, paramObject.getClass(), "dexElements");
    }
    protected void setField(Object obj, Class<?> cl, String field,
                                 Object value) throws NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException {

        Field localField = cl.getDeclaredField(field);
        localField.setAccessible(true);
        localField.set(obj, value);
        localField.setAccessible(false);
    }

    protected Object combineArray(Object arrayLhs, Object arrayRhs) {
        Class<?> localClass = arrayLhs.getClass().getComponentType();
        int i = Array.getLength(arrayLhs);
        int j = i + Array.getLength(arrayRhs);
        Object result = Array.newInstance(localClass, j);
        for (int k = 0; k < j; ++k) {
            if (k < i) {
                Array.set(result, k, Array.get(arrayLhs, k));
            } else {
                Array.set(result, k, Array.get(arrayRhs, k - i));
            }
        }
        return result;
    }


    protected void loadResources() {
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
            addAssetPath.invoke(assetManager, dexOutputDirs);
            mAssetManager = assetManager;
        } catch (Exception e) {
            e.printStackTrace();
        }
        Resources superRes = super.getResources();
        superRes.getDisplayMetrics();
        superRes.getConfiguration();
        mResources = new Resources(mAssetManager, superRes.getDisplayMetrics(), superRes.getConfiguration());
        mTheme = mResources.newTheme();
        mTheme.setTo(super.getTheme());
    }

    @Override
    public AssetManager getAssets() {
        return mAssetManager == null ? super.getAssets() : mAssetManager;
    }

    @Override
    public Resources getResources() {
        return mResources == null ? super.getResources() : mResources;
    }

    @Override
    public Resources.Theme getTheme() {
        return mTheme == null ? super.getTheme() : mTheme;
    }
}
