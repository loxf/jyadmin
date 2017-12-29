
var form = layui.form;
//监听提交
form.on('submit(searchForm)', function(data){
    table.reload('tableReload',
        { where: data.field });
    return false;
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
    height: 'full-255', //容器高度
    page: true,
    cols: [[
        {
            field: 'offerName',
            title: '商品名称',
            fixed: true,
            width: 120
        },
        {
            field: 'catalogName',
            title: '分类',
            width: 100
        },
        {
            field: 'offerType',
            title: '商品类型',
            width: 100,
            templet : '#offerTypeTpl'
        },
        {
            field: 'saleMoney',
            title: '售价',
            width: 80
        },
        {
            field: 'buyPrivi',
            title: '权限&价格',
            width: 240,
            templet : '#buyPriviTpl'
        },
        {
            field: 'maxBp',
            title: '可用积分',
            width: 100,
            templet : '#maxBpTpl'
        },
        {
            field: 'commission',
            title: '特殊分成',
            width: 120,
            templet : '#commissionTpl'
        },
        {
            field: 'indexShow',
            title: '首页轮播',
            width: 100,
            templet : '#indexShowTpl'
        },
        {
            field: 'offerPic',
            title: '商品图片',
            width: 100 ,
            templet : '#offerPicTpl'
        },
        {
            field: 'mainMedia',
            title: '主媒体',
            width: 80 ,
            templet : '#mainMediaTpl'
        },
        {
            field: 'metaData',
            title: '讲师',
            width: 100,
            templet : '#metaDataTpl'
        },
        {
            field: 'offerDesc',
            title: '商品描述',
            width: 180
        },
        {
            field: 'htmlId',
            title: '详情',
            width: 80,
            templet : '#htmlIdTpl'
        },
        {
            field: 'status',
            title: '状态',
            width: 80,
            templet : '#statusTpl'
        },
        {
            field: 'createdAt',
            title: '创建时间',
            width: 180
        },
        {
            field: 'offerId',
            title: '操作',
            width: 250,
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
    var tr = obj.tr; //获得当前行 tr 的DOM对象

    if (layEvent === 'editOffer') { //编辑商品
        editOffer(data, layEvent, obj);
    } else if (layEvent === 'deleteOffer') {// 删除商品
        deleteOffer(data, layEvent, obj);
    } else if (layEvent === 'sendWeiXin') {// 微信推送
        sendWeiXin(data, layEvent, obj);
    } else if (layEvent === 'onOffer') {// 上架
        onOffer(data, layEvent, obj);
    } else if (layEvent === 'offOffer') {// 下架
        offOffer(data, layEvent, obj);
    } else if (layEvent === 'buyOffer') {// 管理员代购
        buyOffer(data, layEvent, obj);
    }
});

function addOffer() {
    var url = 'toAddOffer.html';
    var winConfirm = layer.confirm('请选择新增商品类型', {
        btn: ['新增课程', '新增套餐']
    }, function(index, layero){
        url += '?type=CLASS';
        openNewOffer(url);
        layer.close(winConfirm);
    }, function(index){
        url += '?type=OFFER';
        openNewOffer(url);
        layer.close(winConfirm);
    });
}

function indexRecommend(offerId, type) {
    $.ajax({
        type: "POST",
        url:"indexRecommend.html",
        data : {
            offerId : offerId,
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

function openNewOffer(url) {
    var addLayer = layer.open({
        type: 2
        ,offset: '80px' //具体配置参考：http://www.layui.com/doc/modules/layer.html#offset
        ,id: 'addOffer' //防止重复弹出
        ,area: ['1000px', '600px']
        ,content: url
        ,shade: 0.3
    });
}

function editOffer(data, layEvent, obj) {
    var editLayer = layer.open({
        type: 2
        //,offset: '80px' //具体配置参考：http://www.layui.com/doc/modules/layer.html#offset
        ,id: layEvent //防止重复弹出
        ,area: ['1000px', '600px']
        ,content: 'toEditOffer.html?offerId=' + data.offerId
        ,maxmin:true
        ,shade: 0.3
    });
}

// 代购商品
function buyOffer(data, layEvent, obj) {
    var buyLayer = layer.open({
        type: 2
        //,offset: '80px' //具体配置参考：http://www.layui.com/doc/modules/layer.html#offset
        ,id: layEvent //防止重复弹出
        ,area: ['800px', '600px']
        ,content: 'toBuyOffer.html?offerId=' + data.offerId
        ,maxmin:true
        ,shade: 0.3
    });
}

function deleteOffer(data, layEvent, obj) {
    layer.confirm('确认删除商品？', {icon: 3, title:'警告'}, function(index){
        $.ajax({
            type: "POST",
            url:"deleteOffer.html",
            data : {
                offerId : data.offerId
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

function sendWeiXin(data, layEvent, obj) {
    var metaData = data.metaData;
    var teacherStr = "";
    if(metaData){
        var json = JSON.parse(metaData);
        var teacherArr = json.TEACHER;
        if(teacherArr.length>0){
            for (var inx in teacherArr){
                teacherStr += teacherArr[inx].name + " ";
            }
        }
    }
    $.ajax({
        type: "POST",
        url:contextPath + "/admin/offer/sendWeiXin.html",
        dataType:"json",
        data : {
            offerId : data.offerId,
            type : data.offerType,
            offerName : data.offerName,
            addr : "在线学习平台",
            teachers : teacherStr
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

function onOffer(data, layEvent, obj){
    if(data.status==1){
        layer.msg("商品已经上架");
        return false;
    }
    $.ajax({
        type: "POST",
        url:"onOrOffOffer.html",
        data : {
            offerId : data.offerId,
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

function offOffer(data, layEvent, obj){
    if(data.status==0){
        layer.msg("商品已经下架");
        return false;
    }
    $.ajax({
        type: "POST",
        url:"onOrOffOffer.html",
        data : {
            offerId : data.offerId,
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

function showOffer(offerId) {
    var addLayer = layer.open({
        type: 2
        ,offset: '80px' //具体配置参考：http://www.layui.com/doc/modules/layer.html#offset
        ,id: 'showOffer' //防止重复弹出
        ,area: ['1000px', '600px']
        ,content: 'toShowOffer.html?offerId=' + offerId + '&relType=OFFER'
        ,shade: 0.3
    });
}
function searchList(){
    $("#searchList").click();
}

