<%@page pageEncoding="UTF-8"%>  
    
    <form id="baseInfoForm">
    	<input type="hidden" id="version" name="version" class="version"/>
		<input type="hidden" id="baseId" name="id" value="${id }"/>
		<div class="edit-page-a" style="display: none;">
		<header>
			<h1 class="clearfix">
				<a class="goback fl"><i class="back sr"></i></a>
				<p>详情列表</p>
				<a class="correcting fr"></a>
			</h1>
		</header>
		<div class="main">
			<!-- <div class="add-type">基础信息不能新增
				<em><i class="sr icon"></i>新增</em>
			</div> -->
			<ul>
				<li><div class="input-list">
						
			            <div class="m">
			                <div class="mk sd">
			                    <div class="l-title">姓名<span>*</span></div>
			                    <div class="r-input">
			                         <input type="text" id="cnName" name="cnName" class="cnName" maxlength="30"/>
			                    </div>
			                </div>
			                <div class="mk sd">
			                    <div class="l-title">英文名</div>
			                    <div class="r-input null">
			                         <input type="text" id="engName" name="engName" class="engName" maxlength="30"/>
			                    </div>
			                </div>
			                <!--单独时间选择-->
			                <div class="mk sd">
			                    <div class="l-title time">出生日期<span>*</span></div>
			                    <div class="r-input selarea fr">
			                         <input type="text" readonly  id="baseBirthday" name="birthday"  class="birthday"><!-- 日期控件，ID必须唯一 -->
			                         <i class="sr icon"></i>
			                    </div>
			                </div>
			                <div class="mk sd">
			                    <div class="l-title">性别<span>*</span></div>
			                    <div class="r-input selarea sex" id="sexSelect">
								     <input type="text" id="sexName" class="sexName" readonly placeholder="请选择" /><!-- value -->
								     <input type="hidden" id="sex" name="sex" class="sex"/><!-- key -->
                                     <i class="sr icon"></i>
			                    </div>
			                </div>
			                <div class="mk sd">
			                    <div class="l-title">年龄<span>*</span></div>
			                    <div class="r-input">
			                         <input type="number" id="age" name="age" class="age" maxlength="3" readonly/>
			                    </div>
			                </div>
			                <!--内容较长需要textarea，加样式text-->
			                <div class="mk sd">
			                    <div class="l-title text">国籍<span>*</span></div>
			                    <div class="r-input" id="countrySelect">
			                         <input type="text" id="countryName" class="countryName" readonly placeholder="请选择" />
			                         <input type="hidden" id="country" name="country" class="country" /><!-- key -->
			                         <i class="sr icon"></i>
			                    </div> 
			                </div>
			                <div class="mk sd countryOtherDiv" style="display:none;">
			                    <div class="l-title text"></div>
			                    <div class="r-input null">
			                         <input type="text" id="countryOther" name="countryOther" class="countryOther" placeholder="请填写国家名" />
			                    </div>
			                </div>
			                <div class="mk sd">
			                    <div class="l-title">民族<span>*</span></div>
			                    <div class="r-input" id="nationSelect">
			                         <input type="text" id="nationName" readonly placeholder="请选择" class="nationName" />
			                         <input type="hidden" id="nation" name="nation" class="nation" /><!-- key -->
			                         <i class="sr icon"></i>
			                    </div>
			                </div>
			                <div class="mk sd">
			                    <div class="l-title">户籍<span>*</span></div>
			                    <div class="r-input">
			                         <input type="text" id="householdRegister" name="householdRegister" class="householdRegister" maxlength="200"/>
			                    </div>
			                </div>
			                <div class="mk sd">
			                    <div class="l-title">婚姻状况<span>*</span></div>
			                    <div class="r-input" id="maritalStatusSelect">
			                         <input type="text" id="maritalStatusName" readonly placeholder="请选择" class="maritalStatusName"/>
			                         <input type="hidden" id="maritalStatus" name="maritalStatus" class="maritalStatus"/><!-- key -->
			                         <i class="sr icon"></i>
			                    </div>
			                </div>
			                <div class="mk sd">
			                    <div class="l-title">在沪居住地<span>*</span></div>
			                    <div class="r-input">
			                         <input type="text" id="address" name="address" class="address" maxlength="200"/>
			                    </div>
			                </div>
			                <div class="mk sd">
			                    <div class="l-title">手机号码<span>*</span></div>
			                    <div class="r-input">
			                         <input type="number" id="mobile" name="mobile" class="mobile"  oninput="if(value.length>11)value=value.slice(0,11)" />
			                    </div>
			                </div>
		                    <div class="mk sd">
		                        <div class="l-title">行业相关性<span>*</span></div>
		                        <div class="r-input" id="industryRelevanceSelect">
			                         <input type="text" id="industryRelevanceName" readonly placeholder="请选择" class="industryRelevanceName"/>
			                         <input type="hidden" id="industryRelevance" name="industryRelevance" class="industryRelevance"/><!-- key -->
			                         <i class="sr icon"></i>
			                    </div>
		                    </div>
			                <div class="mk sd industryRelevanceOtherDiv" style="display:none;">
			                    <div class="l-title text"></div>
			                    <div class="r-input null">
			                         <input type="text" id="industryRelevanceOther" name="industryRelevanceOther" class="industryRelevanceOther" placeholder="请填写行业相关性" />
			                    </div>
			                </div>
		                    <div class="mk sd">
		                        <div class="l-title">从业背景<span>*</span></div>
		                        <div class="r-input">
			                         <input type="text" id="workingBackground" name="workingBackground" class="workingBackground" maxlength="100"/>
			                    </div>
		                    </div>
		                    <div class="mk sd">
		                        <div class="l-title">文化程度<span>*</span></div>
		                        <div class="r-input" id="educationSelect">
			                         <input type="text" id="degreeOfEducationName" readonly placeholder="请选择" class="degreeOfEducationName"/>
			                         <input type="hidden" id="degreeOfEducation" name="degreeOfEducation" class="degreeOfEducation"/><!-- key -->
			                         <i class="sr icon"></i>
			                    </div>
		                    </div>
			                <div class="mk sd degreeOfEducationOtherDiv" style="display:none;">
			                    <div class="l-title text"></div>
			                    <div class="r-input null">
			                         <input type="text" id="degreeOfEducationOther" name="degreeOfEducationOther" class="degreeOfEducationOther" placeholder="请填写文化程度" />
			                    </div>
			                </div>
		                    <div class="mk sd">
		                        <div class="l-title">政治面貌<span>*</span></div>
		                        <div class="r-input" id="politicalStatusSelect">
			                         <input type="text" id="politicalName" readonly placeholder="请选择" class="politicalName"/>
			                         <input type="hidden" id="politicalStatus" name="politicalStatus" class="politicalStatus"/><!-- key -->
			                         <i class="sr icon"></i>
			                    </div>
		                    </div>
			                <div class="mk sd politicalStatusOtherDiv" style="display:none;">
			                    <div class="l-title text"></div>
			                    <div class="r-input null">
			                         <input type="text" id="politicalStatusOther" name="politicalStatusOther" class="politicalStatusOther" placeholder="请填写政治面貌" />
			                    </div>
			                </div>
			            </div>
					</div>
				</li>
			</ul>
		</div>
		<div class="double-btn">
			<div class="l quit">
				<em>取消</em>
			</div>
			<div class="r save">
				<em>保存</em>
			</div>
		</div>
	 </div>
    </form>
    
    <form id="baseJobForm">
    	<input type="hidden" id="empVersion" name="version" class="empVersion"/>
    	<input type="hidden" id="departVersion" name="empDepart.depart.version" class="departVersion"/>
    	<input type="hidden" id="positionVersion" name="empPosition.position.version" class="positionVersion"/>
		<input type="hidden" id="jobId" name="id" value="${id }"/>
		<div class="edit-page-a" style="display: none;">
		<header>
			<h1 class="clearfix">
				<a class="goback fl"><i class="back sr"></i></a>
				<p>详情列表</p>
				<a class="correcting fr"></a>
			</h1>
		</header>
		<div class="main">
			<!-- <div class="add-type">基础信息不能新增
				<em><i class="sr icon"></i>新增</em>
			</div> -->
			<ul>
				<li><div class="input-list">
						
						<div class="m">
			                <div class="mk sd">
			                    <div class="l-title text">公司名称<span>*</span></div>
			                    <div class="r-input" id="companySelect">
			                         <input type="text" id="companyName" class="companyName" readonly placeholder="请选择" />
			                         <input type="hidden" id="companyId" name="companyId" class="companyId" /><!-- key -->
			                         <i class="sr icon"></i>
			                    </div> 
			                </div>
			                <div class="mk sd">
			                    <div class="l-title text">员工类型<span>*</span></div>
			                    <div class="r-input" id="empTypeSelect">
			                         <input type="text" id="empTypeName" class="empTypeName" readonly placeholder="请选择" />
			                         <input type="hidden" id="empTypeId" name="empTypeId" class="empTypeId" /><!-- key -->
			                         <i class="sr icon"></i>
			                    </div> 
			                </div>
				                <div class="mk sd">
				                    <div class="l-title">员工编号<span>*</span></div>
				                    <div class="r-input">
			                             <input type="text" id="code" name="code" class="code" maxlength="100"/>
			                        </div>
				                </div>
			                <div class="mk sd">
			                    <div class="l-title">通行证<span>*</span></div>
			                    <div class="r-input">
			                         <input type="text" id="staffStr" class="staffStr" maxlength="30" readonly/>
			                    </div>
			                </div>
			                <div class="mk sd">
			                    <div class="l-title">指纹ID<span></span></div>
			                    <div class="r-input null">
			                         <input type="text" id="fingerprintId" name="fingerprintId" class="fingerprintId" maxlength="30"/>
			                    </div>
			                </div>
			                <div class="mk sd">
			                    <div class="l-title">楼层<span>*</span></div>
			                    <div class="r-input">
			                         <input type="number" id="floorCode" name="floorCode" class="floorCode" maxlength="2"/>
			                    </div>
			                </div>
			                <div class="mk sd">
			                    <div class="l-title">座位号<span>*</span></div>
			                    <div class="r-input">
			                         <input type="text" id="seatCode" name="seatCode" class="seatCode" maxlength="10"/>
			                    </div>
			                </div>
				                <div class="mk sd">
				                    <div class="l-title">工时种类<span>*</span></div>
				                    <div class="r-input" id="workTypeSelect">
			                             <input type="text" id="workTypeName" readonly placeholder="请选择"  class="workTypeName"/>
				                         <input type="hidden" id="workType" name="workType"  class="workType"/><!-- key -->
				                         <i class="sr icon"></i>
				                    </div>
				                </div>
				                <div class="mk sd">
				                    <div class="l-title time">入职日期<span>*</span></div>
				                    <div class="r-input">
			                             <input type="text" id="firstEntryTime" name="firstEntryTime"  class="firstEntryTime" readonly/>
			                        </div>
				                </div>
				                <div class="mk sd">
				                    <div class="l-title">司龄<span>*</span></div>
				                    <div class="r-input">
			                             <input type="text" id="ourAge" name="" class="ourAge" readonly/>
			                        </div>
				                </div>
				                <div class="mk sd">
				                    <div class="l-title">入职前工龄<span>*</span></div>
				                    <div class="r-input">
			                             <input type="text" id="beforeWorkAge" name="beforeWorkAge" class="beforeWorkAge"/>
			                        </div>
				                </div>
				                <div class="mk sd">
				                    <div class="l-title">部门<span>*</span></div>
				                    <div class="r-input sel selCompanyPartment">
			                             <input type="text" id="departName" name="departName" class="departName"/>
			                             <input type="hidden" id="departId" name="empDepart.depart.id" class="departId"/>
			                        </div>
							        <!-- <div class="sel selCompanyPartment">
							            <i></i>
							            <p>选择部门</p>
							            <rr><input type="text" id="departId"/></rr>
							        </div> -->
				                </div>
				                <div class="mk sd">
				                    <div class="l-title">汇报对象<span>*</span></div>
				                    <div class="r-input reporterSelect">
			                             <input type="text" id="reportToLeaderName" name="reportToLeaderName" readonly class="reportToLeaderName"/>
			                             <input type="hidden" id="reportToLeader" name="reportToLeader" class="reportToLeader"/>
			                        </div>
				                </div>
				                <div class="mk sd">
				                    <div class="l-title">部门负责人<span>*</span></div>
				                    <div class="r-input">
			                             <input type="text" id="leaderName" name="empDepart.depart.leaderName" readonly class="leaderName"/>
			                             <input type="hidden" id="leader" name="empDepart.depart.leader" class="leader"/>
			                        </div>
				                </div>
				                <div class="mk sd">
				                    <div class="l-title">职位</div>
				                    <div class="r-input positionSelect">
			                             <input type="text" id="positionName" name="positionName"  class="positionName"/>
			                             <input type="hidden" id="positionId" name="empPosition.position.id" class="positionId"/>
			                        </div>
				                </div>
				                <div class="mk sd">
				                    <div class="l-title">职级</div>
				                    <div class="r-input positionLevelSelect" id="positionLevelSelect">
			                             <input type="text" id="positionLevelName" name="positionLevel" readonly class="positionLevelName"/>
			                        </div>
				                </div>
				                <div class="mk sd">
				                    <div class="l-title">职位序列</div>
				                    <div class="r-input positionSeqSelect" id="positionSeqSelect">
			                             <input type="text" id="positionSeqName" name="positionSeq" readonly class="positionSeqName"/>
			                        </div>
				                </div>
				                <div class="mk sd">
				                    <div class="l-title">职称</div>
				                    <div class="r-input null">
			                             <input type="text" id="positionTitle" name="positionTitle" class="positionTitle"/>
			                        </div>
				                </div>
				                <div class="mk sd">
				                    <div class="l-title">是否排班</div>
				                    <div class="r-input null" id="whetherSchedulSelect">
			                             <input type="text" id="whetherSchedulingName" name="whetherSchedulingName" readonly class="whetherSchedulingName"/>
			                             <input type="hidden" id="whetherScheduling" name="whetherScheduling" class="whetherScheduling"/>
			                        </div>
				                </div>
				                <div class="mk sd">
				                    <div class="l-title time">试用期到期日</div>
				                    <div class="r-input null">
			                             <input type="text" id="probationEndTime" name="probationEndTime"  class="probationEndTime"/>
			                        </div>
				                </div>
				                <div class="mk sd">
				                    <div class="l-title">离职日期</div>
				                    <div class="r-input null">
			                             <input type="text" readonly/>
			                        </div>
				                </div>
				                <div class="mk sd">
				                    <div class="l-title time">合同到期日<span>*</span></div>
				                    <div class="r-input">
			                             <input type="text" id="protocolEndTime" name="protocolEndTime"  class="protocolEndTime"/>
			                        </div>
				                </div>
				                <div class="mk sd">
				                    <div class="l-title">在职状态<span>*</span></div>
				                    <div class="r-input" id="jobStatusSelect">
			                             <input type="text" id="jobStatusName"  class="" readonly placeholder="请选择" /> 
								    	 <input type="hidden" id="jobStatus" name="jobStatus"  class="jobStatus"/>  
                                    	 <i class="sr icon"></i>
			                        </div>
				                </div>
				                <div class="mk sd">
				                    <div class="l-title">邮乐账号</div>
				                    <div class="r-input null">
			                             <input type="text" id="uleAccount" name="uleAccount"  class="uleAccount" maxlength="100"/>
			                        </div>
				                </div>
				                <div class="mk sd">
				                    <div class="l-title">工作邮箱</div>
				                    <div class="r-input null">
			                             <input type="email" id="email" name="email"  class="email"/>
			                        </div>
				                </div>
			            </div>
					</div>
				</li>
			</ul>
		</div>
		<div class="double-btn">
			<div class="l quit">
				<em>取消</em>
			</div>
			<div class="r save">
				<em>保存</em>
			</div>
		</div>
	 </div>
    </form>

