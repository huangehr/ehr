package com.yihu.ehr;

import org.junit.Test;

import java.util.*;

/**
 * Created by progr1mmer on 2018/3/16.
 */
public class MapSort {

    @Test
    public void test() {

        Map<String, Integer> dataMap = new HashMap<String, Integer>();
        dataMap.put("name1", 5);
        dataMap.put("name2", 4);
        dataMap.put("name4", 2);
        dataMap.put("name5", 6);
        dataMap.put("name6", 1);

        Map<String, Integer> resultMap = sortByValue(dataMap);

        resultMap.forEach((k, v) -> System.out.println("key:" + k + "; " + "value:" + v));
    }

    private Map<String, Integer> sortByValue(Map<String, Integer> sourceMap) {
        if (sourceMap == null || sourceMap.isEmpty()) {
            return null;
        }
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        List<Map.Entry<String, Integer>> entryList = new ArrayList<>(sourceMap.entrySet());
        Collections.sort(entryList, new MapValueComparator());

        Iterator<Map.Entry<String, Integer>> iterator = entryList.iterator();
        Map.Entry<String, Integer> tmpEntry = null;
        while (iterator.hasNext()) {
            tmpEntry = iterator.next();
            sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
        }
        return sortedMap;
    }


    class MapValueComparator implements Comparator<Map.Entry<String, Integer>> {

        @Override
        public int compare(Map.Entry<String, Integer> me1, Map.Entry<String, Integer> me2) {

            return - me1.getValue().compareTo(me2.getValue());
        }
    }
}