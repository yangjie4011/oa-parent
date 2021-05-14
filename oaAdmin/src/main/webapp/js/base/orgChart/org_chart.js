$(function(){
	var datascource; //定义全局变量
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data:'',
		url:contextPath + "/depart/getDepartList.htm?departId=1",
		success:function(data){
//			console.log(JSON.stringify(data[0]));
			datascource = {'name': data[0].name,
      				'title': data[0].positionName + ' ' + data[0].leaderName,
      				'children':data[0].children
			};
			
			/*datascource = {
			      'name': 'Lao Lao',
			      'title': 'general manager',
			      'children': [
			        { 'name': 'Bo Miao', 'title': 'department manager' },
			        { 'name': 'Su Miao', 'title': 'department manager',
			          'children': [
			            { 'name': 'Tie Hua', 'title': 'senior engineer' },
			            { 'name': 'Hei Hei', 'title': 'senior engineer',
			              'children': [
			                { 'name': 'Pang Pang', 'title': 'engineer' },
			                { 'name': 'Xiang Xiang', 'title': 'UE engineer' }
			              ]
			            }
			          ]
			        },
			        { 'name': 'Yu Jie', 'title': 'department manager' },
			        { 'name': 'Yu Li', 'title': 'department manager' },
			        { 'name': 'Hong Miao', 'title': 'department manager' },
			        { 'name': 'Yu Wei', 'title': 'department manager' },
			        { 'name': 'Chun Miao', 'title': 'department manager' },
			        { 'name': 'Yu Tie', 'title': 'department manager' }
			      ]
			    };*/
		}
	});

    $('#chart-container').orgchart({
      'data' : datascource,
      'depth': 4,
      'nodeContent': 'title',
      'exportButton': true,
      'exportFilename': 'MyOrgChart'
    });
});







