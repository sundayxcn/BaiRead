package sunday.app.bairead.download;

import java.io.IOException;

import okhttp3.Response;

/**
 * Created by zhongfei.sun on 2017/4/25.
 */

public class BookDownService implements IBookDownload{

    private IBookSave mBookSave;

    public BookDownService(IBookSave bookSave){
        mBookSave = bookSave;
    }

    @Override
    public boolean downloadHtml(String fileName,String url) throws IOException {
        Response response = OKhttpManager.getInstance().connectUrl(url);
        mBookSave.saveByte(fileName,response.body().bytes());
        response.body().close();
        return true;
    }
}
