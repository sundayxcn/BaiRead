package sunday.app.bairead.download;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Response;
import sunday.app.bairead.bookRead.*;
import sunday.app.bairead.database.BookChapter;
import sunday.app.bairead.database.BookInfo;
import sunday.app.bairead.parse.ParseChapter;
import sunday.app.bairead.parse.ParseChapterText;
import sunday.app.bairead.parse.ParseDetail;
import sunday.app.bairead.parse.ParseSearch;
import sunday.app.bairead.parse.ParseXml;
import sunday.app.bairead.utils.FileManager;

/**
 * Created by Administrator on 2017/3/5.
 */

public class BookDownLoad {

    public static final String TEMP_FILE_NAME = FileManager.PATH + "/" + "tempChapter.html";
    public static final String SEARCH_FILE = FileManager.PATH + "/" + "searchResult.html";
    public static final int ERROR_IOException = 78;
    public static final int ERROR_NetworkFailed = ERROR_IOException + 1;
    public static final int ERROR_SocketTimeOut = ERROR_NetworkFailed + 1;
    public static final int ERROR_ParseHtmlFailed = ERROR_SocketTimeOut + 1;


    public static class SearchResult{
        /**
         * 书名
         * */
        public String title;
        /**
         * 作者
         * */
        public String author;
        /**
         * 小说分类
         * */
        public String type;
        /**
         * 是否连载中
         * */
        public boolean serialise;
        /**
         * 链接
         * */
        public String link;
        /**
         * 更新时间
         * */
        public String updateTime;
    }



    public interface DownloadListener<T>{
        /**
         * response 存放的位置
         * */
        String getFileName();

        String getLink();

        long getId();

        void onStart();

        void onError(int errorCode);

        void onResult(T result);
    }


    private static class BookDownLoadHolder {
        private static final BookDownLoad sInstance = new BookDownLoad();
    }

    public static BookDownLoad getInstance(){
        return BookDownLoadHolder.sInstance;
    }


    public  static String createFileDir(String bookName){
        return FileManager.createDir(FileManager.PATH +"/"+bookName);
    }

    public static String getFullChapterFileName(String bookName){
        return FileManager.PATH + "/" + bookName + "/" + BookChapter.FileName;
    }

    public void  updateBookInfoAsync(final DownloadListener<BookInfo> downloadListener){
        if(downloadListener == null) throw new NullPointerException("BookDownLoad 缺少不为空的回调方法");
        downloadListener.onStart();
        OKhttpManager.getInstance().connectUrl(new OKHttpListener() {
            @Override
            public void onFailure(Call call, IOException e) {
                downloadListener.onError(ERROR_SocketTimeOut);
            }

            @Override
            public void onResponse(Call call, Response response) {
                int errorCode = ERROR_NetworkFailed;
                if (response != null && response.body() != null) {
                    String fileName = downloadListener.getFileName();
                    boolean writeSuccess = writeResponseBody(fileName, response);
                    if (writeSuccess) {
                        BookInfo newBookInfo = parseBookInfo(fileName);
                        if(newBookInfo.bookDetail != null){
                            newBookInfo.bookDetail.setId(getId());
                            downloadListener.onResult(newBookInfo);
                            return;
                        }else{
                            errorCode = ERROR_ParseHtmlFailed;
                        }
                    } else {
                        errorCode = ERROR_IOException;
                    }
                }

                downloadListener.onError(errorCode);

            }

            @Override
            public String getLink() {
                return downloadListener.getLink();
            }

            private long getId(){
                return downloadListener.getId();
            }
        });
    }

    public BookInfo updateBookInfo(BookInfo bookInfo,String fileName){
        String bookName = bookInfo.bookDetail.getName();
        createFileDir(bookName);
        Response response = OKhttpManager.getInstance().connectUrl(bookInfo.bookChapter.getChapterLink());
        BookInfo newBookInfo = null;
        if(response != null && response.body() != null){
            String name = fileName == null ?  TEMP_CHAPTER_NAME : fileName;
            if(writeResponseBody(name,response)) {
                newBookInfo = parseBookInfo(name);
            }
        }
        return newBookInfo;
    }

    static final String TEMP_TEXT_NAME = FileManager.PATH + "/" +"tempChapterText.html";
    static final String TEMP_CHAPTER_NAME = FileManager.PATH + "/" +"tempChapter.html";

    public String updateBookChapterText(String url,String fileName) {
        Response response = OKhttpManager.getInstance().connectUrl(url);
        if (response != null && response.body() != null) {
            try {
                String name = fileName == null ?  TEMP_TEXT_NAME : fileName;
                FileManager.writeByte(name, response.body().bytes());
                return parseBookChapterText(name);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }finally {
                response.body().close();
            }
        }

        return null;
    }

    public String updateBookChapterTextAsync(final DownloadListener<String> downloadListener) {
        OKhttpManager.getInstance().connectUrl(new OKHttpListener() {
            @Override
            public void onFailure(Call call, IOException e) {
                downloadListener.onError(ERROR_NetworkFailed);
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (response != null && response.body() != null) {
                    try {
                        String name = downloadListener.getFileName();
                        FileManager.writeByte(name, response.body().bytes());
                        String text = parseBookChapterText(name);
                        downloadListener.onResult(text);
                    } catch (Exception e) {
                        e.printStackTrace();
                        downloadListener.onError(ERROR_ParseHtmlFailed);
                    }finally {
                        response.body().close();
                    }
                }
            }

            @Override
            public String getLink() {
                return downloadListener.getLink();
            }
        });




        return null;
    }

    public void updateSearchAsync(final DownloadListener<ArrayList<SearchResult>> downloadSearchListener){
        downloadSearchListener.onStart();
        OKhttpManager.getInstance().connectUrl(new OKHttpListener() {
            @Override
            public void onFailure(Call call, IOException e) {
                downloadSearchListener.onError(ERROR_SocketTimeOut);
            }

            @Override
            public void onResponse(Call call, Response response) {
                int errorCode = ERROR_NetworkFailed;
                if (response != null && response.body() != null) {
                    String fileName = downloadSearchListener.getFileName();
                    boolean writeSuccess = writeResponseBody(fileName, response);
                    if (writeSuccess) {
                        ArrayList<SearchResult> searchResults = parseBookSearch(fileName);
                        if(searchResults.size() > 0){
                            downloadSearchListener.onResult(searchResults);
                            return;
                        }else{
                            errorCode = ERROR_ParseHtmlFailed;
                        }
                    } else {
                        errorCode = ERROR_IOException;
                    }
                }
                downloadSearchListener.onError(errorCode);
            }

            @Override
            public String getLink() {
                return downloadSearchListener.getLink();
            }
        });
    }



    private String parseBookChapterText(String fileName){
        String text = ParseXml.createParse(ParseChapterText.class).from(fileName).parse();
        return text;
    }

    private BookInfo parseBookInfo(String fileName){
        BookInfo  newBookInfo = new BookInfo();
        newBookInfo.bookDetail = ParseXml.createParse(ParseDetail.class).from(fileName).parse();
        newBookInfo.bookChapter = ParseXml.createParse(ParseChapter.class).from(fileName).parse();
        return newBookInfo;
    }

    private ArrayList<SearchResult> parseBookSearch(String fileName){
        ArrayList<SearchResult> searchResultList = ParseXml.createParse(ParseSearch.class).from(fileName, ParseXml.Charset.UTF8).parse();
        return searchResultList;
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



}
