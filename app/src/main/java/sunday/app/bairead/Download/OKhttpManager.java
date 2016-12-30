package sunday.app.bairead.Download;

import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import sunday.app.bairead.Tool.FileManager;

/**
 * 下载html到本地
 * Created by sunday on 2016/9/14.
 */
public class OKhttpManager{
    private static OKhttpManager mOKhttpManager;
    private OkHttpClient okHttpClient = new OkHttpClient();

    private void OKhttpManager() {

    }


    public interface ConnectListener extends Callback{
        void start(String url);
        void end(String fileName);
    }

    public static OKhttpManager getInstance() {
        if (mOKhttpManager == null) {
            mOKhttpManager = new OKhttpManager();

        }
        return mOKhttpManager;
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    /**
     * 下载网址源码
     * @param url 网站地址
     * @param connectListener 下载过程的回调
     * */
    public void connectUrl(final String url,final ConnectListener connectListener) {
        if(connectListener != null){
            connectListener.start(url);
        }
        final Request request = new Request.Builder().url(url).build();
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
