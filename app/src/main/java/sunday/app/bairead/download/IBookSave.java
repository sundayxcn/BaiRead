package sunday.app.bairead.download;

import java.io.IOException;

/**
 * Created by zhongfei.sun on 2017/5/3.
 */

public interface IBookSave {
    void saveByte(String fileName, byte[] bytes)throws IOException;
}
