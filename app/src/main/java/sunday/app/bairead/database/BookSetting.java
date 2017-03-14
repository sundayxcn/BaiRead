package sunday.app.bairead.database;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by sunday on 2016/12/13.
 */

public class BookSetting {

    static final class Detail implements BaseColumns{
        /**
         * The content:// style URL for this table
         */
        static final Uri CONTENT_URI = Uri.parse("content://" +
                BookContentProvider.AUTHORITY + "/" + BookContentProvider.TABLE_BOOK_DETAIL +
                "?" + BookContentProvider.PARAMETER_NOTIFY + "=true");

        /**
         * The content:// style URL for a given row, identified by its id.
         *
         * @param id The row id.
         * @param notify True to send a notification is the content changes.
         *
         * @return The unique content URL for the specified row.
         */
        static Uri getContentUri(long id, boolean notify) {
            return Uri.parse("content://" + BookContentProvider.AUTHORITY +
                    "/" + BookContentProvider.TABLE_BOOK_DETAIL + "/" + id + "?" +
                    BookContentProvider.PARAMETER_NOTIFY + "=" + notify);
        }

        static final String NAME = "name";

        static final String AUTHOR = "author";

        static final String COVER_IMAGE_LINK = "coverImageLink";

        static final String DESCRIPTION = "description";

        static final String CHAPTER_LATEST = "chapterLatest";

        static final String UPDATE_TIME = "updateTime";

        static final String TYPE = "type";
    }


    static final class Chapter{
        /**
         * The content:// style URL for this table
         */
        static final Uri CONTENT_URI = Uri.parse("content://" +
                BookContentProvider.AUTHORITY + "/" + BookContentProvider.TABLE_BOOK_CHAPTER +
                "?" + BookContentProvider.PARAMETER_NOTIFY + "=true");

        /**
         * The content:// style URL for a given row, identified by its id.
         *
         * @param id The row id.
         * @param notify True to send a notification is the content changes.
         *
         * @return The unique content URL for the specified row.
         */
        static Uri getContentUri(long id, boolean notify) {
            return Uri.parse("content://" + BookContentProvider.AUTHORITY +
                    "/" + BookContentProvider.TABLE_BOOK_CHAPTER + "/" + id + "?" +
                    BookContentProvider.PARAMETER_NOTIFY + "=" + notify);
        }
        static final String  ID = "bookId";
        static final String  LINK = "chapterLink";
        static final String  COUNT = "chapterCount";
        static final String  INDEX = "chapterIndex";
        static final String  CURRENT = "current";

    }


    static final class Mark{
        /**
         * The content:// style URL for this table
         */
        static final Uri CONTENT_URI = Uri.parse("content://" +
                BookContentProvider.AUTHORITY + "/" + BookContentProvider.TABLE_BOOK_MARK +
                "?" + BookContentProvider.PARAMETER_NOTIFY + "=true");

        /**
         * The content:// style URL for a given row, identified by its id.
         *
         * @param id The row id.
         * @param notify True to send a notification is the content changes.
         *
         * @return The unique content URL for the specified row.
         */
        static Uri getContentUri(long id, boolean notify) {
            return Uri.parse("content://" + BookContentProvider.AUTHORITY +
                    "/" + BookContentProvider.TABLE_BOOK_MARK + "/" + id + "?" +
                    BookContentProvider.PARAMETER_NOTIFY + "=" + notify);
        }
        static final String  ID = "bookId";
        static final String INDEX = "chapterIndex";
    }
}
