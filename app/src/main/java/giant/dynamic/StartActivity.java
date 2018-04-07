package giant.dynamic;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by panda on 2018/3/29.
 */

public class StartActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);
    }

    public void simple(View view) {
        startActivity(new Intent(this, SimpleActivity.class));
    }

    public void complex(View view) {
        startActivity(new Intent(this, ComplexActivity.class));
    }

    public void proxy(View view) {
        startActivity(new Intent(this, ProxyActivity.class));
    }

    public void perfect(View view) {
        startActivity(new Intent(this, PerfectActivity.class));
    }
}
