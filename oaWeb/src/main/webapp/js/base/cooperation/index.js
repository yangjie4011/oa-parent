$(function() {
	var div = null;
});

//切换tab
function selectTab(title, index) {
	var url = "";
	if(index == 0) {
		div = $("#processStatus_processing");
		url = contextPath + "/ruProcdef/ruProcdef_processing.htm";
		RefreshTab();
	} else if(index == 1) {
		div = $("#processStatus_processed");
		url = contextPath + "/runTask/runTask_processed.htm";
		RefreshTab();
	} else {
		div = $("#processStatus_done");
		url = contextPath + "/runTask/runTask_done.htm";
		RefreshTab();
	}
	if(div.html() == "") {
		div.html("<iframe src=\""+ url +"\" frameBorder=\"0\" width=\"100%\" height=\"99%\" style=\"background-color:transparent;border:none;\" hideFocus=\"hidefocus\" allowTransparency=\"allowtransparency\" scrolling=\"no\"></iframe>");
	} else {
		div.find('iframe')[0].contentWindow.reload();
	}
}

//刷新当前标签Tabs
function RefreshTab() {
	var currTab =  self.parent.$('#tabs').tabs('getSelected'); //获得当前tab
    var url = $(currTab.panel('options').content).attr('src');
    self.parent.$('#tabs').tabs('update', {
    	tab : currTab,
    	options : {
    		content : div.html("<iframe src=\""+ url +"\" frameBorder=\"0\" width=\"100%\" height=\"99%\" style=\"background-color:transparent;border:none;\" hideFocus=\"hidefocus\" allowTransparency=\"allowtransparency\" scrolling=\"no\"></iframe>")
    	}
    });
}