<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@include file="../../common/common.jsp"%>
<link rel="icon" href="data:;base64,=">
<title>定位签到</title>
<script type="text/JavaScript" src="https://api.map.baidu.com/api?v=3.0&ak=loL2Fg5rYKrsxRon3S8ib64zSMe7kseU"></script>
<script type="text/javascript" src="<%=basePath%>js/base/checkIn/locationCheckIn.js?v=20200420"/></script>
<script src="https://res.wx.qq.com/open/js/jweixin-1.0.0.js"></script>

  <script type="text/javascript">
	var wxobj = {

		//位置服务

		getLocation : function(callback){

			//判断当前客户端版本是否支持指定JS接口

			wx.ready(function(){

			    // config信息验证后会执行ready方法，所有接口调用都必须在config接口获得结果之后，config是一个客户端的异步操作，所以如果需要在页面加载时就调用相关接口，则须把相关接口放在ready函数中调用来确保正确执行。对于用户触发时才调用的接口，则可以直接调用，不需要放在ready函数中。

				wx.checkJsApi({

					jsApiList: ['getLocation'],

					success: function(res) {

						// 以键值对的形式返回，可用的api值true，不可用为false

						//如：{"checkResult":{"chooseImage":true},"errMsg":"checkJsApi:ok"}

						if (res.checkResult.scanQRCode == false) {

							OA.pageLoading(false);
						    OA.titlePup('定位失败！微信版本号过低。','lose');

				            return;

						}

						if (res.checkResult.getLocation == false) {

							OA.pageLoading(false);
						    OA.titlePup('定位失败！请确保手机定位功能开启。','lose');

				            return;

						}

						wx.getLocation({

							type: 'gcj02',						// 默认为wgs84的gps坐标，如果要返回直接给openLocation用的火星坐标，可传入'gcj02'

						    success: function (res) {

								try{
									callback(res);
						   		}catch(error){
						   			OA.pageLoading(false);
								    OA.titlePup('签到失败，可尝试重新签到或联系HR同事。','lose');
						   		}

							},

							cancel: function (res) {
								OA.pageLoading(false);
							    OA.titlePup('签到失败，可尝试重新签到或联系HR同事。','lose');
							},

							fail:function(res) {
							    OA.pageLoading(false);
								OA.titlePup('定位失败，可尝试重新签到或联系HR同事。','lose');
							}

						});

				       

				   }

				});

			});

		}

	};

	jQuery(function(){ 
		OA.pageLoading(false);
		$.ajax({
			async:true,
			type:'post',
			dataType:'json',
			data:{
				url:window.location.href
			},
			url:contextPath + "/locationCheckIn/getWxConfig1.htm",
			success:function(data){
				if(data.success){
					wx.config({

					    //debug: true, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。

					    appId: data.config.appId, // 必填，公众号的唯一标识

					    timestamp: data.config.timestamp, // 必填，生成签名的时间戳

					    nonceStr: data.config.nonceStr, // 必填，生成签名的随机串

					    signature: data.config.signature,// 必填，签名，见附录1

					    jsApiList: ['openLocation','getLocation'] // 必填，需要使用的JS接口列表，所有JS接口列表见附录2

					});
				}else{
				    OA.titlePup('签到失败，可尝试重新签到或联系HR同事。','lose');
				}
			},
			complete:function(XMLHttpRequest,status){
				OA.pageLoading(false);
			}
		});

	});

</script>
</head>
<body class="b-f2f2f2 mt-44">
    <div class="oa-travel_apply">
        <header>
            <h1 class="clearfix"> <c:if test="${!isNotBack}"><a href="<%=basePath%>${returnUrl}" class="goback fl"><i class="back sr"></i></a></c:if>定位签到</h1>    		
        </header>
        
        <c:if test="${otherPlace}">
	        <section class="edit-info">
	       		<div>
		       		<p class="notice" style="color:red;">警示：请务必确保本人签到！</p>
		            <p class="notice" style="color:red;">请确保距离公司100米以内完成签到，</p>
		            <p class="notice" style="color:red;">否则可能无法签到成功。</p>
		            <p class="notice" style="color:red;">请确保当天签到成功后，</p>
		            <p class="notice" style="color:red;">本人已在工作岗位，否则按缺勤处理。</p>
	       		</div>
	
	       		<p id="oaDate" style="text-align:center;margin-top:88px">${oaDate}</p>
	       		
	       		<div style="margin-top:20px;">
	       		   <div style="text-align:center;margin:0 auto;width:100px;height:100px;background-color:#2894FF;border-radius:50%;">
	       		   <p id="oaTime" style="padding-top:20px;color:#fff;">${oaTime}</p>
	       		   <p onclick="checkIn();" style="padding-top:20px;color:#fff;font-size: 25px;">签到</p>
	       		   </div>
	       		</div>
	        </section>
        </c:if>
        
        <c:if test="${!otherPlace}">
          <section class="edit-info">
       		<div>
       			<p class="notice" style="color:blue;">定位签到功能已下线。</p>
       		</div>
        </section>
        </c:if>
    </div>

	<script>
	
	function checkIn(){
		var oaDate = $("#oaDate").text()+ $("#oaTime").text();
		OA.twoSurePop({
			tips:'当前时间为'+oaDate+'，是否确定签到？',
			sureFn:function(){
				OA.pageLoading(true);
				
				wxobj.getLocation(function(r) {
					$.ajax({
						async:true,
						type:'post',
						timeout : 10000,
						dataType:'json',
						data:{
							locationResult:JSON.stringify(r)
						},
						url:contextPath + "/locationCheckIn/checkIn.htm",
						success:function(data){
							if(data.success){
								OA.pageLoading(false);
								OA.titlePup('签到成功。','win');
							}else{
								OA.pageLoading(false);
								OA.titlePup(data.message,'lose');
							}
						},
						complete:function(XMLHttpRequest,status){
						   if(status=="timeout"){
							   OA.titlePup("请求超时！",'lose');
						   }
						}
					});
			    });
			},
			cancelFn:function(){
				
			}
		})
	}


	</script>


    
</body>



</html>