package com.jurtz.marcel.blog_viciousdino.Settings;

public class URLManager {

    // base url
    public static String url = "http://blog.vicious-dino.de/wp-json/wp/v2";

    // all posts (10 newest by wordpress default)
    public static String posts = url + "/posts";

    // newest x posts
    public static String getNewestPosts(int amount) {
        return url + "/posts?filter[posts_per_page]=" + amount + "&fields=id,title";
    }

    // posts for page x
    public static String GetPageUrl(int page) {
        return posts + "?page=" + page;
    }

    // all tags
    public static String tags = url + "/tags";

    // tags for page x
    public static String GetPageUrlTags(int page) {
        return tags + "?page=" + page;
    }

    // posts by tag
    public static String getUrlPostsByTag(int tagID) {
        return posts + "?tags=" + tagID;
    }

}
