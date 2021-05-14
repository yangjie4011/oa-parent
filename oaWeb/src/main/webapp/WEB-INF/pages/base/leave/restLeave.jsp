<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">

<head>
    <%@include file="../../common/common.jsp"%>
    <link rel="icon" href="data:;base64,=">
    <title>调休详情</title>
    <meta charset="UTF-8">
</head>
<body class="b-f2f2f2 mt-55">
    <div class="oa-vacation_year oa-vacation_holiday">
        <header>
            <h1 class="clearfix"><a href="javascript:window.history.go(-1)" class="goback fl"><i class="back sr"></i></a>调休详情<a href="#" class="save fr"></a></h1>
        </header>
        <section class="year-artical">
            <div class="year-info current">
                <div class="head">
                    <p class="days">
                        <span>总剩余调休 </span><span><em class="big">${lastYearRemain+thisYearRemain+lastYearOtherRemain+thisYearOtherRemain}</em> h</span>
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
                        <span>去年剩余调休</span><span><em><fmt:formatNumber type="number" pattern="#.#" value="${lastYearRemain }"/></em> h</span>
                    </p>
                    <p class="days">
                        <span>去年已使用调休 </span><span><em><fmt:formatNumber type="number" pattern="#.#" value="${lastYearUsed }"/></em> h</span>
                    </p>
                    <p class="days">
                        <span>其它调休</span><span><em><fmt:formatNumber type="number" pattern="#.#" value="0"/></em> h</span>
                    </p>
                    <div class="open-btn lastYearOpen" style="top:128px;left:90px;">展开</div>
                    <div class="all-days lastYearList">
                        <c:forEach var="item" items="${lastYearOtherList}" varStatus="status">
		                     <p class="days">
		                         <span>有效期</span><span><em><fmt:formatDate type="date" value="${item.startTime }" pattern="yyyy-MM-dd"/>至<fmt:formatDate type="date" value="${item.endTime }" pattern="yyyy-MM-dd"/></em></span>
		                     </p>
		                     <p class="days">
		                         <span>调休小时数</span><span><em><fmt:formatNumber type="number" pattern="#.#" value="${item.allowRemainDays }"/></em> h</span>
		                     </p>
	                     </c:forEach>
                    </div>
                </div>
                <div class="now-year">
                    <div class="blue">今年</div>
                    <p class="title">有效期：<fmt:formatDate type="date" value="${thisYearStartTime }" pattern="yyyy-MM-dd"/>至<fmt:formatDate type="date" value="${thisYearEndTime }" pattern="yyyy-MM-dd"/></p>
                    <p class="days">
                        <span>当年剩余调休</span><span><em><fmt:formatNumber type="number" pattern="#.#" value="${thisYearRemain }"/></em> h</span>
                    </p>
                    <p class="days">
                        <span>当年已使用调休 </span><span><em><fmt:formatNumber type="number" pattern="#.#" value="${thisYearUsed }"/></em> h</span>
                    </p>
                    <p class="days">
                        <span>其它调休</span><span><em><fmt:formatNumber type="number" pattern="#.#" value="${thisYearOtherRemain}"/></em> 小时</span>
                    </p>
	                <div class="open-btn thisYearOpen" style="top:128px;left:90px;">展开</div>
	                <div class="all-days thisYearList">
	                     <c:forEach var="item" items="${thisYearOtherList}" varStatus="status">
		                     <p class="days">
		                         <span>有效期</span><span><em><fmt:formatDate type="date" value="${item.startTime }" pattern="yyyy-MM-dd"/>至<fmt:formatDate type="date" value="${item.endTime }" pattern="yyyy-MM-dd"/></em></span>
		                     </p>
		                     <p class="days">
		                         <span>调休小时数</span><span><em><fmt:formatNumber type="number" pattern="#.#" value="${item.allowRemainDays }"/></em> h</span>
		                     </p>
	                     </c:forEach>
	                </div>
                </div>
            </div>
        </section>
    </div>
    <script>
        $(function(){
            $('.lastYearOpen').click(function(){
                if($(this).hasClass('on')){
                    $(this).removeClass('on')
                }else{
                    $(this).addClass('on')
                }
                $('.lastYearList').toggle();
            });
            $('.thisYearOpen').click(function(){
                if($(this).hasClass('on')){
                    $(this).removeClass('on')
                }else{
                    $(this).addClass('on')
                }
                $('.thisYearList').toggle();
            })
        })
    </script>
</body>
</html>