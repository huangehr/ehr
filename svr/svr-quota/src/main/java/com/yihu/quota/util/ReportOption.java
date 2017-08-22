package com.yihu.quota.util;


import com.github.abel533.echarts.Option;
import com.github.abel533.echarts.axis.CategoryAxis;
import com.github.abel533.echarts.axis.ValueAxis;
import com.github.abel533.echarts.code.*;
import com.github.abel533.echarts.data.PieData;
import com.github.abel533.echarts.feature.*;
import com.github.abel533.echarts.json.GsonOption;
import com.github.abel533.echarts.series.Bar;
import com.github.abel533.echarts.series.Line;
import com.github.abel533.echarts.series.Pie;

import java.util.ArrayList;
import java.util.HashMap;
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
    public Option getLineEchartOption(String title, String legend , String lineName, List<Map<String, Object>> datalist, int lineCount, String line2Name, List<Map<String, Object>> data2list) {
        Option option = new GsonOption();
        //title
        option.title().setText(title);
        option.title().setSubtext(title + "(subText)");
        option.title().x("center");
        //tooltip
        option.tooltip().trigger(Trigger.axis);
        //toolbox
        option.toolbox().show(true);
        DataView dataView = new DataView();
        dataView.show(true);
        dataView.readOnly(false);
        option.toolbox().feature().put("dataView", Feature.dataView);
        MagicType magicType = new MagicType();
        magicType.show(true);
        List<String> typeList = new ArrayList<String>();
        typeList.add("pie");
        typeList.add("funnel");
        magicType.setType(typeList);
        option.toolbox().feature().put("magicType", magicType);
        Restore restore = new Restore();
        restore.show(true);
        option.toolbox().feature().put("restore", restore);
        SaveAsImage saveAsImage = new SaveAsImage();
        saveAsImage.show(true);
        option.toolbox().feature().put("saveAsImage", saveAsImage);
        //legend (数据需填充)
        option.legend().orient(Orient.vertical);
        option.legend().left(X.left);
        List<String> legendDataList = new ArrayList<String>();
        legendDataList.add("legend1");
        legendDataList.add("legend2");
        option.legend().data(legendDataList);
        //grid
        option.grid().left("3%");
        option.grid().right("9%");
        option.grid().top("22%");
        option.grid().bottom("3%");
        option.grid().containLabel(true);
        //yAxis
        ValueAxis valueAxis = new ValueAxis();
        valueAxis.name("摄氏度");
        valueAxis.type(AxisType.value);
        valueAxis.axisLabel().formatter("{value}℃");
        option.yAxis(valueAxis);
        //xAxis
        CategoryAxis categoryAxis = new CategoryAxis();
        categoryAxis.name("千米");
        //categoryAxis.axisLine().onZero(false);
        categoryAxis.type(AxisType.category);
        //categoryAxis.axisLabel().formatter("千米");
        categoryAxis.boundaryGap(false);
        categoryAxis.data(0, 10, 20, 30, 40, 50, 60, 70, 80);
        option.xAxis(categoryAxis);
        //series
        Line line = new Line();
        line.name("legend1");
        line.type(SeriesType.line);
        line.smooth(true);
        line.stack("stack");
        line.itemStyle().normal().lineStyle().shadowColor("rgba(0,0,0,0.4)");
        line.data(15, 50, 56.5, 46.5, 22.1, 2.5, 27.7, 55.7, 76.5);
        option.series().add(line);

        Line line2 = new Line();
        line2.name("legend2");
        line2.type(SeriesType.line);
        line2.smooth(true);
        line2.stack("stack2");
        line2.itemStyle().normal().lineStyle().shadowColor("rgba(0,0,0,0.4)");
        /**
        if(data2list != null){
            for (Map<String, Object> objectMap : data2list) {
                line2.data(objectMap.get("TOTAL"));
            }
        }
        */
        line2.data(16, 51, 57.5, 47.5, 23.1, 3.5, 28.7, 56.7, 77.5);
        option.series().add(line2);
        return option;
    }

    /**
     * 柱状图
     * @param title 标题
     * @param legend 副标题
     * @param datalist 数据集1
     * @param data2list 数据集2
     */
    public Option getBarEchartOption(String title,String legend ,String barName,List<Map<String, Object>> datalist, String bar2Name ,List<Map<String, Object>> data2list) {
        Option option = new GsonOption();
        //title
        option.title().setText(title);
        option.title().setSubtext(title + "(subText)");
        option.title().x("center");
        //tooltip
        option.tooltip().trigger(Trigger.axis);
        option.tooltip().axisPointer().type(PointerType.shadow);
        //toolbox
        option.toolbox().show(true);
        DataView dataView = new DataView();
        dataView.show(true);
        dataView.readOnly(false);
        option.toolbox().feature().put("dataView", Feature.dataView);
        MagicType magicType = new MagicType();
        magicType.show(true);
        List<String> typeList = new ArrayList<String>();
        typeList.add("pie");
        typeList.add("funnel");
        magicType.setType(typeList);
        option.toolbox().feature().put("magicType", magicType);
        Restore restore = new Restore();
        restore.show(true);
        option.toolbox().feature().put("restore", restore);
        SaveAsImage saveAsImage = new SaveAsImage();
        saveAsImage.show(true);
        option.toolbox().feature().put("saveAsImage", saveAsImage);
        //legend (数据需填充)
        option.legend().orient(Orient.vertical);
        option.legend().left(X.left);
        List<String> legendDataList = new ArrayList<String>();
        legendDataList.add("legend1");
        legendDataList.add("legend2");
        option.legend().data(legendDataList);
        //grid
        option.grid().left("3%");
        option.grid().right("9%");
        option.grid().top("22%");
        option.grid().bottom("3%");
        option.grid().containLabel(true);
        //yAxis
        ValueAxis valueAxis = new ValueAxis();
        valueAxis.name("摄氏度");
        valueAxis.type(AxisType.value);
        valueAxis.axisLabel().formatter("{value}℃");
        option.yAxis(valueAxis);
        //xAxis
        CategoryAxis categoryAxis = new CategoryAxis();
        categoryAxis.name("千米");
        //categoryAxis.axisLine().onZero(false);
        categoryAxis.type(AxisType.category);
        //categoryAxis.axisLabel().formatter("千米");
        categoryAxis.boundaryGap(false);
        categoryAxis.data(0, 10, 20, 30, 40, 50, 60, 70, 80);
        categoryAxis.splitLine().show(false);
        option.xAxis(categoryAxis);
        //series
        Bar bar = new Bar();
        bar.name("legend1");
        bar.type(SeriesType.bar);
        bar.barWidth(20);
        bar.clickable(true);
        bar.itemStyle().normal().label().show(true);
        bar.itemStyle().normal().label().position(Position.top);
        bar.data(15, 50, 56.5, 46.5, 22.1, 2.5, 27.7, 55.7, 76.5);
        option.series().add(bar);

        Bar bar2 = new Bar();
        bar2.name("legend2");
        bar2.type(SeriesType.bar);
        bar2.barWidth(20);
        bar2.clickable(true);
        bar2.itemStyle().normal().label().show(true);
        bar2.itemStyle().normal().label().position(Position.top);
        /**
         if(data2list != null){
         for (Map<String, Object> objectMap : data2list) {
         line2.data(objectMap.get("TOTAL"));
         }
         }
         */
        bar2.data(16, 51, 57.5, 47.5, 23.1, 3.5, 28.7, 56.7, 77.5);
        option.series().add(bar2);
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
        Option option = new GsonOption();
        //title
        option.title().setText(title);
        option.title().setSubtext(title + "(subText)");
        option.title().x("center");
        //tooltip
        option.tooltip().trigger(Trigger.item);
        option.tooltip().formatter("{a} <br/>{b} : {c} ({d}%)");
        //toolbox
        option.toolbox().show(true);
        DataView dataView = new DataView();
        dataView.show(true);
        dataView.readOnly(false);
        option.toolbox().feature().put("dataView", Feature.dataView);
        MagicType magicType = new MagicType();
        magicType.show(true);
        List<String> typeList = new ArrayList<String>();
        typeList.add("pie");
        typeList.add("funnel");
        magicType.setType(typeList);
        option.toolbox().feature().put("magicType", magicType);
        Restore restore = new Restore();
        restore.show(true);
        option.toolbox().feature().put("restore", restore);
        SaveAsImage saveAsImage = new SaveAsImage();
        saveAsImage.show(true);
        option.toolbox().feature().put("saveAsImage", saveAsImage);
        //legend (数据需填充)
        option.legend().orient(Orient.vertical);
        option.legend().left(X.left);
        List<String> legendDataList = new ArrayList<String>();
        legendDataList.add("legend1");
        legendDataList.add("legend2");
        option.legend().data(legendDataList);
        //grid
        option.grid().left("3%");
        option.grid().right("4%");
        option.grid().bottom("3%");
        option.grid().containLabel(true);
        //series
        Pie pie = new Pie();
        pie.name("legend1");
        pie.type(SeriesType.pie);
        pie.radius("55%");
        pie.center(new String[]{"50", "60"});
        pie.itemStyle().emphasis().shadowBlur(10);
        pie.itemStyle().emphasis().shadowOffsetX(0);
        pie.itemStyle().emphasis().shadowColor("rgba(0, 0, 0, 0.5)");
        List<Map<String, Object>> dataList1 = new ArrayList<Map<String, Object>>();
        for(int i = 0; i < 10; i ++) {
            Map<String, Object> temp = new HashMap<String, Object>();
            temp.put("name", "legend1");
            temp.put("value", i + 5);
            dataList1.add(temp);
        }
        pie.setData(dataList1);
        option.series().add(pie);

        Pie pie2 = new Pie();
        pie2.name("legend2");
        pie2.type(SeriesType.pie);
        pie2.radius("55%");
        pie2.center(new String[]{"50%", "60%"});
        pie2.itemStyle().emphasis().shadowBlur(10);
        pie2.itemStyle().emphasis().shadowOffsetX(0);
        pie2.itemStyle().emphasis().shadowColor("rgba(0, 0, 0, 0.5)");
        /**
         if(data2list != null){
         for (Map<String, Object> objectMap : data2list) {
         line2.data(objectMap.get("TOTAL"));
         }
         }
         */
        List<Map<String, Object>> dataList2 = new ArrayList<Map<String, Object>>();
        for(int i = 0; i < 10; i ++) {
            Map<String, Object> temp = new HashMap<String, Object>();
            temp.put("name", "legend2");
            temp.put("value", i + 10);
            dataList2.add(temp);
        }
        pie2.setData(dataList2);
        option.series().add(pie2);
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
