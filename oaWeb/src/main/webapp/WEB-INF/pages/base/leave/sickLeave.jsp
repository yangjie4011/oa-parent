<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="en">

<head>
    <%@include file="../../common/common.jsp"%>
    <link rel="icon" href="data:;base64,=">
    <title>病假详情</title>
    <meta charset="UTF-8">
</head>

<body class="b-f2f2f2 mt-55">
    <div class="oa-vacation_illness oa-vacation_holiday">
        <header>
            <h1 class="clearfix"><a onclick="javascript:window.history.go(-1)" class="goback fl"><i class="back sr"></i></a>病假详情<a href="#" class="save fr"></a></h1>
        </header>

        <section class="year-artical">
            <h3 class="current">今年</h3>
            <div class="year-info current">
                <p class="title">有效期：<fmt:formatDate type="date" value="${sickStartTime }" pattern="yyyy-MM-dd"/>至<fmt:formatDate type="date" value="${sickEndTime }" pattern="yyyy-MM-dd"/></p>
                <p class="days">
                    <span>当年带薪病假总天数 </span><span><em><fmt:formatNumber type="number" pattern="#.#" value="${sickAllowDays }"/></em> 天</span>
                </p>
                <p class="days">
                    <span>当年已使用带薪病假 </span><span><em><fmt:formatNumber type="number" pattern="#.#" value="${sickSalaryUsed }"/></em> 天</span>
                </p>
                <p class="days">
                    <span>当年剩余带薪病假 </span><span><em><fmt:formatNumber type="number" pattern="#.#" value="${sickAllowRemain }"/></em> 天</span>
                </p>
                <%-- <p class="days">
                    <span>截止目前可用带薪病假 </span><span><em><fmt:formatNumber type="number" pattern="#.#" value="${sickActual }"/></em> 天</span>
                </p> --%>
                <p class="days">
                    <span>已使用非带薪病假天数 </span><span><em><fmt:formatNumber type="number" pattern="#.#" value="${sickNotSalaryUsed }"/></em> 天</span>
                </p>
                <%-- <p class="days">
                    <span>已用病假总天数</span><span><em><fmt:formatNumber type="number" pattern="#.#" value="${sickUsed }"/></em> 天</span>
                </p> --%>
                <p class="promot">温馨提示：截止目前，今年在职天数折算的剩余带薪病假天数
                <span style="color:blue;"><fmt:formatNumber type="number" pattern="#.#" value="${reductionDays }"/></span>
			           天，透支
			    <span style="color:blue;"><fmt:formatNumber type="number" pattern="#.#" value="${sickOverUsed }"/></span>
			            天；如果离职，透支带薪病假和非带薪病假将在离职工资结算中扣除
                </p>
            </div>
        </section>

    </div>

</body>

</html>