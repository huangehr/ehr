package com.yihu.ehr.util.ParmUtils;

import java.util.List;

/**
 * Created by Administrator on 2016/1/28.
 */
public class FieldCondition {
    private String col;
    private String logic;// =, sw, ew, like, >, <
    private List<Object> val;
    private String group;

    public String format(String tableName){
        if(getCol()==null || getCol().equals(""))
            return "";
        String val = ":" + getCol();
        if(getLogic().equals("in"))
            val = "("+val+") ";
        String rs = getCol() + " " + getLogic() + " " + val;
        if(tableName.trim().equals(""))
            return " " + rs;
        return " " +tableName + "." + rs;
    }

    public String format(){
        return format("");
    }

    public String getCol() {
        return col;
    }

    public void setCol(String col) {
        this.col = col;
    }

    public String getLogic() {
        if(logic==null || "".equals(logic))
            return "=";
        return logic;
    }

    public void setLogic(String logic) {
        this.logic = logic;
    }

    public List<Object> getVal() {
        return val;
    }

    public Object formatVal(){
        if(getLogic().equals("sw"))
            return "%"+getVal().get(0);
        if (getLogic().equals("ew"))
            return getVal().get(0)+"%";
        if (getLogic().equals("like"))
            return "%"+getVal().get(0)+"%";
        if(getLogic().equals("in"))
            return getVal();
        return getVal().get(0);
    }

    public void setVal(List<Object> val) {
        this.val = val;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public boolean isGroup(){
        return !(getGroup()==null || "".equals(getGroup()));
    }
}
