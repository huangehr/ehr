<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta charset="UTF-8">
    <title></title>
    <script src="lib/jquery/jquery-1.9.1.js" type="text/javascript"></script>
    <link href="lib/ligerUI/skins/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css"/>
    <link href="lib/mCustomScrollbar/jquery.mCustomScrollbar.css" rel="stylesheet">
    <script src="lib/ligerUI/js/core/base.js" type="text/javascript"></script>
    <script src="lib/ligerUI/js/plugins/ligerTree.js" type="text/javascript"></script>
    <script src="lib/ligerUI/js/plugins/customTree.js" type="text/javascript"></script>
    <script src="lib/mCustomScrollbar/jquery.mCustomScrollbar.js" type="text/javascript"></script>
    <script type="text/javascript">
        $(function () {

            var defaults = {
                idFieldName: 'id',
                parentIDFieldName: 'pid',
                isExpand: 2,
                parentIcon: null,
                childIcon: null,
            };
            //来源树对象（ligerTree对象）
            var fromTree = $("#tree_from").ligerTree($.extend({},defaults,{
                data: getData(),
                onCheck: f_onFromTreeCheck
            }));
            //目标树对象
            var toTree = $("#tree_to").ligerTree($.extend({},defaults,{
                data: [],
                onCheck: f_onToTreeCheck
            }));

            $(".l-tree").mCustomScrollbar({theme: "minimal-dark"});//设置滚动条

            window.fromTree = fromTree;
            window.toTree = toTree;

            function f_onFromTreeCheck(node, checked) {
                var fromTreeOption = {
                    fromTree:fromTree,
                    toTree:toTree,
                    checkNodeItem:node,
                    checked:checked,
                    toTarget: "#tree_to",
                    fromFlag: "std",
                    toFlag: "adapter"
                }
                fromTree.f_onFromTreeCheck(fromTreeOption);
            }

            function f_onToTreeCheck(node, checked) {
                var toTreeOption = {
                    fromTree:fromTree,
                    toTree:toTree,
                    checkNodeItem:node,
                    checked:checked,
                    fromFlag: "adapter",
                    toFlag: "std"
                }
                toTree.f_onToTreeCheck(toTreeOption);
            }

            //查询节点
            $("#query_btn").bind("click",function(){
                var queryOption = $("#datasetName").val().trim();
                fromTree.f_onQueryNode(fromTree,queryOption);
            });

            $("#checked_btn").bind("click",function(){
                var notes = toTree.getChecked();
                var toCheckedArr = toTree.f_onNodeCheckedData(notes);

                alert('选择的节点数：' + toCheckedArr.length);
            });



            $("#nochecked_btn").bind("click",function(){
                var notes = toTree.getData();
                var toCheckedArr = toTree.f_onTreeNodeNoChecked(notes);

                alert('选择的节点数：' + toCheckedArr.length);
            });

        });

        function getData() {
            return [{id: "std0", pid: "-1", text: "数据集"},
                {id: "std1", pid: "std0", text: "数据集数据集数据集数据集数据集数据集数据集数据集数据集数据集数据集数据集数据集数据集数据集1"},
                {id: "std3", pid: "std0", text: "数据集3"},
                {id: "std5", pid: "std1", text: "数据集5"},
                {id: "std6", pid: "std1", text: "数据集6"},
                {id: "std7", pid: "std1", text: "数据集7"},
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


    </script>
    <style type="text/css">
        #tree_from {
            position: relative;
            display: inline-block;
            border: 1px solid #d3d3d3;
            width: 300px;
            height: 355px;
            overflow-y: auto;
            overflow-x: hidden;
        }

        #tree_to {
            position: absolute;
            top: 32px;
            left: 370px;
            margin-left: 5px;
            border: 1px solid #d3d3d3;
            width: 300px;
            height: 355px;
            overflow-y: auto;
            overflow-x: hidden;
        }

        .tree-to-title {
            position: absolute;
            top: 14px;
            left: 372px;
        }

        .tree-option {
            position: absolute;
            top: 100px;
            margin-top: 100px;
            left: 329px;
        }

        .u-span {
            font-size: 25px;
            color: #787878;
        }
    </style>
</head>
<body style="padding: 10px">

<div>
    <div>标准:</div>
    <div id="tree_from"></div>
    <div class="tree-option">
		<span class="u-span">
			>
		</span>
    </div>
    <div class="tree-to-title">已选择:</div>
    <div id="tree_to"></div>
</div>

<div style="margin-top: 20px;">数据集名称:<input type="text" id="datasetName"><button id="query_btn">查询</button> </div>
<a class="l-button" id="checked_btn"  style="width:120px;float:left;margin-right:10px;">获取选择(带复选框)</a>
<a class="l-button" id="nochecked_btn"  style="width:120px;float:left;margin-right:10px;">获取选择(不带复选框)</a>

</body>
</html>