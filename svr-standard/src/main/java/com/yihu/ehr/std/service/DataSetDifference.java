package com.yihu.ehr.std.service;

import javafx.util.Pair;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

/**
 * 数据集差异对象. 仅比较数据集本身的属性与其包含的数据元.
 *
 * @author Sand
 * @version 1.0
 * @created 30-6月-2015 16:19:05
 */
public class DataSetDifference {

    Map<String, PropertyDifference> dataSetPropertyDiff;     // 数据集属性差异集合
    Map<Long, Pair<MetaData, MetaData>> changedMetaDataList;
    Set<MetaData> newMetaDataList;
    Set<MetaData> removedMetaDataList;

    Set<DataSet> newDataSetList;
    Set<DataSet> removedDataSetList;

    public DataSetDifference() {
        this.dataSetPropertyDiff = new HashMap<String, PropertyDifference>();
        this.newMetaDataList = new HashSet<>();
        this.removedMetaDataList = new HashSet<>();
        this.changedMetaDataList = new HashMap<>();

        this.newDataSetList=new HashSet<>();
        this.removedDataSetList=new HashSet<>();
    }

    /**
     * 获取被修改的数据集属性名.
     */
    public String[] getChangedProperties() {
        if (dataSetPropertyDiff == null) return null;

        return dataSetPropertyDiff.keySet().toArray(new String[dataSetPropertyDiff.keySet().size()]);
    }

    /**
     * 获取修改后的数据集属性值.
     *
     * @param propertyName
     */
    public Object getNewValue(String propertyName) {
        if (dataSetPropertyDiff.containsKey(propertyName)) {
            PropertyDifference propertyDifference = (PropertyDifference) dataSetPropertyDiff.get(propertyName);
            return propertyDifference.newVaue;
        }

        return null;
    }

    /**
     * 获取原始数据集属性值.
     *
     * @param propertyName
     */
    public Object getOriginValue(String propertyName) {
        if (dataSetPropertyDiff.containsKey(propertyName)) {
            PropertyDifference propertyDifference = (PropertyDifference) dataSetPropertyDiff.get(propertyName);
            return propertyDifference.originValue;
        }

        return null;
    }

    /**
     * 添加一个变化的属性.
     *
     * @param propertyName
     * @param originValue
     * @param newValue
     */
    public void addPropertyDifference(String propertyName, Object originValue, Object newValue) {
        dataSetPropertyDiff.put(propertyName, new PropertyDifference(originValue, newValue));
    }

    /**
     * 获取新添加的数据元
     */
    public MetaData[] getNewMetaDataList() {
        return newMetaDataList.toArray(new MetaData[newMetaDataList.size()]);
    }

    public DataSet[] getNewDataSetList() {
        return newDataSetList.toArray(new DataSet[newDataSetList.size()]);
    }

    /**
     * 获取删除掉的数据元.
     */
    public MetaData[] getRemovedMetaDataList() {
        return removedMetaDataList.toArray(new MetaData[removedMetaDataList.size()]);
    }

    public DataSet[] getRemovedDataSetList() {
        return removedDataSetList.toArray(new DataSet[removedDataSetList.size()]);
    }


    /**
     * 获取被修改的数据元. Key 为数据元的ID, Value 为同一个数据元的不同版本.原始值放在左边.
     */
    public Map<Long, Pair<MetaData, MetaData>> getChangedMetaDataList() {
        return changedMetaDataList;
    }

    /**
     * 添加新创建的数据元.
     *
     * @param metaData
     */
    public void addNewMetaData(MetaData metaData) {
        this.newMetaDataList.add(metaData);
    }

    public void addNewDataSet(DataSet dataSet) {
        this.newDataSetList.add(dataSet);
    }

    /**
     * 批量添加数据元。
     * @param metaDatas
     */
    public void addNewMetaData(List<MetaData> metaDatas){
        newMetaDataList.addAll(metaDatas);
    }

    public void addNewDataSet(DataSet[] dataSets){
        CollectionUtils.addAll(newDataSetList, dataSets);
    }

    /**
     * 添加被删除的数据元.
     *
     * @param metaData
     */
    public void addRemovedMetaData(MetaData metaData) {
        this.removedMetaDataList.add(metaData);
    }

    public void addRemovedDataSet(DataSet dataSet) {
        this.removedDataSetList.add(dataSet);
    }

    /**
     * 批量增加被删除的数据元。
     * @param metaDatas
     */
    public void addRemovedMetaData(List<MetaData> metaDatas){
        removedMetaDataList.addAll(metaDatas);
    }

    /**
     * 添加被修改的数据元.
     *
     * @param origin
     * @param now
     */
    public void addChangedMetaData(MetaData origin, MetaData now) {
        if(origin == null || now == null) throw new IllegalArgumentException("数据元不能为空");
        if(origin.getId() != now.getId()) throw new IllegalArgumentException("非同一个数据元");
        if(origin.getHashCode() == now.getHashCode()) throw new IllegalArgumentException("数据元未被修改");

        this.changedMetaDataList.put(origin.getId(), new Pair<>(origin, now));
    }
}