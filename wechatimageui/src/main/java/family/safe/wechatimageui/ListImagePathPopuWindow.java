package family.safe.wechatimageui;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.BoringLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.List;

import family.safe.wechatimageui.bean.FolderBean;
import family.safe.wechatimageui.utils.ImageLoader;

/**
 * Created by Administrator on 2016/6/30.
 */
public class ListImagePathPopuWindow extends PopupWindow {
    private int mWidth;
    private int mHeight;
    private List<FolderBean> mDatas;
    private View mContentView;
    private ListView mListView;

    public ListImagePathPopuWindow(Context context, List<FolderBean> datas) {
        calWidthAndHeight(context);
        mDatas = datas;
        mContentView = LayoutInflater.from(context).inflate(R.layout.popup_main, null);
        setContentView(mContentView);
        setWidth(mWidth);
        setHeight(mHeight);
        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new BitmapDrawable());
        setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    dismiss();
                    return true;
                }
                return false;
            }
        });
        /**
         * 如果设置BasePopupWindow 基本就是设置布局,设置基本属性,回调两个抽象方法
         */

        initView(context);
        initEvent();
    }

    private void initEvent() {

    }

    private void initView(Context context) {
        mListView = (ListView) mContentView.findViewById(R.id.id_list_dir);
        mListView.setAdapter(new ListImageAdapter(context, mDatas));
    }

    private void calWidthAndHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        mWidth = metrics.widthPixels;
        mHeight = (int) (metrics.heightPixels * 0.7);
    }

    class ListImageAdapter extends ArrayAdapter<FolderBean> {

        private LayoutInflater layoutInflater;
        private List<FolderBean> folderBeens;

        public ListImageAdapter(Context context, List<FolderBean> objects) {
            super(context, 0, objects);
            layoutInflater = LayoutInflater.from(context);
            folderBeens = objects;
            Log.e("hlk", "PopupWindow中数据的长短" + folderBeens.size());
        }

//        getView

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.item_popup_main, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.mDirName = (TextView) convertView.findViewById(R.id.id_tv_dir_name);
                viewHolder.mImageView = (ImageView) convertView.findViewById(R.id.id_dir_item_image);
                viewHolder.mDirCount = (TextView) convertView.findViewById(R.id.id_tv_dir_count);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.mImageView.setImageResource(R.mipmap.pictures_no);
            FolderBean folderBean = getItem(position);
            ImageLoader.getInstance().loadImage(folderBean.getFirstImgPath(), viewHolder.mImageView);
            viewHolder.mDirName.setText(folderBean.getName());
            viewHolder.mDirCount.setText(folderBean.getCount() + "");

            return convertView;
        }

        class ViewHolder {
            ImageView mImageView;
            TextView mDirName;
            TextView mDirCount;
        }
    }
}
