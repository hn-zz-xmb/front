package com.meishi.front.vo;

/**
 * Created by develop on 15-9-4.
 */
public class ComItemVo {

    public ComItemVo(String name ,String value){
        this.name=name;
        this.value=value;
    }


    private String name;
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
