package com.yihu.quota.util;


import com.github.abel533.echarts.Option;
import com.github.abel533.echarts.Polar;
import com.github.abel533.echarts.axis.AxisTick;
import com.github.abel533.echarts.axis.CategoryAxis;
import com.github.abel533.echarts.axis.ValueAxis;
import com.github.abel533.echarts.code.*;
import com.github.abel533.echarts.data.Data;
import com.github.abel533.echarts.data.PieData;
import com.github.abel533.echarts.feature.*;
import com.github.abel533.echarts.json.GsonOption;
import com.github.abel533.echarts.series.Bar;
import com.github.abel533.echarts.series.Line;
import com.github.abel533.echarts.series.Pie;
import com.github.abel533.echarts.series.Radar;
import com.github.abel533.echarts.style.ItemStyle;
import com.github.abel533.echarts.style.itemstyle.Normal;
import com.yihu.ehr.model.echarts.ChartDataModel;
import org.apache.commons.lang.StringUtils;

import java.util.*;


/**
 * Created by janseny on 2017/8/9.
 */
public class ReportOption {

    public static int bar = 1;
    public static int line = 2;
    public static int pie = 3;
    public static int radar = 4;

    /**
     * 折线图和柱状  都支持
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
        option.grid().x("50px");  // left
        option.grid().y("40px");  // top
        option.grid().x2("80px"); // right
        option.grid().y2("50px"); // bottom
//        option.grid().containLabel(true);
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

        categoryAxis.data(xData);
        option.xAxis(categoryAxis);


        if(lineNames.size() > 0 && lineDatas.size() >0 && lineNames.size() == lineDatas.size()) {
            for (int i = 0; i < lineDatas.size(); i++) {
                String lineName = lineNames.get(i);
                String type = "";
                if (types.size() > i) {
                    type = types.get(i);
                } else {
                    type = types.get(types.size() > 0 ? types.size() -1 : 0);
                }
                //series
                Line line = new Line();
                line.name(lineName);
                if (type.equals(String.valueOf(ReportOption.bar))) {
                    line.type(SeriesType.bar);
                } else if (type.equals(String.valueOf(ReportOption.line))) {
                    line.type(SeriesType.line);
                }
                line.smooth(true);
//                line.stack("stack");
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

        //series
        Pie pie = new Pie();
        pie.name(pieName);
        pie.type(SeriesType.pie);
        pie.radius("40%");
        if (null != data2list && data2list.size() > 0) {
            pie.center(new String[]{"25%", "50%"});
        } else {
            pie.center(new String[]{"50%", "50%"});
        }
        pie.itemStyle().normal().labelLine().show(false);
        pie.itemStyle().normal().label().show(false);

//        pie.itemStyle().emphasis().shadowBlur(10);
//        pie.itemStyle().emphasis().shadowOffsetX(0);
//        pie.itemStyle().emphasis().shadowColor("rgba(0, 0, 0, 0.5)");

        List<Object> lineNameList = getList(datalist,"NAME");
        List<Object> lineValList = getList(datalist,"TOTAL");
        List<Map<String,Object>> dataList = new ArrayList<Map<String,Object>>();
        for(int i =0 ;i < lineValList.size() ; i++){
            Map<String,Object> map = new HashMap<>();
            map.put("value",lineValList.get(i));
            map.put("name",lineNameList.get(i));
            dataList.add(map);
        }
        List<Object> list = new ArrayList<>(dataList);
        pie.setData(list);
        option.series().add(pie);

        if(data2list != null && data2list.size() > 0){
            Pie pie2 = new Pie();
            pie2.name(pie2Name);
            pie2.type(SeriesType.pie);
            pie2.radius("40%");
            pie2.center(new String[]{"70%", "50%"});
//            pie2.itemStyle().emphasis().shadowBlur(10);
//            pie2.itemStyle().emphasis().shadowOffsetX(0);
//            pie2.itemStyle().emphasis().shadowColor("rgba(0, 0, 0, 0.5)");

            List<Object> lineNameList2 = getList(data2list,"NAME");
            List<Object> lineValList2 = getList(data2list,"TOTAL");
            List<Map<String,Object>> dataList2 = new ArrayList<Map<String,Object>>();
            for(int i =0 ;i < lineValList2.size() ; i++){
                Map<String,Object> map = new HashMap<>();
                map.put("value",lineValList2.get(i));
                map.put("name",lineNameList2.get(i));
                dataList2.add(map);
            }
            List<Object> list2 = new ArrayList<>(dataList2);
            pie2.setData(list2);
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

    public List<Object> getList(List<Map<String, Object>> dataList,String keyName){
        List<Object> returnList = new ArrayList<>();
        if(dataList != null){
            for (Map<String, Object> objectMap : dataList) {
                for(String key:objectMap.keySet()){
                    returnList.add(objectMap.get(keyName));
                    break;
                }
            }
        }
        return  returnList;
    }

    /**
     *雷达图
     *
     * @param title
     * @param dataList
     * @param count 图形边框最大值
     * @return
     */
    public Option getRadarEchartOption(String title, List<Map<String, Object>> dataList, Integer count) {
        Option option = new GsonOption();
        option.title().setText(title);
        option.tooltip().trigger(Trigger.axis);
        option.tooltip().axisPointer().type(PointerType.shadow);

//        option.grid().left("3%");
//        option.grid().right("9%");
//        option.grid().top("22%");
//        option.grid().bottom("3%");
//        option.grid().containLabel(true);

        // radar 极坐标
        Polar polar = new Polar();

        List<Object> radarNameList = new ArrayList<>();
        Object[] radarVal = new Integer[dataList.size()];   // 数据
        for (int i = 0; i < dataList.size(); i++) {
            for(Map.Entry<String, Object> vo : dataList.get(i).entrySet()) {
                polar.indicator(new Data().text(vo.getKey()).max(count));
                radarNameList.add(vo.getKey());
                radarVal[i] = vo.getValue();
            }
        }

        option.polar(polar);
        // series
        Radar radar = new Radar();
        radar.data(new Data().name(title).value(radarVal));
        radar.itemStyle().normal().areaStyle().type("default");
        option.series().add(radar);
        return option;
    }

