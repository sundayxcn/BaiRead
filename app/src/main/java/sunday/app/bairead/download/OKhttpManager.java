package sunday.app.bairead.download;

import java.io.File;
import java.io.IOException;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import sunday.app.bairead.tool.FileManager;

/**
 * 下载html到本地
 * Created by sunday on 2016/9/14.
 */
public class OKhttpManager{
    private static OKhttpManager mOKhttpManager = new OKhttpManager();
    private OkHttpClient okHttpClient;

    private OKhttpManager(){

    }

    public interface ConnectListener extends Callback{
        void start(String url);
        //void end(String fileName);
    }

    public static OKhttpManager getInstance() {
        return mOKhttpManager;
    }

    public OkHttpClient getOkHttpClient() {
        if(okHttpClient == null){
            long cacheSize = 10 * 1024 * 1024; // 10 MiB

            File cacheDirectory = new File(FileManager.PATH+"/"+"cache");
            Cache cache = new Cache(cacheDirectory, cacheSize);
            okHttpClient = new OkHttpClient.Builder().cache(cache).build();
        }

        return okHttpClient;
    }

    /**
     * 下载网址源码
     * @param connectListener 下载过程的回调
     * */
    public void connectUrl(OKHttpListener connectListener) {
        if(connectListener != null){
            connectListener.onStart();
        }
        //CacheControl cacheControl = new CacheControl.Builder().maxStale(365, TimeUnit.DAYS).build();
        final Request request = new Request.Builder().url(connectListener.getLink())
//                .cacheControl(cacheControl)
//                .addHeader("If-None-Match","\"8051533a2e6cd21:0\"")
//                .addHeader("If-Modified-Since","Wed, 11 Jan 2017 17:14:55 GMT")
                .build();
        Call call = OKhttpManager.getInstance().getOkHttpClient().newCall(request);
        call.enqueue(connectListener);
    }

    /**
     * 下载网址源码
     * @param url 网站地址
     *  阻塞方式
     * */
    public Response connectUrl(final String url) {
//        if(connectListener != null){
//            connectListener.start(url);
//        }
        final Request request = new Request.Builder().url(url).build();
        Call call = OKhttpManager.getInstance().getOkHttpClient().newCall(request);
        //call.enqueue(connectListener);
        try {
            return call.execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }finally {

        }
    }
}
