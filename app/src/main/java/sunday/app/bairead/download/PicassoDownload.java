package sunday.app.bairead.download;

import android.net.Uri;

import com.squareup.picasso.Downloader;
import com.squareup.picasso.NetworkPolicy;

import java.io.IOException;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;

/**
 * Created by zhongfei.sun on 2017/5/18.
 */

public class PicassoDownload implements Downloader {

    private OkHttpClient mClient;

    PicassoDownload(OkHttpClient okHttpClient){
        mClient = okHttpClient;
    }

    @Override
    public Response load(Uri uri, int networkPolicy) throws IOException {
        CacheControl.Builder builder = new CacheControl.Builder();
        if (networkPolicy != 0) {
            if (NetworkPolicy.isOfflineOnly(networkPolicy)) {
                builder.onlyIfCached();
            } else {
                if (!NetworkPolicy.shouldReadFromDiskCache(networkPolicy)) {
                    builder.noCache();
                }
                if (!NetworkPolicy.shouldWriteToDiskCache(networkPolicy)) {
                    builder.noStore();
                }
            }
        }
        Request request = new Request.Builder()
                .cacheControl(builder.build())
                .url(uri.toString())
                .build();
        okhttp3.Response response = mClient.newCall(request).execute();

        int responseCode = response.code();
        if (responseCode >= 300) {
            response.body().close();
            throw new ResponseException(responseCode + " " + response.message(), networkPolicy,
                    responseCode);
        }

        boolean fromCache = response.cacheResponse() != null;

        ResponseBody responseBody = response.body();
        return new Response(responseBody.byteStream(), fromCache, responseBody.contentLength());
    }

    @Override
    public void shutdown() {
        Cache cache = mClient.cache();
        if (cache != null) {
            try {
                cache.close();
            } catch (IOException ignored) {
            }
        }
    }
}
