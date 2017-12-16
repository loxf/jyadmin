
var form = layui.form
    ,laytpl = layui.laytpl;
//监听提交
form.on('submit(searchForm)', function(data){
    table.reload('tableReload',
        { where: data.field });
    return false;
});
form.render();
var table = layui.table;
table.render({ //其它参数在此省略
    id: 'tableReload',
    url: 'list.html',
    method: 'POST',
    elem: '#dataTable', //指定原始表格元素选择器（推荐id选择器）
    height: 'full-225', //容器高度
    page: true,
    where:{catalog:$('#J_catalog').val()},
    cols: [[
        {
            field: 'configName',
            title: '配置名称',
            width: 350
        },
        {
            field: 'configCode',
            title: '配置编码',
            width: 250
        },
        {
            field: 'configValue',
            title: '值',
            width: 150
        },
        {
            field: 'detail',
            title: '详情',
            width: 100,
            templet : '#valueOptTpl'
        },
        {
            field: 'type',
            title: '配置类型',
            width: 120,
            templet : '#typeTpl'
        },
        {
            field: 'status',
            title: '状态',
            width: 100 ,
            templet : '#statusTpl'
        },
        {
            field: 'configId',
            title: '操作',
            width: 260,
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
    limits: [20, 30, 50, 100, 150],
    limit: 30, //默认采用30
    even: true
});
//监听工具条
table.on('tool(userDataTable)', function (obj) { //注：tool是工具条事件名，test是table原始容器的属性 lay-filter="对应的值"
    var data = obj.data; //获得当前行数据
    var layEvent = obj.event; //获得 lay-event 对应的值
    var tr = obj.tr; //获得当前行 tr 的DOM对象

    if (layEvent === 'editConfig') { //编辑配置
        editConfig(data, layEvent, obj);
    } else if (layEvent === 'deleteConfig') {// 删除配置
        deleteConfig(data, layEvent, obj);
    } else if (layEvent === 'onConfig') {// 删除配置
        onConfig(data, layEvent, obj);
    } else if (layEvent === 'offConfig') {// 删除配置
        offConfig(data, layEvent, obj);
    }
});

function onConfig(data, layEvent, obj){
    if(data.status==1){
        layer.msg("配置已启用");
        return false;
    }
    $.ajax({
        type: "POST",
        url:"onOrOffConfig.html",
        data : {
            id : data.id,
            status : 1
        },
        dataType:"json",
        success: function(data) {
            layer.msg(data.msg, {
                time: 1500 //1.5秒关闭（如果不配置，默认是3秒）
            }, function(){
                if(data.code == 1){
                    obj.update({
                        status: 1
                    });
                }
            });
        }
    });
}

function offConfig(data, layEvent, obj){
    if(data.status==0){
        layer.msg("配置已禁用");
        return false;
    }
    $.ajax({
        type: "POST",
        url:"onOrOffConfig.html",
        data : {
            id : data.id,
            status : 0
        },
        dataType:"json",
        success: function(data) {
            layer.msg(data.msg, {
                time: 1500 //1.5秒关闭（如果不配置，默认是3秒）
            }, function(){
                if(data.code == 1){
                    obj.update({
                        status: 0
                    });
                }
            });
        }
    });
}
function addConfig() {
    var addLayer = layer.open({
        type: 2
        ,offset: '80px' //具体配置参考：http://www.layui.com/doc/modules/layer.html#offset
        ,id: 'addConfig' //防止重复弹出
        ,area: ['1000px', '600px']
        ,content: 'toAddConfig.html?catalog=' + $("#J_catalog").val()
        ,shade: 0.3
    });
}

function editConfig(data, layEvent, obj) {
    var addLayer = layer.open({
        type: 2
        ,offset: '80px' //具体配置参考：http://www.layui.com/doc/modules/layer.html#offset
        ,id: 'editConfig' //防止重复弹出
        ,area: ['1000px', '600px']
        ,content: 'toEditConfig.html?catalog=' + $("#J_catalog").val() + "&configCode=" + data.configCode
        ,shade: 0.3
    });
}

function deleteConfig(data, layEvent, obj) {
    layer.confirm('确认删除配置？', {icon: 3, title:'警告'}, function(index){
        $.ajax({
            type: "POST",
            url:"delConfig.html",
            data : {
                catalog : $("#J_catalog").val(),
                configCode : data.configCode
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

function searchList(){
    $("#searchList").click();
}