<form id="schoolForm">
	<input type="hidden" name="employeeId" class="id" value="${id }" /> 
	<input type="hidden" id="companyId" name="companyId"/>
	<input type="hidden" id="delFlag" name="delFlag" value="0" />
	<div class="edit-page-a" style="display: none;">
		<header>
			<h1 class="clearfix">
				<a class="goback fl"><i class="back sr"></i></a>
				<p class="tip">详情列表</p>
				<a class="correcting fr delete" style="display: none;">删除</a>
			</h1>
		</header>
		<div class="main">
			<div class="add-type" >
				<em><i class="sr icon"></i>新增</em>
			</div>
			<ul>
				<li style = "display: none">
					<div class="input-list">
						
						<input type="hidden" name="id" id="id" class="id"/> 
						<input type="hidden" name="version" id="version" class="version"/>
						<div id="addDiv" class="m">
							<div class="mk sd">
								<div class="l-title n-input">开始时间<span>*</span></div>
								<div class="r-input time">
									<input type="text" readonly name="startTime" id="startTime" class="startTime"/>
								</div>
							</div>
							<div class="mk sd">
								<div class="l-title n-input">结束时间<span>*</span></div>
								<div class="r-input time">
									<input type="text" readonly name="endTime" id="endTime" class="endTime"/>
								</div>
							</div>
							<div class="mk sd">
								<div class="l-title">学校名称<span>*</span></div>
								<div class="r-input">
									<input type="text" id="school" name="school"  class="school" maxlength="200"/>
								</div>
							</div>
							<div class="mk sd">
								<div class="l-title">学历<span>*</span></div>
								<div class="r-input" id="educationSelect">
									<input type="text" id="educationName" readonly placeholder="请选择" class="educationName"/>
									<input type="hidden" name="education" id="education" class="education"/>
									<!-- key -->
								</div>
							</div>
							<div class="mk sd">
								<div class="l-title">学位</div>
								<div class="r-input null" id="degreeSelect">
									<input type="text" id="degreeName" readonly placeholder="请选择" class="degreeName"/>
									<input type="hidden" name="degree" id="degree" class="degree"/>
									<!-- key -->
								</div>
							</div>
							<div class="mk sd">
								<div class="l-title">专业</div>
								<div class="r-input null">
									<input type="text" id="major" name="major" class="major"  maxlength="50"/>
								</div>
							</div>
						</div>
					</div>
				</li>
			</ul>
		</div>
		<div class="double-btn" style="display: none;">
			<div class="l quit">
				<em>取消</em>
			</div>
			<div class="r save">
				<em>保存</em>
			</div>
		</div>
	</div>
