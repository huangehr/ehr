package com.yihu.ehr.query.common.model;

/**
 * 统计实体
 * Created by hzp on 2016/04/26.
 */
public class DataStats {
    private String name; //统计字段名
    private String count; //总条数
    private String sum; //总和
    private String avg; //平均值
    private String max;//最大值
    private String min;//最小值

    public DataStats(String name, String count, String sum, String avg, String max, String min){
        this.name = name;
        this.count = count;
        this.sum=sum;
        this.avg=avg;
        this.max=max;
        this.min=min;
    }

    /**
     * 获取统计字段名
     * @return
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getSum() {
        return sum;
    }

    public void setSum(String sum) {
        this.sum = sum;
    }

    public String getAvg() {
        return avg;
    }

    public void setAvg(String avg) {
        this.avg = avg;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }
}
