package sunday.app.bairead.Tool;

import android.os.Environment;

import org.apache.http.util.EncodingUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * eidt Created by sunday on 2016/6/27.
 */
public class FileManager {
    public static final String PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String DIR = "BaiRead";
    //public static final String FileName= PATH+"/"+DIR + "/"+"mainWeb.html";
    public static final String TAG = "snuday";
    public static final String UTF8 = "UTF-8";
    public static final String GB2312 = "gb2312";


    private static FileManager mFileManager;

    private FileManager() {
        createDir();
    }

    public static FileManager getInstance() {
        if (mFileManager == null) {
            mFileManager = new FileManager();
        }
        return mFileManager;
    }


    public boolean createDir() {

        File file = new File(PATH + "/" + DIR);
        if (!file.exists()) {
            file.mkdir();
        }

        return file.exists();
    }

//    public  void writeByte(byte[] bytes){
//            try {
//                FileOutputStream fileOutputStream = new FileOutputStream(FileName,false);
//                fileOutputStream.write(bytes);
//                fileOutputStream.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }finally{
//
//            }
//    }

    public void writeByte(String fileName, byte[] bytes) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(PATH + "/" + DIR + "/" + fileName);
            fileOutputStream.write(bytes);
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

    /**
     * 正则表达式匹配http链接
     */
    public String[] findMatchInFile(String fileName) {
        try {
            FileInputStream fin = new FileInputStream(PATH + "/" + DIR + "/" + fileName);
            int length = fin.available();
            byte[] buffer = new byte[length];
            fin.read(buffer);
            String res = EncodingUtils.getString(buffer, GB2312);
            fin.close();

            String pattern = "'[a-zA-z]+://[^\\s]*";
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(res);
            //int count = m.groupCount();
            String[] bodys = new String[6];
            int i = 0;
            while (m.find()) {
                bodys[i++] = m.group(0).substring(1, m.group(0).length() - 2);
            }
            return bodys;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

}