</form>

<form id="tranningForm">
	<input type="hidden" name="employeeId" value="${id }" />
	<input type="hidden" id="companyId" name="companyId"/> 
	<input type="hidden" id="delFlag" name="delFlag" value="0" />
	<input type="hidden" id="isCompanyTraining" name="isCompanyTraining" value="0" /><!-- 0外部培训 -->
	<div class="edit-page-a" style="display: none;">
		<header>
			<h1 class="clearfix">
				<a class="goback fl"><i class="back sr"></i></a>
				<p class="tip">详情列表</p>
				<a class="correcting fr delete" style="display: none;">删除</a>
			</h1>
		</header>
		<div class="main">
			<div class="add-type" >
				<em><i class="sr icon"></i>新增</em>
			</div>
			<ul>
				<li style = "display: none">
					<div class="input-list">
						
						<input type="hidden" name="id" id="id" class="id"/> 
						<input type="hidden" name="version" id="version" class="version"/>
						<div id="addDiv" class="m">
							<div class="mk sd">
								<div class="l-title n-input">开始时间<span>*</span></div>
								<div class="r-input time">
									<input type="text" readonly name="startTime" id="startTime"  class="startTime"/>
								</div>
							</div>
							<div class="mk sd">
								<div class="l-title n-input">结束时间<span>*</span></div>
								<div class="r-input time">
									<input type="text" readonly name="endTime" id="endTime" class="endTime" />
								</div>
							</div>
							<div class="mk sd">
								<div class="l-title">培训机构<span>*</span></div>
								<div class="r-input">
									<input type="text" id="trainingInstitutions" name="trainingInstitutions" class="trainingInstitutions"  maxlength="200"/>
								</div>
							</div>
							<div class="mk sd">
								<div class="l-title">所获证书</div>
								<div class="r-input null">
									<input type="text" id="obtainCertificate" name="obtainCertificate" class="obtainCertificate"  maxlength="200"/>
								</div>
							</div>
							<div class="mk sd">
								<div class="l-title">培训内容<span>*</span></div>
								<div class="r-input">
									<input type="text" id="content" name="content" class="content"  maxlength="500"/>
								</div>
							</div>
						</div>
					</div>
				</li>
			</ul>
		</div>
		<div class="double-btn" style="display: none;">
			<div class="l quit">
				<em>取消</em>
			</div>
			<div class="r save">
				<em>保存</em>
			</div>
		</div>
	</div>
    </form>
    
    <form id="workForm">
	<input type="hidden" name="employeeId" value="${id }" /> 
	<input type="hidden" id="companyId" name="companyId"/>
	<input type="hidden" id="delFlag" name="delFlag" value="0" />
	<div class="edit-page-a" style="display: none;">
		<header>
			<h1 class="clearfix">
				<a class="goback fl"><i class="back sr"></i></a>
				<p class="tip">详情列表</p>
				<a class="correcting fr delete" style="display: none;">删除</a>
			</h1>
		</header>
		<div class="main">
			<div class="add-type" >
				<em><i class="sr icon"></i>新增</em>
			</div>
			<ul>
				<li style = "display: none">
					<div class="input-list">
						
						<input type="hidden" name="id" id="id" class="id"/> 
						<input type="hidden" name="version" id="version" class="version"/>
						<div id="addDiv" class="m">
							<div class="mk sd">
								<div class="l-title n-input">开始时间<span>*</span></div>
								<div class="r-input time">
									<input type="text" readonly name="startTime" id="startTime"  class="startTime"/>
								</div>
							</div>
							<div class="mk sd">
								<div class="l-title n-input">结束时间<span>*</span></div>
								<div class="r-input time">
									<input type="text" readonly name="endTime" id="endTime" class="endTime" />
								</div>
							</div>
							<div class="mk sd">
								<div class="l-title">公司名称<span>*</span></div>
								<div class="r-input">
									<input type="text" id="companyName" name="companyName" class="companyName"  maxlength="100"/>
								</div>
							</div>
							<div class="mk sd">
								<div class="l-title">职位<span>*</span></div>
								<div class="r-input">
									<input type="text" id="positionName" name="positionName" class="positionName" maxlength="50"/>
								</div>
							</div>
							<div class="mk sd">
								<div class="l-title">主办业务<span>*</span></div>
								<div class="r-input">
									<input type="text" id="positionTask" name="positionTask" class="positionTask" maxlength="50"/>
								</div>
							</div>
						</div>
					</div>
				</li>
			</ul>
		</div>
		<div class="double-btn" style="display: none;">
			<div class="l quit">
				<em>取消</em>
			</div>
			<div class="r save">
				<em>保存</em>
			</div>
		</div>
	</div>
    </form>
    
    <form id="urgentContactForm">
	<input type="hidden" name="employeeId" value="${id }" /> 
	<input type="hidden" id="companyId" name="companyId"/>
	<input type="hidden" id="delFlag" name="delFlag" value="0" />
	<div class="edit-page-a" style="display: none;">
		<header>
			<h1 class="clearfix">
				<a class="goback fl"><i class="back sr"></i></a>
				<p class="tip">详情列表</p>
				<a class="correcting fr delete" style="display: none;">删除</a>
			</h1>
		</header>
		<div class="main">
			<div class="add-type" >
				<em><i class="sr icon"></i>新增</em>
			</div>
			<ul>
				<li style = "display: none">
					<div class="input-list">
						
						<input type="hidden" name="id" id="id" class="id"/> 
						<input type="hidden" name="version" id="version" class="version"/>
						<div id="addDiv" class="m">
							<div class="mk sd">
								<div class="l-title n-input">称谓<span>*</span></div>
								<div class="r-input">
									<input type="text" name="shortName" id="shortName" class="shortName" maxlength="10"/>
								</div>
							</div>
							<div class="mk sd">
								<div class="l-title n-input">姓名<span>*</span></div>
								<div class="r-input">
									<input type="text" name="name" id="name" class="name" maxlength="10"/>
								</div>
							</div>
							<div class="mk sd">
								<div class="l-title">手机号<span>*</span></div>
								<div class="r-input">
									<input type="number" id="companyName" name="mobile" class="mobile" maxlength="11"/>
								</div>
							</div>
							<div class="mk sd">
								<div class="l-title">固定电话<span>*</span></div>
								<div class="r-input">
									<input type="text" id="telphone" name="telphone" class="telphone" maxlength="20"/>
								</div>
							</div>
						</div>
					</div>
				</li>
			</ul>
		</div>
		<div class="double-btn" style="display: none;">
			<div class="l quit">
				<em>取消</em>
			</div>
			<div class="r save">
				<em>保存</em>
			</div>
		</div>
	</div>
    </form>
    
    <form id="spouseForm">
	<input type="hidden" name="employeeId" value="${id }" /> 
	<input type="hidden" id="companyId" name="companyId"/>
	<input type="hidden" id="delFlag" name="delFlag" value="0" />
	<input type="hidden" name="relation" id="relation" class="relation" value="0"/>
	<input type="hidden" id="spouseSex" name="memberSex" /><!-- key -->
	<div class="edit-page-a" style="display: none;">
		<header>
			<h1 class="clearfix">
				<a class="goback fl"><i class="back sr"></i></a>
				<p class="tip">详情列表</p>
				<a class="correcting fr delete" style="display: none;">删除</a>
			</h1>
		</header>
		<div class="main">
			<div class="add-type" >
				<em><i class="sr icon"></i>新增</em>
			</div>
			<ul>
				<li style = "display: none">
					<div class="input-list">
						
						<input type="hidden" name="id" id="id" class="id"/> 
						<input type="hidden" name="version" id="version" class="version"/>
						<div id="addDiv" class="m">
							<div class="mk sd">
								<div class="l-title n-input">配偶姓名<span>*</span></div>
								<div class="r-input">
									<input type="text" name="memberName" id="memberName" class="memberName" maxlength="11"/>
								</div>
							</div>
							<div class="mk sd">
								<div class="l-title n-input">手机号码<span>*</span></div>
								<div class="r-input">
									<input type="number" name="memberTelphone" id="memberTelphone" class="memberTelphone" maxlength="11"/>
								</div>
							</div>
							<div class="mk sd">
								<div class="l-title">工作单位</div>
								<div class="r-input">
									<input type="text" id="memberCompanyName" name="memberCompanyName" class="memberCompanyName" maxlength="100"/>
								</div>
							</div>
						</div>
					</div>
				</li>
			</ul>
		</div>
		<div class="double-btn" style="display: none;">
			<div class="l quit">
				<em>取消</em>
			</div>
			<div class="r save">
				<em>保存</em>
			</div>
		</div>
	</div>
    </form>
    
    <form id="childForm">
	<input type="hidden" name="employeeId" value="${id }" />
	<input type="hidden" id="companyId" name="companyId"/> 
	<input type="hidden" id="delFlag" name="delFlag" value="0" />
	<input type="hidden" name="relation" id="relation" class="relation" value="1"/>
	<div class="edit-page-a" style="display: none;">
		<header>
			<h1 class="clearfix">
				<a class="goback fl"><i class="back sr"></i></a>
				<p class="tip">详情列表</p>
				<a class="correcting fr delete" style="display: none;">删除</a>
			</h1>
		</header>
		<div class="main">
			<div class="add-type" >
				<em><i class="sr icon"></i>新增</em>
			</div>
			<ul>
				<li style = "display: none">
					<div class="input-list">
						
						<input type="hidden" name="id" id="id" class="id"/> 
						<input type="hidden" name="version" id="version" class="version"/>
						<div id="addDiv" class="m">
							<div class="mk sd">
								<div class="l-title">子女姓名<span>*</span></div>
								<div class="r-input">
									<input type="text" name="memberName" id="memberName" class="memberName"  maxlength="20"/>
								</div>
							</div>
							<div class="mk sd">
								<div class="l-title time">出生日期<span>*</span></div>
								<div class="r-input">
									<input type="text" id="birthday" name="birthday" class="birthday" readonly/>
								</div>
							</div>
							<div class="mk sd">
								<div class="l-title">子女性别<span>*</span></div>
			                    <div class="r-input selarea sex" id="sexSelect">
								     <input type="text" id="memberSexName" class="memberSexName" data-id="" readonly placeholder="请选择" /> 
								     <input type="hidden" id="memberSex" name="memberSex" value=""/>  
                                     <i class="sr icon"></i>
			                    </div>
							</div>
						</div>
					</div>
				</li>
			</ul>
		</div>
		<div class="double-btn" style="display: none;">
			<div class="l quit">
				<em>取消</em>
			</div>
			<div class="r save">
				<em>保存</em>
			</div>
		</div>
	</div>
    </form>
    
    <form id="achievementForm">
	<input type="hidden" name="employeeId" value="${id }" /> 
	<input type="hidden" id="companyId" name="companyId"/>
	<input type="hidden" id="delFlag" name="delFlag" value="0" />
	<div class="edit-page-a" style="display: none;">
		<header>
			<h1 class="clearfix">
				<a class="goback fl"><i class="back sr"></i></a>
				<p class="tip">详情列表</p>
				<a class="correcting fr delete" style="display: none;">删除</a>
			</h1>
		</header>
		<div class="main">
			<div class="add-type" >
				<em><i class="sr icon"></i>新增</em>
			</div>
			<ul>
				<li style = "display: none">
					<div class="input-list">
						
						<input type="hidden" name="id" id="id" class="id"/> 
						<input type="hidden" name="version" id="version" class="version"/>
						<div id="addDiv" class="m">
							<div class="mk sd">
								<div class="l-title time">奖惩时间<span>*</span></div>
								<div class="r-input">
									<input type="text" id="processTime" name="processTime" class="processTime" readonly/>
								</div>
							</div>
							<div class="mk sd">
								<div class="l-title">奖惩内容<span>*</span></div>
								<div class="r-input">
									<textarea name="content" id="content" class="content" maxlength="500"></textarea>
								</div>
							</div>
						</div>
					</div>
				</li>
			</ul>
		</div>
		<div class="double-btn" style="display: none;">
			<div class="l quit">
				<em>取消</em>
			</div>
			<div class="r save">
				<em>保存</em>
			</div>
		</div>
	</div>
    </form>
    
    <form id="companyTrainForm">
	<input type="hidden" name="employeeId" value="${id }" /> 
	<input type="hidden" id="companyId" name="companyId"/>
	<input type="hidden" id="delFlag" name="delFlag" value="0" />
	<input type="hidden" id="isCompanyTraining" name="isCompanyTraining" value="1" />
	<div class="edit-page-a" style="display: none;">
		<header>
			<h1 class="clearfix">
				<a class="goback fl"><i class="back sr"></i></a>
				<p class="tip">详情列表</p>
				<a class="correcting fr delete" style="display: none;">删除</a>
			</h1>
		</header>
		<div class="main">
			<div class="add-type" >
				<em><i class="sr icon"></i>新增</em>
			</div>
			<ul>
				<li style = "display: none">
					<div class="input-list">
						
						<input type="hidden" name="id" id="id" class="id"/> 
						<input type="hidden" name="version" id="version" class="version"/>
						<div id="addDiv" class="m">
							<div class="mk sd">
								<div class="l-title time">培训时间<span>*</span></div>
								<div class="r-input">
									<input type="text" id="startTime" name="startTime" class="startTime" readonly/>
								</div>
							</div>
							<div class="mk sd">
								<div class="l-title">培训项目/内容<span>*</span></div>
							</div>
							<div class="sd">
									<textarea style="width:100%;height:100px;" name="trainingProName" id="trainingProName" class="trainingProName" maxlength="100"></textarea>
							</div>
						</div>
					</div>
				</li>
			</ul>
		</div>
		<div class="double-btn" style="display: none;">
			<div class="l quit">
				<em>取消</em>
			</div>
			<div class="r save">
				<em>保存</em>
			</div>
		</div>
	</div>
    </form>
    
    <form id="contractForm">
	<input type="hidden" name="employeeId" value="${id }" /> 
	<input type="hidden" id="companyId" name="companyId"/>
	<input type="hidden" id="delFlag" name="delFlag" value="0" />
	<div class="edit-page-a" style="display: none;">
		<header>
			<h1 class="clearfix">
				<a class="goback fl"><i class="back sr"></i></a>
				<p class="tip">详情列表</p>
				<a class="correcting fr delete" style="display: none;">删除</a>
			</h1>
		</header>
		<div class="main">
			<div class="add-type" >
				<em><i class="sr icon"></i>新增</em>
			</div>
			<ul>
				<li style = "display: none">
					<div class="input-list">
						
						<input type="hidden" name="id" id="id" class="id"/> 
						<input type="hidden" name="version" id="version" class="version"/>
						<div id="addDiv" class="m">
							<div class="mk sd">
								<div class="l-title n-input">签订日期<span>*</span></div>
								<div class="r-input time">
									<input type="text" readonly name="contractSignedDate" id="contractSignedDate"  class="contractSignedDate"/>
								</div>
							</div>
							<div class="mk sd">
								<div class="l-title n-input">合同期限<span>*</span></div>
								<div class="r-input">
									<input type="number" name="contractPeriod" id="contractPeriod" class="contractPeriod" maxlength="5"  style="width:20px;"/>年
								</div>
							</div>
							<div class="mk sd">
								<div class="l-title">试用期期限</div>
								<div class="r-input null">
									<input type="text" id="probationExpire" name="probationExpire" class="probationExpire" maxlength="5" style="width:20px;"/>个月
								</div>
							</div>
							<div class="mk sd">
								<div class="l-title">合同到期日<span>*</span></div>
								<div class="r-input time">
									<input type="text" readonly name="contractEndTime" id="contractEndTime"  class="contractEndTime"/>
								</div>
							</div>
						</div>
					</div>
				</li>
			</ul>
		</div>
		<div class="double-btn" style="display: none;">
			<div class="l quit">
				<em>取消</em>
			</div>
			<div class="r save">
				<em>保存</em>
			</div>
		</div>
	</div>
    </form>
    
    <form id="empAppraiseForm">
	<input type="hidden" name="employeeId" value="${id }" /> 
	<input type="hidden" id="companyId" name="companyId"/>
	<input type="hidden" id="delFlag" name="delFlag" value="0" />
	<div class="edit-page-a" style="display: none;">
		<header>
			<h1 class="clearfix">
				<a class="goback fl"><i class="back sr"></i></a>
				<p class="tip">详情列表</p>
				<a class="correcting fr delete" style="display: none;">删除</a>
			</h1>
		</header>
		<div class="main">
			<div class="add-type" >
				<em><i class="sr icon"></i>新增</em>
			</div>
			<ul>
				<li style = "display: none">
					<div class="input-list">
						
						<input type="hidden" name="id" id="id" class="id"/> 
						<input type="hidden" name="version" id="version" class="version"/>
						<div id="addDiv" class="m">
							<div class="mk sd">
								<div class="l-title time">考核日期<span>*</span></div>
								<div class="r-input">
									<input type="text" readonly id="annualExaminationTime" name="annualExaminationTime" class="annualExaminationTime" />
								</div>
							</div>
							<div class="mk sd">
								<div class="l-title">考核成绩<span>*</span></div>
								<div class="r-input">
									<input type="text" name="score" id="score" class="score" maxlength="10"/>
								</div>
							</div>
							<div class="mk sd">
								<div class="l-title">考核结论<span>*</span></div>
								<div class="r-input">
									<textarea name="conclusion" id="conclusion" class="conclusion" maxlength="500"></textarea>
								</div>
							</div>
						</div>
					</div>
				</li>
			</ul>
		</div>
		<div class="double-btn" style="display: none;">
			<div class="l quit">
				<em>取消</em>
			</div>
			<div class="r save">
				<em>保存</em>
			</div>
		</div>
	</div>
    </form>
    
    <form id="empPositionForm">
	<input type="hidden" name="employeeId" value="${id }" /> 
	<input type="hidden" id="companyId" name="companyId"/>
	<input type="hidden" id="delFlag" name="delFlag" value="0" />
	<div class="edit-page-a" style="display: none;">
		<header>
			<h1 class="clearfix">
				<a class="goback fl"><i class="back sr"></i></a>
				<p class="tip">详情列表</p>
				<a class="correcting fr delete" style="display: none;">删除</a>
			</h1>
		</header>
		<div class="main">
			<div class="add-type" >
				<em><i class="sr icon"></i>新增</em>
			</div>
			<ul>
				<li style = "display: none">
					<div class="input-list">
						
						<input type="hidden" name="id" id="id" class="id"/> 
						<input type="hidden" name="version" id="version" class="version"/>
						<div id="addDiv" class="m">
							<div class="mk sd">
								<div class="l-title time">调整日期<span>*</span></div>
								<div class="r-input">
									<input type="text" readonly id="adjustDate" name="adjustDate" class="adjustDate" />
								</div>
							</div>
							<div class="mk sd">
								<div class="l-title">原岗位<span>*</span></div>
								<div class="r-input" id="positionSelect">
									<input type="text" id="prePositionName" class="prePositionName" readonly placeholder="请选择"/>
						            <input type="hidden" name="prePositionId" id="prePositionId" class="prePositionId"/> 
                                     <i class="sr icon"></i>
								</div>
							</div>
							<div class="mk sd">
								<div class="l-title">现岗位<span>*</span></div>
								<div class="r-input" id="positionSelect">
									<input type="text" id="positionName" class="positionName" readonly placeholder="请选择"/>
						            <input type="hidden" name="positionId" id="positionId" class="positionId"/> 
                                     <i class="sr icon"></i>
								</div>
							</div>
						</div>
					</div>
				</li>
			</ul>
		</div>
		<div class="double-btn" style="display: none;">
			<div class="l quit">
				<em>取消</em>
			</div>
			<div class="r save">
				<em>保存</em>
			</div>
		</div>
	</div>
    </form>
    
    <form id="remarkForm">
	<input type="hidden" name="id" value="${id }" /> 
    <input type="hidden" id="empVersion" name="version" class="empVersion"/>
	<div class="edit-page-a" style="display: none;">
		<header>
			<h1 class="clearfix">
				<a class="goback fl"><i class="back sr"></i></a>
				<p class="tip">备注</p>
			</h1>
		</header>
		<div class="main">
			<ul>
				<li style = "display: block">
					<div class="input-list">
						
						<input type="hidden" name="id" id="id" class="id"/> 
						<input type="hidden" name="version" id="version" class="version"/>
						<div id="addDiv" class="m">
							<div class="mk sd">
								<div class="l-title time">备注</div>
							</div>
							<div class="sd">
									<textarea style="width:100%;height:100px;" name="remark" id="remark" class="remark" maxlength="100"></textarea>
							</div>
						</div>
					</div>
				</li>
			</ul>
		</div>
		<div class="double-btn">
			<div class="l quit">
				<em>取消</em>
			</div>
			<div class="r save">
				<em>保存</em>
			</div>
		</div>
	</div>
    </form>