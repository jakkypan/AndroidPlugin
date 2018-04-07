package giant.plugin.proxy;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

/**
 * Created by panda on 2018/3/30.
 */
@SuppressLint("MissingSuperCall")
public abstract class BasePluginProxyActivity extends AppCompatActivity {
    protected Activity that;

    /**
     * 这个是宿主和插件的连接点
     *
     * @param proxyActivity
     */
    public void setProxy(Activity proxyActivity) {
        that = proxyActivity;
    }

    @Override
    public void setContentView(View view) {
        if (that != null) {
            that.setContentView(view);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // empty super
    }

    @Override
    public void onBackPressed() {
        that.onBackPressed();
    }

    @Override
    public void onDestroy() {
        Log.e("111", "plugin onDestroy");
    }

    @Override
    public void onPause() {
        Log.e("111", "plugin onPause");
    }

    @Override
    public void onResume() {
        Log.e("111", "plugin onResume");
    }

    @Override
    public void onStart() {
        Log.e("111", "plugin onStart");
    }

    @Override
    public void onStop() {
        Log.e("111", "plugin onStop");
    }
}
