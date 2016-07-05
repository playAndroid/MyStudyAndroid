package family.safe.wechatimageui;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import family.safe.wechatimageui.adapter.ImageAdapter;
import family.safe.wechatimageui.bean.FolderBean;

/**
 * 仿微信图片相册选择器
 * 1 , 避免内存溢出
 * a , 压缩图片
 * b , 使用缓存(LRUCaChe)
 */
public class MainActivity extends AppCompatActivity {

    private GridView mGridView;
    private List<String> mImages = new ArrayList<>();
    private RelativeLayout mButton;
    private ImageAdapter imageAdapter;

    private TextView mDirName;
    private TextView mDirCount;

    private File mCurrentDir;
    private int mMaxCount;

    private List<FolderBean> folderBeanList = new ArrayList<>();


    private ProgressDialog mProgressDialog;

    private ListImagePathPopuWindow mImagePathPopuWindow;

    private static final int DATA_LOADED = 0x110;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == DATA_LOADED) {
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
                data2View();
                initImagePopupWindow();
            }
        }
    };

    /**
     * 初始化popupWindow
     */
    private void initImagePopupWindow() {
        mImagePathPopuWindow = new ListImagePathPopuWindow(this, folderBeanList);
        mImagePathPopuWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                lightOn();
            }
        });

    }


    /**
     * 绑定数据到View
     */
    private void data2View() {
        if (mCurrentDir == null) {
            Toast.makeText(this, "未扫描到任何图片", Toast.LENGTH_SHORT).show();
            return;
        }

        mImages = Arrays.asList(mCurrentDir.list());
        imageAdapter = new ImageAdapter(this, mImages, mCurrentDir.getPath());
        mGridView.setAdapter(imageAdapter);
        mDirName.setText(mCurrentDir.getName());
        mDirCount.setText(mMaxCount + "");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        initEven();
    }

    private void initEven() {
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImagePathPopuWindow.showAsDropDown(mButton, 0, 0);
                lightOff();

            }
        });
    }

    /**
     * 屏幕变暗
     */
    private void lightOff() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = .3f;
        getWindow().setAttributes(lp);
    }

    private void lightOn() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 1.0f;
        getWindow().setAttributes(lp);
    }

    /**
     * 利用ContentProvider扫描手机中的所有图片
     */
    private void initData() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "当前存储卡不可用", Toast.LENGTH_SHORT).show();
            return;
        }
        mProgressDialog = ProgressDialog.show(this, null, "正在加载...");
        new Thread() {
            @Override
            public void run() {
                Set<String> mDirPaths = new HashSet<String>();
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver cr = MainActivity.this.getContentResolver();
                Cursor cursor = cr.query(mImageUri, null, MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?"
                        , new String[]{"image/jpeg", "image/png"}, MediaStore.Images.Media.DATE_MODIFIED);
                // 只查询jpeg和png的图片
//                Cursor cursor = cr.query(mImageUri, null,MediaStore.Images.Media.MIME_TYPE + "=? or "+ MediaStore.Images.Media.MIME_TYPE + "=?",
//                        new String[] { "image/jpeg","image/png" },MediaStore.Images.Media.DATE_MODIFIED);
                while (cursor.moveToNext()) {
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    File parentFile = new File(path).getParentFile();
                    if (parentFile == null) continue;
                    String dirPath = parentFile.getAbsolutePath();
                    FolderBean folderBean = null;
                    if (mDirPaths.contains(dirPath)) {
                        continue;
                    } else {
                        mDirPaths.add(dirPath);
                        folderBean = new FolderBean();
                        folderBean.setDir(dirPath);
                        folderBean.setFirstImgPath(path);
                    }

                    if (parentFile.list() == null) continue;

                    int picSize = parentFile.list(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String filename) {
                            if (filename.endsWith(".jpg") || filename.endsWith(".jpeg") || filename.endsWith(".png"))
                                return true;
                            return false;
                        }
                    }).length;
                    Log.e("hlk", "picSize" + picSize);
                    folderBean.setCount(picSize);
                    if (picSize > mMaxCount) {
                        mMaxCount = picSize;
                        mCurrentDir = parentFile;
                    }
                    folderBeanList.add(folderBean);
                }
                cursor.close();
                //执行完扫描图片 通知UI线程更新UIl
                handler.sendEmptyMessage(DATA_LOADED);
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
