package sunday.app.bairead.DataBase;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by sunday on 2016/12/6.
 */

public class BookContentProvider extends ContentProvider {
    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }



    public static class BookDBHelp extends SQLiteOpenHelper{

        public BookDBHelp(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version, null);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            /**
            *
            * */
            String bookDetailSql = "create table bookDetail(_id integer primary key autoincrement," +
                    "name text not null," +
                    "author text "+
                    "chapterLink text not null"+
                    "coverImageLink text," +
                    "coverImageCurrentLink text"+
                    ");";
            /**
             * 书签
             * */
            String bookMarkSql = "create table bookmark(name text not null,markNumber integer not null);";
            db.execSQL(bookDetailSql);
            db.execSQL(bookMarkSql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
