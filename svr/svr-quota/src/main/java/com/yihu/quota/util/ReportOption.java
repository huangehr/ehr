package com.yihu.quota.util;


import com.github.abel533.echarts.Option;
import com.github.abel533.echarts.axis.CategoryAxis;
import com.github.abel533.echarts.axis.ValueAxis;
import com.github.abel533.echarts.code.AxisType;
import com.github.abel533.echarts.code.Magic;
import com.github.abel533.echarts.code.Tool;
import com.github.abel533.echarts.code.Trigger;
import com.github.abel533.echarts.data.PieData;
import com.github.abel533.echarts.feature.MagicType;
import com.github.abel533.echarts.series.Bar;
import com.github.abel533.echarts.series.Line;
import com.github.abel533.echarts.series.Pie;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by janseny on 2017/8/9.
 */
public class ReportOption {

    /**
     * 折线图
     * @param title  标题
     * @param legend 副标题
     * @param lineName 折线1名称
     * @param datalist 数据集 1
     * @param lineCount  图表中折线数 小于等于2
     * @param lineName 折线2名称
     * @param data2list 当lineCount大于1时 Data2list 不为空
     */
    public Option getLineEchartOption(String title,String legend ,String lineName,List<Map<String, Object>> datalist,int lineCount ,String line2Name,List<Map<String, Object>> data2list) {
        Option option = new Option();
        option.title(title);
//        option.toolbox().show(true).feature(Tool.mark, Tool.dataView, new MagicType(Magic.line, Magic.bar), Tool.restore, Tool.saveAsImage);
//        option.calculable(true);
//        option.tooltip().trigger(Trigger.axis).formatter("Temperature : <br/>{b}km : {c}°C");

        ValueAxis valueAxis = new ValueAxis();
        valueAxis.axisLabel().formatter("摄氏度");
        option.xAxis(valueAxis);

        CategoryAxis categoryAxis = new CategoryAxis();
        categoryAxis.axisLine().onZero(false);
        categoryAxis.type(AxisType.value);
        categoryAxis.axisLabel().formatter("千米");
        categoryAxis.boundaryGap(false);
        categoryAxis.data(0, 10, 20, 30, 40, 50, 60, 70, 80);
        option.yAxis(categoryAxis);

        Line line = new Line();
        line.data();
        line.smooth(true);
        line.name(lineName);
        line.itemStyle().normal().lineStyle().shadowColor("rgba(0,0,0,0.4)");
        line.data(15, -50, -56.5, -46.5, -22.1, -2.5, -27.7, -55.7, -76.5);

        Line line2 = new Line();
        line2.data();
        line2.smooth(true);
        line2.name(line2Name);
        line2.itemStyle().normal().lineStyle().shadowColor("rgba(0,0,0,0.4)");
        if(data2list != null){
            for (Map<String, Object> objectMap : data2list) {
                line2.data(objectMap.get("TOTAL"));
            }
        }
        option.series(line);
        return option;
    }

    /**
     * 柱状图
     * @param title 标题
     * @param legend 副标题
     * @param datalist 数据集1
     * @param data2list 数据集2
     */
    public Option getBarEchartOption(String title,String legend ,String barName,List<Map<String, Object>> datalist,String bar2Name ,List<Map<String, Object>> data2list) {
        //创建Option
        Option option = new Option();
        option.title(title);
        //横轴为值轴
        option.xAxis(new ValueAxis().boundaryGap(0d, 0.01));
        //创建类目轴
        CategoryAxis category = new CategoryAxis();
        //柱状数据
        Bar bar = new Bar(barName);
        //循环数据
        for (Map<String, Object> objectMap : datalist) {
            //设置类目
            category.data(objectMap.get("NAME"));
            //类目对应的柱状图
            bar.data(objectMap.get("TOTAL"));
        }
        if(data2list != null){
            Bar bar2 = new Bar(bar2Name);
            for (Map<String, Object> objectMap : data2list) {
                //设置类目
                category.data(objectMap.get("NAME"));
                //类目对应的柱状图
                bar2.data(objectMap.get("TOTAL"));
            }
        }
        //设置类目轴
        option.yAxis(category);
        //设置数据
        option.series(bar);
        //由于药品名字过长，图表距离左侧距离设置180，关于grid可以看ECharts的官方文档
        option.grid().x(180);
        return option;
    }


    /**
     * 饼状图
     * @param title 标题
     * @param legend 副标题
     * @param datalist 数据集1
     * @param data2list 数据集2
     */
    public Option getPieEchartOption(String title,String legend ,String pieName,List<Map<String, Object>> datalist ,String pie2Name,List<Map<String, Object>> data2list) {
        //创建Option
        Option option = new Option();
        option.title(title).tooltip(Trigger.axis).legend(legend);
        //横轴为值轴
        option.xAxis(new ValueAxis().boundaryGap(0d, 0.01));
        //创建类目轴
        CategoryAxis category = new CategoryAxis();
        //饼图数据
        Pie pie = new Pie(pieName);

        //循环数据
        for (Map<String, Object> objectMap : datalist) {
            //设置类目
            category.data(objectMap.get("NAME"));
            //饼图数据
            pie.data(new PieData(objectMap.get("NAME").toString(), objectMap.get("TOTAL")));
        }
        if(data2list != null){
            Pie pie2 = new Pie(pie2Name);
            for (Map<String, Object> objectMap : data2list) {
                //设置类目
                category.data(objectMap.get("NAME"));
                //饼图数据
                pie2.data(new PieData(objectMap.get("NAME").toString(), objectMap.get("TOTAL")));
            }
        }
        //设置类目轴
        option.yAxis(category);
        //饼图的圆心和半径
        pie.center(900,380).radius(100);
        //设置数据
        option.series(pie);
        //由于药品名字过长，图表距离左侧距离设置180，关于grid可以看ECharts的官方文档
        option.grid().x(180);
        return option;
    }


    /**
     * 柱状+饼状 图
     */
    public Option getBarPieEchartOption(String title,String legend ,List<Map<String, Object>> Datalist) {
        List<Map<String, Object>> list = new ArrayList<>();
        //创建Option
        Option option = new Option();
        option.title("剔除药品").tooltip(Trigger.axis).legend("金额（元）");
        //横轴为值轴
        option.xAxis(new ValueAxis().boundaryGap(0d, 0.01));
        //创建类目轴
        CategoryAxis category = new CategoryAxis();
        //柱状数据
        Bar bar = new Bar("金额（元）");
        //饼图数据
        Pie pie = new Pie("金额（元）");
        //循环数据
        for (Map<String, Object> objectMap : list) {
            //设置类目
            category.data(objectMap.get("NAME"));
            //类目对应的柱状图
            bar.data(objectMap.get("TOTAL"));
            //饼图数据
            pie.data(new PieData(objectMap.get("NAME").toString(), objectMap.get("TOTAL")));
        }
        //设置类目轴
        option.yAxis(category);
        //饼图的圆心和半径
        pie.center(900,380).radius(100);
        //设置数据
        option.series(bar, pie);
        //由于药品名字过长，图表距离左侧距离设置180，关于grid可以看ECharts的官方文档
        option.grid().x(180);
        return option;
    }
}
