<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>
<!--######人口基本管理 > 人口基本信息对话框模板页######-->
<div id="div_patient_info_form" data-role-form class="m-form-inline m-form-readonly f-mt20 f-pr">
    <div id="div_patient_img_upload" class="u-upload alone f-ib f-tac f-vam u-upload-img" data-alone-file=true>
        <!--用来存放item-->
        <div id="div_file_list" class="uploader-list" data-attr-scan></div>
    </div>
    <div class="m-form-group">
        <label>姓名：</label>

        <div class="l-text-wrapper m-form-control">
            <input type="text" id="inp_realName" class="required useTitle" data-attr-scan="name"/>
        </div>
    </div>
    <div class="m-form-group">
        <label>身份证号：</label>

        <div class="l-text-wrapper m-form-control">
            <input type="text" id="inp_idCardNo" class="required useTitle " data-attr-scan="idCardNo"/>
        </div>
    </div>
    <div class="m-form-group">
        <label>性别：</label>

        <div class="u-checkbox-wrap m-form-control">
            <input type="radio" value="Male" name="gender" data-attr-scan>男
            <input type="radio" value="Female" name="gender" data-attr-scan>女
        </div>
    </div>
    <div class="m-form-group ">
        <label>民族：</label>

        <div class="l-text-wrapper m-form-control">
            <input type="text" id="inp_patientNation" data-type="select"  class="required useTitle" data-attr-scan="nation"/>
        </div>
    </div>
    <div class="m-form-group ">
        <label>籍贯：</label>

        <div class="l-text-wrapper m-form-control">
            <input type="text" id="inp_patientNativePlace" class="required useTitle" data-attr-scan="nativePlace"/>
        </div>
    </div>
    <div class="m-form-group">
        <label>婚姻状况：</label>

        <div class="m-form-control">
            <input type="text" id="inp_select_patientMartialStatus" data-type="select" data-attr-scan="martialStatus">
        </div>
    </div>
    <div class="m-form-group">
        <label>出生日期：</label>

        <div class="m-form-control">
            <input type="text" id="inp_patientBirthday" class="required useTitle" placeholder="输入日期"
                   required-title=<spring:message code="lbl.must.input"/> data-attr-scan="birthday"/>
        </div>
    </div>
    <div class="m-form-group">
        <label>户籍地址：</label>

        <div class="l-text-wrapper m-form-control">
            <input type="text" id="inp_birthPlace" class="required useTitle" data-attr-scan="birthPlaceFull"/>
        </div>
    </div>
    <div class="m-form-group">
        <label>家庭地址：</label>

        <div class="l-text-wrapper m-form-control">
            <input type="text" id="inp_homeAddress" class="required useTitle" data-attr-scan="homeAddressFull"/>
        </div>
    </div>
    <div class="m-form-group">
        <label>工作地址：</label>

        <div class="l-text-wrapper m-form-control">
            <input type="text" id="inp_workAddress" class="required useTitle" data-attr-scan="workAddressFull"/>
        </div>
    </div>
    <div class="m-form-group">
        <label>户籍性质：</label>

        <div class="u-checkbox-wrap m-form-control">
            <input type="radio" value="temp" name="residenceType" data-attr-scan>农村
            <input type="radio" value="usual" name="residenceType" data-attr-scan>非农村
        </div>
    </div>
    <div class="m-form-group">
        <label>联系方式：</label>

        <div class="l-text-wrapper m-form-control">
            <input type="text" id="inp_patientTel" class="required useTitle" data-attr-scan="telephoneNo"/>
        </div>
    </div>
    <div class="m-form-group">
        <label>邮箱：</label>

        <div class="l-text-wrapper m-form-control">
            <input type="text" id="inp_patientEmail" class="required useTitle" data-attr-scan="email"/>
        </div>
    </div>
    <%--<div id="div_patient_img_upload" class="u-upload alone f-ib f-tac f-vam" data-alone-file=true>--%>
        <%--<div id="div_file_list" class="uploader-list"></div>--%>
    <%--</div>--%>
</div>

<!--######卡管理 > 卡信息对话框模板页######-->
<div id="div_card_info" class="f-pa card-dialog" data-role-form style=" visibility: hidden;left:0;top:0;">
    <div class="f-pr u-bd f-mt20">
        <div class="f-pa f-w70 f-wtl">
            已关联卡
        </div>
        <div class="f-mt30">
            <div class="f-ml10 f-fl">
                <input type="text" id="inp_select_cardType" placeholder="类型" data-type="select" data-attr-scan="">
            </div>
            <div class="f-ml10 f-fl">
                <input type="text" id="inp_card_search" placeholder="卡号"/>
            </div>
            <div class="l-button u-btn u-btn-primary u-btn-small f-ib f-vam  f-ml50" id="div_addCard">
                <span>关联卡</span>
            </div>

            <div id="div_card_info_form" data-role-form class="f-mt10">

                <!--卡基本信息 -->
                <div id="div_card_basicMsg" class="u-card-basicMsg m-form-inline m-form-readonly">
                    <div class="m-form-group">
                        <label>类型：</label>

                        <div class="l-text-wrapper m-form-control">
                            <input type="text" id="inp_cardType" class="required useTitle" data-attr-scan="cardType"/>
                        </div>
                    </div>
                    <div class="m-form-group">
                        <label>卡号：</label>

                        <div class="l-text-wrapper m-form-control">
                            <input type="text" id="inp_cardNo" class="required useTitle" data-attr-scan="number"/>
                        </div>
                    </div>
                    <div class="m-form-group">
                        <label>持有人姓名：</label>

                        <div class="l-text-wrapper m-form-control">
                            <input type="text" id="inp_HolderName" class="required useTitle"
                                   data-attr-scan="ownerName"/>
                        </div>
                    </div>
                    <div class="m-form-group">
                        <label>发行地：</label>

                        <div class="l-text-wrapper m-form-control">
                            <input type="text" id="inp_issueAddress" class="required useTitle" data-attr-scan="local"/>
                        </div>
                    </div>
                    <div class="m-form-group">
                        <label>发行机构：</label>

                        <div class="l-text-wrapper m-form-control">
                            <input type="text" id="inp_issueOrg" class="required useTitle" data-attr-scan="releaseOrgName"/>
                        </div>
                    </div>
                    <div class="m-form-group">
                        <label>关联时间：</label>

                        <div class="l-text-wrapper m-form-control">
                            <input type="text" id="inp_addDate" class="required useTitle" data-attr-scan="createDate"/>
                        </div>
                    </div>
                    <div class="m-form-group">
                        <label>状态：</label>

                        <div class="l-text-wrapper m-form-control">
                            <input type="text" id="inp_cardStatus" class="required useTitle"
                                   data-attr-scan="statusName"/>
                        </div>
                    </div>
                    <div class="m-form-group">
                        <label>说明：</label>

                        <div class="l-text-wrapper m-form-control">
                            <input type="text" id="inp_cardExplain" class="required useTitle"
                                   data-attr-scan="description"/>
                        </div>
                    </div>
                </div>

            </div>
        </div>
    </div>
</div>

<!--######档案管理 > 档案信息对话框模板页######-->
<div id="div_record_info_form">

</div>
