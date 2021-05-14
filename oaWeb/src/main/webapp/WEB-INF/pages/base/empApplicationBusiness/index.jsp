<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@include file="../../common/common.jsp"%>
<link rel="icon" href="data:;base64,=">
<title>出差申请</title>

<link rel="stylesheet" href="http://i0.ulecdn.com/ule/oa/c/region.css?v=2018062223">
<script type="text/javascript" src="http://i0.ulecdn.com/ule/oa/j/region.js?v=2018062338"></script>
<script type="text/javascript" src="<%=basePath%>js/base/empApplicationBusiness/index.js?v=20190701"></script>

</head>
<body class="b-f2f2f2 mt-44">
    <div class="oa-travel_apply">
        <header>
            <h1 class="clearfix"><a href="<%=basePath%>${returnUrl}" class="goback fl"><i class="back sr"></i></a>出差申请<a href="#" class="toproblem fr"><i class="problem sr"></i></a></h1>
       		       		
        </header>

        <section class="edit-info">
       		 <p class="notice">出差申请前请仔细阅读右上角出差帮助说明</p>
            <h3>出差计划</h3>
            <input  type='hidden'  id="addressNum" />
           <div class="selMatter clearfix">
                <h4 class="fl">去程日期</h4>
                <div class="selarea fr">
                    <input type="text" readonly placeholder="请选择日期" id="go-date" data-lcalendar="2017-01-01,9999-12-12">
                    <i class="sr icon"></i>
                </div>
            </div>
            <div class="selMatter clearfix">
                <h4 class="fl">返程日期</h4>
                <div class="selarea fr">
                    <input type="text" readonly placeholder="请选择日期" id="back-date" data-lcalendar="2017-01-01,9999-12-12">
                    <i class="sr icon"></i>
                </div>
            </div>
            <div class="selMatter clearfix datenum">
                <h4 class="fl">时长</h4>
                <div duration="0" id="duration" class="selarea fr">
                    0 天
                </div>
            </div>
            
            <div class="selMatter clearfix">
                <h4 class="fl">出差始发地</h4>
                   <div class="selarea fr">
                    <input id="travelAddress0" class="hiddenTravelClass" type="text"  placeholder="请输入出差始发地" onclick=""/>
                	<input  type='hidden' class='hiddenTravelProvince beginEndProvince'  id="travelProvince" />   
                	<input  type='hidden'  class='hiddenTravelCity beginEndPCity' id="travelCity" />   
               	  </div>
            </div>
            
            <div class="selMatter clearfix">
            <div class='insertBox'  id='insertBox1'>
                <h4 class="fl">地点</h4>
                <div class="selarea fr">
                    <input id="travelAddress1" class="hiddenTravelClass travelAddressVla" type="text" name="adressVal"  placeholder="请输入出差地点" onclick=""/>
                	<input  type='hidden' class='hiddenTravelProvince' id="travelProvince" />   
                	<input  type='hidden' class='hiddenTravelCity' id="travelCity" />   
                </div>
            </div>
            </div>
            <div class="selMatter clearfix addAddress" id="addAddress" style="display:none">
                
            </div>
            
            <div class="addAddressNum" id="addAddressNum" style="display:none">
                <input type="hidden" id="addAddressNum2"  value="0"/>
                <input type="hidden" id="addAddressNum3"  value="0"/>
                <input type="hidden" id="addAddressNum4"  value="0"/>
                <input type="hidden" id="addAddressNum5"  value="0"/>
            </div>
            
            <div class="selMatter clearfix datenum">
                <h4 class="fl2">出差行程</h4>
                    	<span id="beginTravelEnd" style='width:330px;height:70px;'></span>
            </div>
            

            <div class="selMatter clearfix">
                <h4 class="fl">交通工具</h4>
                <div class="selarea fr">
                    <div id="singleSelGroup" class="singleSelGroup">
                        <span><em vehicle="100" class="current"><i class="sr"></i></em><b>火车</b></span>
                        <span><em vehicle="200"><i class="sr"></i></em><b>飞机</b></span>
                    </div>
                </div>
            </div>
            <div class="selMatter clearfix texta">
                <h4 class="fl">出差事由</h4>
                <div class="selarea fr">
                    <div id="singleSelContentGroup" class="singleSelGroup">
                        <span><em businessType="100" class="current"><i class="sr"></i></em><b>项目</b></span>
                        <span><em businessType="200"><i class="sr"></i></em><b>业务</b></span>
                    </div>
                </div>
                <textarea id="reason" rows="5" placeholder="请填写项目／业务名称"></textarea>
            </div>
           <h3 id="detail" style="display:none;">每日行程及工作计划安排</h3>
           <div class="textinput">
            </div>
            <input type="hidden" id="token" name="token" value="${token}"/>
            <div class="btn" onclick="save()">提交</div>
        </section>

    </div>
</body>
</html>