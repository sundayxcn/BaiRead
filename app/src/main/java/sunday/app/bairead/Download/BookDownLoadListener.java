package sunday.app.bairead.download;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import sunday.app.bairead.database.BookChapter;
import sunday.app.bairead.database.BookInfo;
import sunday.app.bairead.parse.ParseChapter;
import sunday.app.bairead.parse.ParseDetail;
import sunday.app.bairead.parse.ParseXml;
import sunday.app.bairead.tool.FileManager;

/**
 * Created by sunday on 2017/3/9.
 */

public abstract class BookDownLoadListener extends OKHttpListener {


    protected String chapterFile;


    public BookDownLoadListener(String bookName){
        chapterFile = FileManager.PATH + "/" + bookName + "/" + BookChapter.FileName;
    }

    @Override
    public void onFailure(Call call, IOException e) {
            e.printStackTrace();
    }

    @Override
    public void onResponse(Call call, Response response) {
        try{
            if (response != null && response.body() != null) {
                FileManager.writeByte(chapterFile, response.body().bytes());
                response.body().close();
                BookInfo newBookInfo = new BookInfo();
                newBookInfo.bookDetail = ParseXml.createParse(ParseDetail.class).parse(chapterFile);
                newBookInfo.bookChapter = ParseXml.createParse(ParseChapter.class).parse(chapterFile);
                onFinish(newBookInfo);
            }
        }catch (Exception e) {
            onFinish(null);
            e.printStackTrace();
        }
    }


    public abstract void onFinish(BookInfo bookInfo);

}


