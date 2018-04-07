package giant.iplugin;

import android.app.Activity;

/**
 * Created by panda on 2018/3/29.
 */

public interface IDynamic {
    /**初始化方法*/
    public void init(Activity activity);
    /**自定义方法*/
    public void showBanner();
    public void showDialog();
    public void showFullScreen();
    public void showAppWall();

}
