package family.safe.studyactivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

/**
 * IntentFilter 的匹配规则
 * Created by Administrator on 2016/7/31.
 */
public class IntentFilterStudyActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //显示调用
        Intent intent = new Intent(this,LaunchModeActivityStudy.class);
        startActivity(intent);
        //隐式调用
        Intent intent1 = new Intent();//action
        intent1.setAction("com.ground.hao.a");
        intent1.addCategory("com.ground.hao.d");
        intent1.setDataAndType(Uri.parse("file://abc"),"text/plain");
        startActivity(intent1);
    }
}
