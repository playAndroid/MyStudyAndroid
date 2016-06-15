package family.safe.wechatimageui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * 仿微信图片相册选择器
 * 1 , 避免内存溢出
 * a , 压缩图片
 * b , 使用缓存(LRUCaChe)
 *
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
