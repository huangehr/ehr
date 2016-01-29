package com.yihu.ehr.util.parm;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/1/28.
 */
public class FieldCondition {
    private String col;
    private String logic;// =, sw, ew, like, >, <
    private List<Object> val;
    private String group;
    private String tableCol;
    public FieldCondition() {

    }
    public FieldCondition(String col, Object val) {
        this.col = col;
        this.addVal(val);
    }

    public FieldCondition(String col, String logic, Object ... vals) {
        this.col = col;
        this.logic = logic;
        this.addVal(vals);
    }

    public FieldCondition(String col, String logic, List<Object> val, String group) {
        this.col = col;
        this.logic = logic;
        this.val = val;
        this.group = group;
    }

    public String format(String modelName, boolean isSql){
        if(getCol()==null || getCol().equals(""))
            return "";
        String val = ":" + getCol();
        if(getLogic().equals("in"))
            val = "("+val+") ";
        String rs = (isSql ? getTableCol() : getCol()) + " " + getLogic() + " " + val;
        if(modelName.trim().equals(""))
            return " " + rs;
        return " " +modelName + "." + rs;
    }

    public String format(){
        return format("", false);
    }

    public String formatSql(){
        return format("", true);
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

    public void addVal(Object ... vals){
        if(this.val==null)
            this.val = new ArrayList<>();
        for(Object val:vals){
            this.val.add(val);
        }
    }

    public String getTableCol() {
        return tableCol;
    }

    public void setTableCol(String tableCol) {
        this.tableCol = tableCol;
    }

    public boolean isValid() {
        return !StringUtils.isEmpty(getTableCol());
    }
}
