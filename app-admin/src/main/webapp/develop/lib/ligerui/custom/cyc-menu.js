$(function(){
	/*总支撑左侧导航*/
	var MenuId=sessionStorage.getItem("MenuId");//获取MenuId
	setTimeout(function(){
		if(MenuId){
			var arr=MenuId.split(",")
			for(var i=0;i<arr.length;i++){
				$("a[data-find='"+arr[i]+"']").click();
				if(i>2){
					$("a[data-find='"+arr[2]+"']").closest("ul").find("ul").attr("style","")
				}

			}
		}
	},500)

	$.extend({MenuInit:function(obj,data){
		$(obj).InitHmtl(obj,data);
	}})
	$.fn.extend({
		InitHmtl:function(obj,data){
			var ObjHtml="";//拼入部分
			var DataDO=data;
			for(var i=0;i<DataDO.data.length;i++){
				var Htmladd='';
				var AClass='';
				var iClass='';
				var url='';
				switch(DataDO.data[i]['level']){
					case 1:AClass="menu-tit1";IClass="a"+DataDO.data[i]['id'];break;//判断是否第一级菜单
					default:AClass="";IClass="me";break;
				}
				if(DataDO.data[i]["url"]){
					url=DataDO.data[i]["url"];
				}
				if(typeof(DataDO.data[i]['pid'])=='undefined'){

					ObjHtml+='<li class="li" data-id="'+DataDO.data[i]["id"]+'"><a href="javascript:void(0);" class="'+AClass+'" data-url="'+url+'" title="'+DataDO.data[i]["text"]+'" data-find="'+DataDO.data[i]["id"]+'"><i class="'+IClass+'"></i>'+DataDO.data[i]["text"]+'</a><ul></ul></li>'
				}else{
					var size=10*(DataDO.data[i]['level']-1);
					/*if($(ObjHtml).find("li[data-id="+DataDO.data[i]['pid']+"]").length!=0){
					 size=20
					 }*/
					ObjHtml=ObjHtml.substr(0,ObjHtml.length-size)+'<li class="li" data-id="'+DataDO.data[i]['id']+'"><a href="javascript:void(0);" class="'+AClass+'" data-url="'+url+'" title="'+DataDO.data[i]["text"]+'" data-find="'+DataDO.data[i]["id"]+'"><i class="'+IClass+'"></i>'+DataDO.data[i]['text']+'</a><ul></ul></li>'+ObjHtml.substr(ObjHtml.length-size,ObjHtml.length);
				}

			};
			ObjHtml=ObjHtml.replace(/<ul><\/ul>/g,'');//删除多余<UL></ul>
			var Objcount='<ul class="menucyc"><li class="li first-tit"><i></i>导航栏菜单</li>'+ObjHtml+'</ul>'//初始化
			//$('body').html(Objcount);
			$(obj).html(Objcount);
			$(".menucyc").menu(".menucyc")

		},
		menu:function(obj){
			var Obj=obj;
			var ObjCyc=$(this).find("a");
			ObjCyc.bind("click",function(){
				if(ObjCyc.attr("href")=="javascript:void(0);"){
					$(this).addClass("on").next("ul").slideDown()
					$(this).closest("li").siblings("li").find("ul").slideUp();
					$(this).closest("li").siblings("li").find(".on").removeClass("on");
					var naval='';//面包屑
					var navalId=''//id
					if($(this).attr("data-url")){
						$("#contentPage").load($(this).attr("data-url"));

						$.each($(".menucyc a.on"),function(i,val){

							naval+="<span>"+$(this).attr("title")+"</span> &gt; ";
							navalId+=$(this).attr("data-find")+",";
						})
						naval=naval.substr(0,naval.length-5);
						navalId=navalId.substr(0,navalId.length-1);
						$("#span_nav_breadcrumb_content").html(naval).attr("data-sesson",navalId).find("span:nth-of-type(1)").addClass("strong")
						$("#span_nav_breadcrumb_content").find("span:nth-last-of-type(1)").addClass("on");
						sessionStorage.setItem("MenuId", navalId);
						naval="";
					}
					//console.log()
				}
			})
		}
	})
	$("body").delegate(".menucyc> li","click",function(){
		$(this).siblings().find("ul").slideUp()
	})
})