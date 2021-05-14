<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <%@include file="../../common/common.jsp"%>
    <link rel="icon" href="data:;base64,=">
    <title>我的履历编辑</title>
</head>
<body class="b-f2f2f2 mt-44">
	<input type="hidden" id="companyId" name="companyId"/>
	<input type="hidden" id="id" name="id" value="${id }"/>
	<input type="hidden" id="authority" name="authority" value="${authority }"/>
    <div class="my-record">
        <header>
            <h1 class="clearfix"><a href="<%=basePath%>${returnUrl}" class="goback fl"><i class="back sr"></i></a>
                <p>我的履历编辑</p><a class="correcting fr"></a></h1>
        </header>
        <jsp:include page="employee_pic.jsp"></jsp:include>
        <div class="input-list" id="empBaseTable">
            <div class="h">基本信息
                <c:if test="${authority == 'hr' }"><!--  -->
                   <div class="edit" id="editBase">编辑</div>
                </c:if>
            </div>
            <div class="m">
                <div class="mk sd">
                    <div class="l-title">姓名</div>
                    <div class="r-input" id="cnName" name="cnName"></div>
                </div>
                <div class="mk sd">
                    <div class="l-title">英文名</div>
                    <div class="r-input" id="engName" name="engName"></div>
                </div>
                <!--单独时间选择-->
                <div class="mk sd">
                    <div class="l-title time">出生日期</div>
                    <div class="r-input" id="birthday" name="birthday"></div>
                </div>
                <div class="mk sd">
                    <div class="l-title">性别</div>
                    <div class="r-input" id="sex"></div>
                </div>
                <div class="mk sd">
                    <div class="l-title">年龄</div>
                    <div class="r-input" id="age"></div>
                </div>
                <!--内容较长需要textarea，加样式text-->
                <div class="mk sd">
                    <div class="l-title text">国籍</div>
                    <div class="r-input" id="countryName"></div>
                </div>
                <div class="mk sd">
                    <div class="l-title">民族</div>
                    <div class="r-input" id="nationName"></div>
                </div>
                <div class="mk sd">
                    <div class="l-title">户籍</div>
                    <div class="r-input"  id="householdRegister"></div>
                </div>
                <div class="mk sd">
                    <div class="l-title">婚姻状况</div>
                    <div class="r-input"  id="maritalStatusName"></div>
                </div>
                <div class="mk sd">
                    <div class="l-title">在沪居住地</div>
                    <div class="r-input"  id="address"></div>
                </div>
                <div class="mk sd">
                    <div class="l-title">手机号码</div>
                    <div class="r-input"  id="mobile"></div>
                </div>
                <div class="mk sd">
                    <div class="l-title">行业相关性</div>
                    <div class="r-input" id="industryRelevanceName"></div>
                </div>
                <div class="mk sd">
                    <div class="l-title">从业背景</div>
                    <div class="r-input" id="workingBackground"></div>
                </div>
                <div class="mk sd">
                    <div class="l-title">文化程度</div>
                    <div class="r-input" id="degreeOfEducationName"></div>
                </div>
                <div class="mk sd">
                    <div class="l-title">政治面貌</div>
                    <div class="r-input" id="politicalName"></div>
                </div>
            </div>
        </div>
        <div class="input-list"  id="empJobTable">
            <div class="h">在职信息
                <c:if test="${authority == 'hr' }"><!--  -->
                    <div class="edit" id="editJob">编辑</div>
                </c:if>
            </div>
            <div class="m">
                <div class="mk sd">
                    <div class="l-title">公司名称</div>
                    <div class="r-input" id="companyName"></div>
                </div>
                <div class="mk sd">
                    <div class="l-title">员工类型</div>
                    <div class="r-input" id="empTypeName"></div>
                </div>
                <div class="mk sd">
                    <div class="l-title">员工编号</div>
                    <div class="r-input" id="code"></div>
                </div>
                <div class="mk sd">
                    <div class="l-title">通行证</div>
                    <div class="r-input" id="staffStr" name="staffStr"></div>
                </div>
                <div class="mk sd">
                    <div class="l-title">指纹ID</div>
                    <div class="r-input" id="fingerprintId" name="fingerprintId"></div>
                </div>
                <div class="mk sd">
                    <div class="l-title">楼层</div>
                    <div class="r-input" id="floorCode" name="floorCode"></div>
                </div>
                <div class="mk sd">
                    <div class="l-title">座位号</div>
                    <div class="r-input" id="seatCode" name="seatCode"></div>
                </div>
                <c:if test="${authority == 'hr' }"><!--  -->
                    <div class="mk sd">
	                    <div class="l-title">工时种类</div>
	                    <div class="r-input" id="workTypeName"></div>
                	</div>
                </c:if>
                <div class="mk sd">
                    <div class="l-title time">入职日期</div>
                    <div class="r-input" id="firstEntryTime"></div>
                </div>
                <c:if test="${authority == 'hr' }">
	                <div class="mk sd">
	                    <div class="l-title">司龄</div>
	                    <div class="r-input" id="ourAge"></div>
	                </div>
	                <div class="mk sd">
	                    <div class="l-title">入司前工龄</div>
	                    <div class="r-input" id="beforeWorkAge"></div>
	                </div>
	                <div class="mk sd">
	                    <div class="l-title">总工龄</div>
	                    <div class="r-input" id="workAge"></div>
	                </div>
                </c:if>
                <div class="mk sd">
                    <div class="l-title">部门</div>
                    <div class="r-input" id="departName"></div>
                </div>
                <div class="mk sd">
                    <div class="l-title">汇报对象</div>
                    <div class="r-input" id="employeeLeader"></div>
                </div>
                <div class="mk sd">
                    <div class="l-title">部门负责人</div>
                    <div class="r-input" id="departLeader"></div>
                </div>
                <div class="mk sd">
                    <div class="l-title">职位</div>
                    <div class="r-input" id="positionName"></div>
                </div>
                <div class="mk sd">
                    <div class="l-title">职级</div>
                    <div class="r-input" id="positionLevelName"></div>
                </div>
                <div class="mk sd">
                    <div class="l-title">职位序列</div>
                    <div class="r-input" id="positionSeqName"></div>
                </div>
                <div class="mk sd">
                    <div class="l-title">职称</div>
                    <div class="r-input" id="positionTitle" name="positionTitle"></div>
                </div>
                <div class="mk sd">
                    <div class="l-title">工作地点</div>
                    <div class="r-input" id="workAddress"></div>
                </div>
                <div class="mk sd">
                    <div class="l-title">是否排班</div>
                    <div class="r-input" id="whetherSchedulingName" name="whetherSchedulingName"></div>
                </div>
                <div class="mk sd">
                    <div class="l-title time">试用期到期日</div>
                    <div class="r-input" id="probationEndTime"></div>
                </div>
                <div class="mk sd">
                    <div class="l-title time">离职日期</div>
                    <div class="r-input" id="quitTime"></div>
                </div>
                <div class="mk sd">
                    <div class="l-title time">合同到期日</div>
                    <div class="r-input"  id="protocolEndTime"></div>
                </div>
                <div class="mk sd">
                    <div class="l-title">在职状态</div>
                    <div class="r-input" id="jobStatus"></div>
                </div>
                <div class="mk sd">
                    <div class="l-title">邮乐账号</div>
                    <div class="r-input"  id="uleAccount"></div>
                </div>
                <div class="mk sd">
                    <div class="l-title">工作邮箱</div>
                    <div class="r-input"  id="email"></div>
                </div>
            </div>
        </div>
        <div class="input-list" id="schoolTable">
            <div class="h">教育经历
                <div class="edit" id="editSchoool">编辑</div>
            </div>
        </div>
        <div class="input-list" id="tranningTable">
            <div class="h">培训证书
                <div class="edit" id="editTrainning">编辑</div>
            </div>
        </div>
        <div class="input-list"  id="workTable">
            <div class="h">工作经历
                <div class="edit" id="editWork">编辑</div>
            </div>
        </div>
        <div class="input-list" id="urgentContactTable">
            <div class="h">紧急联系人
                <div class="edit"  id="editUrgentContact">编辑</div>
            </div>
        </div>
        
        <div class="input-list" id="spouseTable">
            <div class="h">配偶信息
                <div class="edit" id="editSpouse">编辑</div>
            </div>
        </div>
        <div class="input-list" id="childTable">
            <div class="h">子女信息
                <div class="edit" id="editChild">编辑</div>
            </div>
        </div>
        <div class="get-more">
            <p>展开更多项目</p><i></i>
        </div>
        
        
		<div style="display: none;" id="moreDiv">
	        <div class="input-list" id="achievementTable">
	            <div class="h">业绩与奖惩
                <c:if test="${authority == 'hr' }"><!--  -->
                    <div class="edit" id="editAchievement">编辑</div>
                </c:if>
	            </div>
	        </div>
	        <!-- <div class="input-list">
	            <div class="h">从业背景
	            </div>
	            <div class="m">
	                <div class="moudle-ll">
	                </div>
	            </div>
	        </div> -->
	        <div class="input-list" id="companyTrainTable">
	            <div class="h">本公司培训记录
                <c:if test="${authority == 'hr' }"><!--  -->
	                <div class="edit" id="editCompanyTrain">编辑</div>
                </c:if>
	            </div>
	        </div>
	        <div class="input-list" id="contractTable">
	            <div class="h">合同签订记录
                <c:if test="${authority == 'hr' }"><!--  -->
	                <div class="edit" id="editContract">编辑</div>
                </c:if>
	            </div>
	        </div>
	        <div class="input-list" id="empAppraiseTable">
	            <div class="h">考核记录
                <c:if test="${authority == 'hr' }"><!--  -->
	                <div class="edit" id="editAppraise">编辑</div>
                </c:if>
	            </div>
	        </div>
	        <div class="input-list" id="empPositionTable">
	            <div class="h">岗位记录
                <c:if test="${authority == 'hr' }"><!--  -->
	                <div class="edit" id="editPosition">编辑</div>
                </c:if>
	            </div>
	        </div>
	        <div class="input-list" id="remarkTable">
	            <div class="h">备注
                <c:if test="${authority == 'hr' }"><!--  -->
	                <div class="edit" id="editRemark">编辑</div>
                </c:if>
	            </div>
	            <div class="m">
	                <div class="moudle-ll">
	                    <div class="sd text" id="remark" style="width:100%;height:100px;">
	                       
	                    </div>
	                </div>
	            </div>
	        </div>
		</div>
    </div>
    
    <jsp:include flush="true" page="employee_check_app_update.jsp"></jsp:include>
    
    <script type="text/javascript" src="<c:url value="/js/util/dateUtil.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/base/employee/employee_check_app.js?v=20200520"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/base/employee/employee_check_app_update.js?v=20191113"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/base/employee/common_h5_select.js?v=20191113"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/base/employee/employee_pic.js?v=20180417"/>"></script>
</body>
</script>
</html>