package sunday.app.bairead.download;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;
import sunday.app.bairead.database.BookChapter;
import sunday.app.bairead.database.BookInfo;
import sunday.app.bairead.parse.ParseChapter;
import sunday.app.bairead.parse.ParseDetail;
import sunday.app.bairead.parse.ParseXml;
import sunday.app.bairead.tool.FileManager;

/**
 * Created by Administrator on 2017/3/5.
 */

public class BookDownload {

    public interface DownloadListener{
        void onNewChapter(BookInfo bookInfo);
    }

    private DownloadListener downloadListener;

    public BookDownload(DownloadListener downloadListener){
        this.downloadListener = downloadListener;
    }

    public void  updateNewChapter(final BookInfo bookInfo){
        String url = bookInfo.bookChapter.getChapterLink();
        final long id = bookInfo.bookDetail.getId();
        OKhttpManager.getInstance().connectUrl(url, new OKhttpManager.ConnectListener() {
            @Override
            public void start(String url) {

            }

            @Override
            public void onFailure(Call call, IOException e) {
                if(downloadListener != null){
                    downloadListener.onNewChapter(null);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null && response.body() != null) {
                    final String chapterFile = FileManager.PATH + "/" + bookInfo.bookDetail.getName() + "/" + BookChapter.FileName;
                    FileManager.writeByte(chapterFile, response.body().bytes());
                    response.body().close();
                    BookInfo newBookInfo = new BookInfo();
                    newBookInfo.bookDetail = ParseXml.createParse(ParseDetail.class).parse(chapterFile);
                    newBookInfo.bookDetail.setId(id);
                    newBookInfo.bookChapter = ParseXml.createParse(ParseChapter.class).parse(chapterFile);
                    if(downloadListener != null){
                        downloadListener.onNewChapter(newBookInfo);
                    }

                }
            }
        });
    }



}
