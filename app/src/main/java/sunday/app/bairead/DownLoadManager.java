package sunday.app.bairead;

/**
 * Created by sunday on 2016/12/1.
 */
public class DownLoadManager {
    public interface DownLoadSearchListener {
        void start(String bookName);

        void end(String fileName);
    }
}
