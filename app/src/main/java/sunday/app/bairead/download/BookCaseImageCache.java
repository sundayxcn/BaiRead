package sunday.app.bairead.download;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by zhongfei.sun on 2017/5/17.
 */

public class BookCaseImageCache implements ImageCache{

    private Picasso mPicasso;
    public BookCaseImageCache(Context context){
        mPicasso = Picasso.with(context);
    }

    @Override
    public void loadImage(String url, ImageView v) {
        mPicasso.load(url).into(v);
    }
}
