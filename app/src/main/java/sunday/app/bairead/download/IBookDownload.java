package sunday.app.bairead.download;

import java.io.IOException;

/**
 * Created by zhongfei.sun on 2017/5/2.
 */

public interface IBookDownload {
    boolean downloadHtml(String fileName,String url) throws IOException;
}
