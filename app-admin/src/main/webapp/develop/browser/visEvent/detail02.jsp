<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>

<div style="text-align: center">

    <!-- 用药情况 -->
    <%--<div id="b_detail_2" class="vis-details" style="display: none;">--%>
                <div class="medi-bar">
                    <div id="mb_bar_item_1" class="drug-list-bar" >药品清单</div>
                    <div id="mb_bar_item_2" class="pres-list-bar" >处方记录</div>
                    <div id="mb_bar_item_3" class="medi-list-bar all-list-bar-active" >用药记录</div>
                </div>
                <!-- 药品清单 -->
                <div id="mb_bar_detail_1" style="display: none; height: 480px; margin: 10px" class="">
                    <table id="mb_table">
                        <%--<thead>--%>
                        <%--<tr>--%>
                            <%--<th data-field="id" data-formatter="idFormatter">ID</th>--%>
                            <%--<th data-field="name">Item Name</th>--%>
                            <%--<th data-field="price">Item Price</th>--%>
                        <%--</tr>--%>
                        <%--</thead>--%>
                    </table>
                </div>
                <!-- 处方记录 -->
                <div id="mb_bar_detail_2" style="" class="mb_bar_detail_2">
                    <div style="
                        border-bottom: 1px solid #23AFFF;
                        position: absolute;
                        width: 936px;
                        margin-top: 35px;
                    "></div>
                    <a href="javascript:void(0)" class="mb_large_prev">&nbsp;</a>
                    <a href="javascript:void(0)" class="mb_large_next">&nbsp;</a>
                    <div id="formulation_records" class="time-line-horizontal" style="width: 770px; margin: auto"></div>
                </div>
                <!-- 用药记录 -->
                <div id="mb_bar_detail_3" style="" class="medi-detl">
                    <div id="medi_list" class="medi-list">
                        <div class="medi-search" >
                            <div class="ms-year">
                                <div id="ms_year_left" class="ms-left ms-left-active"></div>
                                <div id="ms_tx_year" class="ms-text">2015年</div>
                                <div id="ms_year_right" class="ms-right ms-right-active"></div>
                            </div>
                            <div class="ms-month">
                                <div id="ms_month_left" class="ms-left ms-left-active"></div>
                                <div id="ms_tx_month" class="ms-text">11月</div>
                                <div id="ms_month_right" class="ms-right ms-right-active"></div>
                            </div>
                            <div id="ms_today" class="ms-today">返回今日</div>
                        </div>
                        <div id="medi_info_title" class="medi-info-title" >2015-11-06&nbsp;用药信息</div>

                        <div id="medi_calendar" class="medi-calendar"></div>
                        <div id="medi_info" class="medi-info">
                            <div  class="ms-grid">
                                <div class="ms-grid-row">
                                    <div class="ms-grid-row-seq">1</div>
                                    <div class="ms-grid-row-title">胺碘酮</div>
                                    <div class="ms-grid-row-info">
                                        <div class="ms-grid-row-info-left">2粒</div>
                                        <div class="ms-grid-row-info-right">16:35</div>
                                    </div>
                                </div>

                                <div class="ms-grid-row">
                                    <div class="ms-grid-row-seq">2</div>
                                    <div class="ms-grid-row-title">胺碘酮</div>
                                    <div class="ms-grid-row-info">
                                        <div class="ms-grid-row-info-left">2粒</div>
                                        <div class="ms-grid-row-info-right">16:35</div>
                                    </div>
                                </div>

                                <div class="ms-grid-row">
                                    <div class="ms-grid-row-seq">3</div>
                                    <div class="ms-grid-row-title">胺碘酮</div>
                                    <div class="ms-grid-row-info">
                                        <div class="ms-grid-row-info-left">2粒</div>
                                        <div class="ms-grid-row-info-right">16:35</div>
                                    </div>
                                </div>

                                <div class="ms-grid-row">
                                    <div class="ms-grid-row-seq">4</div>
                                    <div class="ms-grid-row-title">胺碘酮</div>
                                    <div class="ms-grid-row-info">
                                        <div class="ms-grid-row-info-left">2粒</div>
                                        <div class="ms-grid-row-info-right">16:35</div>
                                    </div>
                                </div>

                                <div class="ms-grid-row">
                                    <div class="ms-grid-row-seq">5</div>
                                    <div class="ms-grid-row-title">胺碘酮</div>
                                    <div class="ms-grid-row-info">
                                        <div class="ms-grid-row-info-left">2粒</div>
                                        <div class="ms-grid-row-info-right">16:35</div>
                                    </div>
                                </div>
                            </div>
                            <div id="ms_page_bar" style="margin-top: 30px">
                                <%--<div  class="ms-page-bar">--%>
                                    <%--<a href="javascript:void(0)" class="ms-left ms-page-pre ms-left-active">&nbsp;</a>--%>
                                    <%--<a href="javascript:void(0)" class="ms-page-num ms-page-num-active">1</a>--%>
                                    <%--<a href="javascript:void(0)" class="ms-page-num ">2</a>--%>
                                    <%--<a href="javascript:void(0)" class="ms-page-num ">3</a>--%>
                                    <%--<a href="javascript:void(0)" class="ms-page-num ">4</a>--%>
                                    <%--<a href="javascript:void(0)" class="ms-page-num ">5</a>--%>
                                    <%--<a href="javascript:void(0)" class="ms-right ms-page-next ms-right-active">&nbsp;</a>--%>
                                <%--</div>--%>
                            </div>

                            <div style="display: none" class="ms-no-info">
                                今日无用药信息
                            </div>
                        </div>
                    </div>
                </div>
            </div>



