package sunday.app.bairead.tool;

import java.util.HashMap;


/**
 * Created by sunday on 2017/3/8.
 */

public class NewChapterShow {
    private static NewChapterShow newChapterShow;
    HashMap<Long, Integer> newChapterList = new HashMap<>();

    public static NewChapterShow getInstance() {
        if (newChapterShow == null) {
            newChapterShow = new NewChapterShow();
        }

        return newChapterShow;
    }

    public void clearNewChapterList() {
        newChapterList.clear();
    }

    public void addNewChapter(long nameId, int chapterIndex) {
        newChapterList.put(nameId, chapterIndex);
    }

    public boolean isHaveNewChapter(){
        return newChapterList.size() > 0;
    }

    public void removeNewChapter(long nameId) {
        newChapterList.remove(nameId);
    }

    public boolean isHaveNewChapter(long nameId) {
        return !(newChapterList.get(nameId) == null);
    }
}
