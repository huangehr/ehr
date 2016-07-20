<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>处方笺</title>
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <style>
        body{
            font-family:宋体, Sim sun, 宋体;
            font-size: 14px;
        }
        td div{
            padding-left: 10px;
            height:20px;
            line-height: 20px;
        }
    </style>
</head>
<body style="margin:0;padding:0; -webkit-box-sizing: border-box; -moz-box-sizing: border-box; box-sizing: border-box; ">

  <div style="padding: 0 10px;">
      <div style="padding: 0 10px;">
          <div style="height: 50px; line-height: 50px; text-align: center; font-weight: bold;font-size: 25px;"> 处方笺 </div>

          <table style="width:100%;line-height: 20px;">
              <tr>
                  <td style="padding-left: 85px; position:relative;"><span style="position: absolute;left:0;">门诊/住院号：</span><div style="border-bottom: 1px solid #232323;">${(event_no)!}</div></td>
                  <td style="padding-left: 40px; position:relative;"><span style="position: absolute;left:0;">科室：</span><div style="border-bottom: 1px solid #232323;">${(data_sets.HDSC01_09[0].HDSD00_04_005)!}</div></td>
                  <td style="padding-left: 70px; position:relative;"><span style="position: absolute;left:0;">开方时间：</span><div style="border-bottom: 1px solid #232323;">${(data_sets.HDSC01_09[0].HDSD00_04_006)!}</div></td>
              </tr>
              <tr>
                  <td style="padding-left: 40px; position:relative;"><span style="position: absolute;left:0;">姓名：</span><div style="border-bottom: 1px solid #232323;">${(data_sets.HDSA00_01[0].HDSD00_01_002)!}</div></td>
                  <td style="padding-left: 40px; position:relative;"><span style="position: absolute;left:0;">性别：</span><div style="border-bottom: 1px solid #232323;">${(data_sets.HDSA00_01[0].HDSA00_01_011_VALUE)!}</div></td>
                  <td style="padding-left: 40px; position:relative;"><span style="position: absolute;left:0;">年龄：</span><div style="border-bottom: 1px solid #232323;">${(data_sets.HDSA00_01[0].HDSA00_01_012)!}</div></td>
              </tr>
              <tr>
                  <td style="padding-left: 70px; position:relative;"><span style="position: absolute;left:0;">就诊类型：</span><div style="border-bottom: 1px solid #232323;">门诊</div></td>
                  <td style="padding-left: 75px; position:relative;" colspan="2"><span style="position: absolute;left:0;">地址/电话：</span><div style="border-bottom: 1px solid #232323;">${(data_sets.HDSA00_01[0].JDSA00_01_000)!}/${(data_sets.HDSA00_01[0].HDSD00_01_008)!}</div></td>
              </tr>
              <tr>
                  <td style="padding-left: 70px; position:relative;" colspan="3"><span style="position: absolute;left:0;">临床诊断：</span><div style="border-bottom: 1px solid #232323;">${(data_sets.HDSC01_03[0].HDSD00_01_550_VALUE)!}</div></td>
              </tr>
          </table>
      </div>
      <div style="margin-top: 20px;border-top: 3px solid;"></div>
      <div style="padding: 0 30px;min-height: 400px;">
          <div style="padding: 10px 30px;font-size: 25px;font-weight: bold;">
              R<span style="vertical-align: sub;">P</span>
          </div>
          <table style="width: 100%;">
          <#list data_sets.HDSC01_04 as item>
              <tr style="line-height: 20px;">
                  <td>${item_index+1}、${(item.HDSD00_04_023)!}</td>
                  <td>${(item.HDSD00_04_022_VALUE)!}</td>
                  <td>${(item.HDSD00_04_027_VALUE)!}</td>
                  <td>${(item.HDSD00_04_028)!}${(item.HDSD00_04_025)!}</td>
                  <td>${(item.HDSD00_04_024)!}${(item.HDSD00_04_025)!}</td>
                  <td>${(item.HDSD00_04_026_VALUE)!}</td>
              </tr>
          </#list>
          </table>
      </div>
      <div style="margin-top: 20px;border-top: 3px solid;"></div>
      <div style="padding: 20px 10px;">
          <table style="width:100%;line-height: 20px;">
              <tr>
                  <td style="padding-left: 70px; position:relative;"><span style="position: absolute;left:0;">医师签名：</span><div style="border-bottom: 1px solid #232323;">${(data_sets.HDSC01_09[0].HDSD00_04_007)!}</div></td>
                  <td style="padding-left: 40px; position:relative;">&nbsp;</td>
                  <td style="padding-left: 100px; position:relative;"><span style="position: absolute;left:0;">审核调配药师：</span><div style="border-bottom: 1px solid #232323;">${(data_sets.HDSC01_09[0].HDSD00_04_009)!}&nbsp;${(data_sets.HDSC01_09[0].HDSD00_04_010)!}</div></td>
              </tr>
              <tr>
                  <td style="padding-left: 85px; position:relative;">&nbsp;</td>
                  <td style="padding-left: 40px; position:relative;">&nbsp;</td>
                  <td style="padding-left: 100px; position:relative;"><span style="position: absolute;left:0;">复核发药药师：</span><div style="border-bottom: 1px solid #232323;">${(data_sets.HDSC01_09[0].HDSD00_04_004)!}&nbsp;${(data_sets.HDSC01_09[0].HDSD00_04_003)!}</div></td>
              </tr>
          </table>
      </div>

  </div>

</body>
</html>
