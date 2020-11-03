package Gallery;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.webcamapplication.R;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    public int[] imageArray =  { R.drawable.gallery, R.drawable.gallery_2, R.drawable.gallery_button,
            R.drawable.settings, R.drawable.settings_2,
            R.drawable.start_2, R.drawable.gallery, R.drawable.gallery_2, R.drawable.gallery_button,
            R.drawable.settings, R.drawable.settings_2,
            R.drawable.start_2, R.drawable.gallery, R.drawable.gallery_2, R.drawable.gallery_button,
            R.drawable.settings, R.drawable.settings_2,
            R.drawable.start_2, R.drawable.gallery, R.drawable.gallery_2, R.drawable.gallery_button,
            R.drawable.settings, R.drawable.settings_2,
            R.drawable.start_2, R.drawable.gallery, R.drawable.gallery_2, R.drawable.gallery_button,
            R.drawable.settings, R.drawable.settings_2,
            R.drawable.start_2, R.drawable.gallery, R.drawable.gallery_2, R.drawable.gallery_button,
            R.drawable.settings, R.drawable.settings_2,
            R.drawable.start_2
    };

    public ImageAdapter(Context mContext) {
        this.mContext = mContext;
    }


    public int getCount() {
        return imageArray.length;
    }

    @Override
    public Object getItem(int position) {
        return imageArray[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(mContext);
        imageView.setImageResource(imageArray[position]);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(340, 350));

        return imageView;
    }
}
