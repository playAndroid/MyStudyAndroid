package family.safe.wechatimageui.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.LruCache;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

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
    private Semaphore mSemaphorePoolHandler = new Semaphore(0);//利用信号量控制UIThread

    private Semaphore mSemaphroeQueue;

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
                        mThreadPool.execute(getTask());
                        try {
                            mSemaphroeQueue.acquire();
                        } catch (InterruptedException e) {

                        }
                    }
                };
                Looper.loop();
                mSemaphorePoolHandler.release();//两个子线程相互使用变量时 要保证变量初始化完毕
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
        mSemaphroeQueue = new Semaphore(mThreadCount);
    }

    private Runnable getTask() {
        if (mType == Type.FIFO) {
            return mTaskQueue.removeFirst();
        } else if (mType == Type.LIFO) {
            return mTaskQueue.removeLast();
        }
        return null;
    }

    public static ImageLoader getInstance(int threadCount, Type type) {
        if (mInstance == null) {
            synchronized (ImageLoader.class) {
                if (mInstance == null) {
                    mInstance = new ImageLoader(threadCount, type);
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
    public void loadImage(final String path, final ImageView imageView) {
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
            refreshBitmap(path, imageView, bm);
        } else {
            //如果没有 就加入任务队列
            addTask(new Runnable() {
                @Override
                public void run() {
                    //加载图片
                    //图片压缩
                    //1,获得图片需要显示的大小
                    ImageSize imageSize = getImageSize(imageView);
                    //2,对图片进行压缩
                    Bitmap bitmap = decodeSampledBitmapFromPath(path, imageSize.width, imageSize.height);
                    //将图片加入缓存
                    addBitmapToLruCaChe(path, bitmap);
                    //刷新UI
                    refreshBitmap(path, imageView, bitmap);
                    mSemaphroeQueue.release();
                }
            });
        }
    }

    private void refreshBitmap(String path, ImageView imageView, Bitmap bm) {
        Message message = Message.obtain();
        ImageBeanHolder imageBeanHolder = new ImageBeanHolder();
        imageBeanHolder.bitmap = bm;
        imageBeanHolder.imageView = imageView;
        imageBeanHolder.path = path;
        message.obj = imageBeanHolder;
        mUIHandler.sendMessage(message);
    }

    private void addBitmapToLruCaChe(String path, Bitmap bitmap) {
        if (getBitmapFromLruCache(path) == null) {
            if (bitmap != null) {
                mLruCache.put(path, bitmap);
            }
        }
    }

    /**
     * 通过路径获得压缩的图片
     *
     * @param path
     * @param width
     * @param height
     * @return
     */
    private Bitmap decodeSampledBitmapFromPath(String path, int width, int height) {
        //对图片进行压缩  通过 options
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = calculateSampleSize(options, width, height);//如果==4 就为原图片的 1/4 像素为 1/16
        //根据SampleSize 的值 再次解析图片 为压缩后的.
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;
    }

    /**
     * 计算SampleSize
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private int calculateSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int width = options.outWidth;
        int height = options.outHeight;
        int sampleSize = 1;
        if (width > reqWidth || height > reqHeight) {
            int widthRatio = Math.round(width * 1.0f / reqWidth);
            int heightRatio = Math.round(height * 1.0f / reqHeight);
            sampleSize = Math.max(widthRatio, heightRatio);
        }
        return sampleSize;
    }

    /**
     * 根据ImageView获得适当的宽和高
     *
     * @param imageView
     * @return
     */
    private ImageSize getImageSize(ImageView imageView) {
        DisplayMetrics displayMetrics = imageView.getContext().getResources().getDisplayMetrics();
        ImageSize imageSize = new ImageSize();
        ViewGroup.LayoutParams lp = imageView.getLayoutParams();
        int width = imageView.getWidth(); //获取ImageView实际宽度
        if (width <= 0) {
            width = lp.width; //获取ImageVIew在Layout中声明的宽度
        }
        if (width <= 0) {
            width = imageView.getMaxWidth(); //获取ImageView最大值
        }
        if (width < 0) {
            //获取屏幕最大宽度
            width = displayMetrics.widthPixels;
        }

        int height = imageView.getHeight(); //获取ImageView实际宽度
        if (height <= 0) {
            height = lp.height; //获取ImageVIew在Layout中声明的宽度
        }
        if (height <= 0) {
            height = imageView.getMaxHeight(); //获取ImageView最大值
        }
        if (height < 0) {
            //获取屏幕最大宽度
            height = displayMetrics.heightPixels;
        }
        //无论如何都要进行压缩
        imageSize.width = width;
        imageSize.height = height;
        return imageSize;
    }

    /**
     * 通过反射获取ImageView属性值
     *
     * @param o
     * @param fieldName
     * @return
     */
    private static int getImageViewFieldValue(Object o, String fieldName) {
        int value = 0;
        try {
            Field field = o.getClass().getField(fieldName);
            int fieldValue = field.getInt(o);
            if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
                value = fieldValue;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return value;
    }


    private synchronized void addTask(Runnable runnable) {
        mTaskQueue.add(runnable);
        if (mPoolThreadHandler == null) {
            try {
                mSemaphorePoolHandler.acquire();
            } catch (InterruptedException e) {
            }
        }
        mPoolThreadHandler.sendEmptyMessage(0x110);
    }

    private Bitmap getBitmapFromLruCache(String key) {

        return mLruCache.get(key);
    }

    private class ImageBeanHolder {
        Bitmap bitmap;
        String path;
        ImageView imageView;
    }

    private class ImageSize {
        int width;
        int height;
    }
}
