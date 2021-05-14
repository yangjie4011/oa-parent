<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">

<head>
    <%@include file="../../common/common.jsp"%>
    <link rel="icon" href="data:;base64,=">
    <title>我的履历</title>
</head>
<body class="b-f2f2f2 mt-44">
	<input type="hidden" id="id" name="id" value="${id }"/>
    <div class="my-record">
        <header>
            <h1 class="clearfix"><a href="<%=basePath%>${returnUrl}" class="goback fl"><i class="back sr"></i></a>
                <p>我的履历</p><a class="correcting fr"></a></h1>
        </header>
        <div class="input-list">
            <div class="h">照片</div>
            <div class="m">
                <div class="moudle-ll">
                    <div class="img">
                        <!--替换背景图片来替换头像-->
                        <div class="bg"></div>
                        <!-- <input type="file" />
                        <p>点击跟换</p> -->
                        
		                <!-- <p class="name"><span id="cnName"></span></p>
		                <p><span id="positionName"></span>/<span id="managerName"></span></p>
		                <p>汇报人  <span id="departName"></p> -->
		             </div>
                </div>
            </div>
        </div>
        <div class="input-list" id="empBaseTable">
            <div class="h">基本信息
            </div>
            <div class="m">
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
            </div>
        </div>
        <div class="input-list"  id="empJobTable">
            <div class="h">在职信息
            </div>
            <div class="m">
                <div class="mk sd">
                    <div class="l-title">员工编号</div>
                    <div class="r-input" id="code"></div>
                </div>
                <div class="mk sd">
                    <div class="l-title">工时种类</div>
                    <div class="r-input" id="workTypeName"></div>
                </div>
                <div class="mk sd">
                    <div class="l-title">司龄</div>
                    <div class="r-input" id="ourAge"></div>
                </div>
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
                    <div class="l-title time">入职日期</div>
                    <div class="r-input" id="firstEntryTime"></div>
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
            </div>
            <!-- <div class="m">
                <div class="moudle-ll">
                    <div class="timeDouble sd">
                        <span>2017-03-03</span>至<span>2017-03-03</span>
                    </div>
                    <div class="mk sd">
                        <div class="l-title">学校名称</div>
                        <div class="r-input">上海大学</div>
                    </div>
                    <div class="mk sd">
                        <div class="l-title">学历</div>
                        <div class="r-input">本科</div>
                    </div>
                    <div class="mk sd">
                        <div class="l-title">专业</div>
                        <div class="r-input">设计专业</div>
                    </div>
                </div>
            </div> -->
        </div>
        <div class="input-list" id="tranningTable">
            <div class="h">培训证书
            </div>
            <!-- <div class="m">
                <div class="moudle-ll">
                    <div class="timeDouble sd">
                        <span>2017-03-03</span>至<span>2017-03-03</span>
                    </div>
                    <div class="mk sd">
                        <div class="l-title">培训机构</div>
                        <div class="r-input">上海大学</div>
                    </div>
                    <div class="mk sd">
                        <div class="l-title">所获证书</div>
                        <div class="r-input">前端证书</div>
                    </div>
                    <div class="mk sd">
                        <div class="l-title">培训内容</div>
                        <div class="r-input">设计专业设计专业设计专业设计专业设计专业设计专业设计专业设计专业设计专业设计专业设计专业</div>
                    </div>
                </div>
            </div> -->
        </div>
        <div class="input-list"  id="workTable">
            <div class="h">工作经历
            </div>
            <!-- <div class="m">
                <div class="moudle-ll">
                    双重时间
                    <div class="timeDouble sd">
                        <span>2017-03-03</span>至<span>2017-03-03</span>
                    </div>
                    <div class="mk sd">
                        <div class="l-title">公司名称</div>
                        <div class="r-input">上海公司</div>
                    </div>
                    <div class="mk sd">
                        <div class="l-title">职位</div>
                        <div class="r-input">产品经理</div>
                    </div>
                    <div class="mk sd">
                        <div class="l-title">主办业务</div>
                        <div class="r-input">巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉</div>
                    </div>
                </div>
            </div> -->
        </div>
        <div class="input-list" id="urgentContactTable">
            <div class="h">紧急联系人
            </div>
            <!-- <div class="m">
                <div class="moudle-ll">
                    <div class="block sd friend">朋友</div>
                    <div class="block sd name">王建</div>
                    <div class="block sd phone">13837233332</div>
                </div>
            </div> -->
        </div>
        
        <div class="input-list" id="spouseTable">
            <div class="h">配偶信息
            </div>
            <!-- <div class="m">
                <div class="moudle-ll">
                    <div class="block sd name">王建</div>
                    <div class="block sd phone">13837233332</div>
                    <div class="block sd address">上海公司</div>
                </div>
            </div> -->
        </div>
        <div class="input-list" id="childTable">
            <div class="h">子女信息
            </div>
            <!-- <div class="m">
                <div class="moudle-ll">
                    <div class="block sd sex">男</div>
                    <div class="block sd name">王建</div>
                    <div class="block sd birthday">2017-03-23</div>
                </div>
            </div> -->
        </div>
        <div class="input-list" id="achievementTable">
            <div class="h">业绩与奖惩
            </div>
            <!-- <div class="m">
                <div class="moudle-ll">
                    <div class="time sd">
                        <span>2017-03-03</span>
                    </div>
                    <div class="mk sd">
                        <div class="l-title">奖惩内容</div>
                        <div class="r-input">巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉</div>
                    </div>
                </div>
            </div> -->
        </div>
        <div class="input-list">
            <div class="h">从业背景
                <!-- <div class="edit">编辑</div> -->
            </div>
            <div class="m">
                <!-- <div class="moudle-ll">
                    <div class="block sd r-input" id="industryRelevanceName">行业相关性</div>
                    <div class="block sd r-input" id="workingBackground">从业背景</div>
                </div> -->
                <div class="moudle-ll">
                    <div class="mk sd">
                        <div class="l-title">行业相关性</div>
                        <div class="r-input" id="industryRelevanceName"></div>
                    </div>
                    <div class="mk sd">
                        <div class="l-title">从业背景</div>
                        <div class="r-input" id="workingBackground"></div>
                    </div>
                </div>
            </div>
        </div>
        <div class="input-list" id="companyTrainTable">
            <div class="h">本公司培训记录
            </div>
            <!-- <div class="m">
                <div class="moudle-ll">
                    <div class="time sd">
                        <span>2017-03-03</span>
                    </div>
                    <div class="mk sd text">
                        <div class="l-title">培训项目</div>
                        <div class="r-input">巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉</div>
                    </div>
                </div>
            </div> -->
        </div>
        <div class="input-list" id="contractTable">
            <div class="h">合同记录
            </div>
            <!-- <div class="m">
                <div class="moudle-ll">
                    <div class="mk sd">
                        <div class="l-title">签订日期</div>
                        <div class="r-input">2017-03-22</div>
                    </div>
                    <div class="mk sd">
                        <div class="l-title">合同期限</div>
                        <div class="r-input">3年</div>
                    </div>
                    <div class="mk sd">
                        <div class="l-title">使用期限</div>
                        <div class="r-input">3个月</div>
                    </div>
                    <div class="mk sd">
                        <div class="l-title">合同到期日</div>
                        <div class="r-input">2000-12-03</div>
                    </div>
                </div>
            </div> -->
        </div>
        <div class="input-list" id="empAppraiseTable">
            <div class="h">考核记录
            </div>
            <!-- <div class="m">
                <div class="moudle-ll">
                    <div class="mk sd">
                        <div class="l-title">考核日期</div>
                        <div class="r-input">2017-11-11</div>
                    </div>
                    <div class="mk sd">
                        <div class="l-title">考核成绩</div>
                        <div class="r-input">A</div>
                    </div>
                    <div class="mk sd">
                        <div class="l-title text">考核结论</div>
                        <div class="r-input">巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉</div>
                    </div>
                </div>
            </div> -->
        </div>
        <div class="input-list" id="empPositionTable">
            <div class="h">岗位记录
            </div>
            <!-- <div class="m">
                <div class="moudle-ll">
                    <div class="mk sd">
                        <div class="l-title">调整日期</div>
                        <div class="r-input">2017-09-02</div>
                    </div>
                    <div class="mk sd">
                        <div class="l-title">原岗位</div>
                        <div class="r-input">产品经理</div>
                    </div>
                    <div class="mk sd">
                        <div class="l-title">现岗位</div>
                        <div class="r-input">开发</div>
                    </div>
                </div>
            </div> -->
        </div>
        <div class="input-list" id="remarkTable">
            <div class="h">备注
                <!-- <div class="edit">编辑</div> -->
            </div>
            <div class="m">
                <div class="moudle-ll">
                    <div class="sd text" id="remark">
                       
                    </div>
                </div>
            </div>
        </div>
    </div>

	<script type="text/javascript" src="<c:url value="/js/common/oaCommon.js?v=2017071701"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/base/employee/employee_check_app.js?v=20200520"/>"></script>
    
</body>
</html>