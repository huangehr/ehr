package com.yihu.ehr.std.service;

import javafx.util.Pair;
import org.apache.commons.collections.CollectionUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author AndyCai
 * @version 1.0
 * @created 20-7月-2015 17:14:55
 */
public class DictDifference {

	Map<Long, Pair<DictEntry, DictEntry>> changedDictEntryList;

	private Map<String,PropertyDifference> dictPropertyDiff;
	private Set<DictEntry> newDictEntrys;
	private Set<DictEntry> removedDictEntrys;

	public DictDifference(){
		this.dictPropertyDiff = new HashMap<String, PropertyDifference>();
		this.newDictEntrys = new HashSet<>();
		this.removedDictEntrys = new HashSet<>();
		this.changedDictEntryList = new HashMap<>();
	}

	public void finalize() throws Throwable {

	}

	public void addChangedDictEntry(DictEntry origin, DictEntry now) {
		if(origin == null || now == null) throw new IllegalArgumentException("字典值不能为空");
		if(origin.getId() != now.getId()) throw new IllegalArgumentException("非同一个字典值");
		if(origin.getHashCode() == now.getHashCode()) throw new IllegalArgumentException("字典值未被修改");

		this.changedDictEntryList.put(origin.getId(), new Pair<>(origin, now));
	}

	/**
	 * 
	 * @param dictEntry
	 */
	public void addNewDictEntry(DictEntry dictEntry){
		this.newDictEntrys.add(dictEntry);
	}

	/**
	 * 批量添加字典值。
	 * @param dictEntrys
	 */
	public void addNewDictEntry(DictEntry[] dictEntrys){
		CollectionUtils.addAll(newDictEntrys, dictEntrys);
	}

	/**
	 * 
	 * @param propertyName
	 * @param originValue
	 * @param newValue
	 */
	public void addProPertyDifference(String propertyName , Object originValue, Object newValue)
	{
		dictPropertyDiff.put(propertyName, new PropertyDifference(originValue, newValue));
	}

	/**
	 * 
	 * @param dictEntry
	 */
	public void addRemoveDictEntry(DictEntry dictEntry){
		this.removedDictEntrys.add(dictEntry);
	}

	public void addRemoveDictEntry(DictEntry[] dictEntrys){
		CollectionUtils.addAll(removedDictEntrys,dictEntrys);
	}


	public Map<Long, Pair<DictEntry, DictEntry>>  getChangedDictEntryList(){
		return changedDictEntryList;
	}

	/**
	 * 获取被修改的字典属性名.
	 */
	public String[] getChangedProperties(){
		if(dictPropertyDiff==null)
			return  null;

		return dictPropertyDiff.keySet().toArray(new String[dictPropertyDiff.keySet().size()]);
	}

	/**
	 * 
	 * @param propertyName
	 */
	public Object getNewValue(String propertyName){
		if (dictPropertyDiff.containsKey(propertyName))
		{
			PropertyDifference propertyDifference =(PropertyDifference) dictPropertyDiff.get(propertyName);
			return propertyDifference.newVaue;
		}
		return  null;
	}

	/**
	 * 
	 * @param propertyName
	 */
	public Object getOriginValue(String propertyName){
		if (dictPropertyDiff.containsKey(propertyName))
		{
			PropertyDifference propertyDifference =(PropertyDifference) dictPropertyDiff.get(propertyName);
			return propertyDifference.originValue;
		}
		return null;
	}

	/**
	* 获取新增的字典值
	*/
	public DictEntry[] getNewDictEntryList(){
		if (newDictEntrys==null)
			return null;
		return newDictEntrys.toArray(new DictEntry[newDictEntrys.size()]);
	}
	/**
	 * 获取删除的字典值
	 */
	public DictEntry[] getRemovedDictEntryList(){
		if(removedDictEntrys==null)
			return null;
		return removedDictEntrys.toArray(new DictEntry[removedDictEntrys.size()]);
	}
}//end DictDifference