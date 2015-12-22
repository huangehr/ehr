/******************************************************************************/
/* SYSTEM     : Commons                                                       */
/*                                                                            */
/* SUBSYSTEM  : Util                                                          */
/******************************************************************************/
package com.yihu.ehr.util.operator;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparableComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.collections.comparators.NullComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListSortUtil {

    public static ArrayList sort(ArrayList list, String[] fields) throws IllegalArgumentException {

        if (fields == null) {
            throw new IllegalArgumentException("fields is null.");
        }

        return sort(list, fields, new boolean[fields.length]);
    }

    public static List sort(List list, String[] fields) throws IllegalArgumentException {

        if (fields == null) {
            throw new IllegalArgumentException("fields is null.");
        }

        return sort(list, fields, new boolean[fields.length]);
    }

    public static ArrayList sort(ArrayList list, String[] fields, boolean[] reverses) throws IllegalArgumentException {
        return (ArrayList) sort((List) list, fields, reverses);
    }

    public static List sort(List list, String[] fields, boolean[] reverses) throws IllegalArgumentException {

        if (list == null || list.size() == 0) {
            return list;
        }

        if (fields == null) {
            throw new IllegalArgumentException("fields is null.");
        }

        if (reverses == null) {
            throw new IllegalArgumentException("reverses is null.");
        }

        if (fields.length != reverses.length) {
            throw new IllegalArgumentException("The size of fields and reverses is not in agreement.");
        }

        if (fields.length == 0) {
            return list;
        }

        // マルチカラムソート用のコンパレータ
        ComparatorChain cc = new ComparatorChain();

        for (int i = 0; fields != null && i < fields.length; i++) {

            String field = fields[i];
            boolean reverse = reverses[i];

            if (field.trim().length() == 0) {
                continue;
            }

            cc.addComparator(
                    new BeanComparator(
                            field,
                            new NullComparator(ComparableComparator.getInstance())),
                    reverse);
        }

        if (cc.size() != 0) {
            // ソート
            Collections.sort(list, cc);
        }

        return list;
    }
}
