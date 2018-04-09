package com.icedoc.gen;

public class ParamException extends RuntimeException {
    private static final long serialVersionUID = -1867180478796652938L;

    public ParamException(String string) {
        super(string);
    }

    public ParamException(ParamException e) {
        super(e);
    }

}
