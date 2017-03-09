package sunday.app.bairead.download;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import sunday.app.bairead.parse.ParseSearch;
import sunday.app.bairead.parse.ParseXml;
import sunday.app.bairead.tool.FileManager;

/**
 * Created by sunday on 2017/3/9.
 */

public abstract class BookSearchListener extends OKHttpListener {
    @Override
    public void onFailure(Call call, IOException e) {

    }

    private String fullName;

    public String getFullName(String bookName,String fileName){
        return createFileDir(bookName) + "/" + fileName;
    }

    public String createFileDir(String bookName){
        return FileManager.createDir(FileManager.PATH +"/"+bookName);
    }

    BookSearchListener(String bookName){
        fullName = getFullName(bookName,BookSearch.FILE_NAME);
    }

    @Override
    public void onResponse(Call call, Response response){
        try {
            FileManager.writeByte(fullName, response.body().bytes());
            response.body().close();
            HashMap<String, String> resultMap = ParseXml.createParse(ParseSearch.class).parse(fullName);
            onFinish(resultMap);
        }catch (Exception e){
            e.printStackTrace();
            onFinish(null);
        }
    }
    public abstract void onFinish(HashMap<String,String> resultMap);
}
