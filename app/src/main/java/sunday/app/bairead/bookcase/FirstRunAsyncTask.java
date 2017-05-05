package sunday.app.bairead.bookcase;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.IOException;

import sunday.app.bairead.data.setting.BookChapter;
import sunday.app.bairead.data.setting.BookDetail;
import sunday.app.bairead.data.setting.BookInfo;
import sunday.app.bairead.parse.ParseBase;
import sunday.app.bairead.parse.ParseBookChapter;
import sunday.app.bairead.parse.ParseBookDetail;
import sunday.app.bairead.utils.FileManager;

/**
 * Created by Administrator on 2017/3/27.
 */

public class FirstRunAsyncTask extends AsyncTask<Void, String, Void> {

    private File baseDir;
    private BookcaseContract.Presenter mPresenter;
    private ParseBase<BookDetail> mDetailParse;
    private ParseBase<BookChapter> mChapterParse;

    public FirstRunAsyncTask(@NonNull File fileDir,@NonNull BookcaseContract.Presenter presenter) {
        baseDir = fileDir;
        mPresenter = presenter;
        mDetailParse = new ParseBookDetail();
        mChapterParse = new ParseBookChapter();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {

        File[] fileDirs = baseDir.listFiles(FileManager.fileFilter);
        int bookCount = fileDirs.length;
        int i = 1;
        try {
            for (File fileDir : fileDirs) {
                String fileName = fileDir.getAbsolutePath() + "/" + BookChapter.FileName;
                File file = new File(fileName);
                if (file.exists()) {
                    BookInfo bookInfo = new BookInfo();
                    bookInfo.bookDetail = mDetailParse.from(fileName).parse();
                    bookInfo.bookChapter = mChapterParse.from(fileName).parse();
                    if (bookInfo.bookDetail != null) {
                        mPresenter.addBook(bookInfo);
                        StringBuffer stringBuffer = new StringBuffer("加载第");
                        stringBuffer
                                .append(i)
                                .append('/')
                                .append(bookCount)
                                .append("本书");
                        publishProgress(stringBuffer.toString());
                    }
                    i++;
                }
            }
        }catch (IOException e){

        }finally {
            return null;
        }

    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);

    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}