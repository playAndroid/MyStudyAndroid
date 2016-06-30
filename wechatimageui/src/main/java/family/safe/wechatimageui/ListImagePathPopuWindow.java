package family.safe.wechatimageui;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.BoringLayout;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import java.util.List;

import family.safe.wechatimageui.bean.FolderBean;

/**
 * Created by Administrator on 2016/6/30.
 */
public class ListImagePathPopuWindow extends PopupWindow {
    private int mWidth;
    private int mHeight;
    private List<FolderBean> mDatas;
    private View mContentView;

    public ListImagePathPopuWindow(Context context, List<FolderBean> datas) {
        calWidthAndHeight(context);
        mDatas = datas;
        mContentView = LayoutInflater.from(context).inflate(R.layout.popup_main, null);
        setContentView(mContentView);
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

        initView();
        initEvent();
    }

    private void initEvent() {

    }

    private void initView() {

    }

    private void calWidthAndHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        mWidth = metrics.widthPixels;
        mHeight = (int) (metrics.heightPixels * 0.7);
    }
}
