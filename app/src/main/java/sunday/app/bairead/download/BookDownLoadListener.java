package sunday.app.bairead.download;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;
import sunday.app.bairead.database.BookChapter;
import sunday.app.bairead.database.BookInfo;
import sunday.app.bairead.parse.ParseChapter;
import sunday.app.bairead.parse.ParseDetail;
import sunday.app.bairead.parse.ParseXml;
import sunday.app.bairead.tool.FileManager;

import static sunday.app.bairead.download.BookDownLoadListener.Type.ParseDownLoad;

/**
 * Created by sunday on 2017/3/9.
 */

public abstract class BookDownLoadListener extends OKHttpListener {


    protected String chapterFile;

    private Type type;

    public enum Type{
        ParseSearch,
        ParseDownLoad,
    }

    public BookDownLoadListener(String bookName,Type type){
        this.type = type;
        if(type == ParseDownLoad){
            createFileDir(bookName);
            chapterFile = getFullChapterFileName(bookName);

        }
    }

    @Override
    public void onFailure(Call call, IOException e) {
        e.printStackTrace();
    }

    public static final String TEMP_FILE_NAME = FileManager.PATH + "/" + "tempChapter.html";

    @Override
    public void onResponse(Call call, Response response) {
        try{
            if (response != null && response.body() != null) {
                BookInfo newBookInfo = new BookInfo();
                String fileName = type == ParseDownLoad ? chapterFile : TEMP_FILE_NAME;
                FileManager.writeByte(fileName, response.body().bytes());
                newBookInfo.bookDetail = ParseXml.createParse(ParseDetail.class).from(fileName).parse();
                newBookInfo.bookChapter = ParseXml.createParse(ParseChapter.class).from(fileName).parse();

                onFinish(newBookInfo);
            }
        }catch (Exception e) {
            onFinish(null);
            e.printStackTrace();
        }finally {
            response.body().close();
        }
    }

    private String createFileDir(String bookName){
        return FileManager.createDir(FileManager.PATH +"/"+bookName);
    }

    public static String getFullChapterFileName(String bookName){
        return FileManager.PATH + "/" + bookName + "/" + BookChapter.FileName;
    }


    public abstract void onFinish(BookInfo bookInfo);
    public abstract void onError();
}


