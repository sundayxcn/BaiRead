package sunday.app.bairead.download;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Downloader;
import com.squareup.picasso.Picasso;

import okhttp3.OkHttpClient;

/**
 * Created by zhongfei.sun on 2017/5/17.
 */

public class BookCaseImageCache implements ImageCache{

    private Picasso mPicasso;
    public BookCaseImageCache(Context context){
        OkHttpClient okHttpClient = OKhttpManager.getInstance().getOkHttpClient();
        Downloader picassoDownload = new PicassoDownload(okHttpClient);
        mPicasso = new Picasso.Builder(context).
                downloader(picassoDownload).
                build();
    }

    @Override
    public void loadImage(String url, ImageView v) {
        mPicasso.load(url).into(v);
    }
}
