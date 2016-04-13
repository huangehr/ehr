<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="utf-8"%>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>
<script>

  (function ($, win) {
    $(function () {

      /* ************************** 全局变量定义 **************************** */
      var Util = $.Util;
      var retrieve = null;
      var master = null;

      /* *************************** 函数定义 ******************************* */
      function pageInit() {
        retrieve.init();
        master.init();
      }

      function reloadGrid (url, params) {
        this.grid.set({
          url: url,
          parms: params,
          newPage:1
        });
        this.grid.reload();
      }

      /* *************************** 模块初始化 ***************************** */
      retrieve = {
        $element: $('.m-retrieve-area'),
        $searchNm: $('#inp_search'),
        $type:$('#inp_type'),
        $searchBtn: $('#btn_search'),
        $addBtn: $('#btn_add'),
        $multiDelBtn: $('#btn_multiDel'),

        init: function () {
          this.initDDL(21, this.$type);
          this.$searchNm.ligerTextBox({width: 240 });
          this.bindEvents();
          this.$element.show();
          this.$element.attrScan();
          window.form = this.$element;
        },
        initDDL: function (dictId, target) {
          var target = $(target);
          var dataModel = $.DataModel.init();
          dataModel.fetchRemote("${contextRoot}/dict/searchDictEntryList",{data:{dictId: dictId,page: 1, rows: 10},
            success: function(data) {
              target.ligerComboBox({
                valueField: 'code',
                textField: 'value',
                data: [].concat({code:'',value:''},data.detailModelList)
              });
            }});
        },
        delAdapterOrg: function (code) {
          var dataModel = $.DataModel.init();
          dataModel.updateRemote('${contextRoot}/adapterorg/delAdapterOrg',{
            data:{code: code},
            success:function(data){
              if (data.successFlg) {
                $.Notice.open({type: 'success', msg: '操作成功！'});
                master.reloadGrid();
              } else {
                $.Notice.open({type: 'success', msg: '删除失败！'});
                return;
              }
            }
          });
        },
        bindEvents: function () {
          $.subscribe('adapterOrg:adapterInfo:del',function(event,ids) {
            if(!ids){
              var rows = master.grid.getSelectedRows();
              if(rows.length==0){
                $.Notice.open({type: 'info', msg: '请选择要删除的数据行！'});
                return;
              }
              for(var i=0;i<rows.length;i++){
                ids += ',' + rows[i].id;
              }
              ids = ids.length>0 ? ids.substring(1, ids.length) : ids;
            }
            retrieve.delAdapterOrg(ids);
          });

          retrieve.$searchBtn.click(function () {
            master.reloadGrid();
          });
        }
      };

      master = {
        adapterInfoDialog:null,
        grid: null,
        init: function () {
          var searchNm = $("#inp_search").val();
          var oryType = $("#inp_type").val();

          this.grid = $("#div_adapter_info_grid").ligerGrid($.LigerGridEx.config({
            url: '${contextRoot}/adapterorg/searchAdapterOrg',
            parms: {
              searchNm: searchNm,
              oryType:oryType
            },
            columns: [
              { display: '标准代码', name: 'code',hide:true,  isAllowHide: false },
              { display: '标准类别', name: 'typeValue', width: '15%' ,align:'left'},
              { display: '标准名称',name: 'name', width: '25%', align:'left', isAllowHide: false },
              { display: '采集机构', name: 'orgValue', width: '23%',resizable: true ,align:'left'},
              { display: '继承标准', name: 'parentValue', width: '25%',minColumnWidth: 20 ,align:'left'},
              { display: '操作', name: 'operator', width: '12%', render: function (row) {
//				  var html ='<div class="grid_edit"  style=""  title="维护" onclick="javascript:' + Util.format("$.publish('{0}',['{1}','{2}'])", "adapter:adapterInfo:manager", row.code,'manager') + '"></div>'
//						  +'<div class="grid_edit"  style="" title="修改"' +
//						  ' onclick="javascript:' + Util.format("$.publish('{0}',['{1}','{2}'])", "adapter:adapterInfo:open", row.code,'modify') + '"></div>'
//						  +'<div class="grid_delete"  style="" title="删除"' +
//						  ' onclick="javascript:' + Util.format("$.publish('{0}',['{1}','{2}'])", "adapter:adapterInfo:del", row.code,'del') + '"></div>';
                var html = '<a class="label_a" href="#" title="维护" onclick="javascript:'+Util.format("$.publish('{0}',['{1}','{2}'])","adapter:adapterInfo:manager", row.code,'manager')+'">维护</a>' +
                           '<a class="grid_edit" href="#" title="编辑" style="margin-left:10px;" onclick="javascript:'+Util.format("$.publish('{0}',['{1}','{2}'])","adapter:adapterInfo:open", row.code,'modify')+'"></a>' +
                           '<a class="grid_delete" href="#" title="删除" onclick="javascript:'+Util.format("$.publish('{0}',['{1}','{2}'])","adapter:adapterInfo:del", row.code,'del')+'"></a>';
                return html;
              }}
            ],
            selectRowButtonOnly: false,
            allowHideColumn:false,
            validate : true,
            unSetValidateAttr:false,
            checkbox : true,
            onDblClickRow : function (row){
              //$.publish('adapter:adapterInfo:open',[row.code,'modify']);
            }
          }));
          this.bindEvents();
          // 自适应宽度
          this.grid.adjustToWidth();
        },
        reloadGrid: function () {
          var values = retrieve.$element.Fields.getValues();
          this.grid.options.newPage =  1;
          this.grid.setOptions({parms: $.extend({},values)});
          this.grid.loadData(true);
          //reloadGrid.call(this, '${contextRoot}/adapterorg/searchAdapterOrg', values);
        },

        bindEvents: function () {
          $.subscribe('adapter:adapterInfo:manager',function(event,code,mode){
            var url = '${contextRoot}/orgdataset/initial?treePid=4&treeId=25';
            $("#contentPage").empty();
            $("#contentPage").load(url,{"adapterOrg":code} );

            <%--location.href = '${contextRoot}/orgdataset/initial?treePid=4&treeId=25&adapterOrg='+code;--%>
          });
          $.subscribe('adapter:adapterInfo:open',function(event,code,mode) {

            var title = '';
            //只有new 跟 modify两种模式会到这个函数
            if(mode == 'modify'){
              title = '修改第三方标准';
            }
            else{
              title = '新增第三方标准';
            }
            master.adapterInfoDialog = $.ligerDialog.open({
              height:550,
              width: 460,
              title : title,
              url: '${contextRoot}/adapterorg/template/adapterOrgInfo',
              urlParms: {
                code:code,
                type:'1',
                mode:mode
              },
              isHidden: false,
              opener: true,
              load: true
            });
          });
          $.subscribe('adapter:adapterInfo:del',function(event,code) {
            if(!code){
              var rows = master.grid.getSelectedRows();
              if(rows.length==0){
                $.Notice.warn( '请选择要删除的数据行！');
                return;
              }
              for(var i=0;i<rows.length;i++){
                code += ',' + rows[i].code;
              }
              code = code.length>0 ? code.substring(1, code.length) : code;
            }
            
            $.Notice.confirm('确认删除所选数据？', function (r) {
              if(r){
                var dataModel = $.DataModel.init();
                dataModel.updateRemote('${contextRoot}/adapterorg/delAdapterOrg',{
                  data:{code:code},
                  success:function(data){
                    if(data.successFlg){
                      $.Notice.success( '删除成功！');
                      master.reloadGrid();
                    } else
                      $.Notice.error( '删除失败！');
                  }
                });
              }
            })
          });
        },
      };

      /* ******************Dialog页面回调接口****************************** */
      win.reloadMasterGrid = function () {
        master.reloadGrid();
      };
      win.closeDialog = function (msg) {
        master.adapterInfoDialog.close();
        if(msg)
          $.Notice.success(msg);
      }
      /* *************************** 页面功能 **************************** */
      pageInit();
    });
  })(jQuery, window);

</script>