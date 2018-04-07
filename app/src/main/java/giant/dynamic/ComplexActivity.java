package giant.dynamic;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import giant.iplugin.complex.IBean;
import giant.iplugin.complex.IDynamic;
import giant.iplugin.complex.YKCallBack;

public class ComplexActivity extends BaseActivity {

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.complex);

        Button btn_1 = findViewById(R.id.btn_1);
        Button btn_2 = findViewById(R.id.btn_2);
        Button btn_3 = findViewById(R.id.btn_3);
        Button btn_5 = findViewById(R.id.btn_5);
        Button btn_6 = findViewById(R.id.btn_6);
        Button btn_7 = findViewById(R.id.btn_7);
        Button btn_8 = findViewById(R.id.btn_8);

        btn_1.setOnClickListener(new View.OnClickListener() {//普通调用  反射的方式
            @Override
            public void onClick(View arg0) {
                Class mLoadClassBean;
                try {
                    mLoadClassBean = classLoader.loadClass("giant.plugin.complex.Bean");
                    Object beanObject = mLoadClassBean.newInstance();
                    IBean bean = (IBean) beanObject;
                    String name = bean.getName();
                    Toast.makeText(ComplexActivity.this, name, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.e("DEMO", "msg:" + e.getMessage());
                }
            }
        });
        btn_2.setOnClickListener(new View.OnClickListener() {//带参数调用
            @Override
            public void onClick(View arg0) {
                Class mLoadClassBean;
                try {
                    mLoadClassBean = classLoader.loadClass("giant.plugin.complex.Bean");
                    Object beanObject = mLoadClassBean.newInstance();
                    IBean bean = (IBean) beanObject;
                    bean.setName("宿主程序设置的新名字");
                    Toast.makeText(ComplexActivity.this, bean.getName(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.e("DEMO", "msg:" + e.getMessage());
                }

            }
        });
        btn_3.setOnClickListener(new View.OnClickListener() {//带回调函数的调用
            @Override
            public void onClick(View arg0) {
                Class mLoadClassDynamic;
                try {
                    mLoadClassDynamic = classLoader.loadClass("giant.plugin.complex.Dynamic");
                    Object dynamicObject = mLoadClassDynamic.newInstance();
                    //接口形式调用
                    IDynamic dynamic = (IDynamic) dynamicObject;
                    //回调函数调用
                    YKCallBack callback = new YKCallBack() {
                        public void callback(IBean arg0) {
                            Toast.makeText(ComplexActivity.this, arg0.getName(), Toast.LENGTH_SHORT).show();
                        }
                    };
                    dynamic.methodWithCallBack(callback);
                } catch (Exception e) {
                    Log.e("DEMO", "msg:" + e.getMessage());
                }

            }
        });
        btn_5.setOnClickListener(new View.OnClickListener() {//带资源文件的调用
            @Override
            public void onClick(View arg0) {
//                loadResources();
                loadApkClassLoader(classLoader);
                Class mLoadClassDynamic;
                try {
//                    mLoadClassDynamic = classLoader.loadClass("giant.plugin.complex.Dynamic");
//                    Object dynamicObject = mLoadClassDynamic.newInstance();
//                    //接口形式调用
//                    IDynamic dynamic = (IDynamic) dynamicObject;
//                    dynamic.startPluginActivity(ComplexActivity.this,
//                            classLoader.loadClass("giant.plugin.EmptyActivity"));
                    Intent intent = new Intent(ComplexActivity.this,
                            classLoader.loadClass("giant.plugin.EmptyActivity"));
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e("DEMO", "msg:" + e.getMessage());
                }
            }
        });
        btn_6.setOnClickListener(new View.OnClickListener() {//带资源文件的调用
            @Override
            public void onClick(View arg0) {
//                loadResources();
                inject(classLoader);
                Class mLoadClassDynamic;
                try {
                    mLoadClassDynamic = classLoader.loadClass("giant.plugin.complex.Dynamic");
                    Object dynamicObject = mLoadClassDynamic.newInstance();
                    //接口形式调用
                    IDynamic dynamic = (IDynamic) dynamicObject;
                    dynamic.startPluginActivity(ComplexActivity.this,
                            classLoader.loadClass("giant.plugin.EmptyActivity"));
                } catch (Exception e) {
                    Log.e("DEMO", "msg:" + e.getMessage());
                }
            }
        });
        btn_7.setOnClickListener(new View.OnClickListener() {//带资源文件的调用
            @Override
            public void onClick(View arg0) {
                loadResources();
                Class mLoadClassDynamic;
                try {
                    mLoadClassDynamic = classLoader.loadClass("giant.plugin.complex.Dynamic");
                    Object dynamicObject = mLoadClassDynamic.newInstance();
                    //接口形式调用
                    IDynamic dynamic = (IDynamic) dynamicObject;
                    dynamic.showPluginWindow(ComplexActivity.this);
                } catch (Exception e) {
                    Log.e("DEMO", "msg:" + e.getMessage());
                }
            }
        });
        btn_8.setOnClickListener(new View.OnClickListener() {//带资源文件的调用
            @Override
            public void onClick(View arg0) {
                loadResources();
                Class mLoadClassDynamic;
                try {
                    mLoadClassDynamic = classLoader.loadClass("giant.plugin.complex.Dynamic");
                    Object dynamicObject = mLoadClassDynamic.newInstance();
                    //接口形式调用
                    IDynamic dynamic = (IDynamic) dynamicObject;
                    String content = dynamic.getStringForResId(ComplexActivity.this);
                    Toast.makeText(getApplicationContext(), content + "", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Log.e("DEMO", "msg:" + e.getMessage());
                }
            }
        });

    }
}
