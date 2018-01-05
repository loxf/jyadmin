
var form = layui.form;
//监听提交
form.on('submit(searchForm)', function(data){
    table.reload('tableReload',
        { where: data.field });
    return false;
});
form.on('select(province)', function(data){
    $.getJSON(contextPath + "/admin/city.html?provinceid="+data.value, function(data){
        $("#city").html('');
        if(data.code===1) {
            var optionString = "<option value=\"\" >选择城市</option>";
            $.each(data.data, function (i, item) {
                optionString += "<option value=\"" + item.cityid + "\" >" + item.city + "</option>";
            });
            $("#city").html(optionString);
            form.render('select'); //这个很重要
        } else {
            layer.msg(data.msg);
        }
    });
});
form.render();

var laydate = layui.laydate;
//执行一个laydate实例
laydate.render({
    elem: '#createdAt', //指定元素
    range: '~',
    format: 'yyyy-MM-dd'
});
var table = layui.table;
table.render({ //其它参数在此省略
    id: 'tableReload',
    url: 'list.html',
    method: 'POST',
    elem: '#dataTable', //指定原始表格元素选择器（推荐id选择器）
    height: 435, //容器高度
    page: true,
    cols: [[
        {
            field: 'activeName',
            title: '活动名称',
            width: 120
        },
        {
            field: 'price',
            title: '价格',
            width: 90
        },
        {
            field: 'activePrivi',
            title: '权限&价格',
            width: 260,
            templet : '#activePriviTpl'
        },
        {
            field: 'maxBp',
            title: '可用积分',
            width: 100,
            templet : '#maxBpTpl'
        },
        {
            field: 'studentsNbr',
            title: '活动名单',
            width: 90,
            templet : '#studentsTpl'
        },
        {
            field: 'indexShow',
            title: '首页轮播',
            width: 100,
            templet : '#indexShowTpl'
        },
        {
            field: 'province',
            title: '省',
            width: 90
        },
        {
            field: 'city',
            title: '市',
            width: 90
        },
        {
            field: 'addr',
            title: '地址',
            width: 100
        },
        {
            field: 'activeStartTime',
            title: '活动时间',
            width: 260 ,
            templet : '#activeTimeTpl'
        },
        {
            field: 'status',
            title: '状态',
            width: 80,
            templet : '#statusTpl'
        },
        {
            field: 'pic',
            title: '活动图片',
            width: 90,
            templet : '#picTpl'
        },
        {
            field: 'indexPic',
            title: '首页图片',
            width: 100 ,
            templet : '#indexPicTpl'
        },
        {
            field: 'metaData',
            title: '限定人数',
            width: 90,
            templet : '#metaDataTpl'
        },
        {
            field: 'activeDesc',
            title: '活动描述',
            width: 180
        },
        {
            field: 'htmlId',
            title: '详情',
            width: 80,
            templet : '#htmlIdTpl'
        },
        {
            field: 'createdAt',
            title: '创建时间',
            width: 180
        },
        {
            field: 'activeId',
            title: '操作',
            width: 280,
            fixed: 'right',
            align: 'center',
            toolbar: '#barTable'
        }
    ]],
    request: {
        pageName: 'pager.page', //页码的参数名称，默认：page
        limitName: 'pager.size' //每页数据量的参数名，默认：limit
    },
    response: {
        statusName: 'code', //数据状态的字段名称，默认：code
        statusCode: 1, //成功的状态码，默认：0
        msgName: 'msg', //状态信息的字段名称，默认：msg
        countName: 'total', //数据总数的字段名称，默认：count
        dataName: 'data' //数据列表的字段名称，默认：data
    },
    limits: [30, 50, 100, 150, 300],
    limit: 30, //默认采用30
    even: true
});
//监听工具条
table.on('tool(userDataTable)', function (obj) { //注：tool是工具条事件名，test是table原始容器的属性 lay-filter="对应的值"
    var data = obj.data; //获得当前行数据
    var layEvent = obj.event; //获得 lay-event 对应的值

    if (layEvent === 'onActive') { //发布活动
        if(data.status==0) {
            onActive(data, layEvent, obj);
        } else {
            layer.msg("活动已经发布");
        }
    } else if (layEvent === 'offActive') {// 取消发布
        if(data.status==1) {
            offActive(data, layEvent, obj);
        } else {
            layer.msg("活动已经取消");
        }
    } else if (layEvent === 'editActive') {// 编辑活动
        editActive(data, layEvent, obj);
    } else if (layEvent === 'deleteActive') {// 删除活动
        deleteActive(data, layEvent, obj);
    } else if (layEvent === 'sendWeiXin') {// 微信推送
        sendWeiXin(data, layEvent, obj);
    }
});

