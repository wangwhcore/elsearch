package com.pojo;

public class I18nText {

    public String getFiled() {
        return filed;
    }

    public void setFiled(String filed) {
        this.filed = filed;
    }

    public String getI18nText() {
        return i18nText;
    }

    public void setI18nText(String i18nText) {
        this.i18nText = i18nText;
    }

    public String getChName() {
        return chName;
    }

    public void setChName(String chName) {
        this.chName = chName;
    }

    public String getExtend1() {
        return extend1;
    }

    public void setExtend1(String extend1) {
        this.extend1 = extend1;
    }

    //字段名
    String filed;
    //国际化词条
    String i18nText;
    //对应的中文名称
    String chName;
    String extend1;

    public String getJsonString(){
        return    "{\"filed\":\""+this.getFiled()+"\",\"i18nText\":\""+this.getI18nText()+"\",\"chName\":\""+this.getChName()+"\"}";

    }
}
