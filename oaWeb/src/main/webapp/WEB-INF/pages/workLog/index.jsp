<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@include file="../common/common.jsp"%>
<link rel="icon" href="data:;base64,=">
<style>
    .del{
        float:right;
        display: block;
	    width: 20px;
	    height: 20px;
	    padding: 10px;
	    cursor: pointer;
	    background: url(../css/images/paiban_01.png) no-repeat center;
	    background-size: 17px 17px;
    }
    
    .add{
        vertical-align: bottom;
        display: block;
	    width: 55px;
	    height: 30px;
	    line-height: 30px;
	    text-align:right;
	    color: #24b2f6;
	    float: right;
	    cursor: pointer;
	    background: url(../css/images/paiban_02.png) no-repeat left;
	    background-size: 17px 17px;
    }
</style>
<title>员工日志</title>
<script type="text/javascript" src="<%=basePath%>js/workLog/index.js?v=20200309"/></script>
</head>
<body class="b-f2f2f2 mt-44">
    <div class="oa-travel_apply">
        <header>
            <h1 class="clearfix"><a href="<%=basePath%>${returnUrl}" class="goback fl"><i class="back sr"></i></a>员工日志<a href="<%=basePath%>workLog/notice.htm" class="toproblem fr"><i class="problem sr"></i></a></h1>
       		       		
        </header>

        <section class="edit-info">
       		<p class="notice" style="height: 60px;line-height: 20px;">当日0点至次日24点开放当天的工作日志填写，过时不可补填。工作日志将作为工作绩效评估和人才发展的重要依据之一，请大家认真填写。</p>
           
            <div class="selMatter clearfix datenum">
                <h4 class="fl">日期：</h4>
                <div id="workDate" class="selarea fr">
                    ${workDate }
                </div>
            </div>
            <div style="width:100%;"><span><h3 style="float:left;width:60%;"><span style="color:red;font-weight:bold;">*</span> 工作内容与输出：</h3></span><span onclick="addWorkContent();" style="float:right;padding-right:10px;font-size:14px;" class="add">新增</span></div>
            
            <div class="selMatter clearfix texta workContent">
                <textarea style="width:80%;float:left;" rows="5" placeholder="填写当日完成工作进度、工作任务或工作产出（限100字）"></textarea>
            </div>
            
            <div style="width:100%;"><span><h3 style="float:left;width:60%;"><span style="color:red;font-weight:bold;">*</span> 下一个工作日工作计划：</h3></span><span onclick="addWorkPlan();" style="float:right;padding-right:10px;font-size:14px;" class="add">新增</span></div>

            <div class="selMatter clearfix texta workPlan">
                <textarea style="width:80%;float:left;" rows="5" placeholder="（限100字）"></textarea>
            </div>
            
            <div style="width:100%;"><span><h3 style="float:left;width:60%;">遇到的困难和问题：</h3></span><span onclick="addWorkProblem();" style="float:right;padding-right:10px;font-size:14px;" class="add">新增</span></div>
            
            <div class="selMatter clearfix texta workProblem">
                <textarea style="width:80%;float:left;" rows="5" placeholder="（限100字）"></textarea>
            </div>

            <input type="hidden" id="token" name="token" value="${token}"/>
            <div class="btn" onclick="save()">提交</div>
        </section>

    </div>
</body>
</html>