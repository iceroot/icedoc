package com.icedoc.gen;

import java.util.List;

import com.icedoc.gen.Param;

public class ReturnPojo {
    private String className;
    private String packageName;
    private List<Param> fields;

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

    public List<Param> getFields() {
        return fields;
    }

    public void setFields(List<Param> fields) {
        this.fields = fields;
    }

    @Override
    public String toString() {
        return "ReturnPojo [className=" + className + ", packageName=" + packageName + ", fields=" + fields + "]";
    }

}
