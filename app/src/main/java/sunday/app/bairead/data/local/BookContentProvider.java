package sunday.app.bairead.data.local;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import org.apache.http.util.TextUtils;

import sunday.app.bairead.base.BaiReadApplication;

/**
 * Created by sunday on 2016/12/6.
 */

public class BookContentProvider extends ContentProvider {

    private static final String DATABASE_NAME = "baiRead.db";
    private static final int DATABASE_VERSION = 1;

    public static final String AUTHORITY = "sunday.app.bairead.setting";

    public static final String TABLE_BOOK_DETAIL = "bookDetail";
    public  static final String TABLE_BOOK_CHAPTER = "bookChapter";
    public static final String TABLE_BOOK_MARK = "bookMark";
    public static final String PARAMETER_NOTIFY = "notify";

    private BookDBHelp bookDBHelp;

    private static BookContentProvider INSTANCE = null;

    @Override
    public boolean onCreate() {
        if(INSTANCE == null) {
            bookDBHelp = new BookDBHelp(getContext());
            INSTANCE = this;
//            BaiReadApplication application = (BaiReadApplication) getContext().getApplicationContext();
//            application.setBookContentProvider(this);
        }
        return true;
    }


    public static BookContentProvider getInstance(){
        return INSTANCE;
    }



    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SqlArguments args = new SqlArguments(uri, selection, selectionArgs);
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(args.table);

        SQLiteDatabase db = bookDBHelp.getWritableDatabase();
        Cursor result = qb.query(db, projection, args.where, args.args, null, null, sortOrder);
        result.setNotificationUri(getContext().getContentResolver(), uri);

        return result;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        SqlArguments args = new SqlArguments(uri, null, null);
        if (TextUtils.isEmpty(args.where)) {
            return "vnd.android.cursor.dir/" + args.table;
        } else {
            return "vnd.android.cursor.item/" + args.table;
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        SqlArguments args = new SqlArguments(uri);

        SQLiteDatabase db = bookDBHelp.getWritableDatabase();
        ////dbInsertAndCheck(bookDBHelp, db, args.table, null, initialValues);
        final long rowId = db.insert(args.table, null, initialValues);
        if (rowId <= 0) return null;

        uri = ContentUris.withAppendedId(uri, rowId);
        //sendNotify(uri);

        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SqlArguments args = new SqlArguments(uri, selection, selectionArgs);

        SQLiteDatabase db = bookDBHelp.getWritableDatabase();
        int count = db.delete(args.table, args.where, args.args);
        //if (count > 0) sendNotify(uri);

        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = bookDBHelp.getWritableDatabase();
        //String table = TABLE_FAVORITES;
        SqlArguments sqlargs = new SqlArguments(uri, selection, selectionArgs);
        int count = db.update(sqlargs.table, values, sqlargs.where, sqlargs.args);
        return count;
    }


//    private static long dbInsertAndCheck(BookDBHelp helper,
//                                         SQLiteDatabase db, String table, String nullColumnHack, ContentValues values) {
//        if (table.equals(TABLE_BOOK_DETAIL) && values.containsKey(BookSetting.Detail._ID)) {
//            throw new RuntimeException("Error: attempting to add item without specifying an id");
//        }
//        return db.insert(table, nullColumnHack, values);
//    }


    public long generateNewId() {
        return bookDBHelp.generateNewId();
    }


    public static class BookDBHelp extends SQLiteOpenHelper{

        private long mMaxId = -1;

        public BookDBHelp(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);

            // In the case where neither onCreate nor onUpgrade gets called, we read the maxId from
            // the DB here
            if (mMaxId == -1) {
                mMaxId = initializeMaxId(getWritableDatabase());
            }

        }


        // Generates a new ID to use for an object in your database. This method should be only
        // called from the main UI thread. As an exception, we do call it when we call the
        // constructor from the worker thread; however, this doesn't extend until after the
        // constructor is called, and we only pass a reference to LauncherProvider to LauncherApp
        // after that point
        public long generateNewId() {
            if (mMaxId < 0) {
                throw new RuntimeException("Error: max id was not initialized");
            }
            mMaxId += 1;
            return mMaxId;
        }

        private long initializeMaxId(SQLiteDatabase db) {
            Cursor c = db.rawQuery("SELECT MAX(_id) FROM bookDetail", null);

            // get the result
            final int maxIdIndex = 0;
            long id = -1;
            if (c != null && c.moveToNext()) {
                id = c.getLong(maxIdIndex);
            }
            if (c != null) {
                c.close();
            }

            if (id == -1) {
                throw new RuntimeException("Error: could not query max id");
            }

            return id;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            /**
            *
            * */
            String bookDetailSql = "create table bookDetail (_id integer primary key autoincrement," +
                    "name text not null," +
                    "author text,"+
                    "coverImageLink text," +
                    "description text,"+
                    "chapterLatest text,"+
                    "updateTime text,"+
                    "type integer,"+
                    "status integer,"+
                    "topCase integer,"+
                    "other integer"+
                    ");";

            String bookChapterSql = "create table bookChapter (" +
                    "bookId integer not null," +
                    "chapterLink text,"+
                    "chapterCount integer," +
                    "chapterIndex integer,"+
                    "chapterPage integer,"+
                    "current integer"+
                    ");";

            /**
             * 书签
             * */
            String bookMarkSql = "create table bookMark (bookId integer not null,chapterIndex integer not null);";

            db.execSQL(bookDetailSql);
            db.execSQL(bookChapterSql);
            db.execSQL(bookMarkSql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    static class SqlArguments {
        public final String table;
        public final String where;
        public final String[] args;

        SqlArguments(Uri url, String where, String[] args) {
            if (url.getPathSegments().size() == 1) {
                this.table = url.getPathSegments().get(0);
                this.where = where;
                this.args = args;
            } else if (url.getPathSegments().size() != 2) {
                throw new IllegalArgumentException("Invalid URI: " + url);
            } else if (!TextUtils.isEmpty(where)) {
                throw new UnsupportedOperationException("WHERE clause not supported: " + url);
            } else {
                this.table = url.getPathSegments().get(0);
                this.where = "_id=" + ContentUris.parseId(url);
                this.args = null;
            }
        }

        SqlArguments(Uri url) {
            if (url.getPathSegments().size() == 1) {
                table = url.getPathSegments().get(0);
                where = null;
                args = null;
            } else {
                throw new IllegalArgumentException("Invalid URI: " + url);
            }
        }
    }
}
