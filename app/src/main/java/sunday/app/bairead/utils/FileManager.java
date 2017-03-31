package sunday.app.bairead.utils;

import android.os.Environment;
import android.util.Log;

import org.apache.http.util.EncodingUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * eidt Created by sunday on 2016/6/27.
 */
public class FileManager {
    public static final String DIR = "BaiRead";
    //release版本用此地址
    //public static String PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath()+"/"+DIR;
    public static final String PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+DIR;
    public static final String TEMP_DIR = PATH + "/" + "temp";
    public static final String TEMP_BAIDU_SEARCH_FILE =  TEMP_DIR + "/" + "baiduSearchResult.html";
    public static final String TAG = "snuday";
    public static final String UTF8 = "UTF-8";
    public static final String GB2312 = "gb2312";
    //public static final String SEARCH_FILE = FileManager.PATH + "/" + "searchResult.html";

//    private static FileManager mFileManager;

//    private FileManager() {
//        createDir();
//    }
//
//    public static FileManager getInstance() {
//        if (mFileManager == null) {
//            mFileManager = new FileManager();
//        }
//
//        //boolean s = createDir();
//
//        return mFileManager;
//    }
//
//
       public static String createDir(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return dirPath;
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

    public static String readFile(String fileName){
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



    public static ArrayList<String> readFileByLine(String fileName){
        ArrayList<String> list = new ArrayList<>();
        try{
            File file = new File(fileName);
            if(!file.exists()){
                file.createNewFile();
            }
            InputStream inputStream = new FileInputStream(file);
            InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader buffReader= new BufferedReader(reader);
            String  str;
            while ((str = buffReader.readLine()) != null) {
                list.add(str);
            }
            inputStream.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void writeFileByLine(String fileName,String name) {
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(fileName, true);
            fileWriter.write(name);
            fileWriter.write("\r\n");
            fileWriter.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    public static void writeByte(String fileName, byte[] bytes) throws IOException {
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            fileOutputStream.write(bytes);
            fileOutputStream.close();
    }

    /**
     * 正则表达式匹配http链接
     */
    public static String[] findMatchInFile(String fileName) {
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


    public static void deleteFile(String fileName){
        File file = new File(fileName);
        if (!file.exists())
            return;

        if (file.isFile()) {
            file.delete();
            return;
        }
    }

    /**
     * 清理临时文件夹，不删除文件夹
     * **/
    public static void clearTempFolder(){
        File file = new File(TEMP_DIR);
        clearFolder(file);
    }


    /**
     * 清理文件，不删除文件夹
     * **/
    public static void clearFolder(File file){
        if (!file.exists())
            return;

        if (file.isFile()) {
            file.delete();
            return;
        }

        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            clearFolder(files[i]);
        }
    }

    /**
     * 清理文件，删除文件夹
     * **/
    public static void deleteFolder(File file){
        if (!file.exists())
            return;

        if (file.isFile()) {
            file.delete();
            return;
        }

        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            deleteFolder(files[i]);
        }

        file.delete();
    }


    /**
     * 清理文件，删除文件夹
     * **/
    public static void deleteFolder(String fileName){
        File file = new File(fileName);
        deleteFolder(file);
    }

    public static void writeSearchFile(byte[] bytes) {
        try {
//            File file = new File(TEMP_BAIDU_SEARCH_FILE);
//            if(!file.exists()){
//                file.createNewFile();
//            }
            FileOutputStream fileOutputStream = new FileOutputStream(TEMP_BAIDU_SEARCH_FILE);
            fileOutputStream.write(bytes);
            fileOutputStream.close();
            //Log.e(TAG,"writeByte"+SEARCH_FILE+"  bytes.length=="+bytes.length);
        } catch(IOException e){
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

    public static void deleteAllCahce(){
        File file = new File(PATH);
        file.delete();
    }


    public static final FileFilter fileFilter = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            if (pathname.isDirectory()) {
                if(pathname.getName().contains("cache")) {
                    return false;
                }else if(pathname.getName().contains("temp")){
                    return false;
                }else{
                    return true;
                }
            } else {
                return false;
            }

        }
    };

    public static File[] checkBookCache(){

        final File baseDir = new File(FileManager.PATH);
        if (baseDir.exists()){
            return baseDir.listFiles(fileFilter);
        }else{
            return null;
        }
    }

}
