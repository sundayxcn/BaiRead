package sunday.app.bairead.Tool;

/**
 * Created by sunday on 2016/12/6.
 */

public class SearchLink{
    /**
     * 章节列表页
     * */
    String webLink;

    /**
     * 站点名称
     * */
    String webName;

    SearchLink(String link){
        webLink = link;
    }

    public String getLink(){
        return webLink;
    }

}
