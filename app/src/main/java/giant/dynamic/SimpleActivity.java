package giant.dynamic;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

import dalvik.system.DexClassLoader;
import giant.iplugin.IDynamic;

public class SimpleActivity extends BaseActivity {
    private IDynamic lib;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button showBannerBtn = findViewById(R.id.show_banner_btn);
        Button showDialogBtn = findViewById(R.id.show_dialog_btn);
        Button showFullScreenBtn = findViewById(R.id.show_fullscreen_btn);
        Button showAppWallBtn = findViewById(R.id.show_appwall_btn);
        /**
         * 这里需要说明的一点是：
         *
         * 直接去读取外存的apk会报出异常： java.lang.IllegalArgumentException: Optimized data directory /storage/emulated/0 is not owned by the current user. Shared storage cannot protect your application from code injection attacks.
         *
         * 这个是原因是从4.1.2开始，系统是不允许直接从sdcard中加载字节码数据，因为sdk卡是共享的，这样存在恶意代码的注入攻击。直接是异常是报在DexFile类中。
         *
         * 所以解决的办法是将apk拷贝到app下的私有目录下
         */
//        String dexPath = Environment.getExternalStorageDirectory().toString() + File.separator + "plugin.apk";
        if (new File(apkPath).exists()) {
            DexClassLoader cl = new DexClassLoader(apkPath, dexOutputDirs, null, getClassLoader());

            /**
             * PathClassLoader的方式
             */
//            Intent intent = new Intent("giant.plugin", null);
//            PackageManager pm = getPackageManager();
//            List<ResolveInfo> resolveinfoes = pm.queryIntentActivities(intent, 0);
//            ActivityInfo actInfo = resolveinfoes.get(0).activityInfo;
//            // 获得apk的目录或者jar的目录
//            String apkPath = actInfo.applicationInfo.sourceDir;
//            // native代码的目录
//            String libPath = actInfo.applicationInfo.nativeLibraryDir;
//            PathClassLoader pcl = new PathClassLoader(apkPath, libPath, this.getClassLoader());

            // 加载动态类
            try {
                Class libProviderClazz = cl.loadClass("giant.plugin.Dynamic");
                lib = (IDynamic) libProviderClazz.newInstance();
                if (lib != null) {
                    lib.init(this);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }

        /**下面分别调用动态类中的方法*/
        showBannerBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (lib != null) {
                    lib.showBanner();
                } else {
                    Toast.makeText(getApplicationContext(), "类加载失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
        showDialogBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (lib != null) {
                    lib.showDialog();
                } else {
                    Toast.makeText(getApplicationContext(), "类加载失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
        showFullScreenBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (lib != null) {
                    lib.showFullScreen();
                } else {
                    Toast.makeText(getApplicationContext(), "类加载失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
        showAppWallBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (lib != null) {
                    lib.showAppWall();
                } else {
                    Toast.makeText(getApplicationContext(), "类加载失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
