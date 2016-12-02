package sunday.app.bairead.Tool;

import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import sunday.app.bairead.DataBase.SearchBook;
import sunday.app.bairead.DownLoadManager;
import sunday.app.bairead.SearchManager;

/**
 * 下载html到本地
 * Created by sunday on 2016/9/14.
 */
public class OKhttpManager {
    private static OKhttpManager mOKhttpManager;
    private OkHttpClient okHttpClient = new OkHttpClient();

    private void OKhttpManager() {

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


    public void connectHtml(final String bookName, final DownLoadManager.DownLoadSearchListener searchStatus) {
        searchStatus.start(bookName);
        final Request request = new Request.Builder().url(SearchManager.BAIDU+bookName).build();
        Call call = OKhttpManager.getInstance().getOkHttpClient().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("sunday", "OKHttp onFailure");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //if(response.h)
                String fileName = null;
                FileManager.getInstance().writeBookSearch(bookName, response.body().bytes());
                if (searchStatus != null) {
                    searchStatus.end(fileName);
                }
            }
        });
    }


}
