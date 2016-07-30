package family.safe.studyactivity;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by Administrator on 2016/7/30.
 */
public class NormalActivityLife extends Activity {
    //Activity被创建时调用
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /**
         * 1,Activity一被创建便会执行onCreate方法创建,仅会在第一次创建时调用
         *
         * 2,如果在onSaveInstanceState()方法中曾记录过Activity的状态,savedInstanceState中会保存以前的状态.
         *
         * 3,之后执行onStart()方法;
         *
         * 此方法是做所有初始化设置的地方,创建视图,绑定数据等.
         */
    }

    //Activity由不可见变为可见时调用
    @Override
    protected void onStart() {
        super.onStart();
        /**
         * 1,随着程序是否为用户可见被多次调用
         *
         * 2,从onStart到onStop 为整个Activity的可视生命周期,
         *
         * 3,之后执行onResume()方法;
         *
         * 可以在此进行注册BroadcastReceiver来监控影响UI的改变.或加载一些资源等.
         */
    }

    //Activity可见时就会被调用
    @Override
    protected void onResume() {
        super.onResume();
        /**
         * 1,此时我们获得焦点可与用户进行交互
         *
         * 2,前台的生命周期自onResume()直到onPause()为止.在此期间Activity总是可见的.
         *
         * 3,此时的Activity一定是位于栈顶的.并接受用户的输入.
         *
         * 4,Activity会经常在暂停与恢复之间进行状态转换.
         *
         * 之后总会执行onPause()方法;
         */
    }

    //Activity可见不可操作时被调用
    @Override
    protected void onPause() {
        super.onPause();
        /**
         * 1,当Activity准备去启动另一个Activity时.
         *
         * 2,可以在此方法中将未保存的数据进行持久化,或释放掉一些消耗CPU的资源(比如动画)
         *
         * 3,此方法中执行速度一定要快,下一个活动会等待此方法执行完才会执行.
         *
         * 当Activity回到前台执行onResume();
         * 当Activity变为不可见时执行onStop();
         */
    }

    //Activity完全不可见时被调用
    //退出当前Activity或者跳转到新Activity时被调用
    @Override
    protected void onStop() {
        super.onStop();

        /**
         * 1,停滞状态,此时Activity完全不可见,从可见到完全不可见之时被调用
         *
         * 2,从onStart到onStop 为整个Activity的可视生命周期,随着程序是否为用户可见被多次调用
         *
         * 3,如果我们在onStart中注册了BroadcastReceiver,可在此方法中进行取消注册.或释放一些资源.
         *
         * 4,从而保证停滞的Activity占用更少的内存
         *
         * 如果再次回到前台与用户进行交互则执行onRestart()方法.
         * 如果关闭销毁Activity则执行onDestroy()方法;
         */
    }

    //Activity从后台重新回到前台时被调用
    @Override
    protected void onRestart() {
        super.onRestart();
        /**
         * 重新开始
         * 此时Activity由完全不可见变为可见之前会被调用(onStop-onRestart-onStart之间被调用)
         * 在Activity停止后,在再次启动之前被调用
         *
         * 之后总会执行onStart()方法;
         */
    }

    //退出当前Activity时被调用,调用之后Activity就结束了
    @Override
    protected void onDestroy() {
        super.onDestroy();
        /**
         * 1,销毁Activity之前会被调用
         *
         * 2,可以在此方法中进行资源的释放以节省内存等.
         *
         * 可以使用isFinishing()方法区分Activity是被系统销毁或Activity正常结束.
         */
    }

    //Activity窗口获得或失去焦点时被调用
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        /**
         * 1,在可否与用户交互之间调用
         *
         * 2,可在此方法中获取视图组件的尺寸大小.
         *
         * 3,总是在onResume之后或onPause之后被调用
         */
    }

    /**
     * Activity被系统杀死时被调用.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        /**
         *  例如:
         *  1,屏幕方向改变时,Activity被销毁再重建;
         *  2,当前Activity处于后台,系统资源紧张将其杀死.
         *  3,另外,当跳转到其他Activity或者按Home键回到主屏时该方法也会被调用,系统是为了保存当前View组件的状态.
         *  4,在onStop之前被调用也可能在onPause()之前.
         *  tip: 官方文档为it does so before onStop() and possibly before onPause()..
         *<span style="white-space:pre">  </span>
         *  通常在此方法中保存Activity的一些临时状态.
         */
    }

    /**
     * Activity被系统杀死后再重建时被调用.
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        /**
         * 例如:
         * 1,屏幕方向改变时,Activity被销毁再重建之时;
         * 2,当前Activity处于后台,系统资源紧张将其杀死,用户又启动该Activity.
         * 3,这两种情况下onRestoreInstanceState都会被调用,在onStart之后.
         *
         * 在此方法中我们可以进行一些数据的恢复.
         */
    }
}
