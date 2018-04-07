package giant.dynamic;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by panda on 2018/3/30.
 */

public class AppDemoTest extends ActivityInstrumentationTestCase2<StartActivity> {
    public AppDemoTest() {
        super(StartActivity.class);
    }

    public void testApp(){
        getActivity();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Toast.makeText(getActivity(), "show....", Toast.LENGTH_SHORT).show();
        Log.e("111", "============");


    }
}