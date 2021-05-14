$(function () {
    //设置右侧框架间距
    setMainPadding();
    $("#rowMenuDiv").children("a:first-child").click();
})
var h3 = "";
var h3end = "";
var quitval = "";
var entryval = "";
function ontoggerclinck(obj) {
    h3 = "<h3 class='h3flag' style=' border-left-color: #2f7ee0; width:143px; height:30px; font-size:12px;text-indent: 20px; line-height: 30px;'>";
    h3end = "</h3>";

    if (obj == null) {
    	
    } else {
        var objf = obj.id;
        var classinfo = $("#" + objf + "").parent();
        classinfo.html(h3 + classinfo.html() + h3end);
        setSelecteds();
        classinfo.val(h3 + classinfo + h3end);
    }
    
}
//设置右侧框架间距
function setMainPadding() {
	$('#mainDiv').height($(window).height()-100);
}

//退出登录
var logout = function () {
    window.location.href = basePath + "login/logout.htm";
}

function setSelecteds() {
    $(".current").attr("class", "");
}

function setSelected(id) {
    $(".current").attr("class", "");
    $("#" + id).attr("class", "current");
}

//提示框关闭
var closeDiv = function (divName) {
    $("#" + divName).hide();
}

/**异步加载点击的菜单的子菜单**/
var rowMenuClick = function (id) {
    //设置选中的一级菜单背景颜色
    $("a[name='myResource']").each(function () {
        $(this).css("background-color", "");
    });
    $("#" + id).css("background-color", "#dd0000");

    var defaultUrl = "";

    $("#lefeMenuDiv").empty();
    $.ajax({
        async: true,
        type: 'post',
        dataType: 'json',
        data: { id: id },
        url: basePath + "/login/getLeftMenu.htm",
        success: function (response) {
            var html = "";
            if (typeof (response) != "undefined" && response != null) {
                $.each(response, function (i, item) {
                    var thirdMeun = item.url;
                    if (i == 0) {
                        if (Object.prototype.toString.call(thirdMeun.charAt(1)) === "[object String]" && thirdMeun.charAt(0) == "#") {
                            /**注意：这里如果有子菜单，需在后台封装好数据结构后，在这里迭代添加子菜单**/
                            $.ajax({
                                async: false,
                                url: basePath + "/login/getThirdLeftMenu.htm",
                                type: 'post',
                                dataType: 'json',
                                data: { id: item.id },
                                success: function (j) {
                                    html += "<div class='secondMenu' id=" + item.id + "><h3><a  href='javascript:void(0)' name=" + basePath + item.url + "  target='mainFrame' class='submenu' title=''>" + item.resourceName + "</a></h3>"
                                        + "<ul id='ulList' class='thirdList'>"
                                    $.each(j, function (z, itemThird) {
                                        if (z == 0) {
                                            defaultUrl = basePath + itemThird.url;
                                        }
                                        //循环三级菜单  要拼 html+= 
                                        html += "<li class='submenuToggle'><a href='" + basePath + itemThird.url + "' id='" + itemThird.id + "'  class='submenu' target='mainFrame' title=''>" + itemThird.resourceName + "</a></li>"
                                    });
                                    html += "</ul>"
                                    html += "</div>";
                                }
                            });
                        } else {
                            html += "<div class='secondMenu' id=" + item.id + " ><h3><a href='" + basePath + item.url + "' target='mainFrame' class='submenu'>"
                                + item.resourceName + "</a></h3></div>";
                            defaultUrl = basePath + item.url;
                        }
                        //如果url 截取有两个字符以上  并且含有 # 标识的 二级菜单扩展 则循环三级子菜单
                    } else if (Object.prototype.toString.call(thirdMeun.charAt(1)) === "[object String]" && thirdMeun.charAt(0) == "#") {
                        /**注意：这里如果有子菜单，需在后台封装好数据结构后，在这里迭代添加子菜单**/
                        $.ajax({
                            async: false,
                            url: basePath + "/login/getThirdLeftMenu.htm",
                            type: 'post',
                            dataType: 'json',
                            data: { id: item.id },
                            success: function (j) {
                                html += "<div class='secondMenu' id=" + item.id + "><h3><a href='javascript:void(0)' name=" + basePath + item.url + " target='mainFrame' title=''>" + item.resourceName + "</a></h3>"
                                    + "<ul id='ulList' class='thirdList'>"
                                $.each(j, function (z, itemThird) {
                                    //循环三级菜单  要拼 html+= 
                                    html += "<li class='submenuToggle'><a href='" + basePath + itemThird.url + "' id='" + itemThird.id + "' class='submenu' target='mainFrame' title=''>" + itemThird.resourceName + "</a></li>"
                                });
                                html += "</ul>"
                                html += "</div>";
                            }
                        });

                    } else {
                        html += "<div class='secondMenu' id=" + item.id + "><h3><a href='" + basePath + item.url + "' target='mainFrame' class='submenu'>"
                            + item.resourceName + "</a></h3></div>";
                    }
                });
            }
            $("#lefeMenuDiv").append(html);
            $("#mainFrame").attr("src", defaultUrl);
        }
    });


    //左侧菜单事件绑定
    $('body').on('click','.thirdList li',function(event){
        $(this).addClass('selected').siblings('li').removeClass('selected');
    });
    $('#lefeMenuDiv').on('click','.secondMenu',function(event){
        var curThirdMenu = $(this).children('.thirdList');
        $(this).addClass('selected').siblings('.secondMenu').removeClass('selected');
        if(curThirdMenu && curThirdMenu.children().length){
            var curMenuItem = curThirdMenu.children('li').eq(0);
            var curIframeUrl = curThirdMenu.children('li').eq(0).children('a').attr('href');
            curMenuItem.addClass('selected').siblings('li').removeClass('selected');
            $('#mainFrame').attr('src',curIframeUrl);
        }
    })

}