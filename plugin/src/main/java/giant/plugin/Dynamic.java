package giant.plugin;

import android.app.Activity;
import android.widget.Toast;

import giant.iplugin.IDynamic;

/**
 * Created by panda on 2018/3/29.
 */

public class Dynamic implements IDynamic {

    private Activity mActivity;

    @Override
    public void init(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void showBanner() {
        Toast.makeText(mActivity, "我是ShowBannber方法", 1500).show();
    }

    @Override
    public void showDialog() {
        Toast.makeText(mActivity, "我是ShowDialog方法", 1500).show();
    }

    @Override
    public void showFullScreen() {
        Toast.makeText(mActivity, "我是ShowFullScreen方法", 1500).show();
    }

    @Override
    public void showAppWall() {
        Toast.makeText(mActivity, "我是ShowAppWall方法", 1500).show();
    }

}