package sunday.app.bairead.download;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by sunday on 2017/3/9.
 */

public abstract class OKHttpListener implements Callback {
    @Override
    public abstract void onFailure(Call call, IOException e);

    @Override
    public abstract void onResponse(Call call, Response response);

    public abstract String getLink();

    public abstract void onStart();
}
