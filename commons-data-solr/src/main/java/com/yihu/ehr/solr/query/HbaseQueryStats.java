package com.yihu.ehr.solr.query;

/**
 * ͳ��ʵ��
 * Created by hzp on 2016/04/26.
 */
public class HbaseQueryStats {
    private String name; //ͳ���ֶ���
    private String count; //������
    private String sum; //�ܺ�
    private String avg; //ƽ��ֵ
    private String max;//���ֵ
    private String min;//��Сֵ

    public HbaseQueryStats(String name, String count, String sum, String avg, String max, String min){
        this.name = name;
        this.count = count;
        this.sum=sum;
        this.avg=avg;
        this.max=max;
        this.min=min;
    }

    /**
     * ��ȡͳ���ֶ���
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
