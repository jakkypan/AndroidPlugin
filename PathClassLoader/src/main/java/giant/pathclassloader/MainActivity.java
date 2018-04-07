package giant.pathclassloader;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dalvik.system.PathClassLoader;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private List<PluginBean> plugins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.theme_change) {
            List<HashMap<String, String>> datas = new ArrayList<>();
            plugins = findAllPlugin();
            if (plugins != null && !plugins.isEmpty()) {
                for (PluginBean bean : plugins) {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("label", bean.getLabel());
                    datas.add(map);
                }
                showEnableAllPluginPopup(datas);
            } else {
                Toast.makeText(this, "没有找到插件，请先下载！", Toast.LENGTH_SHORT).show();
            }

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 显示所有可用插件的列表，查找已安装的apk即在/data/app目录下
     * @param datas 可用插件的集合
     */
    private void showEnableAllPluginPopup(final List<HashMap<String, String>> datas) {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_popup, null);
        ListView mListView = (ListView) view.findViewById(R.id.listview);
        mListView.setAdapter(new SimpleAdapter(this, datas, android.R.layout.simple_list_item_1, new String[]{"label"}, new int[]{android.R.id.text1}));
        PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(view, Gravity.TOP | Gravity.END, 0, 0);
        mListView.setOnItemClickListener(this);
    }

    /**
     * 查找手机内所有的插件
     * @return 返回一个插件List
     */
    private List<PluginBean> findAllPlugin() {
        List<PluginBean> plugins = new ArrayList<>();
        PackageManager pm = getPackageManager();
        //通过包管理器查找所有已安装的apk文件
        List<PackageInfo> packageInfos = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        for (PackageInfo info : packageInfos) {
            //得到当前apk的包名
            String pkgName = info.packageName;
            //得到当前apk的sharedUserId
            String shareUesrId = info.sharedUserId;
            //判断这个apk是否是我们应用程序的插件
            if (shareUesrId != null && shareUesrId.equals("plugin.shared.uid") && !pkgName.equals(this.getPackageName())) {
                String label = pm.getApplicationLabel(info.applicationInfo).toString();//得到插件apk的名称
                PluginBean bean = new PluginBean(label,pkgName);
                plugins.add(bean);
            }
        }
        return plugins;
    }

    /**
     * 加载已安装的apk
     * @param packageName 应用的包名
     * @param pluginContext 插件app的上下文
     * @return 对应资源的id
     */
    private int dynamicLoadApk(String packageName, Context pluginContext) throws Exception {
        PathClassLoader pathClassLoader = new PathClassLoader(pluginContext.getPackageResourcePath(),ClassLoader.getSystemClassLoader());
        Class<?> clazz = Class.forName(packageName + ".R$color", true, pathClassLoader);
        Field field = clazz.getDeclaredField("plugin");
        return field.getInt(R.color.class);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            PluginBean bean = plugins.get(position);
            String packageName = bean.getPackageName();
            Context plugnContext = this.createPackageContext(packageName, CONTEXT_IGNORE_SECURITY | CONTEXT_INCLUDE_CODE);
            int resouceId = dynamicLoadApk(packageName, plugnContext);
            findViewById(R.id.mainLayout).setBackgroundColor(plugnContext.getResources().getColor(resouceId));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
