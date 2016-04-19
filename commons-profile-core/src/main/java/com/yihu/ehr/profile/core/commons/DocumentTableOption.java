package com.yihu.ehr.profile.core.commons;

import com.yihu.ehr.profile.core.nostructured.UnStructuredDocument;
import com.yihu.ehr.profile.core.nostructured.UnStructuredProfile;

import java.util.List;

/**
 * @author linaz
 * @version 1.0
 * @created 2016.04.15
 */
public class DocumentTableOption {

    public static final String Table = "UnStructuredDocument";

    //数据集晚点再处理，也一样的和结构化档案存到profile中去。hbase重复的部分会自己覆盖掉

    // 列族
    public enum Family {
        Basic("basic"),
        Document("document"),
        Extension("extension");

        private String family;

        Family(String family) {
            this.family = family;
        }

        public String toString() {
            return family;
        }
    }

    // 列
    public enum BasicQualifier {
        ProfileId("archive_id"),
        InnerVersion("inner_version"),
        LastUpdateTime("last_update_time");

        private String qualifier;

        BasicQualifier(String qualifier) {
            this.qualifier = qualifier;
        }

        public String toString() {
            return qualifier;
        }
    }


    // 列
    public enum DocumentQualifier{
        OrgCode("org_code"),
        PatientId("patient_id"),
        EventNo("event_no"),
        EventDate("event_date"),
        CdaVersion("inner_version"),
        DocumentsJson("documents_json");
        private String qualifier;
        DocumentQualifier(String qualifier){
            this.qualifier = qualifier;
        }
        public String toString(){
            return qualifier;
        }
    }




    /**
     * 获取列族。
     *
     * @return
     */
    public static String[] getFamilies() {
        return new String[]{
                Family.Basic.toString(),
                Family.Document.toString(),
                Family.Extension.toString()
        };
    }

    /**
     * 获取指定族的列.
     *
     * @return
     */
    public static String[] getQualifiers(Family family) {
        if (family == Family.Basic) {
            return new String[]{
                    BasicQualifier.ProfileId.toString(),
                    BasicQualifier.InnerVersion.toString(),
                    BasicQualifier.LastUpdateTime.toString()
            };
        }else if(family == Family.Document){
            return new String[]{
                    DocumentQualifier.OrgCode.toString(),
                    DocumentQualifier.PatientId.toString(),
                    DocumentQualifier.EventNo.toString(),
                    DocumentQualifier.EventDate.toString(),
                    DocumentQualifier.CdaVersion.toString(),
                    DocumentQualifier.DocumentsJson.toString(),
            };
        }

        return null;
    }






    /**
     * 解析文档，文档地下包含
     *
     * @param unStructuredProfile
     * @return
     */
    public static String getDocimentsToQualifier(UnStructuredProfile unStructuredProfile) {

        List<UnStructuredDocument> documentList = unStructuredProfile.getUnStructuredDocumentList();
        return documentList.toString();
    }




}
