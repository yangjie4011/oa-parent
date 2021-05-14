var addEvection = {
       tpls: {
            addhtml: '<div class="insertBox" id="insertBox">\
            <h4 class="fl">请输入省市</h4>\
            <div class="selarea fr">\
                <input type="text"  class="travelAddressVla" name="adressVal" placeholder="请输入省市" id="del">\
                <span class="z_del"  id="del"></span>\
                <input type="hidden" class="hiddenTravelProvince" id="travelProvince" />\
                <input type="hidden" class="hiddenTravelCity"id="travelCity" />\
            </div>\
         </div>'
        },
        init: function () {
            this.click()
        },
        render: function () {
        	
        },
        insert_text: function (self) {
          
            areaSeled.init(function (str) {
                $(self).val(str.province+" "+str.city);
                $(self).siblings('.z_del').show();
            });
            $('.z_listBox').show();
            
        },
        judge: function () {
            var number = 0
            $('.travelAddressVla').each(function () {
                if ($.trim($(this).val()) == "") {
                    number++
                }
            })
            if (number < 1) {
                addEvection.render()
            }
        },
        click: function () {
            $('.edit-info').on("click", '.z_del', function () {
                $(this).parent().parent().remove();
                addEvection.judge()
                addEvection.route_text()
            })
            $('body').on("click", '.travelAddressVla', function () {
                var self = this
                addEvection.insert_text(self)
                $('.z_affirm').click(function () {
                    addEvection.judge()
                    addEvection.route_text()
                    $('.addAddress').show()
                })
            })
        },
        route_text: function () {
           
        },
}

$(function(){
	
	addEvection.init();
    
    //初始化日历
    var calendartime = new lCalendar();
    calendartime.init({
        'trigger': 'answer_8',
        'type': 'date'
    });
    var calendartime = new lCalendar();
    calendartime.init({
        'trigger': 'answer_9',
        'type': 'date'
    });
    
    //控制页面显示
    $("#answer_5").on('click touchstart','span',function(){
		$(this).children('em').addClass('current');
	    $(this).siblings('span').children('em').removeClass('current');
        var answer = $(this).children('em').attr("answer");
        if(answer=="是"){
            $(".isShow").show();
        }else{
       		$(".isShow").hide();
        }
    });
    
    //初始化单选按钮
    $("#answer_4").on('click touchstart','.singleSelGroup span',function(){
        $(this).children('em').addClass('current');
        $(this).siblings('span').children('em').removeClass('current');
    })
    
    $("#answer_6").on('click touchstart','.singleSelGroup span',function(){
        $(this).children('em').addClass('current');
        $(this).siblings('span').children('em').removeClass('current');
    })
    
     $(".answer_12").on('click touchstart','.singleSelGroup span',function(){
        $(".answer_12").find(".singleSelGroup").find("span").children("em").removeClass("current");
        $(this).children('em').addClass('current');
    })
    
    $(".answer_13").on('click touchstart','.singleSelGroup span',function(){
        $(".answer_13").find(".singleSelGroup").find("span").children("em").removeClass("current");
        $(this).children('em').addClass('current');
    })
    
    $(".answer_14").on('click touchstart','.singleSelGroup span',function(){
        $(".answer_14").find(".singleSelGroup").find("span").children("em").removeClass("current");
        $(this).children('em').addClass('current');
    })
    
    $(".answer_16").on('click touchstart','.singleSelGroup span',function(){
        $(".answer_16").find(".singleSelGroup").find("span").children("em").removeClass("current");
        $(this).children('em').addClass('current');
    })
    
    $(".answer_15").on('click touchstart','.singleSelGroup span',function(e){
    	e.preventDefault();
        var obj = $(this).children('em');
    	 if($(obj).hasClass("current")){
    		$(obj).removeClass('current');
    	 }else{
    		$(obj).addClass('current');
    	}
    })
    
})

