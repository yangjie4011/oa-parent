$(function(){
	getPage();
	$("#all").click(function(){
		getPage();
	})
	$("#unRead").click(function(){
		getPage(1);
	})
})
function getPage(readFlag){
	//查询列表初始化
	$('#userMsgList').datagrid({
        url: contextPath + "/empMsg/getPageList.htm",
        queryParams : {readFlag:readFlag},
        methord: 'post',
        height:580,
        fitColumns: true,
        sortName: 'id',
        sortOrder: 'desc',
        idField: 'id',
        pageSize: 10,
        pageList: [5,10,20],
        pagination: true,
        striped: true, //奇偶行是否区分
        singleSelect: false,//单选模式
        rownumbers: false,//行号
        columns: [[
            {field:'id',width:0,hidden:true},
            {field:'readFlag',width:0,hidden:true},
            { field: 'type', title: '消息类型', width: '10%',
           	  formatter:function(value,row,index){
           		if('1'==value){
           			return '入职'
           		}else if('2' == value){
           			return '工作';
           		}
           	  }
            },
            { field: 'title', title: '消息标题', width: '20%' },
            { field: 'content', title: '消息内容', width: '70%'}
        ]], 
        onClickRow: function (index, row) {
        	if(row.readFlag==1){
        		$.ajax({
            		async:false,
            		type:'post',
            		dataType:'json',
            		data:{id:row.id},
            		url:contextPath + "/empMsg/read.htm",
            		success:function(data){
            			if(data.success){
            				if($("#all").hasClass(".active")){
            					getPage();
            				}else{
            					getPage(1);
            				}
            			}else{
            				$.messager.alert('提示信息', data.message, "info", function() {});
            			}
            		}
            	});
        	}
        }
    });
}