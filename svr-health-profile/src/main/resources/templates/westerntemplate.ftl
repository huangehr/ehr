<!doctype html>
<html lang="en">
 <head>
  <meta charset="UTF-8">
  <title>Document</title>
  <style>
    body{
	  width:800px;
	  margin:auto auto;
	  font-size:12px;
	  font-family:宋体；
    }
    .title{
	  text-align:center;
	  font-size:20px;
	  margin-top:20px;
	}
	.top-table,.bottom-table{
	  width:760px;
	  margin:20px;
	  table-layout:fixed;
	}
	.row{
	    height:28px;
		line-height: 28px;
	}
	.field{
	  width:33.33%;
	  white-space:nowrap;
	}
	.field-value{
	  display:inline-block;
      border-bottom:1px solid #232323;
	  height:18px;
	  line-height:18px;
	  margin-left:5px;
	  /*vertical-align:-5px;*/
      padding:0px;
	  overflow:hidden;
	}
	.middle-div{
	  border-top:3px solid #232323;
	  border-bottom:3px solid #232323;
	  min-height:400px;
	}
	.icon{
	  margin-left:50px;
	  margin-top:10px;
	  font-size:20px;
	  font-family:宋体;
	  font-weight:bold;
	}
	.list-table{
	  margin-left:65px;
	  margin-top:20px;
	  margin-bottom:10px;
	  width:620px;
	  table-layout:fixed;
	}
	.list-tr{
	  height:24px;
	  line-height:24px;
	}
	.list-td{
	  white-space:nowrap;
	  overflow:hidden;
	  text-align:left;
	  padding-right:10px;
	}
  </style>
 </head>
 <body>
   <div class="title">处方笺</div>
   <div class="top-div">
     <table class="top-table">
	   <tr class="row">
	     <td class="field">门诊/住院号:<span class="field-value" style="width:167px">&nbsp;${(event_no)!}</span></td>
		 <td class="field">科室:<span class="field-value" style="width:208px">&nbsp;${(data_sets.HDSC01_09[0].HDSD00_04_005)!}</span></td>
		 <td class="field">开方时间:<span class="field-value" style="width:190px">&nbsp;${(data_sets.HDSC01_09[0].HDSD00_04_006)!}</span></td>
	   </tr>
	   <tr class="row">
	     <td class="field">姓名:<span class="field-value" style="width:207px">&nbsp;${(data_sets.HDSA00_01[0].HDSD00_01_002)!}</span></td>
		 <td class="field">性别:<span class="field-value" style="width:208px">&nbsp;${(data_sets.HDSA00_01[0].HDSA00_01_011_VALUE)!}</span></td>
		 <td class="field">年龄:<span class="field-value" style="width:214px">&nbsp;${(data_sets.HDSA00_01[0].HDSA00_01_012)!}</span></td>
	   </tr>
	   <tr class="row">
	     <td class="field">就诊类型:<span class="field-value" style="width:183px">&nbsp;初诊</span></td>
		 <td class="field" style="width:66.66%" colspan="2">地址/电话:<span class="field-value" style="width:439px">&nbsp;${(data_sets.HDSA00_01[0].JDSA00_01_000)!}/${(data_sets.HDSA00_01[0].HDSD00_01_008)!}</span></td>
	   </tr>
	   <tr class="row">
	     <td class="field" style="width:100%" colspan="3">临床诊断:<span class="field-value" style="width:696px">&nbsp;${(data_sets.HDSC01_03[0].HDSD00_01_549)!}</span></td>
	   </tr>
	 </table>
   </div>
   <div class="middle-div">
     <div class="icon-div"><div class="icon">RP.</div></div>
     <table class="list-table">
		 <#list data_sets.HDSC01_04 as item>
		   <tr class="list-tr">
			 <td class="list-td" style="width:210px;">${item_index+1}、${(item.HDSD00_04_023)!}</td>
			 <td class="list-td" style="width:100px;">${(item.HDSD00_04_021)!}</td>
			 <td class="list-td" style="width:50px;">${(item.HDSD00_04_028)!}${(item.JDSD00_84_001)!}</td>
			 <td class="list-td" style="width:100px;">${(item.HDSD00_04_027_VALUE)!}</td>
			 <td class="list-td" style="width:70px;">每次${(item.HDSD00_04_024)!}${(item.HDSD00_04_025)!}</td>
			 <td class="list-td" style="width:70px;">${(item.HDSD00_04_026_VALUE)!}</td>
		   </tr>
		 </#list>
     </table>
   </div>
   <div class="bottom-div">
     <table class="bottom-table">
	   <tr class="row">
	     <td style="width:100%">
		   <div style="float:left;;white-space: nowrap">医师签名:<span class="field-value" style="width:180px;">&nbsp;${(data_sets.HDSC01_09[0].HDSD00_04_007)!}</span></div>
		   <div style="float:right;;white-space: nowrap">审核调配药师:<span class="field-value" style="width:200px;">&nbsp;${(data_sets.HDSC01_09[0].HDSD00_04_009)!}</span></div>
		 </td>
	   </tr>
	   <tr class="row">
	     <td style="width:100%">
		   <div style="float:right;;white-space: nowrap">复核发药药师:<span class="field-value" style="width:200px;">&nbsp;${(data_sets.HDSC01_09[0].HDSD00_04_010)!}</span></div>
		 </td>
	   </tr>
	 </table>
   </div>
 </body>
</html>
