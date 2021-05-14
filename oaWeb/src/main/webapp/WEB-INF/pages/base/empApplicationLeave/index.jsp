<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@include file="../../common/common.jsp"%>
<link rel="icon" href="data:;base64,=">
<title>请假</title>
<script type="text/javascript" src="<%=basePath%>js/base/empApplicationLeave/index.js?v=20200527"></script>
</head>
<body class="b-f2f2f2 mt-55 pb-54">
    <div class="oa-leave">
        <header>
            <h1 class="clearfix"><a href="<%=basePath%>${returnUrl}" class="goback fl"><i class="back sr"></i></a>请假<a href="#" class="toproblem fr"><i class="problem sr"></i></a></h1>
        </header>
        <section class="seltime">
           <div class="selTimeTpl">
                <div class="selMatter clearfix">
                    <h4 class="fl">假期类型</h4>
                    <div class="selarea fr holidayType">
                        <input type="text" data-id="" readonly placeholder="请选择" />
                        <i class="sr icon"></i>
                    </div>
                </div>
                <div class="selMatter clearfix normal-holiday">
                    <h4 class="fl">开始日期</h4>
                    <div class="selarea fr">
                        <input type="text" readonly placeholder="请选择开始时间" id="start-date" data-lcalendar="2017-06-17,9999-12-31">
                        <i class="sr icon"></i>
                    </div>
                </div>
                <div class="selMatter clearfix normal-holiday">
                    <h4 class="fl">结束日期</h4>
                    <div class="selarea fr">
                        <input type="text" readonly placeholder="请选择结束时间" id="end-date" data-lcalendar="2017-06-17,9999-12-31">
                        <i class="sr icon"></i>
                    </div>
                </div>
                <div class="selMatter clearfix datenum normal-holiday">
                    <h4 class="fl">请假天数</h4>
                    <div class="selarea fr">
                        
                    </div>
                </div>

                <div class="selMatter clearfix lactation-holiday">
                    <h4 class="fl">开始日期</h4>
                    <div class="selarea fr">
                        <input type="text" readonly placeholder="请选择开始时间" id="lactation-start-date" data-lcalendar="2017-01-01,9999-12-31">
                        <i class="sr icon"></i>
                    </div>
                </div>
                <div class="selMatter clearfix datenum lactation-holiday">
                    <h4 class="fl">结束日期</h4>
                    <div class="selarea fr lactation-holiday-end-time">
                        
                    </div>
                </div>
                <div class="selMatter clearfix datenum lactation-holiday">
                    <h4 class="fl">子女数</h4>
                    <div class="selarea fr childrenNum">
                        
                    </div>
                </div>
                <div class="selMatter clearfix datenum lactation-holiday">
                    <h4 class="fl">哺乳假小时数</h4>
                    <div class="selarea fr hourNum">
                        
                    </div>
                </div>
                <div class="selMatter clearfix lactation-holiday">
                    <h4 class="fl">请假时间</h4>
                    <div class="selarea fr holidayTime">
                        <input type="text" readonly placeholder="请选择" data-id=""/>
                        <i class="sr icon"></i>
                    </div>
                </div>
                <div class="selMatter clearfix datenum lactation-holiday" id="lactationDays" style="display:none;">
                    <h4 class="fl">请假天数</h4>
                    <div class="selarea fr">
                        
                    </div>
                </div>
                <div class="selMatter clearfix living-holiday">
                    <h4 class="fl">子女出生日期</h4>
                    <div class="selarea fr">
                        <input type="text" readonly placeholder="请选择开始时间" id="living-start-date" data-lcalendar="2017-06-17,9999-12-31">
                        <i class="sr icon"></i>
                    </div>
                </div>
                <div class="selMatter clearfix living-holiday">
                    <h4 class="fl">子女数量</h4>
                    <div class="selarea fr">
                        <input type="text" placeholder="请输入" id="living-child-num" data-lcalendar="2017-06-17,9999-12-31">
                    </div>
                </div>
                <div class="selMatter clearfix living-holiday">
                    <h4 class="fl">生产情况</h4>
                    <div class="selarea fr">
                        <div id="singleSelGroup" class="singleSelGroup">
                            <span><em data-id="100" class="current"><i class="sr"></i></em><b>顺产</b></span>
                            <span><em data-id="200" class=""><i class="sr"></i></em><b>难产</b></span>
                        </div>
                    </div>
                </div>
                <div class="selMatter clearfix datenum living-holiday">
                    <h4 class="fl">开始日期</h4>
                    <div class="selarea fr">
                        <input type="text" readonly placeholder="请选择开始时间" id="maternityLeaveStart" data-lcalendar="2017-06-17,9999-12-31">
                        <i class="sr icon"></i>
                    </div>
                </div>
                <div class="selMatter clearfix datenum living-holiday">
                    <h4 class="fl">结束日期</h4>
                    <div class="selarea fr">
                        <input type="text" readonly placeholder="请选择结束时间" id="maternityLeaveEnd" data-lcalendar="2017-06-17,9999-12-31">
                        <i class="sr icon"></i>
                    </div>
                </div>
                <div class="selMatter clearfix datenum living-holiday">
                    <h4 class="fl">请假天数</h4>
                    <div class="selarea fr">
                        
                    </div>
                </div>
                 <div class="selMatter clearfix abortion-holiday" style="display:none;">
                    <h4 class="fl">开始日期</h4>
                    <div class="selarea fr">
                        <input type="text" readonly placeholder="请选择开始时间" id="abortion-start-date" data-lcalendar="2017-06-17,9999-12-31">
                        <i class="sr icon"></i>
                    </div>
                </div>
                <div class="selMatter clearfix abortion-holiday" style="display:none;">
                    <h4 class="fl">妊娠周期</h4>
                    <div class="selarea fr abortionSel">
                        <input type="text" readonly placeholder="请选择" data-id="100" value="4个月以下"/>
                        <i class="sr icon"></i>
                    </div>
                </div>
                <div class="selMatter clearfix datenum abortion-holiday" style="display:none;">
                <h4 class="fl">结束日期</h4>
                <div class="selarea fr">

                </div>
            </div>
            <div class="selMatter clearfix datenum abortion-holiday" style="display:none;">
                <h4 class="fl">请假天数</h4>
                <div class="selarea fr">

                </div>
            </div>
            
             <div class="selMatter clearfix funeral-holiday" style="display:none;">
                    <h4 class="fl">亲属关系</h4>
                    <div class="selarea fr relatives">
                        <input type="text" readonly placeholder="请选择" data-id="100" value="父母"/>
                        <i class="sr icon"></i>
                    </div>
             </div>
             <div class="selMatter clearfix funeral-holiday" style="display:none;">
                 <h4 class="fl">开始日期</h4>
                 <div class="selarea fr">
                     <input type="text" readonly placeholder="请选择开始时间" id="funeral-date" data-lcalendar="2017-06-17,9999-12-31">
                     <i class="sr icon"></i>
                 </div>
             </div>
             <div class="selMatter clearfix funeral-holiday" style="display:none;">
                 <h4 class="fl">结束日期</h4>
                 <div class="selarea fr">
                 </div>
             </div>
             <div class="selMatter clearfix datenum funeral-holiday" style="display:none;">
                 <h4 class="fl">请假天数</h4>
                 <div class="selarea fr">
                     
                 </div>
             </div>
             
             <div class="selMatter clearfix antenatal-holiday" style="display:none;">
                 <h4 class="fl">开始日期</h4>
                 <div class="selarea fr">
                     <input type="text" readonly placeholder="请选择开始时间" id="antenatal-start-date" data-lcalendar="2017-06-17,9999-12-31">
                     <i class="sr icon"></i>
                 </div>
             </div>
             <div class="selMatter clearfix antenatal-holiday" style="display:none;">
                 <h4 class="fl">预产期</h4>
                 <div class="selarea fr">
                 <input type="text" readonly placeholder="请选择预产期" id="antenatal-end-date" data-lcalendar="2017-06-17,9999-12-31">
                     <i class="sr icon"></i>
                 </div>
             </div>
             <div class="selMatter clearfix datenum antenatal-holiday" style="display:none;">
                 <h4 class="fl">请假天数</h4>
                 <div class="selarea fr">
                     
                 </div>
             </div>
             
             <div class="selMatter clearfix marriage-holiday" style="display:none;">
                 <h4 class="fl">开始日期</h4>
                 <div class="selarea fr">
                     <input type="text" readonly placeholder="请选择开始时间" id="marriage-start-date" data-lcalendar="2017-06-17,9999-12-31">
                     <i class="sr icon"></i>
                 </div>
             </div>
             <div class="selMatter clearfix marriage-holiday" style="display:none;">
                 <h4 class="fl">结束日期</h4>
                 <div class="selarea fr">
                     
                 </div>
             </div>
             <div class="selMatter clearfix datenum marriage-holiday" style="display:none;">
                 <h4 class="fl">请假天数</h4>
                 <div class="selarea fr">
                     
                 </div>
             </div>
            </div>
        </section>

        <section class="leave-thing">
            <h3>请假事由</h3>
            <textarea id="reason" name="reason" placeholder="请输入请假事由" id="" cols="30" rows="10"></textarea>
        </section>

        <section class="leave-contacts">
            <h3>假期联系信息</h3>
            <p class="person-info clearfix">
                <em class="fl">紧急联系电话</em><input id="mobile" name="mobile" type="number" oninput="if(value.length>11)value=value.slice(0,11)"  value="${mobile}" class="fl" placeholder="请输入紧急联系电话">
            </p>
            <p class="person-info clearfix">
                <em class="fl">假期代理人</em><input id="agentId" name="agentId" type="text" class="fl" data-id="" placeholder="请选择假期代理人" readonly="readonly">
            </p>
            <p class="person-info clearfix">
                <em class="fl">代理人电话</em><input id="agentMobile" name="agentMobile" type="number" oninput="if(value.length>11)value=value.slice(0,11)" value="${managerMobile }"  class="fl" placeholder="请输入假期代理人电话">
            </p>
        </section>
        
        
        <section class="leave-contacts leave-thing">
            <h3>请假邮件通知(邮件将发送至代理人)</h3>
            <p class="person-info clearfix">
                <em class="fl">邮件抄送人</em><input id="toPersions" name="toPersions" type="text" class="fl" data-id="" placeholder="请选择请假通知邮件抄送人">
            </p>
        </section>
        <section class="leave-thing">
            <h3>抄送邮箱</h3>
            <textarea id="toEmails" name="toEmails" placeholder="可输入需另抄送邮箱以逗号隔开" cols="30" rows="10"></textarea>
        </section>
        <input type="hidden" id="leaveType"  />
        <input type="hidden" id="token" name="token" value="${token}"/>
        <input type="hidden" id="employeeId"  value="${employeeId}"/>
        <section class="subbtn" id="save" onclick="save();">提交</section>
    </div>  
</body>
</html>