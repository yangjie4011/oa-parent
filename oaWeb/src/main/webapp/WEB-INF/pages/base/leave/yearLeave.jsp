<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">

<head>
    <%@include file="../../common/common.jsp"%>
    <link rel="icon" href="data:;base64,=">
    <title>年假详情</title>
    <meta charset="UTF-8">
</head>
<body class="b-f2f2f2 mt-55">
    <div class="oa-vacation_year oa-vacation_holiday">
        <header>
            <h1 class="clearfix"><a href="javascript:window.history.go(-1)" class="goback fl"><i class="back sr"></i></a>年假详情<a href="#" class="save fr"></a></h1>
        </header>
        <section class="year-artical">
            <div class="year-info current">
                <div class="head">
                    <p class="days">
                        <span>总剩余年假 </span><span><em class="big">${lastYearRemain + thisYearRemain }</em> 天</span>
                    </p>
                </div>
                <div class="last-year">
                    <div class="blue">去年</div>
                    <p class="title">
	                    <c:if test="${!empty lastYearStartTime}">
	                                                          有效期：<fmt:formatDate type="date" value="${lastYearStartTime }" pattern="yyyy-MM-dd"/>至<fmt:formatDate type="date" value="${lastYearEndTime }" pattern="yyyy-MM-dd"/>
	                    </c:if>
                    </p>
                    <p class="days">
                        <span>去年剩余年假</span><span><em><fmt:formatNumber type="number" pattern="#.#" value="${lastYearRemain }"/></em> 天</span>
                    </p>
                </div>
                <div class="now-year">
                    <div class="blue">今年</div>
                    <p class="title">有效期：<fmt:formatDate type="date" value="${thisYearStartTime }" pattern="yyyy-MM-dd"/>至<fmt:formatDate type="date" value="${thisYearEndTime }" pattern="yyyy-MM-dd"/></p>
                    <p class="days">
                        <span>当年年假总天数</span><span><em><fmt:formatNumber type="number" pattern="#.#" value="${thisYearAllowRemain }"/></em> 天</span>
                    </p>
                    <div class="open-btn">展开</div>
                    <div class="all-days">
                        <p class="days">
                            <span>法定年假</span><span><em><fmt:formatNumber type="number" pattern="#.#" value="${thisYearLawDays }"/></em> 天</span>
                        </p>
                        <p class="days">
                            <span>福利年假</span><span><em><fmt:formatNumber type="number" pattern="#.#" value="${thisYearWelfareDays }"/></em> 天</span>
                        </p>
                    </div>
                    <p class="days">
                        <span>当年已使用年假</span><span><em><fmt:formatNumber type="number" pattern="#.#" value="${thisYearUsed }"/></em> 天</span>
                    </p>
                    <p class="days">
                        <span>当年剩余年假</span><span><em><fmt:formatNumber type="number" pattern="#.#" value="${thisYearRemain }"/></em> 天</span>
                    </p>
                    <%-- <p class="days">
                        <span>截止目前可用年假</span><span><em><fmt:formatNumber type="number" pattern="#.#" value="${thisYearActual-thisYearUsed }"/></em> 天</span>
                    </p> --%>
                    <p class="promot">温馨提示：截止目前，今年在职天数折算的剩余年假天数
                    <span style="color:blue;"><fmt:formatNumber type="number" pattern="#.#" value="${thisReductionDays }"/></span>
                                                   天，透支
                    <span style="color:blue;"><fmt:formatNumber type="number" pattern="#.#" value="${thisYearOverUsed }"/></span>
                                                   天；如果离职，透支年假将在离职工资结算中扣除。
                    </p>
                </div>

            </div>
        </section>
        <%-- <a href="<%=basePath %>empApplicationLeave/index.htm?urlType=11"><div class="foot-btn">假期申请</div></a> --%>
    </div>
    <script>
        $(function(){
            $('.open-btn').click(function(){
                if($(this).hasClass('on')){
                    $(this).removeClass('on')
                }else{
                    $(this).addClass('on')
                }
                $('.all-days').toggle();
            })
        })
    </script>
</body>
</html>