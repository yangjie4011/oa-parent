<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@include file="../../common/common.jsp"%>
<link rel="icon" href="data:;base64,=">
<title>新员工入职</title>
<script type="text/javascript" src="<%=basePath%>js/base/empApplicationRegister/register_handle.js?v=20200110"></script>
<style type="text/css">
	.b textarea{
		display: block;
	    width: 100%;
	    height: 85px;
	    resize: none;
	    outline: none;
	    padding: 22px 15px;
	    box-sizing: border-box;
	    -webkit-box-sizing: border-box;
	    margin: 0;
	    border: 0;
	    color: #333;
	    font-size: 14px;
	}
</style>
</head>
<body class="b-f2f2f2 mt-55 pb-54">
    <div class="oa-new_woker index-kaoqin">
        <header>
            <h1 class="clearfix"><a href="<%=basePath%>${returnUrl}" class="goback fl"><i class="back sr"></i></a>新员工入职<a href="#" class="save fr"></a></h1>
        </header>
       
        <div class="new-head">
            <a href="<%=basePath%>ruProcdef/viewTaskFlow.htm?instanceId=${emp.processInstanceId}&statu=${emp.approvalStatus}&title=新员工入职">
                <div class="img"></div>
                <div class="p1">
                <c:if test="${emp.approvalStatus==100}">
                 处理中，等待<span>[${taskVO.actName}]</span>审批
                    </c:if>
                    <c:if test="${emp.approvalStatus==200}">
                         已完成
                     </c:if>
                     <c:if test="${emp.approvalStatus==300}">
                         已拒绝
                     </c:if>
                     <c:if test="${emp.approvalStatus==400}">
                         已撤销
                     </c:if>
                </div>
                <div class="p3"><fmt:formatDate value="${emp.createTime}"  pattern="yyyy-MM-dd HH:mm:ss"  /></div>
                <i></i>
            </a>
        </div>
        
        <section class="new-woker-info">
            <section class="name-info">
                <div class="selMatter clearfix datenum">
                    <h4 class="fl">公司名称</h4>
                    <div class="selarea fr">
                        ${emp.companyName}
                    </div>
                    <input type="hidden" id="companyId" name="companyId" value="${emp.companyId}">
                </div>
                <div class="selMatter clearfix datenum">
                    <h4 class="fl">员工类型</h4>
                    <div class="selarea fr">
                        ${emp.employeeTypeCName}
                    </div>
                </div>
                <div class="selMatter clearfix datenum">
                    <h4 class="fl">工时种类</h4>
                    <div class="selarea fr">
                        ${emp.workTypeName}
                    </div>
                </div>
                <div class="selMatter clearfix datenum">
                    <h4 class="fl">是否排班</h4>
                    <div class="selarea fr">
                        ${emp.whetherSchedulingName}
                    </div>
                </div>
                <div class="selMatter clearfix datenum">
                    <h4 class="fl">中文名</h4>
                    <div class="selarea fr">
                        ${emp.cnName}
                    </div>
                </div>
                <div class="selMatter clearfix datenum">
                    <h4 class="fl">英文名</h4>
                    <div class="selarea fr">
                        ${emp.engName}
                    </div>
                </div>      
                <c:if test="${mobile!=null}">
	                <div class="selMatter clearfix datenum">
	                    <h4 class="fl">手机号</h4>
	                    <div class="selarea fr">
	                        ${mobile}
	                    </div>
	                </div>      
                </c:if>
                <div class="selMatter clearfix datenum">
                    <h4 class="fl">员工编号</h4>
                    <div class="selarea fr">
                        ${emp.code}
                    </div>
                </div>
                <div class="selMatter clearfix datenum">
                    <h4 class="fl">指纹ID</h4>
                    <div class="selarea fr fingerprintId">
                        ${emp.fingerprintId}
                    </div>
                </div>
                <c:if test="${!(emp.approvalStatus ==100 && isSelf)}">
                  <div class="selMatter clearfix datenum">
	                   <h4 class="fl">入职日期</h4>
	                   <div class="selarea fr">
	                       <fmt:formatDate value="${emp.employmentDate}"  pattern="yyyy-MM-dd"  />
	                   </div>
                   </div>
                </c:if>
                <c:if test="${emp.approvalStatus ==100 && isSelf}">
                  <div class="selMatter clearfix datenum">
                    <h4 class="fl">入职日期</h4>
                    <div class="selarea fr">
                         <c:if test="${emp.type==0}">
                         	<input type="text" readonly placeholder="请选择入职日期" value="<fmt:formatDate value="${emp.employmentDate}"  pattern="yyyy-MM-dd"  />" id="delay-work" data-lcalendar="2017-06-17,9999-12-31">
                         </c:if>
                         <c:if test="${emp.type==1}">
                            <input type="text" readonly placeholder="请选择入职日期" value="<fmt:formatDate value="${emp.employmentDate}"  pattern="yyyy-MM-dd"  />" id="delay-work" data-lcalendar="${fristEntryTime},9999-12-31">
                         </c:if>
                        <i class="sr icon"></i>
                    </div>
                   </div>
                </c:if>        
            </section>
            <div class="selMatter clearfix datenum">
                <h4 class="fl">部门</h4>
                <div class="selarea fr">
                    ${emp.departName1}
                </div>
            </div>
            <div class="selMatter clearfix datenum">
                <h4 class="fl">职位</h4>
                <div class="selarea fr">
                    ${emp.positionName}
                </div>
            </div>
            <div class="selMatter clearfix datenum">
                <h4 class="fl">职位序列</h4>
                <div class="selarea fr">
                    <c:if test="${emp.positionSeq!= null}">${emp.positionSeq}</c:if>
                </div>
            </div>
            <div class="selMatter clearfix datenum">
                <h4 class="fl">职级</h4>
                <div class="selarea fr">
                    <c:if test="${emp.positionLevel!= null}">${emp.positionLevel}</c:if>
                </div>
            </div>
            <div class="selMatter clearfix datenum">
                <h4 class="fl">汇报对象</h4>
                <div class="selarea fr">
                   ${emp.leaderName}
                </div>
            </div>
            <div class="selMatter clearfix datenum">
                <h4 class="fl">部门负责人</h4>
                <div class="selarea fr">
                    ${emp.departHeader}
                </div>
            </div>
            <div class="selMatter clearfix datenum">
                <h4 class="fl">工作地点</h4>
                <div class="selarea fr">
                   <c:if test="${emp.workAddressProvince!= null}">${emp.workAddressProvince}</c:if>
                    &nbsp;
                    <c:if test="${emp.workAddressCity!= null}">${emp.workAddressCity}</c:if>
                </div>
            </div>
            <c:if test="${emp.remarks!= null}">
	            <div class="selMatter clearfix datenum">
		            <h4 class="fl">备注</h4>
		            ${emp.remarks}
	            </div>
	            <div class="selMatter clearfix datenum">
		            
	            </div>
	            <div class="selMatter clearfix datenum">
		            
	            </div>
            </c:if>
            <c:if test="${emp.nodeCode!='personnel' and emp.nodeCode!='executive' and emp.nodeCode!='IT'}">
            <section class="it-wrap">
	                <section class="it-own">
	                    <div class="selMatter clearfix">
	                        <h4 class="fl">工作邮箱</h4>
	                        <div class="selarea fr email">
	                            ${emp.email}
	                        </div>
	                    </div>
	                    <div class="selMatter clearfix">
	                        <h4 class="fl">分机号</h4>
	                        <div class="selarea fr">
	                            ${emp.extensionNumber}
	                        </div>
	                    </div>
	                </section>
	            </section>
	            <section class="admin-wrap">
	                <section class="it-own">
	                    <div class="selMatter clearfix">
	                        <h4 class="fl">楼层</h4>
	                        <div class="selarea fr">
	                            ${emp.floorNum}
	                        </div>
	                    </div>
	                    <div class="selMatter clearfix">
	                        <h4 class="fl">座位号</h4>
	                        <div class="selarea fr">
	                            ${emp.seatNo}
	                        </div>
	                    </div>
	                </section>
	            </section>
            </c:if>
            <c:if test="${emp.nodeCode=='IT'}">
	            <section class="it-wrap">
	                <section class="it-own">
	                    <div class="selMatter clearfix">
	                        <h4 class="fl">工作邮箱</h4>
	                        <div class="selarea fr">
	                            <input <c:if test="${isRepeat}">value="${email}"</c:if> <c:if test="${!isRepeat}">value="${emp.email}"</c:if> id="email" name="email" type="text" class="fr" placeholder="请输入邮箱"/>
	                        </div>
	                    </div>
	                    <div class="selMatter clearfix">
	                        <h4 class="fl">分机号</h4>
	                        <div class="selarea fr">
	                            <input value="${emp.extensionNumber}" id="extensionNumber" name="extensionNumber" type="number" oninput="if(value.length>4)value=value.slice(0,4)" class="fr" placeholder="请输入分机号" />
	                        </div>
	                    </div>
	                </section>
	            </section>
	            <section class="admin-wrap">
	                <section class="it-own">
	                    <div class="selMatter clearfix">
	                        <h4 class="fl">楼层</h4>
	                        <div class="selarea fr">
	                            ${emp.floorNum}
	                        </div>
	                    </div>
	                    <div class="selMatter clearfix">
	                        <h4 class="fl">座位号</h4>
	                        <div class="selarea fr">
	                            ${emp.seatNo}
	                        </div>
	                    </div>
	                </section>
	            </section>
            </c:if>
            <c:if test="${emp.nodeCode=='executive'}">
	            <section class="it-wrap">
	                <section class="it-own">
	                    <div class="selMatter clearfix">
	                        <h4 class="fl">工作邮箱</h4>
	                        <div class="selarea fr email">
	                           ${emp.email}
	                        </div>
	                    </div>
	                    <div class="selMatter clearfix">
	                        <h4 class="fl">分机号</h4>
	                        <div class="selarea fr">
	                            ${emp.extensionNumber}
	                        </div>
	                    </div>
	                </section>
	            </section>
	            <section class="admin-wrap">
	                <section class="it-own">
	                    <div class="selMatter clearfix">
	                        <h4 class="fl">楼层</h4>
	                        <div class="selarea fr floorName">
	                            <input data-id="${emp.floorId}" value="${emp.floorNum}" id="floorId" name="floorId" type="text" readonly placeholder="请选择楼层" />
	                            <i class="sr icon"></i>
	                        </div>
	                    </div>
	                    <div class="selMatter clearfix">
	                        <h4 class="fl">座位号</h4>
	                        <div class="selarea fr">
	                            <input value="${emp.seatNo}" id="seatNo" name="seatNo" type="text" class="fr" placeholder="请输入座位号" />
	                        </div>
	                    </div>
	                </section>
	            </section>
            </c:if>
        </section>
        <input type="hidden" id="id" name="id" value="${emp.processInstanceId}"/>
        <input type="hidden" id="id1" name="id1" value="${emp.id}"/>
        <input type="hidden" id="taskId" name="taskId" value="${taskId}"/>
        <input type="hidden" id="token" name="token" value="${token}"/>
        <input type="hidden" id="identificationNum" name="identificationNum" value="${identificationNum}"/>
        <input type="hidden" id="isRepeat" name="isRepeat" value="${isRepeat}"/>
        <input type="hidden" id="code" name="code" value="${code}"/>
        <c:if test="${emp.nodeCode=='IT' and canApprove}">
           <section id="itHandle" class="foot-btn">提交</section>
        </c:if>
        <c:if test="${emp.nodeCode=='executive' and canApprove}">
           <section id="adHandle" class="foot-btn">提交</section>
        </c:if>
        <c:if test="${emp.nodeCode=='personnel' and canApprove}">
	        <section class="foot-btns">
	            <div id="cancel" class="btn-cancel">
	                <div class="area">
	                    <i></i><span>取消入职</span>
	                </div>
	            </div>
	            <div id="delay" class="btn-delay">
	                <div class="area">
	                    <i></i><span>延时入职</span>
	                </div>
	            </div>
	            <div id="confirm" class="btn-ensure">
	                <div class="area">
	                    <span>确认入职</span>
	                </div>
	            </div>
	        </section>
        </c:if>
       
      <c:if test="${isPersonnel=='no'}">  
         <c:if test="${emp.approvalStatus ==100 && isSelf}">
	          <div class="b">
	               <textarea id="approvalReason" name="approvalReason" placeholder="请填写撤回原因（非必填）" cols="30" rows="10"></textarea>
	          </div>
	          <section class="foot-btns">
	          <div class="btn-cancel" onclick="cancel()">
                <div class="area">
                    <i></i><span>取消入职</span>
                </div>
            	</div>
	          <div class="btn-ensure"  onclick="cancelRunBackTask()">
	          		<div class="area">
	                    <span>撤销</span>
	                </div>
	            </div>
	          </section>
        </c:if>
      </c:if>  
    </div>
    
</body>
</html>