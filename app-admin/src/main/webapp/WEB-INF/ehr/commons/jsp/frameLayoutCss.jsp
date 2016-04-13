<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="utf-8"%>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>
<style type="text/css">
	body { padding-bottom: 5px;}
	.m-logo{ height:88px; background:url(${staticRoot}/images/tab.png); }
	.u-nav-breadcrumb { height: 40px; line-height: 40px; }
	#div_main_content {  width:100%;margin:0; padding:0; overflow: hidden; }

	.m-nav-menu{ background-color: #EFFAFE; z-index: 100; }
	.m-snav-title { width: 190px; line-height: 40px; padding-left: 42px; }

	.m-snav-title .img-bgp { height:22px; width: 22px; position: absolute; left: 10px; top: 9px; background:url(${staticRoot}/images/icon_Menu.png);  }
	.m-nav-tree .l-tree .l-box { width: 24px; }
	.m-nav-tree .l-tree li .l-body,
	.m-nav-tree .l-tree .l-box,
	.m-nav-tree .l-tree a, .l-tree span,
	.m-nav-tree .l-tree .l-selected span { height: 40px; line-height: 40px; }
	.m-nav-tree .l-tree>li { border-bottom: 1px solid #D0D0D0; background: #D4E7F0; }
	.m-nav-tree .l-tree>li>.l-body { font-weight: bold; }
	.m-nav-tree .l-tree>li>.l-body .l-box { margin-right: 10px; }
	.m-nav-tree .l-tree .l-box { background: none; }
	.m-nav-tree .l-children { background: #FFF; }

	.m-nav-tree .l-tree .l-over span { text-decoration: none; }
	.m-nav-tree .l-children .l-body.l-selected { background: #2D9BD2; }
	.m-nav-tree .l-tree .l-selected span { background: inherit; border: none; }
	.m-nav-tree .l-tree .l-children .l-selected span { color: #FFF; }
	.m-nav-tree .l-tree .l-tree-icon-leaf { background: url(${staticRoot}/images/icon_Ins.png) no-repeat 0 10px; }
	.m-nav-tree .l-tree .l-selected .l-tree-icon-leaf { background: url(${staticRoot}/images/After1_btn_pre.png) no-repeat 5px 16px; }
	/*.m-nav-tree .l-tree .l-selected span {
		background: #2D9BD2;
		color: #FFF;
	  }*/

	#div_notice_container { position: absolute; width: 500px; left: 50%; margin-left: -300px; z-index: 100; }
	.u-notice .messageControlBar { background-image: url(${staticRoot}/images/xiazhan02.png); background-repeat: no-repeat; width: 38px;height: 9px;margin: 0 auto;cursor: pointer; }
	.u-notice .msgContent { overflow: hidden; border-radius: 5px; box-shadow: rgba(0, 0, 0, 0.0980392) 0 2px 4px; cursor: pointer; background: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABsAAAAoCAYAAAAPOoFWAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAPZJREFUeNq81tsOgjAMANB2ov7/7ypaN7IlIwi9rGuT8QSc9EIDAsAznxvY4pXPKr05RUE5MEVB+TyWfCEl9LZApYopCmo9C4FKSMtYoI8Bwv79aQJU4l6hXXCZrQbokJEksxHo9KMOgc6w1atHXM8K9DVC7FQnJ0i8iK3QooGgbnyKgMDygBWyYFZoqx4qS27KqLZJjA1D0jK6QJcYEQEiWv9PGkTsbqxQ8oT+ZtZB6AkdsJnQDnMoHXHLGKOgDYuCWmYhEERCI5gaamW0bnHdA3k2ltlIN+2qKRyCND0bhqSYCyTB3CAOc4WusBEIpkeBuPgJMAAX8Hs1NfqHRgAAAABJRU5ErkJggg==) 0% 0% repeat-x scroll; }
	.u-notice.success .msgContent { border: 1px solid rgb(80, 194, 78); color: rgb(0, 100, 0);  background-color: rgb(144, 238, 144)}
	.u-notice.error .msgContent { border: 1px solid  rgb(194, 78, 84); color:  #fff;  background-color: #ff0000;}
	.u-notice.success .messageControlBar{ background-position: 0 0; }
	.u-notice.error .messageControlBar{ background-position: 0 -9px; }

	.grid_edit{
		display:inline-block;
		width: 40px;
		height: 40px;
		cursor:pointer;
		background: url(${staticRoot}/images/app/bianji01_btn.png) center no-repeat;
	}
	.grid_edit:hover{
		background: url(${staticRoot}/images/app/bianji_btn_pre.png) center no-repeat;
	}

	.grid_delete{
		display:inline-block;
		width: 40px;
		height: 40px;
		cursor:pointer;
		background: url(${staticRoot}/images/app/shanchu01_btn.png) center no-repeat;
	}

	.grid_delete:hover{
		background: url(${staticRoot}/images/app/shanchu_btn_pre.png) center no-repeat;
	}

	.grid_on{
		display: inline-block;
		width: 60px;
		height: 40px;
		cursor:pointer;
		background: url(${staticRoot}/images/app/on01_btn.png) center  no-repeat;
		background-size:60px 20px;
	}

	.grid_on:hover{
		background: url(${staticRoot}/images/app/on01_btn.png) center no-repeat;
		background-size:60px 20px;
	}

	.grid_off{
		display: inline-block;
		width: 60px;
		height: 40px;
		cursor:pointer;
		background: url(${staticRoot}/images/app/off01_pre.png) center  no-repeat;
		background-size:60px 20px;
	}

	.grid_off:hover{
		background: url(${staticRoot}/images/app/off01_pre.png) center no-repeat;
		background-size:60px 20px;

	}

	.label_a{
		vertical-align: top;
		text-decoration: underline;
	}

	.usr_msg{
		margin-top: 26px;
		line-height: 22px;
		margin-right: 20px;
		color: #fff;
		/*width: 260px;*/
	}
	/**/
	#menucyc-scroll #mCSB_1_container{ margin-right:0;}
	.mCS-dark.mCSB_scrollTools .mCSB_dragger .mCSB_dragger_bar{background-color: rgba(0,0,0,0.2)}
</style>
