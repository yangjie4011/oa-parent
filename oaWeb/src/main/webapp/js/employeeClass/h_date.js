/* 
* @Author: heguocheng
* @Date:   2017-06-02 09:19:37
* @Last Modified by:   Administrator
* @Last Modified time: 2017-08-03 11:39:41
*/
(function(root,$){

	root.h_dateBag = {
		
		isLeap:function(year){
			return year%400 == 0 ? (year%100 != 0 ? 1: (year%400 == 0 ? 1:0)):0;
		},
		clearDate:function(id){

			document.getElementById(id).innerHTML = '';

		},
		//判断月份下一月显示
		curMonth:0,
		datePage:function(data){
			if(data.paramsDate){
				var h_today = new Date(data.paramsDate+'/01');
			}else{
				h_today = new Date();
			}
			var self = this,
				//当前月份

				//获取年份用来判断是否是闰年
				h_year = [],
				//月份数组
				h_month = [],
				//天 数组
				h_day = [],
				//获取当前月份的第一天
				h_firstday = [],
				//周 几
				h_weekDay = [],
				//创建日历结构html
				date_html = '',
				//建立月份数组
				h_monthArr = [],
				//确定所需当前月份日历所需行数
				h_rowsNum = [],

				//循环索引
				i,k,
				_index,//每个单元格的索引值
				dateTime,//每个单元格对应的几号 的判断条件
				date,//实际显示的几号
				//日期 月份小于10 补0 参数
				monthRepair,
				dayRepair,
				//包含日期大盒子
				dateContainer;


			self.clearDate(data.id);
			//获取今天的相关数据
			h_year.push(h_today.getFullYear());

			h_month.push(h_today.getMonth());

			h_month.push(h_today.getMonth());

			h_day.push(h_today.getDate());


			//确定这个月的一号位置
			h_firstday.push(new Date(h_year[0], h_month[0], 1));
			//确定一号是周几
			h_weekDay.push(h_firstday[0].getDay());
			//12月份的天数数组
			h_monthArr.push([31, 28 + self.isLeap(h_year[0]), 31, 30, 31, 30, 31, 31, 30, 31, 30, 31]);
			//确定本月有几行
			/**
			 * h_weekDay[0] 第一天周几
			 * h_month[0] 几月
			 * h_monthArr[0][h_month[0]]) 本月有几天
			 * h_weekDay[0]+1+h_monthArr[0][h_month[0]] 本月至少需要多少个格子来装日期
			 */
			h_rowsNum.push(Math.ceil((h_weekDay[0]+1+h_monthArr[0][h_month[0]])/7));

			date_html+= '<table><tr><th>周日</th><th>周一</th><th>周二</th><th>周三</th><th>周四</th><th>周五</th><th>周六</th></tr>';
			
			//利用行，列二维数组循环
			for(i=0;i<h_rowsNum[0];i++){ //行

				date_html+='<tr>';

				for(k=0;k<7;k++){

					//为每行每个元素添加索引值
					_index = 7*i + k;

					//今天是几号
					/**
					 * 当前行索引 - 当月的第一天是周几
					 */
					dateTime = _index - h_weekDay[0] + 1;

					//如果当前数值小于等于0 或者大于该月份日期最大值 都用空格代替
					date = (dateTime<=0 || dateTime>h_monthArr[0][h_month[0]]) ? '' : dateTime;
					//月份 日子 小于10 补0
					monthRepair = (h_month[0]+1)<10?'0'+(h_month[0]+1):(h_month[0]+1);
					dayRepair = date<10?'0'+date:date;

					var bindDate = h_year[0]+'-'+monthRepair+'-'+dayRepair;
					//判断放假的条件总结
					var judgeHolidaysCondition = _index%7 == 0 || _index == 6 || _index == 13 || _index == 20 || _index == 27 || _index == 34;

					//具体今天是什么节日
					var todayHoliday = self.judgeToday(bindDate,self.holidays).holiday_name;
					var workType = self.judgeToday(bindDate,self.holidays).workType;
					if(self.curMonth === 0){ //如果是本月
						if(date < h_day[0]){
							//date '' 或 '2'
							if( (judgeHolidaysCondition && self.judgeToday(bindDate,self.holidays)) || (!judgeHolidaysCondition && self.judgeToday(bindDate,self.holidays))){
								if(date){
									if(workType==3){
										date_html+= '<td class="default" legal="1" date_day="'+bindDate+'"><span>'+(todayHoliday?'<em>'+todayHoliday+'</em>':date)+'</span><em></em></td>';
									}else{
										date_html+= '<td class="default" date_day="'+bindDate+'"><span>'+(todayHoliday?'<em>'+todayHoliday+'</em>':date)+'</span><em></em></td>';
									}
								}else{
									date_html+= '<td date_day="'+bindDate+'"><span>'+(todayHoliday?'<em>'+todayHoliday+'</em>':date)+'</span><em></em></td>';
								}
								
							}else if(judgeHolidaysCondition && !self.judgeToday(bindDate,self.holidays)){
								if(date){
									date_html+= '<td class="default week_end" date_day="'+bindDate+'"><span>'+date+'</span></td>';
								}else{
									date_html+= '<td class="" date_day="'+bindDate+'"><span>'+date+'</span></td>';
								}
								
							}else{
								if(date){
									date_html+= '<td class="default" date_day="'+bindDate+'"><span>'+date+'</span></td>';
								}else{
									date_html+= '<td class="" date_day="'+bindDate+'"><span>'+date+'</span></td>';
								}
							}
							
						}else if(new Date().getFullYear() == new Date(bindDate).getFullYear() && new Date().getMonth() == new Date(bindDate).getMonth() && new Date().getDate() == new Date(bindDate).getDate()){
							//判断周末
							if( (judgeHolidaysCondition && self.judgeToday(bindDate,self.holidays)) || (!judgeHolidaysCondition && self.judgeToday(bindDate,self.holidays))){
								if(workType==3){
									date_html+='<td class="default" legal="1" date_day="'+bindDate+'"><span>'+date+'</span></td>';
								}else{
									date_html+='<td class="default" date_day="'+bindDate+'"><span>'+date+'</span></td>';
								}
							}else if(judgeHolidaysCondition && !self.judgeToday(bindDate,self.holidays)){
								date_html+='<td class="default week_end" date_day="'+bindDate+'"><span>'+date+'</span></td>';
							}else{
								date_html+='<td class="default" date_day="'+bindDate+'"><span>'+date+'</span></td>';
							}
						}else{
							
							if( (judgeHolidaysCondition && self.judgeToday(bindDate,self.holidays)) || (!judgeHolidaysCondition && self.judgeToday(bindDate,self.holidays))){
								if(workType==3){
									date_html+='<td class="default" legal="1" date_day="'+bindDate+'"><span>'+(todayHoliday?'<em>'+todayHoliday+'</em>':date)+'</span></td>';
								}else{
									date_html+='<td class="default" date_day="'+bindDate+'"><span>'+(todayHoliday?'<em>'+todayHoliday+'</em>':date)+'</span></td>';
								}
							}else if( judgeHolidaysCondition && !self.judgeToday(bindDate,self.holidays)){
								
								date_html+='<td class="default week_end" date_day="'+bindDate+'"><span>'+date+'</span></td>';
							}else{
								date_html+='<td class="default" date_day="'+bindDate+'"><span>'+date+'</span></td>';
							}
							
						}
					}else if(self.curMonth < 0){ //前几个月

						if( (judgeHolidaysCondition && self.judgeToday(bindDate,self.holidays)) || (!judgeHolidaysCondition && self.judgeToday(bindDate,self.holidays))){
							if(workType==3){
								date_html+= '<td class="default" legal="1" date_day="'+bindDate+'"><span>'+date+'</span><em>'+(todayHoliday?'<em>'+todayHoliday+'</em>':date)+'</em></td>';
							}else{
								date_html+= '<td class="default" date_day="'+bindDate+'"><span>'+date+'</span><em>'+(todayHoliday?'<em>'+todayHoliday+'</em>':date)+'</em></td>';
							}
						}else if( judgeHolidaysCondition && !self.judgeToday(bindDate,self.holidays)){
							date_html+= '<td class="default week_end" date_day="'+bindDate+'"><span>'+date+'</span></td>';
						}else{
							date_html+= '<td class="default week_end" date_day="'+bindDate+'"><span>'+date+'</span></td>';
						}
						
					}else if(self.curMonth > 0){ //未来几个月
						if( (judgeHolidaysCondition && self.judgeToday(bindDate,self.holidays)) || (!judgeHolidaysCondition && self.judgeToday(bindDate,self.holidays))){
							if(workType==3){
								date_html+='<td class="default" legal="1" date_day="'+bindDate+'"><span>'+date+'</span><em>'+(todayHoliday?'<em>'+todayHoliday+'</em>':date)+'</em></td>';
							}else{
								date_html+='<td class="default" date_day="'+bindDate+'"><span>'+date+'</span><em>'+(todayHoliday?'<em>'+todayHoliday+'</em>':date)+'</em></td>';
							}
						}else if( judgeHolidaysCondition && !self.judgeToday(bindDate,self.holidays)){
							date_html+='<td class="default week_end" date_day="'+bindDate+'"><span>'+date+'</span></td>';
						}else{
							date_html+='<td class="default week_end" date_day="'+bindDate+'"><span>'+date+'</span></td>';
						}
						
					}
				}

				date_html+='</tr>';
			}
			date_html+='</table>';

			dateContainer = document.createElement('div');

			dateContainer.className = 'dateContainer';

			dateContainer.innerHTML = date_html;

			document.getElementById(data.id).appendChild(dateContainer);
			if(data.list){//遍历日历增加数据
				for(var a = 0; a < data.list.length; a++){
					$('.default').each(function(i){
						if($(this).attr("date_day")==data.list[a].classDate){
							$('.default:eq('+i+')').attr('id',data.list[a].classSettingId);
							$('.default:eq('+i+')').attr('name',data.list[a].name);
							$('.default:eq('+i+')').attr('oldId',data.list[a].oldClassSettingId);
							$('.default:eq('+i+')').attr('oldNname',data.list[a].oldName);
							$('.default:eq('+i+')').attr('oldStartTime',data.list[a].oldStartTime);
							$('.default:eq('+i+')').attr('oldEndTime',data.list[a].oldEndTime);
							$('.default:eq('+i+')').attr('mustAttnTime',data.list[a].mustAttnTime);
							$('.default:eq('+i+')').attr('startTime',data.list[a].startTime);
							$('.default:eq('+i+')').attr('endTime',data.list[a].endTime);
							if(data.list[a].name){
								if(data.list[a].classSettingId==0){
									$('.default:eq('+i+')').find('span').append('<p></p>')
								}else{
									$('.default:eq('+i+')').find('span').append('<p>'+ data.list[a].name +'</p>')
								}
							}else{
								$('.default:eq('+i+')').find('span').append('<p></p>')
							}
							if(data.list[a].classSettingId){//标记蓝色
								if(typeof($('.default:eq('+i+')').attr("legal"))!="undefined"&&$('.default:eq('+i+')').attr("legal")==1){
									$('.default:eq('+i+')').addClass('arg');
									if(data.list[a].isMove==1){
										$('.default:eq('+i+')').addClass('adjustT');
										$('.default:eq('+i+')').addClass('current');
										$('.default:eq('+i+')').find('span').find('p').text(data.list[a].name);
									}else{
										$('.default:eq('+i+')').addClass('current');
									}
								}else{
									if(data.list[a].isMove==1){
										$('.default:eq('+i+')').addClass('adjustT');
										$('.default:eq('+i+')').addClass('current');
										$('.default:eq('+i+')').find('span').find('p').text(data.list[a].name);
									}else{
										$('.default:eq('+i+')').addClass('current');
									}
								}
							}else{
								if(data.list[a].isMove==1){
									$('.default:eq('+i+')').addClass('adjustT');
									$('.default:eq('+i+')').addClass('current');
									$('.default:eq('+i+')').find('span').find('p').text(data.list[a].name);
								}
							}
						}
					})
				}
			}
			
			//绑定日历事件
			//self.bindEvent(data.id);
			//插件外自己绑定事件
			if(data.callback)data.callback();
		},
		//判断今天是否为节假日
		judgeToday:function(today,holidays){
			for(var i=0;i<holidays.length;i++){
				if(holidays[i].day === today && holidays[i].type == '0'){
					if(holidays[i].subject){
						return {
							holiday_name:holidays[i].subject,
							workType:holidays[i].workType
						}
					}else{
						return true;
					}
					
				}
			}

			return false;
		},
		holidays:null,
		//初始化地图节假日
		initDatePage:function(data){
			var self = this;
			$.getJSON(data.url, function(res){
				self.holidays = res;
				self.datePage(data);
			})

		},
		bindEvent:function(id){
			var self = this;
			var nextBtn = document.getElementById('nextMonth');
			var preBtn = document.getElementById('preMonth');
			var todBtn = document.getElementById('todayBtn');

			nextBtn.onclick = function(){
				self.curMonth++;
				self.datePage({
					id:id
				});
			}

			preBtn.onclick = function(){
				self.curMonth--;
				self.datePage({
					id:id
				});
			}

			todBtn.onclick = function(){
				self.curMonth = 0;
				self.datePage({
					id:id
				});
			}

		}
	}
})(window,$)