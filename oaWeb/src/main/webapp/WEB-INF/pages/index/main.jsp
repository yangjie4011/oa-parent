<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">

<head>
    <%@include file="../common/common.jsp"%>
    <link rel="icon" href="data:;base64,=">
    <title>OA-首页</title>
    <meta charset="UTF-8">
    <script type="text/javascript" src="<%=basePath%>js/index/initMain.js?v=20200922"></script>
</head>


<body class="b-f2f2f2 mt-260">
	<input type="hidden" id="companyId" value="${requestScope.companyId }"/>
    <!-- <div class="return_heart" id="return_heart">
        <a href="#">随心邮</a>
    </div> -->
    <div class="oa-index_new">
        <header>
            <h1 class="clearfix">
                <a href="<%=basePath%>employee/indexPerson.htm" class="toperson fl">
                    <i class="personal sr"></i>
                </a>企业首页
                <a href="<%=basePath%>empMsg/index_mynews.htm" class="tonews fr">
                    <i class="news sr">
                        <div id="unReadMsgCount"></div>
                        <!-- <dd class="on"></dd> -->
                    </i>
                </a>
            </h1>

            <div class="hgc-userInfo clearfix">
                <div class="headInfo">
                	<!-- 图片默认为空，后台异步加载 -->
                    <div class="pic" id="empPic" style="background:#fff url('') no-repeat center;background-size:contain;-webkit-background-size:contain">
                        <%-- <img src="" alt=""> --%>
                    </div>
                    <span>${cnName }</span>
                    <em> </em>
                </div>
                <div class="workerInfo">
                    <p class="compony">${companyName }</p>
                    <p class="worker">${positionName }</p><!-- ${departName }  -->
                    <p class="email icon">${email }</p>
                    <p class="phone icon">${extensionNumber }</p>
                </div>
                <div class="logoInfo">
                    <img id = "logoImg" data="${oaUle }" src="http://i0.beta.ulecdn.com/ule/oa/i/hgc-icon-logo.png" width="100%" height="100%" />
                </div>
            </div>
	        <ul class="hgc-menuIndex">
		        <li>
		            <div class="menu-icon">
				        <a href="<%=basePath%>ruProcdef/my_examine.htm?urlType=1&current=0">
		                    <span class="hgc-icon-daiban"></span>
		                    <span id="unCount2">我的待办</span><!-- 我的待办 -->
				                    
				        </a>
		            </div>
		        </li>
	            <li>
		            <div class="menu-icon">
			            <a href = "<%=basePath%>runTask/index.htm?urlType=1">
		                   <span class="hgc-icon-shengqing"></span>
		                   <span>我的申请</span>
			            </a>   
		            </div> 
	            </li>
	            <li>
	                <div class="menu-icon">
	                    <a href = "<%=basePath%>employeeClass/myClassView.htm">
		                    <span class="hgc-icon-rili"></span>
		                    <span>工作日历</span>
	                    </a>
	                </div>
	            </li>
	            <li>
	                <div class="menu-icon">
	                    <a href="<%=basePath%>employeeApp/addressList.htm?urlType=1">
		                    <span class="hgc-icon-tongxunlu"></span>
		                    <span>通讯录</span>
	                    </a>
	                </div>
	            </li>
	        </ul>

        </header>

        <input type="hidden" value="10" id="totalTime" />
        <input type="hidden" value="2" id="partTime" />
        <div class="swiper-container hgc-attendance">
            <div class="swiper-wrapper">
                <div class="swiper-slide" id="attnDiv">
                    <div class="timeWrap">
                        <canvas width="100" height="100" id="arcAttendance" class="arcAttendance"></canvas>
                        <strong>
                            <em class="attnProportion"></em>%
                            <span>出勤率</span>
                        </strong>
                    </div>
                    <div class="timeAttendance">
                        <p>应出勤</p>
                        <p>
                            <span class="allAttnTime"></span><!--complete 175h -->
                            <em class="mustAttnTime"></em><!-- /176h -->
                            <i class="attnStatus" style="display:none">考勤异常</i>
                            <input type="hidden" id = "allAttnTime">
                            <input type="hidden" id = "mustAttnTime">
                        </p>
                    </div>
                </div>
                <div class="swiper-slide holidy-list"  id="leaveDiv">
                    <div class="holiday-year holiday">
                        <strong>年假</strong>
                        <span>剩余
                            <em id="leftYearLeave"></em>天</span>
                    </div>
                    <div class="holiday-ill holiday">
                        <strong>带薪病假</strong>
                        <span>剩余
                            <em id="leftSickLeave"></em>天</span>
                    </div>
                    <div class="holiday-rest holiday">
                        <strong>调休</strong>
                        <span>剩余
                            <em id="leftRestLeave"></em>h</span>
                    </div>
                </div>
            </div>
            <!-- 如果需要分页器 -->
            <div class="swiper-pagination"></div>
            <!-- 如果需要导航按钮 -->
            <div class="swiper-button-prev swiper-button"></div>
            <div class="swiper-button-next swiper-button"></div>
        </div>
        <div class="hgc-initiator first">
            <h3 class="clearfix">发起申请 <a href="<%=basePath%>login/indexAdd.htm?urlType=1" class="fr">更多</a></h3>
            <ul class="func-list">
                <c:if test="${workAddressType == 0}">
	                <li class="p1">
	                    <a href="<%=basePath%>empApplicationLeave/index.htm?urlType=1" title="请假">
	                        <i></i>请假</a>
	                </li>
	
	                <li class="p3">
	                    <a href="<%=basePath%>empApplicationAbnormalAttendance/index.htm?urlType=1" title="异常考勤">
	                        <i></i>异常考勤</a>
	                </li>
	                <li class="p8">
	                    <a href="<%=basePath%>empApplicationBusiness/index.htm?urlType=1" title="出差">
	                        <i></i>出差</a>
	                </li>
	                <li class="p9">
	                    <a href="<%=basePath%>empApplicationOutgoing/index.htm?urlType=1" title="外出">
	                        <i></i>外出</a>
	                </li> 
                </c:if>
                
                <c:if test="${workAddressType == 1}">
                    <c:if test="${workAddressProvince eq '北京'}">
	                	<li class="p1">
		                    <a href="<%=basePath%>empApplicationLeave/index.htm?urlType=1" title="请假">
		                        <i></i>请假</a>
		                </li>
		
		                <li class="p3">
		                    <a href="<%=basePath%>empApplicationAbnormalAttendance/index.htm?urlType=1" title="异常考勤">
		                        <i></i>异常考勤</a>
		                </li>
		                <li>
		                    <a href="<%=basePath%>locationCheckIn/index.htm?urlType=1" title="定位签到">
		                        <i style="background: url(../css/images/qiandao.png);background-size:contain;"></i>定位签到</a>
		                </li>
		                <li class="p8">
		                    <a href="<%=basePath%>empApplicationBusiness/index.htm?urlType=1" title="出差">
		                        <i></i>出差</a>
		                </li>
                 	</c:if>
                    <c:if test="${workAddressProvince ne '北京'}">
		                <li class="p8">
		                    <a href="<%=basePath%>empApplicationBusiness/index.htm?urlType=1" title="出差">
		                        <i></i>出差</a>
		                </li>
		                <li class="p9">
		                    <a href="<%=basePath%>empApplicationOutgoing/index.htm?urlType=1" title="外出">
		                        <i></i>外出</a>
		                </li> 
                 	</c:if>
                
                </c:if>
            </ul>
        </div>
        <div class="hgc-initiator">
            <h3 class="clearfix">其他应用 <a href="javascript:;" class="fr"><!-- 收起 --></a></h3>
            <ul class="func-list">

                <li class="p1">
                    <a id="sxyUlePeople" href="#" title="">
                        <span class="icon-youleren"></span>邮乐人</a>
                </li>
                <li class="p2">
                <a  id="sxyTomNews" href="#" title="">
                    <span class="icon-tom"></span>TOM资讯</a>
                </li>
                <li class="p3">
                <a id="sxyIcon" href="#" title="">
                    <span class="icon-suixinyou"></span>随心邮</a>
                </li>
                <li class="p4">
                <a id="sxyJdGame" href="#" title="">
                    <span class="icon-jindou"></span>金豆Game</a>
                </li>
                <!-- <li class="p4">
                <a href="" title="">
                    <span class="icon-kuaixun"></span>企业快讯</a>
                </li>   -->
                <li class="p4">
                <a id="sxySetting" href="#" title="">
                    <span class="icon-shezhi"></span>设置</a>
                </li>
            </ul>
        </div>
        <div class="theEnd">已经到底了啦～</div>
    </div>
</body>
<script>
	//判断数组中是否包含某字符串
	Array.prototype.contains = function(needle) {
		for (i in this) {
			if (this[i].indexOf(needle) > 0)
				return i;
		}
		return -1;
	}
	var devicePixelRatioWidth = window.screen.width * window.devicePixelRatio;
	var devicePixelRatioHeight = window.screen.height * window.devicePixelRatio;
	var userAgent = navigator.userAgent;//获取userAgent信息
	var isWeixin = userAgent.toLowerCase().indexOf('micromessenger') != -1;
	var isSaveExt = getCookieValue("isSaveExt");
	if(isWeixin){
		if(isSaveExt != "0") {
			$.ajax({
		   		async : false,
		   		type : "post",
		 		dataType:"json",
		 		data : {"userAgent":userAgent,"resolution":devicePixelRatioWidth + "*" +devicePixelRatioHeight},
		   		url : contextPath + "/employeeDevice/save.htm",
		   		success : function(data) {	   			
		   				
		   		}
			});
		}
	}
</script>
</html>