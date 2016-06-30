package family.safe.wechatimageui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import family.safe.wechatimageui.R;
import family.safe.wechatimageui.utils.ImageLoader;

/**
 * Created by Administrator on 2016/6/30.
 */
public class ImageAdapter extends BaseAdapter {

    private String mDirPahth;
    private List<String> mImgPaths;
    private LayoutInflater layoutInflater;

    public static final Set<String> selectPath = new HashSet<>();

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
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;
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
        viewHolder.mImage.setColorFilter(null);
        viewHolder.mSelect.setImageResource(R.mipmap.picture_unselected);
        final String imagePaht = mDirPahth + "/" + mImgPaths.get(position);
        ImageLoader.getInstance(3, ImageLoader.Type.LIFO).loadImage(mDirPahth + "/" + mImgPaths.get(position), viewHolder.mImage);


        viewHolder.mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectPath.contains(imagePaht)) {
                    selectPath.remove(imagePaht);
                    viewHolder.mImage.setColorFilter(null);
                    viewHolder.mSelect.setImageResource(R.mipmap.picture_unselected);
                } else {
                    selectPath.add(imagePaht);
                    viewHolder.mImage.setColorFilter(Color.parseColor("#77000000"));
                    viewHolder.mSelect.setImageResource(R.mipmap.pictures_selected);

                }
//                notifyDataSetChanged();
            }
        });
        if (selectPath.contains(imagePaht)) {

        }
        return convertView;
    }

    class ViewHolder {
        ImageView mImage;
        ImageButton mSelect;
    }
}
