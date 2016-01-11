package com.yihu.ehr.constants;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.09.22 11:54
 */
public enum JobType {
    EhrArchiver("EhrArchiver"),

    EhrIndexer("EhrIndexer");

    //
    private String jobType;

    JobType(String jobType){
        this.jobType = jobType;
    }

    public String toString(){
        return jobType;
    }
}
