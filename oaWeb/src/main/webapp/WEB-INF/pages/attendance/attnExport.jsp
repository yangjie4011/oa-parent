<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<%@include file="../common/common.jsp"%>
<link rel="icon" href="data:;base64,=">
<title>员工考勤报表导出</title>
    <meta charset="UTF-8">
</head>

<body class="b-f2f2f2 mt-44">
    <div class="day">
        <header>
            <h1 class="clearfix"><a href="javascript:window.history.go(-1);" class="goback fl"><i class="back sr"></i></a>
                <p>员工考勤报表导出</p>
            </h1>
        </header>
        <div class="banci-list">
            <ul>
                <li>
                      <div class="main">
                          <h4 class="l">报表类型</h4>
                          <div class="r"  id="exportListSelect">
					      <input type="text" id="reportName" class="reportName" readonly placeholder="请选择" /><!-- value -->
					      <input type="hidden" id="reportId" name="reportId" class="reportId"/><!-- key -->
                             <i class="sr icon"></i>
                          </div>
                      </div>
                </li>
                <li>
                    <div class="main">
                        <h4 class="l">开始时间</h4>
                        <div class="r">
                            <input type="text" readonly="readonly" placeholder="请选择时间" id="startTime" class="date"/>
                            <i class="icon"></i>
                        </div>
                    </div>
                </li>
                <li>
                    <div class="main">
                        <h4 class="l">结束时间</h4>
                        <div class="r">
                            <input type="text" readonly="readonly" placeholder="请选择时间" id="endTime" class="date"/>
                            <i class="icon"></i>
                        </div>
                    </div>
                </li>
                <li>
                    <div class="main">
                        <h4 class="l">查询范围</h4>
                        <div class="r">
                                <div class="radio" id="entireRadio">全公司</div>
                                <div class="radio on" id="departRadio">部门</div>
                        </div>
                    </div>
                </li>
                <li>
                    <div class="main">
                        <h4 class="l">选择部门</h4>
                        <div class="r">
                            <input type="text" readonly="readonly" placeholder="请选择部门" id="selt" />
                            <input type="hidden" id="departId" class="departId"/>
                            <i class="icon"></i>
                        </div>
                    </div>
                </li>
            </ul>
        </div>
        <div class="foot-btn">导出报表</div>
    </div>

    <script>
        $(function () {
            listTypeSelect.init('daochulist');
            
            downBox();
            
            $(".foot-btn").click(function(){
            	exportReport();
            });
        });
        
        var exportReport = function(){
        	OA.pageLoading(true);
        	
        	var reportId = $("#reportId").val();
        	var startTime = $("#startTime").val();
        	var endTime = $("#endTime").val();
        	var departId;
        	
        	if($("#entireRadio").hasClass("on")){//全公司，部门不取值
        		
        	}else if($("#departRadio").hasClass("on")){
        		departId = $("#departId").val();
        	}
        	$.ajax({
        	    type:"POST",
        		url : contextPath + "/empAttn/exportAttnData.htm",
        		data : {reportId:reportId,startTime:startTime,endTime:endTime,departId:departId},
        		dataType : 'json',
        		success : function(response) {
        			OA.pageLoading(false);
        			if(response.success){
        				OA.titlePup(response.message, 'win');
        			}else{
        				OA.titlePup(response.message, 'lose');
        			}
        		},  
		        error:function(data, status, e){ //服务器响应失败时的处理函数  
		        	OA.pageLoading(false);
		            OA.titlePup('服务器响应失败,请重试','lose');
		        }
        	})
        }

        var downBox = function() {//下拉总体样式
        	var list = [ {
        		id : 'exportListSelect',
        		values : [ '员工考勤明细', '实时打卡明细','月度加班单明细表','月度加班统计汇总表','月度缺勤统计表','月度缺勤总数表','假期明细报表','月度假期统计表','晚间餐费与交通费统计','晚间餐费与交通费补贴月度明细'],
        		keys : [ 'empAttnDetail', 'actualTimeRecord' ,'applicationOverTimeDetail','applicationOverTimeSumReport','monthLack','monthLackDetail','leaveReports','monthLeaveSummary','monthAllowanceTimes','monthAllowanceDetails']
        	}];

        	//返写下拉列表事件
        	//点击赋值事件
        	for (var a = 0; a < list.length; a++) {
        		$('.banci-list #' + list[a].id + '').find('input').attr('readonly',
        				'readonly');
        		$('.banci-list #' + list[a].id + '').click(function() {
        			var m = $(this);
        			for (var b = 0; b < list.length; b++) {
        				if (m.attr('id') == list[b].id) {
        					var values = list[b].values;
        					var keys = list[b].keys;
        					var index;
        					OA.bPup(values, function(t) {
        						m.find('input[type="text"]').val(t);

        						//获取选中值的下标，由于不想改动前端js，故在这里处理
        						for (row in values) {
        							if (values[row] == t) {
        								index = row;
        								break;
        							}
        						}
        						m.find('input[type="hidden"]').val(keys[index]);
        					})
        				}
        			}
        		})
        	}
        }
    </script>
</body>
</html>