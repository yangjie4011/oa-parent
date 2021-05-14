<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@include file="../common/common.jsp"%>
<link rel="icon" href="data:;base64,=">
<title>每日健康打卡</title>
<link rel="stylesheet" href="http://i0.ulecdn.com/ule/oa/c/region.css?v=2018062223">
<script type="text/javascript" src="http://i0.ulecdn.com/ule/oa/j/region.js?v=2018062338"/></script>
<script type="text/javascript" src="<%=basePath%>js/dayHeath/sign.js?v=20200218"/></script>
</head>
<body class="b-f2f2f2 mt-44">
    <div class="oa-out_apply">
        <header>
            <h1 class="clearfix"><a class="fl"><i class="back sr"></i></a>每日健康打卡<a href="#" class="save fr"></a></h1>
        </header>

  		<c:if test="${isSign}">
            <section class="edit-info">
               <p class="notice" style="font-size:14px;">今日您已完成健康上报。</p>
            </section>
        </c:if>
        <c:if test="${!isSign}">
           <section class="edit-info">
           
            <c:if test="${needBaseInfo}">
            	<div class="selMatter clearfix">
	               <div class='insertBox'  id='insertBox1'>
		                <h4 class="fl">1、户籍</h4>
		                <div class="selarea fr">
		                    <input readonly="readonly" id="answer_1" value="${answer_1}" class="hiddenTravelClass travelAddressVla" type="text" name="adressVal"  placeholder="请填写省市" onclick=""/>
		                </div>
		            </div>
	            </div>
	            
	            <div class="selMatter clearfix">
	            	<div class='insertBox'  id='insertBox1'>
		                <h4 class="fl">2、籍贯</h4>
		                <div class="selarea fr">
		                    <input readonly="readonly" id="answer_2" value="${answer_2}" class="hiddenTravelClass travelAddressVla" type="text" name="adressVal"  placeholder="请填写省市" onclick=""/>
		                </div>
		            </div>
	            </div>
	          
	            <div class="selMatter clearfix datenum texta">
	                <h4 class="fl" style="width:100%;" id="question_3">3、现上海常住地址</h4>
	                <textarea id="answer_3" rows="5" placeholder="市-区-街道/镇-路-详细地址">${answer_3}</textarea>
	            </div>
	            
	            <div class="selMatter clearfix datenum texta">
	                <h4 class="fl" style="width:100%;" id="question_4">4、是否接触过湖北地区人员</h4>
	                <div class="selarea fr" style="float:left;width:100%;text-align:left;">
	                    <div id="answer_4" class="singleSelGroup">
	                        <span><em <c:if test="${answer_4=='是'}">class="current"</c:if>  answer="是"><i class="sr"></i></em><b>是</b></span>
	                        <span><em <c:if test="${answer_4=='否'}">class="current"</c:if> answer="否"><i class="sr"></i></em><b>否</b></span>
	                    </div>
	                </div>
	            </div>
	           
	           <div class="selMatter clearfix datenum texta">
	                <h4 class="fl" style="width:100%;" id="question_5">5、2020年春节休假是否离开过上海(或工作城市)</h4>
	                <div class="selarea fr" style="float:left;width:100%;text-align:left;">
	                    <div class="singleSelGroup" id="answer_5">
	                        <span><em  <c:if test="${answer_5=='是'}">class="current"</c:if> answer="是"><i class="sr"></i></em><b>是</b></span>
	                        <span><em  <c:if test="${answer_5=='否'}">class="current"</c:if> answer="否"><i class="sr"></i></em><b>否</b></span>
	                    </div>
	                </div>
	           </div>
	         
	           <div class="selMatter clearfix datenum texta isShow">
	                <h4 class="fl" style="width:100%;" id="question_6">6、是否去过或途径湖北？</h4>
	                <div class="selarea fr" style="float:left;width:100%;text-align:left;">
	                    <div id="answer_6" class="singleSelGroup">
	                        <span><em <c:if test="${answer_6=='是'}">class="current"</c:if> answer="是"><i class="sr"></i></em><b>是</b></span>
	                        <span><em <c:if test="${answer_6=='否'}">class="current"</c:if> answer="否"><i class="sr"></i></em><b>否</b></span>
	                    </div>
	                </div>
	           </div>
	         
	           <div class="selMatter clearfix datenum texta isShow">
	                <h4 class="fl" style="width:100%;" id="question_7">7、离开上海（或工作城市）去的地方</h4>
	                <textarea id="answer_7" rows="5" placeholder="每个地方都需要填写">${answer_7}</textarea>
	           </div>
	           
	           <div class="selMatter clearfix isShow">
	                <h4 class="fl" id="question_8">8、离沪日期</h4>
	                <div class="selarea fr">
	                    <input value="${answer_8}" id="answer_8" type="text" readonly placeholder="请选择离沪日期" data-lcalendar="2017-01-01,9999-12-12">
	                    <i class="sr icon"></i>
	                </div>
	            </div>
	            
	            <div class="selMatter clearfix isShow">
	                <h4 class="fl" id="question_9">返沪日期</h4>
	                <div class="selarea fr">
	                    <input value="${answer_9}"  id="answer_9" type="text" readonly placeholder="请选择返沪日期" data-lcalendar="2017-01-01,9999-12-12">
	                    <i class="sr icon"></i>
	                </div>
	            </div>
	            
	            <div class="selMatter clearfix isShow">
	                <h4 class="fl" style="width:100%;height:68px;color:red;line-height:30px;">注意：未返沪也不能确定返沪日期的，请统一填写2021年1月1日</h4>
	            </div>
	            
	            <div class="selMatter clearfix datenum texta isShow">
	                <h4 id="question_10" class="fl" style="width:100%;">出行方式</h4>
	                <textarea id="answer_10" rows="5" placeholder="自驾、飞机应填航班号、火车应填班次等">${answer_10}</textarea>
	            </div>
            
            
            </c:if>
           
          	<div class="selMatter clearfix">
                <div class='insertBox'  id='insertBox1'>
	                <h4 class="fl"><c:if test="${needBaseInfo}">9、</c:if><c:if test="${!needBaseInfo}">1、</c:if>当前所在地</h4>
	                <div class="selarea fr">
	                    <input readonly="readonly" id="answer_11" value="${answer_11}" class="hiddenTravelClass travelAddressVla" type="text" name="adressVal"  placeholder="请填写省市" onclick=""/>
	                </div>
		        </div>
            </div>
         
            <div class="selMatter clearfix datenum texta answer_12">
                <h4 id="question_12" class="fl" style="width:100%;"><c:if test="${needBaseInfo}">10、</c:if><c:if test="${!needBaseInfo}">2、</c:if>今天你的个人健康状况</h4>
               
                <div class="selarea fr" style="float:left;width:100%;text-align:left;">
                    <div class="singleSelGroup">
                        <span><em answer="良好"><i class="sr"></i></em><b>良好</b></span>
                    </div>
                 
                </div>
                
                <div class="selarea fr" style="float:left;width:100%;text-align:left;">
     
                    <div  class="singleSelGroup">
                        <span><em answer="感冒无发热（37.2°及以下）"><i class="sr"></i></em><b>感冒无发热（37.2°及以下）</b></span>
                    </div>
                </div>
                
                <div class="selarea fr" style="float:left;width:100%;text-align:left;">
     
                    <div  class="singleSelGroup">
                        <span><em answer="发热、咳嗽、头疼乏力或呼吸困难等症状（发热37.3°及以上）"><i class="sr"></i></em><b>发热、咳嗽、头疼乏力或呼吸困难等症状</br>（发热37.3°及以上）</b></span>
                    </div>
                </div>
                
                <div class="selarea fr"  style="margin-top:30px;float:left;width:100%;text-align:left;">
                     <textarea id="answer_12_extra_1" rows="5" placeholder="填写发热度数，具体症状描述"></textarea>
                </div>
                
                <div class="selarea fr" style="margin-top:20px;float:left;width:100%;text-align:left;">
     
                    <div  class="singleSelGroup">
                        <span><em answer="疑似新冠肺炎"><i class="sr"></i></em><b>疑似新冠肺炎</b></span>
                    </div>
                </div>
                
                <div class="selarea fr" style="float:left;width:100%;text-align:left;">
     
                    <div  class="singleSelGroup">
                        <span><em answer="确诊新冠肺炎"><i class="sr"></i></em><b>确诊新冠肺炎</b></span>
                    </div>
                </div>
                
                <div class="selarea fr" style="float:left;width:100%;text-align:left;">
     
                    <div  class="singleSelGroup">
                        <span><em answer="以上均不是，请填写。。。"><i class="sr"></i></em><b>以上均不是，请填写。。。</b></span>
                    </div>
                </div>
                <div class="selarea fr"  style="float:left;width:100%;text-align:left;">
                     <textarea id="answer_12_extra_2" rows="5" placeholder="以上均不是，请填写。。。"></textarea>
                </div>
           
            </div>
          
         
            <div style="margin-top:10px;" class="selMatter clearfix datenum texta answer_13">
                <h4 id="question_13" class="fl" style="width:100%;"><c:if test="${needBaseInfo}">11、</c:if><c:if test="${!needBaseInfo}">3、</c:if>是否封城封村</h4>
               
                <div class="selarea fr" style="float:left;width:100%;text-align:left;">
                    <div class="singleSelGroup">
                        <span><em answer="封城"><i class="sr"></i></em><b>封城</b></span>
                    </div>
                 
                </div>
                
                <div class="selarea fr" style="float:left;width:100%;text-align:left;">
     
                    <div  class="singleSelGroup">
                        <span><em answer="封村"><i class="sr"></i></em><b>封村</b></span>
                    </div>
                </div>
                
                <div class="selarea fr" style="float:left;width:100%;text-align:left;">
     
                    <div  class="singleSelGroup">
                        <span><em answer="均未封"><i class="sr"></i></em><b>均未封</b></span>
                    </div>
                </div>
                
                <div class="selarea fr" style="float:left;width:100%;text-align:left;">
     
                    <div  class="singleSelGroup">
                        <span><em answer="以上均不是，请填写。。。"><i class="sr"></i></em><b>以上均不是，请填写。。。</b></span>
                    </div>
                </div>
                <div class="selarea fr"  style="float:left;width:100%;text-align:left;">
                     <textarea id="answer_13_extra" rows="5" placeholder="以上均不是，请填写。。。"></textarea>
                </div>
           
            </div>
          
            <div style="margin-top:10px;" class="selMatter clearfix datenum texta answer_14">
                <h4 id="question_14" class="fl" style="width:100%;"><c:if test="${needBaseInfo}">12、</c:if><c:if test="${!needBaseInfo}">4、</c:if>被隔离情况</h4>
               
                <div class="selarea fr" style="float:left;width:100%;text-align:left;">
                    <div class="singleSelGroup">
                        <span><em answer="医学隔离"><i class="sr"></i></em><b>医学隔离</b></span>
                    </div>
                 
                </div>
                
                <div class="selarea fr" style="float:left;width:100%;text-align:left;">
     
                    <div  class="singleSelGroup">
                        <span><em answer="小区被隔离"><i class="sr"></i></em><b>小区被隔离</b></span>
                    </div>
                </div>
                
                <div class="selarea fr" style="float:left;width:100%;text-align:left;">
     
                    <div  class="singleSelGroup">
                        <span><em answer="居住大楼被隔离"><i class="sr"></i></em><b>居住大楼被隔离</b></span>
                    </div>
                </div>
                
                <div class="selarea fr" style="float:left;width:100%;text-align:left;">
     
                    <div  class="singleSelGroup">
                        <span><em answer="14天返沪期内居家隔离"><i class="sr"></i></em><b>14天返沪期内居家隔离</b></span>
                    </div>
                </div>
                
                <div class="selarea fr" style="float:left;width:100%;text-align:left;">
     
                    <div  class="singleSelGroup">
                        <span><em answer="无需隔离"><i class="sr"></i></em><b>无需隔离</b></span>
                    </div>
                </div>
           
            </div>
         
            <div class="selMatter clearfix datenum texta answer_15">
                <h4 id="question_15" class="fl" style="width:100%;"><c:if test="${needBaseInfo}">13、</c:if><c:if test="${!needBaseInfo}">5、</c:if>你近期是否有密切接触以下人员：（含家属）</h4>（多选）
               
                <div class="selarea fr" style="float:left;width:100%;text-align:left;">
                    <div class="singleSelGroup">
                        <span><em answer="2020年1月在湖北逗留过的人员"><i class="sr"></i></em><b>2020年1月在湖北逗留过的人员</b></span>
                    </div>
                 
                </div>
                
                <div class="selarea fr" style="float:left;width:100%;text-align:left;">
     
                    <div  class="singleSelGroup">
                        <span><em answer="新冠肺炎确诊患者"><i class="sr"></i></em><b>新冠肺炎确诊患者</b></span>
                    </div>
                </div>
                
                <div class="selarea fr" style="float:left;width:100%;text-align:left;">
     
                    <div  class="singleSelGroup">
                        <span><em answer="新冠肺炎确诊患者有密切接触的人员"><i class="sr"></i></em><b>新冠肺炎确诊患者有密切接触的人员</b></span>
                    </div>
                </div>
                
                <div class="selarea fr" style="float:left;width:100%;text-align:left;">
     
                    <div  class="singleSelGroup">
                        <span><em answer="新冠肺炎疑似患者"><i class="sr"></i></em><b>新冠肺炎疑似患者</b></span>
                    </div>
                </div>
                
                <div class="selarea fr" style="float:left;width:100%;text-align:left;">
     
                    <div  class="singleSelGroup">
                        <span><em answer="新冠肺炎疑似患者有密切接触的人员"><i class="sr"></i></em><b>新冠肺炎疑似患者有密切接触的人员</b></span>
                    </div>
                </div>
                
                 <div class="selarea fr" style="float:left;width:100%;text-align:left;">
     
                    <div  class="singleSelGroup">
                        <span><em answer="以上皆否"><i class="sr"></i></em><b>以上皆否</b></span>
                    </div>
                </div>
           
            </div>
         
           <div  class="selMatter clearfix datenum texta answer_16">
                <h4 id="question_16" class="fl" style="width:100%;"><c:if test="${needBaseInfo}">14、</c:if><c:if test="${!needBaseInfo}">6、</c:if>家属健康情况</h4>
               
                <div class="selarea fr" style="float:left;width:100%;text-align:left;">
                    <div class="singleSelGroup">
                        <span><em answer="均健康"><i class="sr"></i></em><b>均健康</b></span>
                    </div>
                 
                </div>
                
                <div class="selarea fr" style="float:left;width:100%;text-align:left;">
     
                    <div  class="singleSelGroup">
                        <span><em answer="感冒无发热（37.2°及以下）"><i class="sr"></i></em><b>感冒无发热（37.2°及以下）</b></span>
                    </div>
                </div>
                
                <div class="selarea fr" style="float:left;width:100%;text-align:left;">
     
                    <div  class="singleSelGroup">
                        <span><em answer="发热、咳嗽、头疼乏力或呼吸困难等症状（发热37.3°及以上）"><i class="sr"></i></em><b>发热、咳嗽、头疼乏力或呼吸困难等症状<br/>（发热37.3°及以上）</b></span>
                    </div>
                </div>
                
                <div class="selarea fr" style="margin-top:30px;float:left;width:100%;text-align:left;">
     
                    <div  class="singleSelGroup">
                        <span><em answer="疑似新冠肺炎"><i class="sr"></i></em><b>疑似新冠肺炎</b></span>
                    </div>
                </div>
                
                <div class="selarea fr" style="float:left;width:100%;text-align:left;">
     
                    <div  class="singleSelGroup">
                        <span><em answer="确诊新冠肺炎"><i class="sr"></i></em><b>确诊新冠肺炎</b></span>
                    </div>
                </div>

            </div>
            

        </section>
         <input type="hidden" id="token" name="token" value="${token}"/>
         <input type="hidden" id="needBaseInfo" name="needBaseInfo" value="${needBaseInfo}"/>
        <div class="btn" onclick="save();">提交</div>
           
        </c:if>



        
    </div>
</body>
</html>