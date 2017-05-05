package sunday.app.bairead.download;

import java.io.IOException;

import okhttp3.Response;
import sunday.app.bairead.utils.FileManager;

/**
 * Created by zhongfei.sun on 2017/4/25.
 */

public class BookDownService implements IBookDownload{

    @Override
    public boolean downloadHtml(String fileName,String url) throws IOException {
        Response response = OKhttpManager.getInstance().connectUrl(url);
        FileManager.getInstance().saveByte(fileName,response.body().bytes());
        response.body().close();
        return true;
    }
}
