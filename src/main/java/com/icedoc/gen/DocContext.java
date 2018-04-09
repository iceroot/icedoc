package com.icedoc.gen;

import java.util.ArrayList;
import java.util.List;

import com.icedoc.doc.Doc;

public class DocContext {
    private static String basePath = null;
    private static String host = null;
    private static String postType = null;
    private static String title = null;
    private static String version = null;
    private static int index = 1;
    private static List<Doc> docList = new ArrayList<Doc>();

    public static String getBasePath() {
        return basePath;
    }

    public static void setBasePath(String basePath) {
        DocContext.basePath = basePath;
    }

    public static String getHost() {
        return host;
    }

    public static void setHost(String host) {
        DocContext.host = host;
    }

    public static String getPostType() {
        return postType;
    }

    public static void setPostType(String postType) {
        DocContext.postType = postType;
    }

    public static String getTitle() {
        return title;
    }

    public static void setTitle(String title) {
        DocContext.title = title;
    }

    public static String getVersion() {
        return version;
    }

    public static void setVersion(String version) {
        DocContext.version = version;
    }

    public static List<Doc> getDocList() {
        return docList;
    }

    public static void setDocList(List<Doc> docList) {
        DocContext.docList = docList;
    }

    public static int getIndex() {
        return index;
    }

    public static void setIndex(int index) {
        DocContext.index = index;
    }

}
