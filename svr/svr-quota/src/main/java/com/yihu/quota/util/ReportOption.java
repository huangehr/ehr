package com.yihu.quota.util;


import com.github.abel533.echarts.Option;
import com.github.abel533.echarts.axis.AxisTick;
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

    public static int bar = 1;
    public static int line = 2;
    public static int pie = 3;

    /**
     * 折线图
     * @param title  标题
     * @param legend 副标题
     * @param xName x轴名称
     * @param yName yz轴名称
     * @param lineName 折线1名称
     * @param datalist 数据集 1，key x 坐标轴数据列，val y轴坐标数据列
     * @param lineName 折线2名称
     * @param data2list 当lineCount大于1时 Data2list 不为空
     */
    public Option getLineEchartOption(String title, String legend ,String xName,String yName,String lineName, List<Map<String, Object>> datalist, String line2Name, List<Map<String, Object>> data2list) {
        Option option = new GsonOption();
        //title
        option.title().setText(title);
//        option.title().setSubtext(title);//小标题
        option.title().x("center");
        //tooltip
        option.tooltip().trigger(Trigger.axis);
        //toolbox
//        option.toolbox().show(false);
//        DataView dataView = new DataView();
//        dataView.show(true);
//        dataView.readOnly(false);
//        option.toolbox().feature().put("dataView", Feature.dataView);
//        MagicType magicType = new MagicType();
//        magicType.show(true);
//        List<String> typeList = new ArrayList<String>();
//        typeList.add("pie");
//        typeList.add("funnel");
//        magicType.setType(typeList);
//        option.toolbox().feature().put("magicType", magicType);
//        Restore restore = new Restore();
//        restore.show(true);
//        option.toolbox().feature().put("restore", restore);
//        SaveAsImage saveAsImage = new SaveAsImage();
//        saveAsImage.show(true);
//        option.toolbox().feature().put("saveAsImage", saveAsImage);

        //legend (数据需填充)
//        option.legend().orient(Orient.vertical);
//        option.legend().left(X.left);
//        List<String> legendDataList = new ArrayList<String>();
//        legendDataList.add(legend);
//        option.legend().data(legendDataList);
        //grid
        option.grid().left("3%");
        option.grid().right("9%");
        option.grid().top("22%");
        option.grid().bottom("3%");
        option.grid().containLabel(true);
        //yAxis
        ValueAxis valueAxis = new ValueAxis();
        valueAxis.name(yName);
        valueAxis.type(AxisType.value);
//        valueAxis.axisLabel().formatter("℃");//单位
        option.yAxis(valueAxis);

        //xAxis
        CategoryAxis categoryAxis = new CategoryAxis();
        categoryAxis.name(xName);
        categoryAxis.type(AxisType.category);
//        categoryAxis.axisLabel().formatter("千米");//单位
//        categoryAxis.boundaryGap(false);

        List<Object> lineNameList = getList(datalist,"NAME");
        List<Object> lineValList = getList(datalist,"TOTAL");
        Object[] lineName1 = (String[])lineNameList.toArray(new String[lineNameList.size()]);
        categoryAxis.data(lineName1);
        option.xAxis(categoryAxis);

        //series
        Line line = new Line();
        line.name(lineName);
        line.type(SeriesType.line);
        line.smooth(true);
        line.stack("stack");
        line.itemStyle().normal().lineStyle().shadowColor("rgba(0,0,0,0.4)");

        Object[] lineVal1 = (Object[])lineValList.toArray(new Object[lineValList.size()]);
        line.data(lineVal1);
        option.series().add(line);

        if(data2list != null){
            Line line2 = new Line();
            line2.name(line2Name);
            line2.type(SeriesType.line);
            line2.smooth(true);
            line2.stack("stack2");
            line2.itemStyle().normal().lineStyle().shadowColor("rgba(0,0,0,0.4)");

            List<Object> lineValList2 = getList(data2list,"TOTAL");
            line2.data(lineValList2);
            option.series().add(line2);
        }
        return option;
    }

    /**
     * 折线图
     * @param title  标题
     * @param xName x轴名称
     * @param yName yz轴名称
     * @param xData x轴数据集
     * @param lineDatas 数据集
     * @param lineNames 折线名称集合
     */
    public Option getLineEchartOptionMoreChart(String title, String xName, String yName, Object[] xData,  List<List<Object>> lineDatas, List<String> lineNames,List<String> types) {
        Option option = new GsonOption();
        //title
        option.title().setText(title);
//        option.title().setSubtext(title);//小标题
        option.title().x("center");
        //tooltip
        option.tooltip().trigger(Trigger.axis);
        //toolbox
        //grid
        option.grid().left("3%");
        option.grid().right("9%");
        option.grid().top("22%");
        option.grid().bottom("3%");
        option.grid().containLabel(true);
        //yAxis
        ValueAxis valueAxis = new ValueAxis();
        valueAxis.name(yName);
        valueAxis.type(AxisType.value);
//        valueAxis.axisLabel().formatter("℃");//单位
        option.yAxis(valueAxis);

        //xAxis
        CategoryAxis categoryAxis = new CategoryAxis();
        categoryAxis.name(xName);
        categoryAxis.type(AxisType.category);

//        AxisTick axisTick = new AxisTick();
//        axisTick.setType
//        categoryAxis.axisTick(axisTick);


//        categoryAxis.axisLabel().formatter("千米");//单位
        categoryAxis.data(xData);
        option.xAxis(categoryAxis);


        if(lineNames.size() > 0 && lineDatas.size() >0 && lineNames.size() == lineDatas.size()) {
            for (int i = 0; i < lineDatas.size(); i++) {
                String lineName = lineNames.get(i);
                String type = types.get(i);
                //series
                Line line = new Line();
                line.name(lineName);
                if (type.equals(String.valueOf(ReportOption.bar))) {
                    line.type(SeriesType.bar);
                } else if (type.equals(String.valueOf(ReportOption.line))) {
                        line.type(SeriesType.line);
                }
                line.smooth(true);
                line.stack("stack");
                line.itemStyle().normal().lineStyle().shadowColor("rgba(0,0,0,0.4)");

                List<Object> lineValList = lineDatas.get(i);
                Object[] lineVal = (Object[]) lineValList.toArray(new Object[lineValList.size()]);
                line.data(lineVal);
                option.series().add(line);
            }
        }
        return option;
    }

    /**
     * 柱状图
     * @param title 标题
     * @param legend 副标题
     * @param datalist 数据集1
     * @param data2list 数据集2
     */
    public Option getBarEchartOption(String title,String legend ,String xName,String yName ,String barName,List<Map<String, Object>> datalist, String bar2Name ,List<Map<String, Object>> data2list) {
        Option option = new GsonOption();
        //title
        option.title().setText(title);
//        option.title().setSubtext(title );
        option.title().x("center");
        //tooltip
        option.tooltip().trigger(Trigger.axis);
        option.tooltip().axisPointer().type(PointerType.shadow);
        //toolbox
//        option.toolbox().show(false);
//        DataView dataView = new DataView();
//        dataView.show(true);
//        dataView.readOnly(false);
//        option.toolbox().feature().put("dataView", Feature.dataView);
//        MagicType magicType = new MagicType();
//        magicType.show(true);
//        List<String> typeList = new ArrayList<String>();
//        typeList.add("pie");
//        typeList.add("funnel");
//        magicType.setType(typeList);
//        option.toolbox().feature().put("magicType", magicType);
//        Restore restore = new Restore();
//        restore.show(true);
//        option.toolbox().feature().put("restore", restore);
//        SaveAsImage saveAsImage = new SaveAsImage();
//        saveAsImage.show(true);
//        option.toolbox().feature().put("saveAsImage", saveAsImage);

        //legend (数据需填充)
//        option.legend().orient(Orient.vertical);
//        option.legend().left(X.left);
//        List<String> legendDataList = new ArrayList<String>();
//        legendDataList.add(legend);
//        option.legend().data(legendDataList);
        //grid
        option.grid().left("3%");
        option.grid().right("9%");
        option.grid().top("22%");
        option.grid().bottom("3%");
        option.grid().containLabel(true);
        //yAxis
        ValueAxis valueAxis = new ValueAxis();
        valueAxis.name(yName);
        valueAxis.type(AxisType.value);
//        valueAxis.axisLabel().formatter("{value}℃");
        option.yAxis(valueAxis);

        //xAxis
        CategoryAxis categoryAxis = new CategoryAxis();
        categoryAxis.name(xName);
        //categoryAxis.axisLine().onZero(false);
        categoryAxis.type(AxisType.category);
        //categoryAxis.axisLabel().formatter("千米");
//        categoryAxis.boundaryGap(false);

        List<Object> lineNameList = getList(datalist,"NAME");
        List<Object> lineValList = getList(datalist,"TOTAL");
        Object[] nameVal1 = (Object[])lineNameList.toArray(new Object[lineNameList.size()]);
        categoryAxis.data(nameVal1);
//        categoryAxis.splitLine().show(false);
        option.xAxis(categoryAxis);

        //series
        Bar bar = new Bar();
        bar.name(barName);
        bar.type(SeriesType.bar);
        bar.barWidth(20);
        bar.clickable(true);
        bar.itemStyle().normal().label().show(true);
        bar.itemStyle().normal().label().position(Position.top);

        Object[] lineVal1 = (Object[])lineValList.toArray(new Object[lineValList.size()]);
        bar.data(lineVal1);
        option.series().add(bar);

        if(data2list != null){
            Bar bar2 = new Bar();
            bar2.name(bar2Name);
            bar2.type(SeriesType.bar);
            bar2.barWidth(20);
            bar2.clickable(true);
            bar2.itemStyle().normal().label().show(true);
            bar2.itemStyle().normal().label().position(Position.top);

            List<Object> lineValList2 = getList(data2list,"TOTAL");
            Object[] lineVal2 = (Object[])lineValList2.toArray(new Object[lineValList2.size()]);
            bar2.data(lineVal2);
            option.series().add(bar2);
        }

        return option;
    }

    public List<Object> getList(List<Map<String, Object>> dataList,String keyName){
        List<Object> returnList = new ArrayList<>();
        for (Map<String, Object> objectMap : dataList) {
            for(String key:objectMap.keySet()){
                returnList.add(objectMap.get(keyName));
                break;
            }
        }
        return  returnList;
    }


    /**
     * 饼状图
     * @param title 标题
     * @param legend 副标题
     * @param datalist 数据集1
     * @param data2list 数据集2
     */
    public Option getPieEchartOption(String title,String legend ,String pieName,List<Map<String, Object>> datalist ,
                                     String pie2Name,List<Map<String, Object>> data2list) {
        Option option = new GsonOption();
        //title
        option.title().setText(title);
        option.title().setSubtext(title);
        option.title().x("center");

        //tooltip
        option.tooltip().trigger(Trigger.item);
//        option.tooltip().formatter("{a} <br/>{b} : {c} ({d}%)");

        //toolbox
//        option.toolbox().show(false);
//        DataView dataView = new DataView();
//        dataView.show(true);
//        dataView.readOnly(false);
//        option.toolbox().feature().put("dataView", Feature.dataView);
//        MagicType magicType = new MagicType();
//        magicType.show(true);
//        List<String> typeList = new ArrayList<String>();
//        typeList.add("pie");
//        typeList.add("funnel");
//        magicType.setType(typeList);
//        option.toolbox().feature().put("magicType", magicType);
//        Restore restore = new Restore();
//        restore.show(true);
//        option.toolbox().feature().put("restore", restore);
//        SaveAsImage saveAsImage = new SaveAsImage();
//        saveAsImage.show(true);
//        option.toolbox().feature().put("saveAsImage", saveAsImage);
//
//        //legend (数据需填充)
//        option.legend().orient(Orient.vertical);
//        option.legend().left(X.left);
//        List<String> legendDataList = new ArrayList<String>();
//        legendDataList.add(legend);
//        option.legend().data(legendDataList);

        //grid
        option.grid().left("3%");
        option.grid().right("4%");
        option.grid().bottom("3%");
        option.grid().containLabel(true);
        //series
        Pie pie = new Pie();
        pie.name(pieName);
        pie.type(SeriesType.pie);
        pie.radius("55%");
        pie.center(new String[]{"50%", "60%"});
        pie.itemStyle().emphasis().shadowBlur(10);
        pie.itemStyle().emphasis().shadowOffsetX(0);
        pie.itemStyle().emphasis().shadowColor("rgba(0, 0, 0, 0.5)");

        List<Object> lineNameList = getList(datalist,"NAME");
        List<Object> lineValList = getList(datalist,"TOTAL");
        List<Map<String,Object>> dataList = new ArrayList<Map<String,Object>>();
        for(int i =0 ;i < lineValList.size() ; i++){
            Map<String,Object> map = new HashMap<>();
            map.put("value",lineValList.get(i));
            map.put("name",lineNameList.get(i));
            dataList.add(map);
        }

        pie.setData(dataList);
        option.series().add(pie);

        if(data2list != null && data2list.size() > 0){
            Pie pie2 = new Pie();
            pie2.name(pie2Name);
            pie2.type(SeriesType.pie);
            pie2.radius("55%");
            pie2.center(new String[]{"50%", "60%"});
            pie2.itemStyle().emphasis().shadowBlur(10);
            pie2.itemStyle().emphasis().shadowOffsetX(0);
            pie2.itemStyle().emphasis().shadowColor("rgba(0, 0, 0, 0.5)");

            List<Object> lineNameList2 = getList(data2list,"NAME");
            List<Object> lineValList2 = getList(data2list,"TOTAL");
            List<Map<String,Object>> dataList2 = new ArrayList<Map<String,Object>>();
            for(int i =0 ;i < lineValList2.size() ; i++){
                Map<String,Object> map = new HashMap<>();
                map.put("value",lineValList2.get(i));
                map.put("name",lineNameList2.get(i));
                dataList2.add(map);
            }
            pie2.setData(dataList2);
            option.series().add(pie2);
        }
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
