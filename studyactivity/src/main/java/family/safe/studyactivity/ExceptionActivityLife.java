package family.safe.studyactivity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

/**
 * 异常情况下的Activity,生命周期
 */
public class ExceptionActivityLife extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //异常销毁时调用, 在此保存当前的状态
        //调用时机在onStop之前
        //将此时保存的Bundle对象,传递给onCreate和onRestoreInstanceState方法
        outState.putChar("destory", 'a');
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //当前Activity被异常销毁重建时调用,此时可以获取销毁时保存的状态
        //在onStart之后被调用
        char destory = (char) savedInstanceState.get("destory");
        Log.e("groundHao", destory + "");
    }
}
