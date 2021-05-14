<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@include file="../../common/common.jsp"%>
<link rel="icon" href="data:;base64,=">
<title>新员工入职</title>
<link rel="stylesheet" href="http://i0.ulecdn.com/ule/oa/c/region.css?v=2018062223">
<script type="text/javascript" src="http://i0.ulecdn.com/ule/oa/j/region.js?v=20200302"></script>
<script type="text/javascript" src="<%=basePath%>js/base/employee/common_h5_select.js?v=20191111"></script>
<script type="text/javascript" src="<%=basePath%>js/base/empApplicationRegister/register.js?v=20200302"></script>

</head>
<body class="b-f2f2f2 mt-55 pb-54">
<div class="oa-new_woker_fillin oa-travel_apply">
        <header>
            <h1 class="clearfix"><a href="<%=basePath%>${returnUrl}" class="goback fl"><i class="back sr"></i></a>新员工入职<a href="#" class="save fr"></a></h1>
        </header>
        <section class="new-woker-info">
            <section class="name-info">
               <div class="selMatter clearfix">
	                <h4 class="fl" style="width:50%;">外包/实习生转内</h4>
	                <div class="selarea fr" style="width:50%;">
	                    <div id="typeSelGroup" class="singleSelGroup">
	                        <span id="showCode"><em type="1"><i class="sr"></i></em><b>是</b></span>
	                        <span id="hideCode"><em type="0" class="current"><i class="sr"></i></em><b>否</b></span>
	                    </div>
	                </div>
                </div>
                <div class="selMatter clearfix code" style="display:none;">
	                 <h4 class="fl">员工编号</h4>
	                 <div class="selarea fr">
	                     <input type="text" name="code" id="code">
	                 </div>
	            </div>
	            <div class="selMatter clearfix">
	                 <h4 class="fl">公司名称</h4>
	                 <div class="selarea fr companyName">
	                     <input type="text" data-id="" readonly placeholder="请选择" />
	                     <i class="sr icon"></i>
	                 </div>
	            </div>
	            <div class="selMatter clearfix">
	                 <h4 class="fl">员工类型</h4>
	                 <div class="selarea fr empType">
	                     <input type="text" data-id="" readonly placeholder="请选择" />
	                     <i class="sr icon"></i>
	                 </div>
	            </div>
	            <div class="selMatter clearfix">
	                 <h4 class="fl">工时种类</h4>
	                 <div class="selarea fr workType">
	                     <input type="text" data-id="" id="workType" readonly placeholder="请选择" />
	                     <i class="sr icon"></i>
	                 </div>
	            </div>
	            <div class="selMatter clearfix">
	                 <h4 class="fl">是否排班</h4>
	                 <div class="selarea fr whetherScheduling">
	                     <input type="text" data-id="" id="whetherScheduling" readonly placeholder="请选择" />
	                     <i class="sr icon"></i>
	                 </div>
	            </div>
	            <div class="selMatter clearfix">
	                 <h4 class="fl">中文名</h4>
	                 <div class="selarea fr">
	                     <input type="text" name="cnName" id="cnName">
	                 </div>
	            </div>
	            <div class="selMatter clearfix">
	                 <h4 class="fl">英文名</h4>
	                 <div class="selarea fr">
	                     <input type="text" name="engName" id="engName">
	                 </div>
	            </div>
	            <div class="selMatter clearfix">
	                <h4 class="fl">出生日期</h4>
	                <div class="selarea fr">
	                    <input type="text" readonly placeholder="同身份证" id="birthDate" data-lcalendar="1900-01-01,9999-12-12">
	                    <i class="sr icon"></i>
	                </div>
	            </div>
	            <div class="selMatter clearfix">
	                <h4 class="fl">性别</h4>
	                <div class="selarea fr">
	                    <div id="singleSelGroup" class="singleSelGroup">
	                        <span><em id="male" sex="0" class="current"><i class="sr"></i></em><b>男</b></span>
	                        <span><em id="female" sex="1"><i class="sr"></i></em><b>女</b></span>
	                    </div>
	                </div>
                </div>
	            <div class="selMatter clearfix">
	                 <h4 class="fl">手机号</h4>
	                 <div class="selarea fr">
	                     <input name="mobile" id="mobile" type="number" oninput="if(value.length>11)value=value.slice(0,11)">
	                 </div>
	            </div>
	            <div class="selMatter clearfix">
	                <h4 class="fl">入职日期</h4>
	                <div class="selarea fr">
	                    <input type="text" readonly placeholder="请选择日期" id="employmentDate" data-lcalendar="2017-01-01,9999-12-12">
	                    <i class="sr icon"></i>
	                </div>
	            </div>
	            <div class="selMatter clearfix">
	                 <h4 class="fl">部门</h4>
	                 <div class="selarea fr depart">
	                     <input type="text" data-id="" readonly placeholder="请选择" />
	                     <i class="sr icon"></i>
	                 </div>
	            </div>
	            <div id="group" class="selMatter clearfix" style="display:none;">
	                 <h4 class="fl">组别</h4>
	                 <div class="selarea fr group">
	                     <input type="text" data-id="" readonly placeholder="请选择" />
	                     <i class="sr icon"></i>
	                 </div>
	            </div>
	            <div class="selMatter clearfix">
	                 <h4 class="fl">职位</h4>
	                 <div class="selarea fr position">
	                     <input type="text" level="" data-id="" readonly placeholder="请选择" />
	                     <i class="sr icon"></i>
	                 </div>
	            </div>
	            <div class="selMatter clearfix">
	                 <h4 class="fl">职位序列</h4>
	                 <div class="selarea fr positionSeq">
	                     <input type="text" data-id="" readonly placeholder="请选择" />
	                     <i class="sr icon"></i>
	                 </div>
	            </div>
	            <div class="selMatter clearfix">
	                 <h4 class="fl">职级</h4>
	                 <div class="selarea fr positionLevel">
	                     <input type="text" data-id="" readonly placeholder="请选择" />
	                     <i class="sr icon"></i>
	                 </div>
	            </div>
	            <div class="selMatter clearfix">
	                 <h4 class="fl">汇报对象</h4>
	                 <div class="selarea fr leader">
	                     <input type="text" data-id="" readonly placeholder="请选择" />
	                     <i class="sr icon"></i>
	                 </div>
	            </div>
	            <div class="selMatter clearfix">
	                 <h4 class="fl">部门负责人</h4>
	                 <div class="selarea fr">
	                     <input id="departHeader" type="text" readonly="readonly"/>
	                     <i class="sr icon"></i>
	                 </div>
	            </div>
	            <div class="selMatter clearfix">
                <h4 class="fl">工作地点</h4>
                   <div class="selarea fr">
                    <input id="travelAddress0" class="hiddenTravelClass" type="text"  placeholder="请输入工作点" onclick=""/>
                	<input  type='hidden' class='hiddenTravelProvince beginEndProvince'  id="travelProvince" />   
                	<input  type='hidden'  class='hiddenTravelCity beginEndPCity' id="travelCity" />   
               	  </div>
                </div>
	            <div class="selMatter clearfix">
		            <h4 class="fl">备注</h4>
	            	<textarea id="remarks" placeholder="非必填" rows="5"></textarea>
            	</div>
        	</section>
        	<section class="leave-contacts leave-thing">
	            <h3>入职邮件通知(邮件将发送至代理人)</h3>
	            <p class="person-info clearfix">
	                <em class="fl">邮件抄送人</em><input id="toPersions" name="toPersions" type="text" class="fl" data-id="" placeholder="请选择入职通知邮件抄送人">
	            </p>
            </section>
	        <section class="leave-thing">
	            <h3>抄送邮箱</h3>
	            <textarea id="toEmails" name="toEmails" placeholder="可输入需另抄送邮箱以逗号隔开" cols="30" rows="10"></textarea>
	        </section>
        </section>
        <section class="foot-btn" onclick="save();">提交</section>
        <input type="hidden" id="token" name="token" value="${token}"/>
    </div>
</body>
</html>