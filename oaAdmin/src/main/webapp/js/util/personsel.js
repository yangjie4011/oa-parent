/*$(function(){
	//初始化员工类型
	
});
*/
/**
 * 组织结构人员选择
 * by luffy
 */
//对象原型扩展
String.prototype.substitute = function (data) {
    if (data && typeof (data) == 'object') {
        return this.replace(/\{([^{}]+)\}/g, function (match, key) {
            var value = data[key];
            return (value !== undefined) ? '' + value : '';
        });
    } else {
        return this.toString();
    }
}
var personSelHtml = '<div class="hgc_personSelBg"><div class="personSelBox"><h2>选择人员<i class="close"></i></h2><div class="personSelContent"><div class="personSelCondition" id="personSelCondition"><form id="routinue"><div class="routinue clearfix"><div class="fieldset fl"><span class="field-name fl">员工编号</span><div class="field-value fl"><input type="text" name="code" class="field-input" placeholder="员工编号"></div></div><div class="fieldset fl"><span class="field-name fl">姓名</span><div class="field-value fl"><input type="text" name="cnName" class="field-input" placeholder="姓名"></div></div><div class="fieldset fl"><span class="field-name fl">部门</span><div class="field-value fl"><select name="firstDepart"></select></div></div><div class="fieldset fl"><span class="field-name fl">入职日期起</span><div class="field-value fl"><input type="text" name="firstEntryTimeBegin" class="field-input inputDate" placeholder="入职日期"></div></div><div class="fieldset fl"><span class="field-name fl">入职日期止</span><div class="field-value fl"><input type="text" name="firstEntryTimeEnd" class="field-input inputDate" placeholder="入职日期"></div></div>		<div class="fieldset fl"><span class="field-name fl">员工类型</span><div class="field-value fl"> <select name="empTypeId"></select></div></div>	<span class="moreBtn" style="bottom: -20px;" data-isOpen="0">更多查询条件</span></div></form><form id="special"><div class="special clearfix"></div></form><div class="searchBtns"><button class="search">查询</button><button class="reset">重置</button></div></div><div class="personInfo clearfix"><div class="organizationTree fl"><ul class="personList searchListBox"></ul></div><div class="seledPerson fr"><h3>已选：0人</h3><ul></ul></div></div></div><div class="button-wrap"><button id="hgc_passDetail" class="red-but"><span>确定</span></button><button id="hgc_refuseDetail" class="small grey-but"><span>取消</span></button></div></div></div>';