function save(){
    
	var list = new Array();
    //判断是否登记过
	if($("#needBaseInfo").val()==true||$("#needBaseInfo").val()=="true"){
		var answer_1 = $("#answer_1").val().trim();
	    if(answer_1==""){
	    	OA.titlePup('请填写户籍！','lose');
			return;
	    }
	    var detail_1 = {
			type:1,
			question_num:1,
			question:"户籍",
			answer_num:1,
			answer:answer_1
		};
		list.push(detail_1);
	    var answer_2 = $("#answer_2").val().trim();
	    if(answer_2==""){
	    	OA.titlePup('请填写籍贯！','lose');
			return;
	    }
	    var detail_2 = {
			type:1,
			question_num:2,
			question:"籍贯",
			answer_num:2,
			answer:answer_2
		};
		list.push(detail_2);
	    var answer_3 = $("#answer_3").val().trim();
	    if(answer_3==""){
	    	OA.titlePup('请填写现上海常住地址！','lose');
			return;
	    }
	    var detail_3 = {
			type:1,
			question_num:3,
			question:"现上海常住地址",
			answer_num:3,
			answer:answer_3
	    };
	    list.push(detail_3);
	    var answer_4 = "";
	    $("#answer_4").find("em").each(function(){
			if($(this).hasClass("current")){
			   answer_4 = $(this).attr("answer");
			}
		})
	    if(answer_4==""){
	    	OA.titlePup('请勾选是否接触过湖北地区人员！','lose');
			return;
	    }
	    var detail_4 = {
			type:1,
			question_num:4,
			question:"是否接触过湖北地区人员",
			answer_num:4,
			answer:answer_4
	    };
	    list.push(detail_4);
	    var answer_5 = "";
	    $("#answer_5").find("em").each(function(){
			if($(this).hasClass("current")){
			   answer_5 = $(this).attr("answer");
			}
		})
	    if(answer_5==""){
	    	OA.titlePup('请勾选2020年春节休假是否离开过上海(或工作城市)！','lose');
			return;
	    }
	    var detail_5 = {
			type:1,
			question_num:5,
			question:"2020年春节休假是否离开过上海(或工作城市)",
			answer_num:5,
			answer:answer_5
	    };
	    list.push(detail_5);
	    var answer_6 = "";
	    $("#answer_6").find("em").each(function(){
			if($(this).hasClass("current")){
			   answer_6 = $(this).attr("answer");
			}
		})
		var answer_7 = $("#answer_7").val().trim();
		var answer_8 = $("#answer_8").val().trim();
		var answer_9 = $("#answer_9").val().trim();
		var answer_10 = $("#answer_10").val().trim();
	    if(answer_5=="是"){
	    	if(answer_6==""){
		    	OA.titlePup('请勾选是否去过或途径湖北！','lose');
				return;
	    	}
	    	var detail_6 = {
				type:1,
				question_num:6,
				question:"是否去过或途径湖北",
				answer_num:6,
				answer:answer_6
		    };
		    list.push(detail_6);
	     	if(answer_7==""){
		    	OA.titlePup('请填写离开上海（或工作城市）去的地方！','lose');
				return;
	    	}
	     	var detail_7 = {
				type:1,
				question_num:7,
				question:"离开上海（或工作城市）去的地方",
				answer_num:7,
				answer:answer_7
		    };
		    list.push(detail_7);
	    	if(answer_8==""){
		    	OA.titlePup('请填写离沪日期！','lose');
				return;
	    	}
	    	var detail_8 = {
				type:1,
				question_num:8,
				question:"离沪日期",
				answer_num:8,
				answer:answer_8
		    };
		    list.push(detail_8);
	    	if(answer_9==""){
		    	OA.titlePup('请填写返沪日期！','lose');
				return;
	    	}
	    	var detail_9 = {
				type:1,
				question_num:9,
				question:"返沪日期",
				answer_num:9,
				answer:answer_9
		    };
		    list.push(detail_9);
	    	if(answer_10==""){
		    	OA.titlePup('请填写自驾、飞机应填航班号、火车应填班次等！','lose');
				return;
	    	}
	    	var detail_10 = {
				type:1,
				question_num:10,
				question:"自驾、飞机应填航班号、火车应填班次等",
				answer_num:10,
				answer:answer_10
		    };
		    list.push(detail_10);
	    }else{
	    	var detail_6 = {
				type:1,
				question_num:6,
				question:"是否去过或途径湖北",
				answer_num:6,
				answer:""
		    };
		    list.push(detail_6);
		    var detail_7 = {
				type:1,
				question_num:7,
				question:"离开上海（或工作城市）去的地方",
				answer_num:7,
				answer:""
		    };
		    list.push(detail_7);
		    var detail_8 = {
				type:1,
				question_num:8,
				question:"离沪日期",
				answer_num:8,
				answer:""
		    };
		    list.push(detail_8);
		    var detail_9 = {
				type:1,
				question_num:9,
				question:"返沪日期",
				answer_num:9,
				answer:""
		    };
		    list.push(detail_9);
		    var detail_10 = {
				type:1,
				question_num:10,
				question:"自驾、飞机应填航班号、火车应填班次等",
				answer_num:10,
				answer:""
		    };
		    list.push(detail_10);
	    }
	}
	
    var answer_11 = $("#answer_11").val().trim();
    if(answer_11==""){
    	OA.titlePup('请填写当前所在地！','lose');
		return;
    }
    var detail_11 = {
		type:2,
		question_num:11,
		question:"当前所在地",
		answer_num:11,
		answer:answer_11
    };
    list.push(detail_11);
    var answer_12 = "";
    $(".answer_12").find("em").each(function(){
		if($(this).hasClass("current")){
		   answer_12 = $(this).attr("answer");
		}
	})
	if(answer_12==""){
    	OA.titlePup('请勾选今天你的个人健康状况！','lose');
		return;
    }
    var answer_12_extra = "";
	if(answer_12=="发热、咳嗽、头疼乏力或呼吸困难等症状（发热37.3°及以上）"){
		answer_12_extra = $("#answer_12_extra_1").val().trim();
		if(answer_12_extra==""){
			OA.titlePup('填写发热度数，具体症状描述！','lose');
			return;
		}
	}else if(answer_12=="以上均不是，请填写。。。"){
		answer_12_extra = $("#answer_12_extra_2").val().trim();
		if(answer_12_extra==""){
			OA.titlePup('以上均不是，请填写。。。！','lose');
			return;
		}
	}
	if(answer_12_extra!=""){
		answer_12 = answer_12+"；"+answer_12_extra;
	}
	var detail_12 = {
		type:2,
		question_num:12,
		question:"今天你的个人健康状况",
		answer_num:12,
		answer:answer_12
    };
    list.push(detail_12);
	var answer_13 = "";
    $(".answer_13").find("em").each(function(){
		if($(this).hasClass("current")){
		   answer_13 = $(this).attr("answer");
		}
	})
	if(answer_13==""){
    	OA.titlePup('请勾选是否封城封村！','lose');
		return;
    }
	var answer_13_extra = "";
	if(answer_13=="以上均不是，请填写。。。"){
		answer_13_extra = $("#answer_13_extra").val().trim();
		if(answer_13_extra==""){
			OA.titlePup('以上均不是，请填写。。。！','lose');
			return;
		}
	}
	if(answer_13_extra!=""){
		answer_13 = "以上均不是，请填写。。。；"+answer_13_extra;
	}
	var detail_13 = {
		type:2,
		question_num:13,
		question:"是否封城封村",
		answer_num:13,
		answer:answer_13
    };
    list.push(detail_13);
    var answer_14 = "";
    $(".answer_14").find("em").each(function(){
		if($(this).hasClass("current")){
		   answer_14 = $(this).attr("answer");
		}
	})
	if(answer_14==""){
    	OA.titlePup('请勾选被隔离情况！','lose');
		return;
    }
    var detail_14 = {
		type:2,
		question_num:14,
		question:"被隔离情况",
		answer_num:14,
		answer:answer_14
    };
    list.push(detail_14);
    var answer_15 = "";
    $(".answer_15").find("em").each(function(){
		if($(this).hasClass("current")){
		   answer_15 = $(this).attr("answer")+"；";
		}
	})
	if(answer_15==""){
    	OA.titlePup('请勾选你近期是否有密切接触以下人员：（含家属）！','lose');
		return;
    }
    var detail_15 = {
		type:2,
		question_num:15,
		question:"近期是否有密切接触以下人员：（含家属）",
		answer_num:15,
		answer:answer_15
    };
    list.push(detail_15);
    var answer_16 = "";
    $(".answer_16").find("em").each(function(){
		if($(this).hasClass("current")){
			answer_16 = $(this).attr("answer");
		}
	})
	if(answer_16==""){
    	OA.titlePup('请勾选家属健康情况！','lose');
		return;
    }
    var detail_16 = {
		type:2,
		question_num:16,
		question:"家属健康情况",
		answer_num:16,
		answer:answer_16
    };
    list.push(detail_16);
	OA.twoSurePop({
		tips:'确认提交吗？',
		sureFn:function(){
			OA.pageLoading(true);
			$.ajax({
				async:true,
				type:'post',
				timeout : 5000,
				dataType:'json',
				data:{
					detailList:JSON.stringify(list),
					token:$("#token").val()
				},
				url:contextPath + "/dayHeath/save.htm",
				success:function(data){
					if(data.success){
						window.location.href=contextPath + "/login/index.htm";
					}else{
						OA.pageLoading(false);
						OA.titlePup(data.message,'lose');
						$("#token").val(data.token);
					}
				},
				complete:function(XMLHttpRequest,status){
				   if(status=="timeout"){
					   window.location.href=contextPath + "/login/index.htm";
				   }
				}
			});
		},
		cancelFn:function(){
			
		}
	})
	
}