package sunday.app.bairead.Tool;

import android.os.Environment;
import android.util.Log;

import org.apache.http.util.EncodingUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * eidt Created by sunday on 2016/6/27.
 */
public class FileManager {
    public static final String DIR = "BaiRead";
    public static final String PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+DIR;
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

        File file = new File(PATH);
        if (!file.exists()) {
            file.mkdirs();
        }

        return file.exists();
    }

    public boolean createDir(String dirPath) {

        File file = new File(PATH +"/"+ dirPath);
        if (!file.exists()) {
            file.mkdirs();
        }

        return file.exists();
    }


//    public void writeBookSearch(String fileName, byte[] bytes){
//        String fileDirPath = PATH + "/" + DIR + "/" + fileName;
//        File fileDir = new File(fileDirPath);
//        if(!fileDir.exists()){
//            fileDir.mkdirs();
//        }
//
//
//        writeByte(fileDirPath+"/"+SearchManager.SEARCH_TXT,bytes);
//    }


    public ArrayList<String> readFileByLine(String fileName){
        ArrayList<String> list = new ArrayList<>();
        String fileDirPath = PATH + "/" + fileName;
        try{
            File file = new File(fileDirPath);
            InputStream inputStream = new FileInputStream(file);
            InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader buffReader= new BufferedReader(reader);
            String  str;
            while ((str = buffReader .readLine()) != null) {
                list.add(str);
            }
            inputStream.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public String readFile(String fileName){
        String fileDirPath = PATH + "/" + fileName;
        String string = null;

        try{
            File file = new File(fileDirPath);
            InputStream inputStream = new FileInputStream(file);
            InputStreamReader reader = new InputStreamReader(inputStream,"GBK");
            BufferedReader buffReader= new BufferedReader(reader);
            String  str = null;
            while ((str = buffReader .readLine()) != null) {
                string = string + str;
            }
            inputStream.close();
        }catch (Exception e) {
            e.printStackTrace();
        }

//        try {
//            String ss = new String(string.getBytes(),"UTF-8");
//            string = ss;
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }

        return string;
    }


    public void writeFileByLine(String fileName,String name) throws IOException {
        String fileDirPath = PATH + "/" + fileName;
        File file = new File(fileDirPath);

        if(!file.exists()){
            file.createNewFile();
        }

        FileWriter fileWriter = new FileWriter(fileDirPath, true);
        fileWriter.write(name);
        fileWriter.write("\r\n");
        fileWriter.close();
    }


    public void writeByte(String fileName, byte[] bytes) {

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            fileOutputStream.write(bytes);
            fileOutputStream.close();
            Log.e(TAG,"writeByte"+fileName+"  bytes.length=="+bytes.length);
        } catch(IOException e){
            e.printStackTrace();
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
