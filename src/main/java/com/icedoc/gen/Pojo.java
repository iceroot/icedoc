package com.icedoc.gen;

import java.util.List;

public class Pojo {
    private String basePath;
    private String className;
    private String packageName;
    private List<Method> methodx;

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public List<Method> getMethodx() {
        return methodx;
    }

    public void setMethodx(List<Method> methodx) {
        this.methodx = methodx;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

}
