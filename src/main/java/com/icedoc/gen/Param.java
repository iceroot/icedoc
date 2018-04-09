package com.icedoc.gen;

public class Param {
    private String type;
    private String name;
    private String need;
    private String comment;

    public Param() {

    }

    public Param(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public Param(String type, String name, String comment) {
        this.type = type;
        this.name = name;
        this.comment = comment;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getNeed() {
        return need;
    }

    public void setNeed(String need) {
        this.need = need;
    }

}
