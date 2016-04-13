/*
 * JavaScript Object: customTree
 * @created by llh 2015.11.9
 *
 *
 */
(function (window, $, undefined) {

    var data, dom, pid, childrenNode, thirdNode;
    var toTreeData; //目标树要添加节点的实际data（带有特殊标志）

    var treePrototype = $.ligerui.controls.Tree.prototype;

    /**
     * 扩展方法1：
     * f_onQueryNode（说明：查询节点）
     * @param fromTree 目标树对象
     * @param queryNm 要查询的条件值
     */
    treePrototype.f_onQueryNode = function (fromTree, queryNm) {
        if (!isStrEmpty(queryNm)) {
            fromTree.hide($(fromTree.element).find("li"));//除根节点外，先隐藏所有节点
            var liArr = $('span[title*="' + queryNm + '"]').closest('li');//查询出符合条件的结果集
            for (var i = 0; i < liArr.length; i++) {
                var outlinelevel = $(liArr[i]).attr("outlinelevel");
                if (outlinelevel == "3") {//查询结果集为三级节点，则二级节点也要显示
                    $(liArr[i]).parent().parent().parent().parent().show();//显示根节点
                    $(liArr[i]).parent().parent().show();//显示二级节点
                } else if (outlinelevel == "2") {//查询结果集为二级节点，则子节点也要显示
                    $(liArr[i]).parent().parent().show();//显示根节点
                    $(liArr[i]).find("li").show();//显示三级节点
                }else  if (outlinelevel == "1"){//根节点
                    $(liArr[i]).find("li").show();//显示二级节点
                    $(liArr[i]).find("li").find("li").show();//显示二级节点
                }
                $(liArr[i]).show();//显示子节点
            }
        } else {//查询条件为空，则显示所有节点
            fromTree.show($(fromTree.element).find("li"));
        }
    }

    /**
     * 扩展方法1：
     * f_onQueryNode（说明：查询节点）
     * @param fromTree 目标树对象
     * @param queryNm 要查询的条件值
     */
    treePrototype.f_onSimQueryNode = function (fromTree, queryNm) {
        if (!isStrEmpty(queryNm)) {
            fromTree.hide($(fromTree.element).find("li").not('.l-onlychild'));//除根节点外，先隐藏所有节点
            var liArr = $('span[title*="' + queryNm + '"]').closest('li');//查询出符合条件的结果集
            for (var i = 0; i < liArr.length; i++) {
                var outlinelevel = $(liArr[i]).attr("outlinelevel");
                if (outlinelevel == "3") {//查询结果集为三级节点，则二级节点也要显示
                    $(liArr[i]).parent().parent().parent().parent().show();//显示一级节点
                    $(liArr[i]).parent().parent().show();//显示二级节点
                } else if (outlinelevel == "2") {//查询结果集为二级节点，则子节点也要显示
                    $(liArr[i]).find("li").show();//显示三级节点
                    $(liArr[i]).parent().parent().show();//显示一级节点
                }
                $(liArr[i]).show();//显示自己
            }
        } else {//查询条件为空，则显示所有节点
            fromTree.show($(fromTree.element).find("li"));
        }
    }

    /**
     * 扩展方法2：
     * f_onTreeNodeChecked（说明：获取树节点有复选框的数据）
     * @param node 有勾选的节点集合
     * @returns {Array}
     */
    treePrototype.f_onNodeCheckedData = function (nodes) {
        var toTreeData;
        var toCheckedArr = [];
        for (var i = 0; i < nodes.length; i++) {
            var data = nodes[i].data;
            toTreeData = [{id: data.id, pid: data.pid, text: data.text}]; //父节点要添加的实际data
            toCheckedArr.push(toTreeData);
        }
        return toCheckedArr;
    }

    /**
     * 扩展方法3：
     * f_onTreeNodeNoChecked（说明：获取树节点没有复选框的数据）
     * @param node
     * @returns {*}
     */
    treePrototype.f_onTreeNodeNoChecked = function (nodes) {
        var toNoCheckedArr = [];
        getDataNotes(nodes, toNoCheckedArr);
        return toNoCheckedArr.length == 1 ? "" : toNoCheckedArr;
    }

    /**
     * 递归获取树的数据，status（add，delete）有两种，但是这边只取状态为add的数据
     * @param data 勾选的数据
     * @param toNoCheckedArr 递归结束后返回的数组对象
     */
    function getDataNotes(data, toNoCheckedArr) {
        for (var i = 0; i < data.length; i++) {
            if (data[i].__status == "add") {//界面显示的树信息
                var toTreeData = {id: data[i].id.replace("adapter", ""), pid: data[i].pid.replace("adapter", ""), text: data[i].text};
                toNoCheckedArr.push(toTreeData);
                if (data[i].children && data[i].children.length) {
                    getDataNotes(data[i].children, toNoCheckedArr);//获取下一级节点的数据
                }
            }
        }
    }

    /**
     * 扩展方法4：
     * f_onFromTreeCheck（说明：来源树复选框点击事件）
     * @param options
     * @returns {boolean}
     */
    treePrototype.f_onFromTreeCheck = function (options) {
        var fromTree = options.fromTree; //来源树对象（ligerTree对象）
        var toTree = options.toTree; //目标树对象
        var fromFlag = options.fromFlag; //来源树特殊标志 如：（id="std987",fromFlag="std"）
        var toFlag = options.toFlag; //目标树特殊标志
        var from = $(options.checkNodeItem); //来源树勾选的内容
        var toTarget = options.toTarget; //目标节点id
        var checked = options.checked;
        var fromFlagReg = new RegExp(fromFlag,"g");
        debugger;
        //来源树到目标树节点生成
        for (var i = 0; i < from.length; i++) {
            data = from[i].data;
            //是否父节点,即全选情况
            if (fromTree.hasChildren(data)) {
                childrenNode = data.children == null ? "" : data.children;//获取子节点的数据
                if (data.pid == "-1") {//根节点
                    if (!checked) {//全选未选中
                        if (fromTree.getData().length > 0) {//删除右树所有节点，包含根节点
                            var firstId = fromTree.getData()[0].id.replace(fromFlagReg, toFlag);
                            var liArr = $("#" + firstId).find("li");
                            toTree.remove($("#" + firstId));//删除所有节点
                        }
                        return false;
                    }

                    dom = $('#' + data.id.replace(fromFlagReg, toFlag), toTarget)[0];//根节点
                    if(dom==undefined){
                        dom = $("#mCSB_2_container")[0];
                        toTreeData = [{id: data.id.replace(fromFlagReg, toFlag), pid: "-1", text:  $($("#"+data.id).find(".l-body")[0]).find("span").text(),ischecked:checked}]; //添加树节点的实际data
                        toTree.append(dom, toTreeData); //添加树节点
                        dom = $('#' + data.id.replace(fromFlagReg, toFlag), toTarget)[0];//父节点的父节点dom
                    }
                    else
                        toTree.clear(dom);
                    var fromData = fromTree.getData()[0].children;
                    var toData = [];
                    function replaceFlag(data){
                        var d;
                        for(var o=0; o<data.length; o++){
                            d = data[o];
                            toData.push({
                                id: d.id.replace(fromFlagReg, toFlag),
                                ischecked: checked,
                                pid: d.pid.replace(fromFlagReg, toFlag),
                                text: d.text});

                            if(d.children)
                                replaceFlag(d.children);
                        }
                    }
                    replaceFlag(fromData);
                    toTree.append(dom, toData); //添加树节点
                } else {//不是根节点，但是该节点下可能有子节点
                    if (checked) {
                        if ($('#' + data.id.replace(fromFlagReg, toFlag)).length == 0) {//说明元素不存在，则新增
                            dom = $('#' + data.pid.replace(fromFlagReg, toFlag), toTarget)[0];//父节点的父节点dom
                            if(dom==undefined){
                                dom = $("#mCSB_2_container")[0];
                                toTreeData = [{id: data.pid.replace(fromFlagReg, toFlag), pid: "-1", text:  $($("#"+data.pid).find(".l-body")[0]).find("span").text(),ischecked:checked}]; //添加树节点的实际data
                                toTree.append(dom, toTreeData); //添加树节点
                                dom = $('#' + data.pid.replace(fromFlagReg, toFlag), toTarget)[0];//父节点的父节点dom
                            }
                            toTreeData = [{id: data.id.replace(fromFlagReg, toFlag), pid: data.pid.replace(fromFlagReg, toFlag), text: data.text,ischecked:checked}]; //添加树节点的实际data

                            for (var k = 0; k < childrenNode.length; k++) {
                                toTreeData.push({id: childrenNode[k].id.replace(fromFlagReg, toFlag), pid: childrenNode[k].pid.replace(fromFlagReg, toFlag), text: childrenNode[k].text,ischecked:checked});
                            }
                            //toTree.append(dom, toTreeData); //添加树节点
                        }else if(childrenNode && childrenNode.length>0){
                            dom = $('#' + data.id.replace(fromFlagReg, toFlag))[0];
                            toTreeData = [];
                            for (var k = 0; k < childrenNode.length; k++) {
                                if( $('#' + childrenNode[k].id.replace(fromFlagReg, toFlag)).length==0)
                                    toTreeData.push({id: childrenNode[k].id.replace(fromFlagReg, toFlag), pid: childrenNode[k].pid.replace(fromFlagReg, toFlag), text: childrenNode[k].text,ischecked:checked});
                            }
                        }
                        toTree.append(dom, toTreeData); //添加树节点
                    } else {
                        toTree.remove($('#' + data.id.replace(fromFlagReg, toFlag)));//删除节点
                        if($($('#' + data.pid.replace(fromFlagReg, toFlag)).find(".l-children")[0]).find("li").length==0){
                            toTree.remove($('#' + data.pid.replace(fromFlagReg, toFlag)))//删除根节点
                        }
                    }
                }
            } else {//单个节点
                pid = data.pid;
                if(pid=="-1"){
                    return false;
                }
                dom = $('#' + pid.replace(fromFlagReg, toFlag), toTarget)[0]; //父节点dom
                //父节点是否存在,不存在要创建
                if (dom == null) {
                    var parentData = fromTree.getDataByID(pid); //父节点的data
                    if ($('#' + parentData.id.replace(fromFlagReg, toFlag)).length == 0 && checked) {//说明元素不存在,且选中节点，则新增
                        var parentDom = $('#' + parentData.pid.replace(fromFlagReg, toFlag), toTarget)[0]; //父节点的父节点dom
                        if(parentDom==undefined){
                            parentDom = $("#mCSB_2_container")[0];
                            toTreeData = [{id: $("#"+data.pid).parent().parent().attr("id").replace(fromFlagReg, toFlag), pid: -1, text:  $($("#"+$("#"+data.pid).parent().parent().attr("id")).find(".l-body")[0]).find("span").text(),ischecked:checked}]; //添加根节点
                            toTree.append(parentDom, toTreeData); //添加树节点
                            parentDom = $('#' + $("#"+data.pid).parent().parent().attr("id").replace(fromFlagReg, toFlag), toTarget)[0];//根节点dom
                        }
                        toTreeData = [{id: parentData.id.replace(fromFlagReg, toFlag), pid: parentData.pid.replace(fromFlagReg, toFlag), text: parentData.text,ischecked:checked}]; //父节点要添加的实际data
                        toTree.append(parentDom, toTreeData); //添加父节点
                        dom = $('#' + pid.replace(fromFlagReg, toFlag), toTarget)[0]; //提供父节点的dom
                    } else {//不选中，则移除节点
                        toTree.remove($('#' + parentData.id.replace(fromFlagReg, toFlag)));//删除节点
                    }
                }
                if ($('#' + data.id.replace(fromFlagReg, toFlag)).length == 0 && checked) {//说明元素不存在，则新增
                    toTreeData = [{id: data.id.replace(fromFlagReg, toFlag), pid: pid.replace(fromFlagReg, toFlag), text: data.text,ischecked:checked}]; //添加树节点的实际data
                    toTree.append(dom, toTreeData); //添加树节点
                } else {//不选中，则移除节点
                    if ($('#' + data.pid.replace(fromFlagReg, toFlag)).find(".l-children").find("li").length <= 1) {//父节点下只有一个子节点，则删除父节点和子节点
                        toTree.remove($('#' + data.pid.replace(fromFlagReg, toFlag)));//删除父节点
                        toTree.remove($('#' + data.id.replace(fromFlagReg, toFlag)));//删除子节点
                    } else {
                        toTree.remove($('#' + data.id.replace(fromFlagReg, toFlag)));//删除子节点
                    }
                    if ($("#" + fromTree.getData()[0].id.replace(fromFlagReg, toFlag)).find(".l-children").find("li").length == 0) {
                        $($('#' + fromTree.getData()[0].id.replace(fromFlagReg, toFlag)).find(".l-body").find("div")[1]).removeClass("l-checkbox-checked").addClass("l-checkbox-unchecked");//不选中根节点复选框
                    }
                }
            }
        }
    }

    /**
     * 扩展方法5：
     * f_onToTreeCheck（说明：目标树复选框点击事件）
     * @param options
     */
    treePrototype.f_onToTreeCheck = function (options) {
        debugger;
        var fromTree = options.fromTree; //来源树对象（ligerTree对象）
        var toTree = options.toTree; //目标树对象toTree
        var fromFlag = options.fromFlag; //来源树特殊标志 如：（id="std987",fromFlag="std"）
        var toFlag = options.toFlag; //目标树特殊标志
        var toNode = $(options.checkNodeItem); //来源树勾选的内容
        var node = options.checkNodeItem;
        var checked = options.checked;
        var fromFlagReg = new RegExp(fromFlag,"g");

        if (!checked) {
            if (toNode.length > 0) {
                var data = toNode[0].data;
                if (data.pid == "-1") {//根节点
                    childrenNode = data.children;//获取根节点的下一级节点
                    for (var k = 0; k < childrenNode.length; k++) {
                        $($('#' + childrenNode[k].id.replace(fromFlagReg, toFlag)).find(".l-body").find("div")[2]).removeClass("l-checkbox-checked l-checkbox-incomplete").addClass("l-checkbox-unchecked");//不选中根节点复选框
                        //toTree.remove($('#' + childrenNode[k].id));//删除节点
                        thirdNode = childrenNode[k].children == null ? "" : childrenNode[k].children;//获取下级节点的子节点
                        for (var j = 0; j < thirdNode.length; j++) {
                            $($('#' + thirdNode[j].id.replace(fromFlagReg, toFlag)).find(".l-body").find("div")[3]).removeClass("l-checkbox-checked").addClass("l-checkbox-unchecked");//不选中根节点复选框
                            //toTree.remove($('#' + thirdNode[j].id));//删除节点
                        }
                    }
                    toTree.remove($("#" + data.id));//删除目标树根节点
                    $($("#" + data.id.replace(fromFlagReg, toFlag)).find(".l-body").find("div")[1]).removeClass("l-checkbox-checked l-checkbox-incomplete").addClass("l-checkbox-unchecked");//去除来源树根节点的复选框
                } else {//子节点
                    if ($(node.target).attr("outlinelevel") == "2" && $("#" + data.id.replace(fromFlagReg, toFlag)).find(".l-children").find("li").length == 0) {//根节点下的子节点，没有子节点
                        $($('#' + data.id.replace(fromFlagReg, toFlag)).find(".l-body").find("div")[2]).removeClass("l-checkbox-checked l-checkbox-incomplete").addClass("l-checkbox-unchecked");
                        toTree.remove(node.target);
                    } else {//根节点下的子节点，再有子节点
                        var toTreeRoot = toTree.getParentTreeItem(node, 1);
                        toTree.remove(node.target);
                        if (data.children == null) {//单个节点
                            $($('#' + data.id.replace(fromFlagReg, toFlag)).find(".l-body").find("div")[3]).removeClass("l-checkbox-checked").addClass("l-checkbox-unchecked");//移除来源选择状态
                            if ($("#" + data.pid).find(".l-children").find("li").length == 0) {//目标树下只有一个节点是，父节点也要删除掉
                                $($("#"+data.pid.replace(fromFlagReg, toFlag)).find(".l-body").find("div")[2]).removeClass("l-checkbox-incomplete").addClass("l-checkbox-unchecked");//移除来源树该节点的半选择状态
                                if($("#"+data.pid).parent().parent().find("li").length == 1){
                                    toTree.remove($("#"+$("#"+data.pid).parent().parent().attr("id")));//删除根节点
                                }
                                toTree.remove($('#' + data.pid));
                            }else{
                                $($('#' + data.pid).find(".l-body").find("div")[2]).removeClass("l-checkbox-incomplete").addClass("l-checkbox-checked");//移除父节点半选状态
                                $($('#' + data.pid.replace(fromFlagReg, toFlag)).find(".l-body").find("div")[2]).removeClass("l-checkbox-checked").addClass("l-checkbox-incomplete");//添加来源父节点半选状态
                            }
                        } else {
                            $($('#' + data.id.replace(fromFlagReg, toFlag)).find(".l-body").find("div")[2]).removeClass("l-checkbox-checked").addClass("l-checkbox-unchecked");
                            if ($(".l-checkbox-checked", $($('#' + data.id.replace(fromFlagReg, toFlag)).find(".l-body").find("div")[2])).length == 0) {
                                $($('#' + data.id.replace(fromFlagReg, toFlag)).find(".l-body").find("div")[2]).removeClass("l-checkbox-incomplete");;//移除来源树该节点的半选择状态
                            }
                            childrenNode = data.children == null ? "" : data.children;//获取子节点数据
                            for (var k = 0; k < childrenNode.length; k++) {
                                $($('#' + childrenNode[k].id.replace(fromFlagReg, toFlag)).find(".l-body").find("div")[3]).removeClass("l-checkbox-checked").addClass("l-checkbox-unchecked");//不选中根节点复选框
                            }
                            if($($('#' + data.pid).find(".l-children")[0]).find("li").length==0){
                                toTree.remove($('#' + data.pid));
                            }
                            $($('#' + data.id.replace(fromFlagReg, toFlag)).find(".l-body").find("div")[1]).removeClass("l-checkbox-incomplete");;//移除来源树该节点的半选择状态
                        }
                        $( $(toTreeRoot).find(".l-body:eq(0)").find("div:eq(1)")).removeClass("l-checkbox-incomplete").addClass("l-checkbox-checked");//移除根节点半选状态
                        var fromTreeToot = $("#" + toTreeRoot.id.replace(fromFlagReg, toFlag));
                        var fromTreeRootCheckBox = $($(fromTreeToot).find(".l-body:eq(0)").find("div:eq(1)"));
                        if($(".l-checkbox-checked", fromTreeToot).length>0){//确认半选状态
                            fromTreeRootCheckBox.removeClass("l-checkbox-checked").addClass("l-checkbox-incomplete");
                        }else{
                            fromTreeRootCheckBox.removeClass("l-checkbox-incomplete l-checkbox-checked").addClass("l-checkbox-unchecked");
                        }
                    }
                }
            }
        }
    }

    //判断字符串是否为空
    function isStrEmpty(str) {
        return str == null || !str || typeof str == undefined || str == '';
    }

    function isJson(obj){

        return typeof(obj) == "object" && Object.prototype.toString.call(obj).toLowerCase() == "[object object]" && !obj.length;
    }

})(window, jQuery);