function addActive() {
    var addLayer = layer.open({
        type: 2
        ,offset: '80px' //具体配置参考：http://www.layui.com/doc/modules/layer.html#offset
        ,id: 'addActive' //防止重复弹出
        ,area: ['1000px', '600px']
        ,content: 'toAddActive.html'
        ,shade: 0.3
    });
}

function indexRecommend(activeId, type) {
    $.ajax({
        type: "POST",
        url:"indexRecommend.html",
        data : {
            activeId : activeId,
            type : type
        },
        dataType:"json",
        success: function(data) {
            layer.msg(data.msg);
            if(data.code == 1){
                searchList();
            }
        }
    });
}

function editActive(data, layEvent, obj) {
    var editLayer = layer.open({
        type: 2
        //,offset: '80px' //具体配置参考：http://www.layui.com/doc/modules/layer.html#offset
        ,id: layEvent //防止重复弹出
        ,area: ['1000px', '600px']
        ,content: 'toEditActive.html?activeId=' + data.activeId
        ,maxmin:true
        ,shade: 0.3
    });
}

function deleteActive(data, layEvent, obj) {
    layer.confirm('确认删除活动？', {icon: 3, title:'警告'}, function(index){
        $.ajax({
            type: "POST",
            url:"deleteActive.html",
            data : {
                activeId : data.activeId
            },
            dataType:"json",
            success: function(data) {
                layer.msg(data.msg, {
                    time: 1500 //1.5秒关闭（如果不配置，默认是3秒）
                }, function(){
                    if(data.code == 1){
                        obj.del();
                    }
                    layer.close(index);
                });
            }
        });
    });
}

function onActive(data, layEvent, obj){
    $.ajax({
        type: "POST",
        url:"onOrOffActive.html",
        data : {
            activeId : data.activeId,
            status : 1
        },
        dataType:"json",
        success: function(data) {
            layer.msg(data.msg, {
                time: 1500 //1.5秒关闭（如果不配置，默认是3秒）
            }, function(){
                if(data.code == 1){
                    searchList();
                }
            });
        }
    });
}

function offActive(data, layEvent, obj){
    $.ajax({
        type: "POST",
        url:"onOrOffActive.html",
        data : {
            activeId : data.activeId,
            status : 0
        },
        dataType:"json",
        success: function(data) {
            layer.msg(data.msg, {
                time: 1500 //1.5秒关闭（如果不配置，默认是3秒）
            }, function(){
                if(data.code == 1){
                    searchList();
                }
            });
        }
    });
}

function openCustListOfActive(activeId) {
    var custListOfActive = layer.open({
        type: 2
        ,offset: '80px' //具体配置参考：http://www.layui.com/doc/modules/layer.html#offset
        ,id: 'custListOfActive' //防止重复弹出
        ,area: ['1000px', '600px']
        ,content: 'toStudentList.html?activeId=' + activeId
        ,shade: 0.3
    });
}

function searchList(){
    $("#searchList").click();
}

function sendWeiXin(data, layEvent, obj) {
    var addr = data.province + "-" + data.city;
    $.ajax({
        type: "POST",
        url:contextPath + "/admin/offer/sendWeiXin.html",
        dataType:"json",
        data : {
            offerId : data.activeId,
            type : "ACTIVE",
            offerName : data.activeName,
            addr : addr,
            teachers : "现场揭示"
        },
        success: function(data) {
            if(data.code==1){
                layer.msg("推送成功");
            } else {
                layer.msg(data.msg);
            }
        }
    });
}