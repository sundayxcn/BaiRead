package sunday.app.bairead.data.setting;

import android.content.ContentValues;

import java.util.HashMap;

public class BookDetail extends BookBase {



    /**
     * 玄幻
     * */
    public static final int TYPE_XUANHUAN = 0;
    /**
     * 奇幻
     * */
    public static final int TYPE_QIHUAN = TYPE_XUANHUAN + 1;
    /**
     * 武侠
     * */
    public static final int TYPE_WUXIA = TYPE_QIHUAN + 1;
    /**
     * 仙侠
     * */
    public static final int TYPE_XIANXIA = TYPE_WUXIA + 1;
    /**
     * 都市
     * */
    public static final int TYPE_DUSHI = TYPE_XIANXIA + 1;
    /**
     * 职场
     * */
    public static final int TYPE_ZHICHANG = TYPE_DUSHI + 1;
    /**
     * 军事
     * */
    public static final int TYPE_JUNSHI = TYPE_ZHICHANG + 1;
    /**
     * 历史
     * */
    public static final int TYPE_LISHI = TYPE_JUNSHI + 1;
    /**
     * 游戏
     * */
    public static final int TYPE_YOUXI = TYPE_LISHI + 1;
    /**
     * 体育
     * */
    public static final int TYPE_TIYU = TYPE_YOUXI + 1;
    /**
     * 科幻
     * */
    public static final int TYPE_KEHUAN = TYPE_TIYU + 1;
    /**
     * 灵异
     * */
    public static final int TYPE_LINGYI = TYPE_KEHUAN + 1;
    /**
     * 女生
     * */
    public static final int TYPE_NVSHENG = TYPE_LINGYI + 1;
    /**
     * 二次元
     * */
    public static final int TYPE_ERCIYUAN = TYPE_NVSHENG + 1;

    public static final int TYPE_COUNT = TYPE_ERCIYUAN + 1;

    /**
     * 小说类型以起点的类型作为标准，以中文拼音命名
     * */

    public static final String[] typeArray = new String[]{
                "玄幻",
                "奇幻",
                "武侠",
                "仙侠",
                "都市",
                "职场",
                "军事",
                "历史",
                "游戏",
                "体育",
                "科幻",
                "灵异",
                "女生",
                "二次元",
    };

    /**
     * 书名
     */
    private String name;
    /**
     * 作者
     */
    private String author;
    /**
     * 封面图片链接
     **/
    private String coverImageLink;
    /**
     * 简介
     **/
    private String description;
    /**
     * 最新章节
     */
    private String chapterLatest;
    /**
     * 最后更新时间
     */
    private String updateTime;

    /**
     * 小说分类
     * */
    public int type;


    /**
     * 是否连载中
     * */
    public boolean status;

    /**
     * 书架置顶
     * */

    public boolean topCase;

    /**
     * 预留
     * */
    public int other;



    private BookDetail(Builder builder) {
        this.name = builder.name;
        this.author = builder.author;
        this.coverImageLink = builder.coverImageLink;
        this.description = builder.description;
        this.chapterLatest = builder.chapterLatest;
        this.updateTime = builder.updateTime;
        this.id = builder.id;
        this.type = builder.type;
        this.status = builder.status;
        this.topCase = builder.topCase;
    }

    public void setUpdateTime(String time){
        updateTime = time;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }


    public void setChapterLatest(String title){
        chapterLatest = title;
    }

    public String getChapterLatest() {
        return chapterLatest;
    }

    public String getCoverImageLink() {
        return coverImageLink;
    }

    public String getDescription() {
        return description;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public boolean isTopCase(){
        return topCase;
    }

    public void setTopCase(boolean isTopcase){
        topCase = isTopcase;
    }

    /**
     * @return 连载返回true，完结返回false
     * */
    public boolean isStatus(){
        return status;
    }


    public static boolean getStatus(String status){
        if(status == null){
            return true;
        }else if(status.contains("连载")){
            return true;
        }else{
            return false;
        }

    }


    public int getType(){
        return type;
    }


    public void setType(int type){
        this.type = type;
    }


    private static int getTypeInt(String type){
        int size = typeArray.length;
        for(int i = 0;i< size;i++){
            if(type.equals(typeArray[i])){
                return i;
            }
        }
        return TYPE_DUSHI;
    }


    public void onAddToDatabase(ContentValues values) {
        values.put(BookSetting.Detail.NAME, name);
        values.put(BookSetting.Detail.AUTHOR, author);
        values.put(BookSetting.Detail.COVER_IMAGE_LINK, coverImageLink);
        values.put(BookSetting.Detail.DESCRIPTION, description);
        values.put(BookSetting.Detail.CHAPTER_LATEST, chapterLatest);
        values.put(BookSetting.Detail.UPDATE_TIME, updateTime);
        values.put(BookSetting.Detail.STATUS, status);
        values.put(BookSetting.Detail.TYPE, type);
        //书架置顶标识
        values.put(BookSetting.Detail.TOP_CASE, topCase);

        values.put(BookSetting.Detail.OTHER, other);
    }

    /**
     * Html meta标签对应的我们有用的属性
     */
    public static class Meta {
        final static public String NAME = "og:novel:book_name";
        final static public String AUTHOR = "og:novel:author";
        final static public String DESCRIPTION = "og:description";
        final static public String IMAGE = "og:image";
        final static public String CHAPTER_LATEST = "og:novel:latest_chapter_name";
        final static public String UPDATE_TIME = "og:novel:update_time";
        final static public String CHAPTER_URL = "og:novel:read_url";
        final static public String STATUS = "og:novel:status";
        final static public String TYPE = "og:novel:category";
    }

    public static class Builder {
        private String name;
        private String author;
        private String coverImageLink;
        private String description;
        private String chapterLatest;
        private String updateTime;
        private int type;
        private boolean status;
        private long id;
        private boolean topCase;

        public Builder(HashMap metaMap) {
            name = (String) metaMap.get(Meta.NAME);
            author = (String) metaMap.get(Meta.AUTHOR);
            description = (String) metaMap.get(Meta.DESCRIPTION);
            coverImageLink = (String) metaMap.get(Meta.IMAGE);
            chapterLatest = (String) metaMap.get(Meta.CHAPTER_LATEST);
            updateTime = (String) metaMap.get(Meta.UPDATE_TIME);
            String typeString = (String) metaMap.get(Meta.TYPE);
            type = BookDetail.getTypeInt(typeString);
            String statusString = (String) metaMap.get(Meta.STATUS);
            status = BookDetail.getStatus(statusString);
        }

        public Builder(){

        }

        public Builder setAuthor(String author) {
            this.author = author;
            return this;
        }

        public Builder setChapterLatest(String chapterLatest) {
            this.chapterLatest = chapterLatest;
            return this;
        }

        public Builder setCoverImageLink(String coverImageLink) {
            this.coverImageLink = coverImageLink;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
            return this;
        }

        public Builder setId(long id) {
            this.id = id;
            return this;
        }

        public Builder setStatus(int status){
            this.status = status == 1;
            return this;
        }

        public Builder setType(int type){
            this.type = type;
            return this;
        }

        public Builder setTopCase(int topcase){
            this.topCase = topcase == 1;
            return this;
        }


        public BookDetail build() {
            return new BookDetail(this);
        }

    }

}
