package com.yihu.ehr.profile.core.perspective;

import java.util.Date;

/**
 * 体格检查。
 *
 * @author Sand
 * @created 2016.04.25 16:57
 */
public class PhysicalExam {
    public static class BodySignal{
        Date date;          // 测量时间

        String temperature; // 体温
        String breath;      // 呼吸
        String pulse;       // 脉搏

        String lbp;         // 舒张压
        String ubp;         // 收缩压
    }
}
