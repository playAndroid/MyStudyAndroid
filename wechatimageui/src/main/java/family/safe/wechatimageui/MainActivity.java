package family.safe.wechatimageui;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import family.safe.wechatimageui.bean.FolderBean;

/**
 * 仿微信图片相册选择器
 * 1 , 避免内存溢出
 * a , 压缩图片
 * b , 使用缓存(LRUCaChe)
 */
public class MainActivity extends AppCompatActivity {

    private GridView mGridView;
    private RelativeLayout mButton;

    private TextView mDirName;
    private TextView mDirCount;

    private File mCurrentDir;
    private int mMaxCount;

    private List<FolderBean> folderBeanList = new ArrayList<>();

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        initEven();
    }

    private void initEven() {

    }

    /**
     * 利用ContentProvider扫描手机中的所有图片
     */
    private void initData() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "当前存储卡不可用", Toast.LENGTH_SHORT).show();
            return;
        }
        mProgressDialog = ProgressDialog.show(this, null, "正在加载...");

        new Thread() {
            @Override
            public void run() {
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver cr = MainActivity.this.getContentResolver();
                Cursor cursor = cr.query(mImageUri, null, MediaStore.Images.Media.MIME_TYPE + " = ? or" + MediaStore.Images.Media.MIME_TYPE + " = ? "
                        , new String[]{"image/jpeg", "image/png"}, MediaStore.Images.Media.DATE_MODIFIED);
            }
        }.start();
    }

    private void initView() {
        mGridView = (GridView) findViewById(R.id.id_gridView);
        mButton = (RelativeLayout) findViewById(R.id.id_rl_button);
        mDirName = (TextView) findViewById(R.id.id_tv_title);
        mDirCount = (TextView) findViewById(R.id.id_tv_count);

    }
}
