package JavaBeans;

import java.io.Serializable;

/**
 * Created by Mohammed on 11/3/2016.
 */

public class Trailer implements Serializable {
    private String name;
    private String site;
    private String size;
    private String url;

    public Trailer(String name, String site, String size, String url){
        this.name = name;
        this.site = site;
        this.size = size;
        this.url = url;
    }

    public String getName(){
        return name;
    }
    public String getSite(){
        return site;
    }
    public String getSize(){
        return size;
    }
    public String getUrl(){
        return url;
    }
}
