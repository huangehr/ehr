<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="utf-8"%>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>

<!--######机构管理页面 > 机构信息对话框模板页######-->
<div id="div_organization_info_form" class="m-form-inline" data-role-form="" style="overflow:auto">
    <div class="m-form-group f-mt20 m-form-readonly">
        <label><spring:message code="lbl.org.code"/><spring:message code="spe.colon"/></label>
        <div class="l-text-wrapper m-form-control essential">
            <input type="text" id="org_code" class=" f-w240"  data-attr-scan="organizationCode"/>
        </div>
    </div>

    <div class="m-form-group">
        <label><spring:message code="lbl.org.fullName"/><spring:message code="spe.colon"/></label>
        <div class="l-text-wrapper m-form-control essential">
            <input type="text" id="full_name" class="required useTitle f-w240 max-length-100 validate-special-char"  required-title=<spring:message code="lbl.must.input"/> data-attr-scan="fullName"/>
        </div>
    </div>

    <div class="m-form-group">
        <label><spring:message code="lbl.org.shortName"/><spring:message code="spe.colon"/></label>
        <div class="l-text-wrapper m-form-control essential">
            <input type="text" id="short_name" class="required useTitle f-w240 max-length-100 validate-special-char"  required-title=<spring:message code="lbl.must.input"/> data-attr-scan="shortName"/>
        </div>
    </div>

    <div class="m-form-group">
        <label><spring:message code="lbl.local"/><spring:message code="spe.colon"/></label>
        <div class="l-text-wrapper m-form-control essential">
            <input type="text" id="location"  class="required useTitle f-w240 max-length-500 validate-special-char" data-type="comboSelect"  required-title=<spring:message code="lbl.must.input"/> data-attr-scan="location"/>
        </div>
    </div>

    <div class="m-form-group">
        <label><spring:message code="lbl.join.mode"/><spring:message code="spe.colon"/></label>
        <div class="l-text-wrapper m-form-control essential">
            <input type="text" id="settled_way"  class="required" data-type="select"  data-attr-scan="settledWay">
        </div>
    </div>
    <div class="m-form-group">
        <label>联系人<spring:message code="spe.colon"/></label>
        <div class="l-text-wrapper m-form-control">
            <input type="text" id="admin" class="useTitle f-w240 max-length-50 validate-special-char"  data-attr-scan="admin"/>
        </div>
    </div>
    <div class="m-form-group">
        <label>联系方式<spring:message code="spe.colon"/></label>
        <div class="l-text-wrapper m-form-control essential">
            <input type="text" id="tel" class="required useTitle validate-mobile-and-phone f-w240"  required-title=<spring:message code="lbl.must.input"/> validate-mobile-and-phone-title="请输入正确的手机号码或固话"  data-attr-scan="tel"/>
        </div>
    </div>

    <div class="m-form-group">
        <label><spring:message code="lbl.org.type"/><spring:message code="spe.colon"/></label>
        <div class="l-text-wrapper m-form-control essential">
            <input type="text" id="org_type" class="required f-w240" data-type="select" required-title=<spring:message code="lbl.must.input"/> data-attr-scan="orgType"/>
        </div>
    </div>

    <div class="m-form-group">
        <label><spring:message code="lbl.tip"/><spring:message code="spe.colon"/></label>
        <div class="l-text-wrapper m-form-control">
            <input type="text" id="tags" class="f-w240 max-length-100 validate-special-char" data-attr-scan="tags"/>
        </div>
    </div>
    <div>
        <hr class="u-border">
        <div class="f-pr u-bd">
            <div class="f-pa f-w20 f-wtl">
                高级
            </div>
            <div class="m-form-group" hidden="hidden" id="div_publicKeyMessage">
                <label>公钥信息:</label>
                <div class="l-text-wrapper m-form-control ">
                    <textarea type="text" class="required useTitle u-public-key-msg" data-attr-scan="publicKey"
                              readonly="readonly"></textarea>
                </div>
            </div>
            <div class="m-form-group" hidden="hidden" id="div_publicKey_validTime">
                <label>有效时间：</label>
                <div class="l-text-wrapper m-form-control ">
                    <input type="text" class="required useTitle u-f-mt5"
                           data-attr-scan="validTime"/>
                </div>
            </div>
            <div class="m-form-group" hidden="hidden" id="div_publicKey_startTime">
                <label>生成时间：</label>
                <div class="l-text-wrapper m-form-control">
                    <input type="text" class="required useTitle u-f-mt5"
                           data-attr-scan="startTime"/>
                </div>
            </div>
            <div class="l-button u-btn u-btn-primary u-btn-small f-ib f-vam f-ml100 u-btn-pk-color f-mt10 f-mb10" id="div_publicKey">
                <span>公钥管理</span>
            </div>
        </div>
    </div>

    <div id="div_public_manage" class="u-public-manage">
        <div class="l-button u-btn u-btn-small u-btn-cancel f-ib f-vam u-btn-color f-mb10" id="div_allot_publicKey">
            <span>分配公钥</span>
        </div>
        <textarea class="txt-public-content" id="txt_publicKey_message" data-attr-scan="publicKey"></textarea><br>
        <label class="f-fl">有效时间:</label><label id="lbl_publicKey_validTime" class="f-fl"></label><br>
        <label class="f-ml-t">生成时间:</label><label id="lbl_publicKey_startTime" class="f-mb"></label><br>
        <div class="l-button u-btn u-btn-primary u-btn-small f-ib f-vam f-t30" id="div_affirm_btn">
            <span>关闭</span>
        </div>
    </div>
	<div class="m-form-control pane-attribute-toolbar" id="div_footer">
		<div class="l-button u-btn u-btn-primary u-btn-large f-ib f-vam save-toolbar" id="div_update_btn">
			<span><spring:message code="btn.save"/></span>
		</div>
		<div class="l-button u-btn u-btn-cancel u-btn-large f-ib f-vam close-toolbar" id="btn_cancel">
			<span><spring:message code="btn.close"/></span>
		</div>
	</div>

</div>


<%--
<div id="div_public_manage" class="u-public-manage">
    <div class="l-button u-btn u-btn-cancel u-btn-small f-ib f-vam u-btn-color" id="div_allot_publicKey">
        <span>分配公钥</span>
    </div>
    <textarea class="txt-public-content" id="txt_publicKey_message" data-attr-scan="publicKey"></textarea>
    <label class="f-ml">有效时间：</label><label id="lbl_publicKey_validTime" ></label>
    <label class="f-ml-t">生成时间：</label><label id="lbl_publicKey_startTime" ></label>
    <div class="l-button u-btn u-btn-primary u-btn-small f-ib f-vam f-t30" id="div_affirm_btn">
        <span>确认</span>
    </div>
</div>--%>
