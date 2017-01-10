package com.yihu.ehr.query;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.2.1
 */
public class FieldCondition {
    private String col;       //过滤字段 ，不可为空
    private String logic;    //过滤方式，默认为=;   =, sw, ew, like, >, <， between， >=, <=
    private List<Object> val;//过滤值， 值为空则不过滤
    private String group;   //分组，  多个过滤器中的group相同时  用or连接
    private String tableCol;//数据库字段， 初始化根据实体自动设置， user设置无效

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

    /**
     * 格式化过滤条件
     * @param modelName 视图名
     * @param isSql true：返回sql形式， false：返回jpa形式
     * @return
     */
    public String format(String modelName, boolean isSql){
        if(getCol()==null || getCol().equals("") || getVal()==null || getVal().size()==0)
            return "";
        String val = getValMapping();
        if(val==null)
            return "";
        String rs = (isSql ? getTableCol() : getCol()) + " " + getLogic() + " " + val;
        if(modelName.trim().equals(""))
            return " " + rs;
        return " " +modelName + "." + rs;
    }

    /**
     * 格式化过滤条件
     * @return 返回jpa形式
     */
    public String format(){
        return format("", false);
    }

    /**
     * 格式化过滤条件
     * @return 返回sql形式
     */
    public String formatSql(){
        return format("", true);
    }

    /**
     * 判断是否存在分组信息
     * @return
     */
    public boolean isGroup(){
        return !(getGroup()==null || "".equals(getGroup()));
    }

    /**
     * 添加值
     * @param vals
     */
    public void addVal(Object ... vals){
        if(this.val==null)
            this.val = new ArrayList<>();
        for(Object val:vals){
            this.val.add(val);
        }
    }

    /**
     * 判断数据表是否包含有该过滤字段
     * @return
     */
    public boolean isValid() {
        return !StringUtils.isEmpty(getTableCol()) && !(getVal()==null || getVal().size()==0)
                 && !(getCol()==null || getCol().equals("")) && isLogicValid();
    }

    /**
     * 判断查询方式是否符合规范
     * @return
     */
    public boolean isLogicValid(){
        String logic = getLogic();
        if(logic.equals("=") || logic.equals("like") || logic.equals("sw") || logic.equals("ew") ||
                logic.equals("<") || logic.equals(">") || logic.equals(">=") || logic.equals("<=") ||
                    logic.equals("in") || logic.equals("not in") || logic.equals("between"))
            return true;
        return false;
    }
    /**
     * 获取占位符
     * @return
     */
    private String getValMapping(){
        String logic = getLogic();
        String val = ":" + getCol();
        if(logic.equals("in") || logic.equals("not in"))
            return  "("+val+") ";
        if(logic.equals("between"))
            return val + "1 and " +val+"2 ";
        if(logic.equals("=") || logic.equals("like") || logic.equals("sw") || logic.equals("ew") ||
                logic.equals("<") || logic.equals(">") || logic.equals(">=") || logic.equals("<=")){
            return val;
        }
        return null;
    }

    /**
     * 格式化 值， 不支持between
     * between形式： 调用getVal(), 获取值，  占位符为 between col + "1" and  col + "2"
     * @return
     */
    public Object formatVal(){
        if(getLogic().equals("sw"))
            return "%"+getVal().get(0);
        if (getLogic().equals("ew"))
            return getVal().get(0)+"%";
        if (getLogic().equals("like"))
            return "%"+getVal().get(0)+"%";
        if(getLogic().equals("in") || getLogic().equals("not in"))
            return getVal();
        return getVal().get(0);
    }


    /************************************************************************************/
    /***************            getter  &  setter                            ************/
    /***************                                                         ************/
    /************************************************************************************/
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

    public void setVal(List<Object> val) {
        this.val = val;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getTableCol() {
        return tableCol;
    }

    public void setTableCol(String tableCol) {
        this.tableCol = tableCol;
    }

}
