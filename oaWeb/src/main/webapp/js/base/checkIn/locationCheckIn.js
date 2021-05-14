function checkIn(){
	var oaDate = $("#oaDate").text()+ $("#oaTime").text();
	OA.twoSurePop({
		tips:'当前时间为'+oaDate+'，是否确定签到？',
		sureFn:function(){
			OA.pageLoading(true);
			var geolocation = new BMap.Geolocation();
			geolocation.getCurrentPosition(function(r){
			 	if(this.getStatus() == BMAP_STATUS_SUCCESS){
			 		$.ajax({
						async:true,
						type:'post',
						timeout : 5000,
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
								OA.titlePup('签到失败，可尝试重新签到或联系HR同事。','lose');
							}
						},
						complete:function(XMLHttpRequest,status){
						   if(status=="timeout"){
							   OA.titlePup(data.message,'lose');
						   }
						}
					});
			 	}
			 	else {
			 		OA.pageLoading(false);
					OA.titlePup('签到失败，可尝试重新签到或联系HR同事。','lose');
			 	}        
			 });
		},
		cancelFn:function(){
			
		}
	})
}