var PersonSel_luffy = {
    init: function (params) {
        $('body').append(personSelHtml);

        this.params = params;

        //组织架构图数据格式
        this.orgClassify = [];

        this.isOpenMoreCondition = false;
        //自定义更多查询条件
        this.getMoreCondition(this.params.conditions);

        this.getSearchCondition();

        this.organizationTreeRander();

        this.bindEvent();
    },
    apis: {
        //通过搜索条件获取员工信息
    	
        getWorkersBySearchApi: basePath+"employee/getListByPage.htm?page=1&rows=300",
        //获取组织架构树
        getWorkersByDefaultApi: basePath+"depart/getEmployeeTreeList.htm",
        //查询筛选条件里面的select 信息
        getSearchConditionApi: basePath+"sysConfig/getSelectData.htm",
        //departId 
        getSelectDataByDepartIdApi: basePath+"sysConfig/getSelectDataByDepartId.htm"

    },
    tpls: {
        orgTreeTpl: '<li class="{liClass}">{isArrow}<label class="{checkboxClass}"><input type="checkbox" class="checkItem checkItem-{id} {pIdClass}" {checked} data-id="{id}" data-pid="{pId}"></label><span>{name}{isCount}</span>',
        seledWorkerTpl: '<li><span>{name}{count}<i data-pId="{pId}" data-id="{id}" class="del"></i></span></li>',
        optionTpl: '<option value="{id}">{name}</option>',

    	//新增员工类型
        
		empTypeTpl: '<option value="{emptypeId}">{typeCName}</option>',

        
        fieldSetTpl: '<div class="fieldset fl">\
                <span class="field-name fl">{name}</span>\
                <div class="field-value fl">\
                    {content}\
                </div>\
            </div>',
        fieldInputHtml: '<input type="text" name="{field}" class="field-input {dateClass}" {readOnly}>',
        fieldSelectHtml: '<select name="{field}"></select>',
        //筛选条件数据映射
        //缺少 户籍和毕业院校 出生日期起止 是否排班
        searchConditionMap: {
            quitTimeBegin: { name: '离职日期起', tag: 'inputDate' },
            quitTimeEnd: { name: '离职日期止', tag: 'inputDate' },
            reportToLeader: { name: '汇报对象', tag: 'select' },
            departLeaderName: { name: '部门负责人', tag: 'select' },
            positionId: { name: '职位', tag: 'select' },
            sex: { name: '性别', tag: 'select' },
            degreeOfEducation: { name: '文化程度', tag: 'select' },
            politicalName: { name: '政治面貌', tag: 'select' },
            maritalStatusName: { name: '婚姻状况', tag: 'select' },
            protocolEndTimeBegin: { name: '合同到期起', tag: 'inputDate' },
            protocolEndTimeEnd: { name: '合同到期止', tag: 'inputDate' },
            workTypeName: { name: '工时种类', tag: 'select' },
            whetherSchedulingName: { name: '是否排班', tag: 'select' },
            householdRegister: { name: '户籍', tag: 'input' },
            birthdayBegin: { name: '出生日期起', tag: 'inputDate' },
            birthdayEnd: { name: '出生日期止', tag: 'inputDate' },
            school: { name: '毕业院校', tag: 'input' },

        }
    },
    //自定义分配 更多查询条件
    //传入参数 数组{condition:[放入你的条件字段名]}
    conditions: ['quitTimeBegin', 'quitTimeEnd', 'reportToLeader', 'departLeaderName', 'positionId', 'sex', 'degreeOfEducation', 'politicalName', 'maritalStatusName', 'protocolEndTimeBegin', 'protocolEndTimeEnd', 'workTypeName', 'whetherSchedulingName', 'householdRegister', 'school', 'birthdayBegin', 'birthdayEnd'],
    getMoreCondition: function (conditions) {
        var self = this;
        var conditionMap = self.tpls.searchConditionMap;

        var conditions = conditions || self.conditions;
        var conditionHtml = '';

        conditions.forEach(function (item) {
            var content = '';
            var tag = conditionMap[item].tag;
            if (tag == 'input') {
                content = self.tpls.fieldInputHtml.substitute({ field: item });
            } else if (tag == 'inputDate') {
                content = self.tpls.fieldInputHtml.substitute({ field: item, dateClass: 'inputDate', readOnly: 'readOnly' })
            } else {
                content = self.tpls.fieldSelectHtml.substitute({ field: item });
            }

            conditionHtml += self.tpls.fieldSetTpl.substitute({ name: conditionMap[item].name, content: content })
        })

        $('#special .special').html(conditionHtml);


        $('.field-input.inputDate').click(function () {
            WdatePicker({ dateFmt: 'yyyy-MM-dd' });
        })
        $('.field-input.inputDate').focus(function () {
            WdatePicker({ dateFmt: 'yyyy-MM-dd' });
        })

    },
    //url,data,cb
    getJSON: function (url, data, cb) {
        var self = this;

        var url = arguments[0], cb = arguments[arguments.length - 1]
        var data = arguments.length > 2 ? arguments[1] : {};

        $.ajax({
            url: url,
            dataType: "jsonp",
            data: data,
            jsonp: "jsonpCallback",
            success: function (data) {
                cb(data);
            }
        });
    },
    //员工信息筛选条件获取
    getSearchCondition: function () {
        var self = this;
        /**
         * mLeaderList-汇报对象，positionList-职位
         * empType-员工类型，departList-部门，departHeaderList-部门负责人
         * politicalStatus-政治面貌，educationLevel-文化程度
         * typeOfWork-工时类型，maritalStatus-婚姻状况
         */
        self.getJSON(self.apis.getSearchConditionApi, function (res) {
            //遍历select下拉框数据 
            res.sex = [{ name: '男', id: '0' }, { name: '女', id: '1' }];
            for (var item in res) {
                var optionArr = res[item];

                var optionHtml = '<option value="">--请选择--</option>';
                optionArr.forEach(function (item) {
                    optionHtml += self.tpls.optionTpl.substitute(item);
                })
                if (item == 'departList') {
                    item = 'firstDepart';
                }
                $('#personSelCondition select[name="' + item + '"]').html(optionHtml);
                
               
				if (item == 'empTypeId') {
					 var empTypeTpl = '<option value="">--请选择--</option>';
					optionArr.forEach(function (item) {
						empTypeTpl += self.tpls.empTypeTpl.substitute(item);
					})
					$('#personSelCondition select[name="empTypeId"]').html(empTypeTpl);
                }
				
                
            }
        })

    },
    //组织树渲染
    organizationTreeRander: function () {
        var self = this;

        self.getJSON(self.apis.getWorkersByDefaultApi, function (data) {
            //循环渲染树状图
            var orgHtml = '';
            //递归处理数据
            function formatData(parentId) {
                var itemData = [];
                orgHtml += '<ul class="list">';
                data.forEach(function (item, index) {
                    if (item.pId == parentId) {
                        //区分部门title 和 单个员工title（无count属性） 如果部门下没有员工 则不显示该部门
                        if (item.count && !+item.count) return;

                        var singleTreeData = {
                            liClass: item.count ? 'group' : 'staff',
                            isArrow: item.count ? '<i></i>' : '',
                            checkboxClass: item.count ? 'selGroup' : 'selSingle',
                            isCount: item.count ? '(' + item.count + ')' : '',
                            name: item.name,
                            id: item.id,
                            pId: item.pId
                        }
                        orgHtml += self.tpls.orgTreeTpl.substitute(singleTreeData);

                        item.children = +item.count ? formatData(item.id) : [];
                        itemData.push(item);
                        orgHtml += '</li>';
                    }
                })
                orgHtml += '</ul>';
                return itemData;
            }

            self.orgClassify = formatData('0');

            self.copyOrgClassify = $.extend(true, [], self.orgClassify);

            $('.organizationTree').append(orgHtml).children('ul').eq(1).removeClass('list').addClass('personList defaultList');
        })

    },
    //根据条件查询部门员工
    getWorkersBySearch: function (params) {
        var self = this;
        self.getJSON(self.apis.getWorkersBySearchApi, params, function (data) {
            var searchData = data.rows;
            var searchHtml = '';
            searchData.forEach(function (item) {
                item.pId = item.depart.id;
                item.name = item.cnName;
                item.pIdClass = 'checkItem_' + item.pId;

                item.checked = $('.defaultList .checkItem-' + item.id).prop('checked') ? 'checked' : '';

                searchHtml += self.tpls.orgTreeTpl.substitute(item);
            })

            $('.searchListBox').html(searchHtml).show().siblings().hide();
        })

    },
    //渲染右侧已经选中的人员
    seledWorkerRander: function (workers) {

        var self = this;
        var departData = $.extend(true, {}, workers);

        var seledCount = 0;

        var seledHtml = '';
        for (var departId in departData) {
            var depart = departData[departId];
            seledCount += depart.count;
            if (depart.departName) {
                seledHtml += self.tpls.seledWorkerTpl.substitute({
                    id: departId,
                    name: depart.departName,
                    count: '(' + depart.count + ')'
                })
            } else {
                if (depart.count) {
                    depart.children.forEach(function (item) {
                        item.pId = departId;
                        seledHtml += self.tpls.seledWorkerTpl.substitute(item);
                    })
                }
            }

        }
        $('.seledPerson h3').html('已选：' + seledCount + '人');
        $('.seledPerson ul').html(seledHtml);
    },
    formToJson: function (data) {
        data = decodeURIComponent(data);
        data = data.replace(/&/g, '\",\"');
        data = data.replace(/=/g, '\":\"');
        data = '{\"' + data + '\"}';
        data = JSON.parse(data);
        return data;
    },
    //组织树事件绑定
    bindEvent: function () {

        var self = this;
        //更多查询条件中 汇报对象和职位 是跟随部门联动的
        $('select[name="departList"]').change(function () {
            var departId = $(this).find("option:selected").attr('value');
            self.getJSON(self.apis.getSelectDataByDepartIdApi, { departId: departId }, function (res) {
                for (var item in res) {
                    var optionArr = res[item];
                    var optionHtml = '<option value="">--请选择--</option>';
                    optionArr.forEach(function (item) {
                        optionHtml += self.tpls.optionTpl.substitute(item);
                    })
                    //汇报对象
                    $('#personSelCondition select[name="' + item + '"]').html(optionHtml);

                }
            })
        })
        //点击弹出更多查询条件
        $('.moreBtn').click(function () {
            //isOpen 0 弹出更多查询条件 1 关闭更多查询条件
            var isOpen = +$(this).attr('data-isOpen');
            var nextStatus = isOpen ? '0' : '1';
            $('#special .special')[isOpen ? 'hide' : 'show']();
            $(this).attr('data-isOpen', nextStatus);

            self.isOpenMoreCondition = !isOpen;
        })
        //点击根据条件的查询按钮
        $('.searchBtns button').click(function (event) {
            event.preventDefault();
            //查询和表单内容重置
            if ($(this).hasClass('search')) {
                var params = $.extend(self.formToJson($('#routinue').serialize()), self.isOpenMoreCondition ? self.formToJson($('#special').serialize()) : {});
                self.getWorkersBySearch(params);
            } else {
                $('.searchListBox').hide().siblings().show();
                $(":input","#personSelCondition").val('');
            }
        })

        //通过搜索出来的员工列表
        $('.searchListBox').on('change', '.checkItem', function () {
            var id = $(this).attr('data-id');
            var isSeled = $(this).prop('checked');
            $('.defaultList .checkItem-' + id).click();
        })

        var orgTreeEl = $('.organizationTree');
        //点击展开 或 关闭部门
        orgTreeEl.on('click', 'i', function (event) {
            event.stopPropagation();
            var isOpen = $(this).hasClass('open');
            $(this)[isOpen ? 'removeClass' : 'addClass']('open').siblings('.list')[isOpen ? 'hide' : 'show']();
        })
        //员工选择
        //全局已经选择的部门内部的员工信息 ==> 用来判断是否该部门员工已经被全选
        var seledWokerData = {};
        /**
         * 所需数据结构
         * [pid]{
         *      count:Number,
         *      children:[length],
         *      departName:String
         * }
         */
        //1选择部门
        orgTreeEl.on('change', '.selGroup .checkItem', function () {
            var isSeled = $(this).prop('checked');
            var id = $(this).attr('data-id');
            var false_this = $(this).parent();
            var childrenCheckEl = false_this.siblings('.list').find('.checkItem');

            $('.checkItem_' + id).prop('checked', isSeled);
            //员工全选时 设置总数量
            self.copyOrgClassify.forEach(function (item) {
                if (item.id == id) {
                    seledWokerData[id] = (function () {
                        return {
                            children: isSeled ? item.children : [],
                            count: isSeled ? +item.count : 0,
                            departName: isSeled ? item.name : ''
                        }
                    })()
                }
            })
            //设置部门选中状态
            childrenCheckEl.prop('checked', isSeled);
            self.seledWorkerRander(seledWokerData);
        })

        //1选择员工
        orgTreeEl.on('change', '.selSingle .checkItem', function () {
            var isSeled = $(this).prop('checked');
            var pId = $(this).attr('data-pId');
            var id = $(this).attr('data-id');
            var departCheckEl = $(this).closest('ul').siblings('label').children('.checkItem');
            //计算部门员工总数
            var departWorkerCount = 0;
            //部门名称
            var departName = '';
            //是否已经全选
            var isAllSeled = false;
            //员工全选时 设置总数量\]
            for (var i = 0, ilen = self.orgClassify.length; i < ilen; i++) {
                var item = self.orgClassify[i];
                if (item.id == pId) {
                    //计算部门员工总数
                    departWorkerCount = item.count;
                    departName = item.name;
                    //添加或者删除已经选择的单个员工数据
                    for (var j = 0, jlen = item.children.length; j < jlen; j++) {
                        var child = item.children[j];
                        if (child.id == id) {
                            //重新组装已选的员工信息
                            if (isSeled) {
                                if (!seledWokerData[pId]) {
                                    seledWokerData[pId] = {
                                        children: [],
                                        count: 0
                                    }
                                }
                                seledWokerData[pId].children.push(child);
                                seledWokerData[pId].count++;
                            } else {
                                for (var m = 0, mlen = seledWokerData[pId].children.length; m < mlen; m++) {
                                    var selItem = seledWokerData[pId].children[m];
                                    if (selItem.id == id) {
                                        seledWokerData[pId].children.splice(m, 1);
                                        break;
                                    }
                                }
                                seledWokerData[pId].count--;
                            }
                            break;
                        }
                    }
                    break;
                }
            }

            //是否已经全选
            isAllSeled = seledWokerData[pId].count >= departWorkerCount;
            //全选的时候右侧默认只显示 部门的名称 非全选的时候右侧默认显示该部门已经选择的员工信息
            seledWokerData[pId].departName = isAllSeled ? departName : '';
            //设置搜索区域的员工选择
            $('.searchListBox .checkItem-' + id).prop('checked', isSeled);
            //设置部门选中状态
            departCheckEl.prop('checked', isAllSeled);
            self.seledWorkerRander(seledWokerData);

        })

        //删除已选 部门或者员工
        $('.seledPerson').on('click', '.del', function () {
            var id = $(this).attr('data-id');
            $('.defaultList .checkItem-' + id).trigger('click');
        })

        /**
         * 出身日期：待定，户籍：待定，婚姻状况：maritalStatusName，合同到期日：protocolEndTimeBegin，protocolEndTimeEnd工时种类：workTypeName是否排班：whetherSchedulingName
         * 
         * 
         * 员工编号：code，姓名：cnName，部门：departList（id集合），入职开始：firstEntryTimeBegin，入职结束：firstEntryTimeEnd
         * 离职开始：quitTimeBegin，离职结束：quitTimeEnd，汇报对象：reportToLeader，
            部门负责人：departLeaderName，职位：positionId，性别：sex，文化程度：degreeOfEducation，毕业院校：待定，政治面貌：politicalName
         */
        var personSelBoxEl = $('.hgc_personSelBg');
        $('#hgc_passDetail').click(function () {
            self.params.cb(seledWokerData);
            personSelBoxEl.remove();
        })
        $('#hgc_refuseDetail').click(function () {
            personSelBoxEl.remove();
        })
        //关闭按钮
        $('.personSelBox').on('click', 'h2 .close', function () {
            personSelBoxEl.remove();
        })
    }

}

