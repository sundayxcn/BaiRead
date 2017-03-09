package sunday.app.bairead.download;

import sunday.app.bairead.database.BookInfo;

/**
 * Created by Administrator on 2017/3/5.
 */

public class BookDownLoad {

    public interface DownloadListener{
        void onNewChapter(BookInfo bookInfo);
    }

    private DownloadListener downloadListener;

    public BookDownLoad(DownloadListener downloadListener){
        this.downloadListener = downloadListener;
    }


    public void  updateNewChapter(final BookInfo bookInfo){
        final String bookName = bookInfo.bookDetail.getName();
        final String link = bookInfo.bookChapter.getChapterLink();
        final long id = bookInfo.bookDetail.getId();
        OKhttpManager.getInstance().connectUrl(new BookDownLoadListener(bookName){

            @Override
            public String getLink() {
                return link;
            }

            @Override
            public void onStart() {

            }

            @Override
            public void onFinish(BookInfo bookInfo) {
                if(downloadListener != null){
                    if(bookInfo != null) {
                        bookInfo.bookDetail.setId(id);
                    }
                    downloadListener.onNewChapter(bookInfo);
                }
            }
        });
    }



}
