package giant.dexclassloader;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dalvik.system.DexClassLoader;


public class MainActivity extends AppCompatActivity {
    private String apkDir = "";
    private List<HashMap<String,String>> datas;
    private ListView mListView;
    private final List<String> apkName = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        apkDir = getFilesDir() + File.separator + "plugin";
        File apkDirFile = new File(apkDir);
        if (!apkDirFile.exists()) {
            apkDirFile.mkdirs();
        }
        copyApkFromAssets();
        Toast.makeText(this, "拷贝完成", Toast.LENGTH_SHORT).show();
    }

    private void copyApkFromAssets() {
        try {
            String[] assets = getResources().getAssets().list("plugin");
            for (String asset : assets) {
                if (!asset.endsWith(".apk")) {
                    continue;
                }
                InputStream is = getResources().getAssets().open("plugin/" + asset);
                String apkPath = apkDir + File.separator + asset;
                FileOutputStream out = new FileOutputStream(apkPath);
                byte[] b = new byte[1024];
                int n = 0;
                while ((n = is.read(b)) != -1) {
                    out.write(b, 0, n);
                }
                is.close();
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载apk获得内部资源
     * @param apkDir apk目录
     * @param apkName apk名字,带.apk
     * @throws Exception
     */
    private void dynamicLoadApk(String apkDir, String apkName, String apkPackageName) throws Exception {
        File optimizedDirectoryFile = getDir("dex", Context.MODE_PRIVATE);
        DexClassLoader dexClassLoader = new DexClassLoader(apkDir+File.separator+apkName, optimizedDirectoryFile.getPath(), null, ClassLoader.getSystemClassLoader());
        Class<?> clazz = dexClassLoader.loadClass(apkPackageName + ".R$mipmap");
        Field field = clazz.getDeclaredField("one");
        int resId = field.getInt(R.id.class);
        Resources mResources = getPluginResources(apkName);
        if (mResources != null) {
            findViewById(R.id.background).setBackgroundDrawable(mResources.getDrawable(resId));
        }
    }

    /**
     * @param apkName
     * @return 得到对应插件的Resource对象
     */
    private Resources getPluginResources(String apkName) {
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
            addAssetPath.invoke(assetManager, apkDir+File.separator+apkName);
            Resources superRes = this.getResources();
            return new Resources(assetManager, superRes.getDisplayMetrics(), superRes.getConfiguration());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.skin) {
            //第二步先查找并得到该apk目录下所有的apk信息
            datas = searchAllPlugin(apkDir);
            //第三步显示查找后可用的apk插件
            showCanEnabledPlugin(datas);
            //第四步处理用户的点击事件,并设置相应的皮肤
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    HashMap<String,String> map =datas.get(position);
                    if(map!=null){
                        String pkgName = map.get("pkgName");
                        String apkname = apkName.get(position);
                        try {
                            dynamicLoadApk(apkDir, apkname, pkgName);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }
    private List<HashMap<String,String>> searchAllPlugin(String apkDir){
        List<HashMap<String,String>> lists = new ArrayList<>();
        File dir = new File(apkDir);
        if(dir.isDirectory()){
            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    return filename.endsWith(".apk");
                }
            };
            File[] apks =dir.listFiles(filter);
            for (int i = 0; i < apks.length; i++) {
                File temp = apks[i];
                apkName.add(temp.getName());
                String[] info = getUninstallApkInfo(this,apkDir+File.separator+temp.getName());
                HashMap<String,String> map = new HashMap<>();
                map.put("label",info[0]);
                map.put("pkgName",info[1]);
                lists.add(map);
                map = null;
            }
        }
        return lists;
    }
    /**
     * 列出应用中的可用插件,由于只是示例演示，该demo就简单的把apk插件放在assets目录下，然后运行时候拷贝到sd卡的plugin目录下
     */
    private void showCanEnabledPlugin(List<HashMap<String,String>> datas) {
        if(datas==null||datas.isEmpty()){
            Toast.makeText(this, "没有找到，请先下载插件！", Toast.LENGTH_SHORT).show();
        }
        View view = LayoutInflater.from(this).inflate(R.layout.layout_item,null,false);
        mListView = (ListView) view.findViewById(R.id.listview);
        mListView.setAdapter(new SimpleAdapter(this, datas, android.R.layout.simple_list_item_1, new String[]{"label"}, new int[]{android.R.id.text1}));
        PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAtLocation(view, Gravity.TOP | Gravity.RIGHT, 0, 0);
    }

    /**
     * 获取未安装apk的信息
     * @param context
     * @param archiveFilePath apk文件的path
     * @return
     */
    private String[] getUninstallApkInfo(Context context, String archiveFilePath) {
        String[] info = new String[2];
        PackageManager pm = context.getPackageManager();
        PackageInfo pkgInfo = pm.getPackageArchiveInfo(archiveFilePath, PackageManager.GET_ACTIVITIES);
        if (pkgInfo != null) {
            ApplicationInfo appInfo = pkgInfo.applicationInfo;
            String versionName = pkgInfo.versionName;//版本号
            Drawable icon = pm.getApplicationIcon(appInfo);//图标
            String appName = pm.getApplicationLabel(appInfo).toString();//app名称
            String pkgName = appInfo.packageName;//包名
            info[0] = appName;
            info[1] = pkgName;
        }
        return info;
    }
}
