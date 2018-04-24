package com.yihu.ehr.util.id;

import java.util.Random;

/**
 * @author Cws
 * @date 2018/02/22 08:58
 */
public class RandomUtil {
    public String getRandomString(int length) {

        String str = "0123456789";
        StringBuffer buffer = new StringBuffer();

        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(str.length() - 1);//0~61
            buffer.append(str.charAt(number));
        }
        return buffer.toString();
    }

}
