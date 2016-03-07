<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>
<%@include file="/WEB-INF/ehr/commons/jsp/commonHead.jsp" %>

<style type="text/css">
    .l-tree-div {
        width: 300px;
        height: 300px;
        padding-left: 5px;
        padding-top: 10px;
        overflow: hidden;
        font: 12px/1.14 \5b8b\4f53;
        color: #333;
        outline: 0;
        border: 1px solid #c8c8c8;
        margin: 50px;
    }
</style>

<!--注意点：ul外层div的class必须为l-tree-div -->
<div class="l-tree-div">
    <ul id="u-dataset-tree"></ul>
</div>


<%@include file="/WEB-INF/ehr/commons/jsp/commonFoot.jsp" %>
<script>
    $(function () {
        //初始化树
        var tree = $("#u-dataset-tree").ligerTree({
            data: getData(),
            isExpand: 2,
            parentIcon: '',
            childIcon: '',
            checkbox: false,
            idFieldName: "id",
            parentIDFieldName: 'pid',
            adjustToWidth: true
        });

        $(".l-tree").mCustomScrollbar({theme: "minimal-dark"});//给树设置滚动条

        function getData() {
            return [{id: "std0", pid: "", text: "数据集"},
                {id: "std1", pid: "std0", text: "数据集数据集数据集数据集数据集数据集数据集数据集数据集数据集数据集数据集1"},
                {id: "std2", pid: "std0", text: "数据集2"},
                {id: "std3", pid: "std0", text: "数据集3"},
                {id: "std4", pid: "std0", text: "数据集4"},
                {id: "std5", pid: "std1", text: "数据集数据集数据集数据集数据集数据集数据集数据集数据集数据集数据集数据集数据集5"},
                {id: "std6", pid: "std1", text: "数据集6"},
                {id: "std7", pid: "std1", text: "数据集7"},
                {id: "std8", pid: "std2", text: "数据集8"},
                {id: "std9", pid: "std2", text: "数据集9"},
                {id: "std10", pid: "std3", text: "数据集10"},
                {id: "std11", pid: "std3", text: "数据集11"},
                {id: "std12", pid: "std0", text: "数据集12"},
                {id: "std13", pid: "std12", text: "数据集13"},
                {id: "std14", pid: "std12", text: "数据集14"},
                {id: "std15", pid: "std12", text: "数据集15"},
                {id: "std16", pid: "std0", text: "数据集16"},
                {id: "std17", pid: "std16", text: "数据集17"},
                {id: "std18", pid: "std16", text: "数据集18"},
                {id: "std19", pid: "std16", text: "数据集19"},
                {id: "std20", pid: "std16", text: "数据集20"}
            ]
        }
    });
</script>