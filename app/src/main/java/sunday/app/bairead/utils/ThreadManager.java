package sunday.app.bairead.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

/**
 * Created by sunday on 2017/3/13.
 */

public class ThreadManager {
    private static ThreadManager threadManager;
    private ExecutorService executorService;
    private ThreadManager(){
        int cpuCount = Runtime.getRuntime().availableProcessors();
        //留一个专用的thread缓存章节
        if(cpuCount - 1 > 0){
            cpuCount = cpuCount - 1;
        }
        executorService= Executors.newFixedThreadPool(cpuCount);
    }

    public static ThreadManager getInstance(){
        if(threadManager == null){
            threadManager = new ThreadManager();

        }
        return threadManager;
    }

    public void work(Runnable runnable){
        executorService.submit(runnable);
    }


}
