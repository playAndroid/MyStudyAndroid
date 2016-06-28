package family.safe.wechatimageui;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import family.safe.wechatimageui.bean.FolderBean;
import family.safe.wechatimageui.utils.ImageLoader;

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

    private static final int DATA_LOADED = 0x110;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == DATA_LOADED) {
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
                data2View();
            }
        }
    };

    /**
     * 绑定数据到View
     */
    private void data2View() {
        if (mCurrentDir == null) {
            Toast.makeText(this, "未扫毛到任何图片", Toast.LENGTH_SHORT).show();
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
//                Cursor cursor = cr.query(mImageUri, null, MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?"
//                        , new String[]{"image/jpeg", "image/png"}, MediaStore.Images.Media.DATE_MODIFIED);
                // 只查询jpeg和png的图片
                Cursor cursor = cr.query(mImageUri, null,
                        MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[] { "image/jpeg", "image/png" },
                        MediaStore.Images.Media.DATE_MODIFIED);
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

    class ImageAdapter extends BaseAdapter {

        private String mDirPahth;
        private List<String> mImgPaths;
        private LayoutInflater layoutInflater;

        public ImageAdapter(Context context, List<String> datas, String dirPath) {
            this.mDirPahth = dirPath;
            this.mImgPaths = datas;
            this.layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mImgPaths.size();
        }

        @Override
        public Object getItem(int position) {
            return mImgPaths.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.item_girdview, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.mImage = (ImageView) convertView.findViewById(R.id.id_item_image);
                viewHolder.mSelect = (ImageButton) convertView.findViewById(R.id.id_item_select);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            //重置状态
            viewHolder.mImage.setImageResource(R.mipmap.pictures_no);
            viewHolder.mSelect.setImageResource(R.mipmap.picture_unselected);

            ImageLoader.getInstance(3, ImageLoader.Type.LIFO).loadImage(mDirPahth + "/" + mImgPaths.get(position), viewHolder.mImage);

            return convertView;
        }

        class ViewHolder {
            ImageView mImage;
            ImageButton mSelect;
        }
    }
}
