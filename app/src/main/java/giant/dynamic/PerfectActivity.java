package giant.dynamic;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by panda on 2018/3/31.
 */
public class PerfectActivity extends BasePerfectActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Button button = new Button(this);
        button.setText("启动TargetActivity");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivityForResult(new Intent(PerfectActivity.this,
                            classLoader.loadClass("giant.plugin.perfect.PerfectActivity")), 1000);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        setContentView(button);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1000) {
            Toast.makeText(this, "backed from plugin", Toast.LENGTH_SHORT).show();
        }
    }
}