    /**
     * 嵌套饼图
     *
     * @param title
     * @param dataModel
     * @return
     */
    public Option getNestedPieEchartOption(String title, ChartDataModel dataModel) {
        Option option = new GsonOption();
        // title
        option.title().setText(title);
//        option.title().setSubtext(title);
//        option.title().x("center");

        // tooltip
        option.tooltip().trigger(Trigger.item);

        // series
        List<Pie> pieList = new ArrayList<>();
        pieList = getPie(pieList, dataModel);
        String temp = "20";
        for (int i = 0; i < pieList.size(); i++) {
            Pie pie = pieList.get(i);
            pie.name(title);
            if (i == 0) {
                pie.radius("5%", "20%");
            } else {
                pie.radius((Integer.parseInt(temp) + 5) + "%", (Integer.parseInt(temp) + 20 )+ "%");
                temp = (Integer.parseInt(temp) + 20) + "";
            }
            option.series().add(pie);
        }
        return option;
    }

    public List<Pie> getPie(List<Pie> pieList, ChartDataModel dataModel) {
        Pie pie = new Pie();
        if (null == dataModel.getChildren()) {
            pie = envelopPie(pie, dataModel.getList());
            pieList.add(pie);
            return pieList;
        } else {
            pie = envelopPie(pie, dataModel.getList());
            pieList.add(pie);
            pieList = getPie(pieList, dataModel.getChildren());
            return pieList;
        }
    }

    public Pie envelopPie(Pie pie, List<Map<String, Object>> dataModel) {
        pie.type(SeriesType.pie);
        Normal normal = new Normal();
        normal.label().position("inner");
        normal.labelLine().show(false);
        ItemStyle itemStyle = new ItemStyle();
        itemStyle.normal(normal);
        pie.setItemStyle(itemStyle);
//        pie.label().normal(normal);
//        pie.itemStyle().emphasis().shadowBlur(10);
//        pie.itemStyle().emphasis().shadowOffsetX(0);
//        pie.itemStyle().emphasis().shadowColor("rgba(0, 0, 0, 0.5)");

        List<Object> pieNameList = getList(dataModel,"NAME");
        List<Object> pieValList = getList(dataModel,"TOTAL");
        List<Map<String,Object>> dataList = new ArrayList<Map<String,Object>>();
        for(int i = 0 ;i < pieValList.size(); i++){
            Map<String,Object> map = new HashMap<>();
            map.put("value", pieValList.get(i));
            map.put("name", pieNameList.get(i));
            dataList.add(map);
        }
//        pie.setData(dataList);
        List<Object> list = new ArrayList<>(dataList);
        pie.setData(list);
        return pie;
    }
}
