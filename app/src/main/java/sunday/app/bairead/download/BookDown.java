package sunday.app.bairead.download;

import java.io.IOException;

import okhttp3.Response;
import retrofit.Retrofit;
import retrofit.GsonConverterFactory;
import retrofit.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.Subscriber;
import sunday.app.bairead.data.setting.BookChapter;
import sunday.app.bairead.data.setting.BookInfo;
import sunday.app.bairead.parse.ParseChapter;
import sunday.app.bairead.parse.ParseDetail;
import sunday.app.bairead.parse.ParseXml;
import sunday.app.bairead.utils.FileManager;

/**
 * Created by zhongfei.sun on 2017/4/25.
 */

public class BookDown {

    private Retrofit retrofit = null;

    private static BookDown INSTANCE = null;

    private BookDownService bookDownService = null;

    public static BookDown getInstance(){
        if(INSTANCE == null){
            INSTANCE = new BookDown();
        }
        return INSTANCE;
    }

    private BookDown(){
    }


    public Observable<BookInfo> updateNewChapter(BookInfo bookInfo){
        return Observable.create(subscriber -> {
            Response response = OKhttpManager.getInstance().connectUrl(bookInfo.bookChapter.getChapterLink());
            BookInfo bookInfo1 = responseToBookInfo(response,bookInfo.bookDetail.getName());
            if(bookInfo1.bookDetail != null){
                subscriber.onNext(bookInfo1);
            }
            subscriber.onCompleted();
        });
    }


    public BookInfo responseToBookInfo(Response response,String bookName){
        if (response != null && response.body() != null) {
            FileManager.createFileDir(bookName);
            String fileName = getFullChapterFileName(bookName);
            boolean writeSuccess = writeResponseBody(fileName, response);
            if (writeSuccess) {
                BookInfo newBookInfo = parseBookInfo(fileName);
                return newBookInfo;
            } else {
                return null;
            }
        }else{
            return null;
        }
    }

    private boolean writeResponseBody(String fileName,Response response){
        try {
            FileManager.writeByte(fileName, response.body().bytes());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }finally {
            response.body().close();
        }
    }

    private BookInfo parseBookInfo(String fileName){
        BookInfo  newBookInfo = new BookInfo();
        newBookInfo.bookDetail = ParseXml.createParse(ParseDetail.class).from(fileName).parse();
        newBookInfo.bookChapter = ParseXml.createParse(ParseChapter.class).from(fileName).parse();
        return newBookInfo;
    }

    public String getFullChapterFileName(String bookName){
        return FileManager.PATH + "/" + bookName + "/" + BookChapter.FileName;
    }
}
