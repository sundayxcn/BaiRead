package sunday.app.bairead.download;

import android.util.Log;

import java.io.File;
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
        e.printStackTrace();
        onError();
        Log.d("sunday","BookSearchListener--onFailure");
    }


    @Override
    public void onResponse(Call call, Response response){
        try {
            FileManager.writeSearchFile(response.body().bytes());
            HashMap<String, String> resultMap = ParseXml.createParse(ParseSearch.class).from(FileManager.SEARCH_FILE).parse();
            onFinish(resultMap);
        }catch (Exception e){
            e.printStackTrace();
            onFinish(null);
        }finally {
            response.body().close();
        }
    }

    public abstract void onError();

    public abstract void onFinish(HashMap<String,String> resultMap);
}
