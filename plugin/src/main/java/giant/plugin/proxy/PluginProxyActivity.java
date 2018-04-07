package giant.plugin.proxy;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by panda on 2018/3/30.
 */
public class PluginProxyActivity extends BasePluginProxyActivity {

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        LinearLayout linearLayout = new LinearLayout(that);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        TextView tv = new TextView(that);
        tv.setText("I am plugin, you called me by proxy manner");
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(that, "you clicked me", Toast.LENGTH_SHORT).show();
            }
        });
        linearLayout.addView(tv);

        Button button = new Button(that);
        button.setText("测试startActivity");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                that.startActivity(new Intent(that, PluginProxyActivity2.class));
            }
        });
        linearLayout.addView(button);

        Button button2 = new Button(that);
        button2.setText("测试startActivityForResult");
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                that.startActivityForResult(new Intent(that, PluginProxyActivity2.class), 1000);
            }
        });
        linearLayout.addView(button2);

        setContentView(linearLayout);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK) {
            Toast.makeText(that, "i am backed", Toast.LENGTH_SHORT).show();
        }
    }
}
