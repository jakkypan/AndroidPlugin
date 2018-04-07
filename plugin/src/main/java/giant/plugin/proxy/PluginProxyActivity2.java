package giant.plugin.proxy;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

/**
 * Created by panda on 2018/3/30.
 */

public class PluginProxyActivity2 extends BasePluginProxyActivity {
    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        TextView tv = new TextView(that);
        tv.setText("I am plugin 2, you called me by proxy manner，Click me ");
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                that.setResult(Activity.RESULT_OK, null);
                that.finish();
            }
        });
        setContentView(tv);
    }

    @Override
    public void onBackPressed() {
        that.setResult(Activity.RESULT_OK, null);
        that.finish();
    }
}
