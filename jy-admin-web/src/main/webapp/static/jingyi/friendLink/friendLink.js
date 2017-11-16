
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
    height: 435, //容器高度
    page: true,
    cols: [[
        {
            field: 'name',
            title: '友商名称',
            width: 120
        },
        {
            field: 'url',
            title: '链接',
            width: 180,
            templet : '#urlTpl'
        },
        {
            field: 'pic',
            title: '图标',
            width: 120,
            templet : '#picTpl'
        },
        {
            field: 'order',
            title: '优先级',
            width: 100
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
            title: '操作',
            width: 280,
            fixed: 'right',
            align: 'center',
            toolbar: '#barTable'
        }
    ]],
    request: {
        pageName: 'page', //页码的参数名称，默认：page
        limitName: 'size' //每页数据量的参数名，默认：limit
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

    if (layEvent === 'onFriendLink') { //发布
        if(data.status==0) {
            onFriendLink(data, layEvent, obj);
        } else {
            layer.msg("链接已经发布");
        }
    } else if (layEvent === 'offFriendLink') {// 取消发布
        if(data.status==1) {
            offFriendLink(data, layEvent, obj);
        } else {
            layer.msg("链接已经取消");
        }
    } else if (layEvent === 'editFriendLink') {// 编辑
        editFriendLink(data, layEvent, obj);
    } else if (layEvent === 'deleteFriendLink') {// 删除
        deleteFriendLink(data, layEvent, obj);
    }
});

function addFriendLink() {
    var addLayer = layer.open({
        type: 2
        ,offset: '80px' //具体配置参考：http://www.layui.com/doc/modules/layer.html#offset
        ,id: 'addFriendLink' //防止重复弹出
        ,area: ['1000px', '600px']
        ,content: 'toAddFriendLink.html'
        ,shade: 0.3
    });
}

function editFriendLink(data, layEvent, obj) {
    var editLayer = layer.open({
        type: 2
        //,offset: '80px' //具体配置参考：http://www.layui.com/doc/modules/layer.html#offset
        ,id: layEvent //防止重复弹出
        ,area: ['1000px', '600px']
        ,content: 'toEditFriendLink.html?id=' + data.id
        ,maxmin:true
        ,shade: 0.3
    });
}

function deleteFriendLink(data, layEvent, obj) {
    layer.confirm('确认删除链接？', {icon: 3, title:'警告'}, function(index){
        $.ajax({
            type: "POST",
            url:"rmFriendLink.html",
            data : {
                id : data.id
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

function onFriendLink(data, layEvent, obj){
    $.ajax({
        type: "POST",
        url:"onOrOffFriendLink.html",
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

function offFriendLink(data, layEvent, obj){
    $.ajax({
        type: "POST",
        url:"onOrOffFriendLink.html",
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

function searchList(){
    $("#searchList").click();
}