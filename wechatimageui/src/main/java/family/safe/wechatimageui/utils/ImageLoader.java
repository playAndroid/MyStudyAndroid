package family.safe.wechatimageui.utils;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.LruCache;
import android.widget.ImageView;

import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 图片加载管理类
 * Created by Administrator on 2016/6/16.
 */
public class ImageLoader {
    private static ImageLoader mInstance;

    /**
     * 图片缓存的核心对象
     */
    private LruCache<String, Bitmap> mLruCache;

    /**
     * 线程池
     */
    private ExecutorService mThreadPool;
    /**
     * 默认线程池数量
     */
    private static final int DEAFULT_THREAD_COUNT = 1;
    /**
     * 队列的调度方式
     */
    private Type mType = Type.LIFO;//加载方式
    /**
     * 任务队列
     */
    private LinkedList<Runnable> mTaskQueue;

    /**
     * 后台轮询线程
     */
    private Thread mPoolThread;
    private Handler mPoolThreadHandler;

    /**
     * UI线程中的Handler
     */
    private Handler mUIHandler;

    public enum Type {
        LIFO, FIFO;
    }

    private ImageLoader(int mThreadCount, Type type) {
        init(mThreadCount, type);
    }

    private void init(int mThreadCount, Type type) {
        /**
         * 后台轮询线程
         */
        mPoolThread = new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                mPoolThreadHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        //线程池去取出一个任务进行执行
                    }
                };
                Looper.loop();
            }
        };
        mPoolThread.start();
        /**
         * 获得应用最大可用内存
         */
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int maxCache = maxMemory / 8;
        mLruCache = new LruCache<String, Bitmap>(maxCache) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();//每行的字节数 * 高度 为图片大小
            }
        };

        mThreadPool = Executors.newFixedThreadPool(mThreadCount);
        mTaskQueue = new LinkedList<>();
        mType = type;
    }

    public static ImageLoader getInstance() {
        if (mInstance == null) {
            synchronized (ImageLoader.class) {
                if (mInstance == null) {
                    mInstance = new ImageLoader(DEAFULT_THREAD_COUNT, Type.LIFO);
                }
            }
        }
        return mInstance;
    }

    /**
     * 根据path 设置图片
     *
     * @param path
     * @param imageView
     */
    public void loadImage(String path, ImageView imageView) {
        imageView.setTag(path);//给ImageVIew设置tag 防止图片混乱
        if (mUIHandler == null) {
            mUIHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    //获取得到图片,为ImageView回调设置图片
                    ImageBeanHolder holder = (ImageBeanHolder) msg.obj;
                    ImageView imageView = holder.imageView;
                    Bitmap bm = holder.bitmap;
                    String path = holder.path;
                    if (imageView.getTag().toString().equals(path)) {
                        imageView.setImageBitmap(bm);
                    }
                }
            };
        }
        //根据path从缓存中取出bitmap
        Bitmap bm = getBitmapFromLruCache(path);
        if (bm != null) {
            Message message = Message.obtain();
            ImageBeanHolder imageBeanHolder = new ImageBeanHolder();
            imageBeanHolder.bitmap = bm;
            imageBeanHolder.imageView = imageView;
            imageBeanHolder.path = path;
            message.obj = imageBeanHolder;
            mUIHandler.sendMessage(message);
        }else{
            //如果没有 就加入任务队列
        }
    }

    private Bitmap getBitmapFromLruCache(String key) {

        return mLruCache.get(key);
    }

    private class ImageBeanHolder {
        Bitmap bitmap;
        String path;
        ImageView imageView;
    }
}
