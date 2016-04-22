<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="utf-8"%>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>
<script >
  (function ($, win) {
    $(function () {

      /* ************************** 全局变量定义 **************************** */
      var Util = $.Util;
      var retrieve = null;
      var master = null;
      var conditionArea = null;
      var adapterPlanId = ${dataModel};
      var entryRetrieve = null;
      var entryMater = null;
      var cfgModel = 0;
      var changeFlag=false;
      var cfg = [
        {
          left:{title:'平台数据集', cls:'', search:'/adapterDataSet/dataSetList', goAdd:'', del:''},
          right:{title:'数据元映射', cls:'', search:'/adapterDataSet/metaDataList', goAdd:'/adapterDataSet/gotoModify', del:'/adapterDataSet/delete',delLs:'/adapterDataSet/delete'}
        },
        {
          left:{title:'平台字典', cls:'', search:'/adapterDict/searchAdapterDict', goAdd:'', del:''},
          right:{title:'字典项映射', cls:'', search:'/adapterDict/searchAdapterDictEntry', goAdd:'/adapterDict/gotoModify', del:'/adapterDict/delete',delLs:'/adapterDict/delete'}
        }
      ];
      /* *************************** 函数定义 ******************************* */
      function pageInit() {
        resizeContent();
        retrieve.init();
        conditionArea.init();
        entryRetrieve.init();
        master.init();
        master.reloadGrid();
        entryMater.init();
      }

      function reloadGrid (url, params, columns) {

        if(columns)
          this.grid.set({
            columns:columns,
            url: url,
            parms: params,
            newPage:1
          });
        else
          this.grid.set({
            url: url,
            parms: params,
            newPage:1
          });
//        this.grid.reload();
      }

      function resizeContent(){
        var contentW = $('#grid_content').width();
        var leftW = $('#div_left').width();
        $('#div_right').width(contentW-leftW-20);
      }
      /* *************************** title模块初始化 ***************************** */
      conditionArea = {
        $element: $('#conditionArea'),
        $adapter_plan_name :$('#adapter_plan_name'),
        $adapter_plan_parent :$('#adapter_plan_parent'),
        $adapter_plan_type :$('#adapter_plan_type'),
        $adapter_plan_org :$('#adapter_plan_org'),
        $btn_switch_dataSet :$('#switch_dataSet'),
        $btn_switch_dict :$('#switch_dict'),
        init : function () {
          this.initAdapterOrg();
          this.bindEvents();
          this.$element.show();
        },
        initAdapterOrg: function () {
          var self = this;
          var dataModel = $.DataModel.init();
			conditionArea.$adapter_plan_org.css({width: 200})

          dataModel.fetchRemote("${contextRoot}/adapter/model",{
            data:{id:adapterPlanId},
            success: function(data) {
              var model = data.obj;
              self.$adapter_plan_name.val(model.name);
              self.$adapter_plan_parent.val(model.parentName);
              self.$adapter_plan_type.val(model.typeValue);
              self.$adapter_plan_org.val(model.orgValue);
            },
            error: function () {
             // alert(1)
            }
          });
        },
        bindEvents : function () {
          this.$btn_switch_dataSet.click(function () {
            if(cfgModel==0)
              return;
            cfgModel = 0;
            retrieve.setTitle();
            entryRetrieve.setTitle();
            changeFlag=true;
			  conditionArea.$btn_switch_dataSet.addClass('btn-primary');
			  conditionArea.$btn_switch_dict.removeClass('btn-primary');
            master.reloadGrid();
          });

          this.$btn_switch_dict.click(function () {
            if(cfgModel==1)
              return;
            cfgModel = 1;
            retrieve.setTitle();
            entryRetrieve.setTitle();
            changeFlag=true;
			  conditionArea.$btn_switch_dataSet.removeClass('btn-primary');
			  conditionArea.$btn_switch_dict.addClass('btn-primary');
            master.reloadGrid();
          })
        }
      };

      /* *************************** left模块初始化 ***************************** */
      retrieve = {
        $element: $('#retrieve'),
        $searchNm: $('#searchNm'),
        $addBtn: $('#btn_create'),
        $title :$('#left_title'),
        init: function () {
          this.$searchNm.ligerTextBox({width: 240, isSearch: true, search: function () {
            master.reloadGrid();
          }});
          this.$element.show();
        },
        setTitle: function () {
            this.$title.html(cfg[cfgModel].left.title);
        }
      };

      master = {
        infoDialog: null,
        grid: null,
        init: function () {
          if(this.grid)
            return;
          this.grid = $("#div_left_grid").ligerGrid($.LigerGridEx.config({
            url: '${contextRoot}'+cfg[cfgModel].left.search,
            columns: this.getColumn(),
            delayLoad:true,
            selectRowButtonOnly: true,
            allowHideColumn:false,
            validate : true,
            unSetValidateAttr:false,
            onBeforeShowData: function (data) {
              if(data.detailModelList.length==0){
                entryMater.reloadGrid('');
              }
            },
            onAfterShowData: function () {
              this.select(0);
            },
            onSelectRow: function(row){
              entryMater.reloadGrid();
            }
          }));
          this.bindEvents();
          // 自适应宽度
          this.grid.adjustToWidth();

        },
        reloadGrid: function () {

          var searchNm = $("#searchNm").val();
          var values = {
            searchNm: searchNm,
            adapterPlanId: adapterPlanId
          };
          if (changeFlag){
            var url = '${contextRoot}' + cfg[cfgModel].left.search;
            reloadGrid.call(this, url, values, this.getColumn());
          }else{
            this.grid.setOptions({parms: $.extend({},values), newPage:1});
            //重新查询
            this.grid.loadData(true);
          }

        },
        bindEvents: function () {
          $.subscribe('grid:left:open',function(event,id,mode){

          });

          $.subscribe('grid:left:delete',function(event,id) {

          });
        },
        getColumn: function () {
          var columnCfg =[];
          var code = 'code';
          var name = 'name';
//          if(cfgModel==1){
//            code = 'dictCode';
//            name = 'dictName';
//          }
          columnCfg = [
            { display: 'id', name: 'id', hide:true },
            { display: '代码', name: code,width: '50%', isAllowHide: false ,align:'left' },
            { display: '名称',name: name, width: '50%',isAllowHide: false ,align:'left' }
//            { display: '操作', name: 'operator', width: '34%', render: function (row) {
//              var html = '<a class="grid_edit" href="#" title="编辑" onclick="javascript:'+Util.format("$.publish('{0}',['{1}','{2}'])","grid:left:open", row.id,'modify')+'"></a>' +
//                      '<a class="grid_delete" href="#" title="删除" onclick="javascript:'+Util.format("$.publish('{0}',['{1}'])","grid:left:delete", row.id)+'"></a>';
//              return html;
//            }}
          ];
          return columnCfg;
        }
      };

      /* *************************** right模块初始化 ***************************** */
      entryRetrieve = {
        $element: $('#entryRetrieve'),
        $searchNm: $('#searchNmEntry'),
        $title: $('#right_title'),
        init: function () {
          this.$searchNm.ligerTextBox({width: 240, isSearch: true, search: function () {
            entryMater.reloadGrid();
          }});
          this.$element.show();
        },
        setTitle: function () {
          this.$title.html(cfg[cfgModel].right.title);
        }
      };

      entryMater = {
        entryInfoDialog: null,
        grid: null,
        init: function (dictId) {
          if(this.grid)
            return;
          this.grid = $("#div_relation_grid").ligerGrid($.LigerGridEx.config({
            url: '${contextRoot}'+cfg[cfgModel].right.search,
            columns: this.getColumn(),
            delayLoad:true,
            selectRowButtonOnly: false,
            allowHideColumn:false,
            validate : true,
            unSetValidateAttr:false,
            checkbox:true,
            onDblClickRow : function (row){
              //$.publish('grid:right:open',[row.id, 'modify']);
            }
          }));
          this.bindEvents();
          // 自适应宽度
          this.grid.adjustToWidth();
        },
        reloadGrid: function (parentId) {
          var searchNmEntry = $("#searchNmEntry").val();
          if(parentId!=''){
            var row = master.grid.getSelectedRow();
            if(row){
              parentId = row.id;
            }
          }
          var values = {
            adapterPlanId :adapterPlanId,
            searchNmEntry: searchNmEntry,
            dataSetId:parentId,
            dictId:parentId
          };
          if (changeFlag){
            reloadGrid.call(this, '${contextRoot}'+cfg[cfgModel].right.search, values, this.getColumn());
          }else{
            this.grid.setOptions({parms: $.extend({},values), newPage:1});
            //重新查询
            this.grid.loadData(true);
          }
          changeFlag=false;
        },
        bindEvents: function () {
          //窗体改变大小事件
          $(window).bind('resize', function() {
            resizeContent();
          });
          //
          $.subscribe('grid:right:open',function(event,id,mode){
            if(!master.grid.getSelectedRow()){
              if(cfgModel==0)
                $.Notice.warn('请先添加数据集数据！');
              else
                $.Notice.warn('请先添加平台字典数据！');
              return;
            }
            var title = mode == 'modify'?'修改' :'新增';
            title += cfg[cfgModel].right.title;
            entryMater.entryInfoDialog = $.ligerDialog.open({
              height:460,
              width: 460,
              title : title,
              url: '${contextRoot}'+cfg[cfgModel].right.goAdd,
              urlParms: {
                id:id,
                mode:mode
              },
              isHidden: false,
              opener: true,
              load : true
            });
          });

          $.subscribe('grid:right:delete',function(event,ids) {
            debugger
            if(!ids){
              var rows = entryMater.grid.getSelectedRows();
              if(rows.length==0){
                $.Notice.warn( '请选择要删除的数据行！');
                return;
              }
              for(var i=0;i<rows.length;i++){
                ids += ','+rows[i].id;
              }
              ids = ids.length>0?ids.substring(1, ids.length):ids;
            }

            $.Notice.confirm('确认删除所选数据？', function (r) {
              if(r){
                var extParms = {
                  plan_id: adapterPlanId
                };
                var parent = master.grid.getSelectedRow();
                if(parent){
                  extParms.data_set_id = parent.id;
                }
                var dataModel = $.DataModel.init();
                dataModel.updateRemote('${contextRoot}'+cfg[cfgModel].right.delLs,{
                  data:{ids: ids, extParms: JSON.stringify(extParms)},
                  success:function(data){
                    if (data.successFlg) {
                      $.Notice.success('操作成功！');
                      entryMater.reloadGrid();
                    }else{
                      $.Notice.error(data.errorMsg);
                    }
                  }
                });
              }
            })
          });
        },
        getColumn: function () {
          var columnCfg =[];
          if(cfgModel==0){
            columnCfg = [
              { display: 'id', name: 'id', hide:true },
              { display: 'metaDataId', name: 'metaDataId', hide:true },
              { display: '标准数据元代码', name: 'metaDataCode',width: '14%', isAllowHide: false ,align:'left' },
              { display: '标准数据元名称',name: 'metaDataName', width: '14%',isAllowHide: false  ,align:'left'},
              { display: '机构数据表代码', name: 'orgDataSetCode',width: '14%', isAllowHide: false  ,align:'left'},
              { display: '机构数据表名称',name: 'orgDataSetName', width: '14%',isAllowHide: false  ,align:'left'},
              { display: '机构数据元代码', name: 'orgMetaDataCode',width: '14%', isAllowHide: false  ,align:'left'},
              { display: '机构数据元名称',name: 'orgMetaDataName', width: '14%',isAllowHide: false  ,align:'left'},
              { display: '操作', name: 'operator', width: '16%', render: function (row) {
				  var html ='<div class="grid_edit"  style=""  title="修改" onclick="javascript:' + Util.format("$.publish('{0}',['{1}','{2}','{3}'])", "grid:right:open", row.id,'modify') + '"></div>'
						  +'<div class="grid_delete"  style="" title="删除"' +
						  'onclick="javascript:' + Util.format("$.publish('{0}',['{1}'])", "grid:right:delete", row.id) + '"></div>';
				  var html = '<a class="grid_edit" href="#" title="编辑" onclick="javascript:'+Util.format("$.publish('{0}',['{1}','{2}','{3}'])","grid:right:open", row.id,'modify')+'"></a>' +
                        '<a class="grid_delete" href="#" title="删除" onclick="javascript:'+Util.format("$.publish('{0}',['{1}'])","grid:right:delete", row.id)+'"></a>';
                return html;
              }}
            ]
          }
          else{
            columnCfg = [
              { display: 'id', name: 'id', hide:true },
              { display: 'dictEntryId', name: 'dictEntryId', hide:true },
              { display: '标准字典项代码', name: 'dictEntryCode',width: '14%', isAllowHide: false ,align:'left' },
              { display: '标准字典项值',name: 'dictEntryName', width: '14%',isAllowHide: false  ,align:'left'},
              { display: '机构字典代码', name: 'orgDictCode',width: '14%', isAllowHide: false ,align:'left' },
              { display: '机构字典名称',name: 'orgDictName', width: '14%',isAllowHide: false  ,align:'left'},
              { display: '机构字典项代码', name: 'orgDictEntryCode',width: '14%', isAllowHide: false  ,align:'left'},
              { display: '机构字典项值',name: 'orgDictEntryName', width: '14%',isAllowHide: false  ,align:'left'},
              { display: '操作', name: 'operator', width: '16%', render: function (row) {
//				  var html ='<div class="grid_edit"  style=""  title="修改" onclick="javascript:' + Util.format("$.publish('{0}',['{1}','{2}','{3}'])", "grid:right:open", row.id,'modify') + '"></div>'
//						  +'<div class="grid_delete"  style="" title="删除"' +
//						  ' onclick="javascript:' + Util.format("$.publish('{0}',['{1}'])", "grid:right:delete", row.id) + '"></div>';
                var html = '<a class="grid_edit" href="#" title="编辑" onclick="javascript:'+Util.format("$.publish('{0}',['{1}','{2}','{3}'])","grid:right:open", row.id,'modify')+'"></a>' +
                        '<a class="grid_delete" href="#" title="删除" onclick="javascript:'+Util.format("$.publish('{0}',['{1}'])","grid:right:delete", row.id)+'"></a>';
                return html;
              }}
            ]
          }
          return columnCfg;
        }
      };
      /* ******************Dialog页面回调接口****************************** */
      win.reloadEntryMasterGrid = function () {
          entryMater.reloadGrid();
      };
      win.getAdapterPlanId = function () {
        return adapterPlanId;
      };
      win.getParentId = function () {
        return master.grid.getSelectedRow().id;
      };
      win.getDialogOpener = function () {
        return cfgModel;
      };
      win.closeDialog = function (msg) {
        entryMater.entryInfoDialog.close();
        if(msg)
          $.Notice.success(msg);
      };
      /* *************************** 页面功能 **************************** */
      pageInit();
    });
  })(jQuery, window);
</script>
