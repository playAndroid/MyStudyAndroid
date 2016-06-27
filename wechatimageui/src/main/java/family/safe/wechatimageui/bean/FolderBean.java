package family.safe.wechatimageui.bean;

/**
 * Created by Administrator on 2016/6/27.
 */
public class FolderBean {
    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
        int indexOf = dir.indexOf("/");
        this.name = dir.substring(indexOf);
    }

    public String getFirstImgPath() {
        return firstImgPath;
    }

    public void setFirstImgPath(String firstImgPath) {
        this.firstImgPath = firstImgPath;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    /**
     * d当前文件夹路径
     */
    private String dir;
    private String firstImgPath;
    private String name;
    private int count;
}
