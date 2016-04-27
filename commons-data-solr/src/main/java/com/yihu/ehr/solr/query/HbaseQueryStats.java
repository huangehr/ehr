package com.yihu.ehr.solr.query;

/**
 * 统计实体
 * Created by hzp on 2016/04/26.
 */
public class HbaseQueryStats {
    private String name; //统计字段名
    private String count; //总条数
    private String sum; //总和
    private String avg; //平均值
    private String max;//最大值
    private String min;//最小值

    public HbaseQueryStats(String name, String count, String sum, String avg, String max, String min){
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

    public String getCount() {
        return count;
    }

    public String getSum() {
        return sum;
    }

    public String getAvg() {
        return avg;
    }

    public String getMax() {
        return max;
    }

    public String getMin() {
        return min;
    }

}
