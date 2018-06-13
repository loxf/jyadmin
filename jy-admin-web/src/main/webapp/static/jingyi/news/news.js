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
    url: contextPath + '/admin/news/pager.html',
    method: 'POST',
    elem: '#dataTable', //指定原始表格元素选择器（推荐id选择器）
    height: 'full-225', //容器高度
    where:{type:$('#type').val()},
    page: true,
    cols: [[
        {
            field: 'title',
            title: '标题',
            width: 200
        },
        {
            field: 'detailUrl',
            title: '推广链接',
            width: 150,
            templet : '#detailUrlTpl'
        },
        {
            field: 'status',
            title: '状态',
            width: 80,
            templet : '#statusTpl'
        },
        {
            field: 'source',
            title: '来源',
            width: 100
        },
        {
            field: 'keyword',
            title: '关键字',
            width: 220
        },
        {
            field: 'description',
            title: '描述',
            width: 260
        },
        {
            field: 'deployTime',
            title: '发布时间',
            width: 180
        },
        {
            field: 'readTimes',
            title: '阅读量',
            width: 80
        },
        {
            field: 'titleId',
            title: '操作',
            width: 200,
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

    if (layEvent === 'editNews') { //编辑新闻
        editNews(data, layEvent, obj);
    } else if (layEvent === 'deployNews') {// 删除新闻
        deployNewsOrNot(data, layEvent, obj);
    } else if (layEvent === 'sendWeiXin') {// 删除新闻
        sendWeiXin(data, layEvent, obj);
    } else if (layEvent === 'deleteNews') {// 删除新闻
        deleteNews(data, layEvent, obj);
    } else if (layEvent === 'preView'){
        preView(data, layEvent, obj);
    }
});

function addNews() {
    var addLayer = layer.open({
        type: 2
        ,offset: '80px' //具体配置参考：http://www.layui.com/doc/modules/layer.html#offset
        ,id: 'addNews' //防止重复弹出
        ,area: ['1000px', '600px']
        ,content: contextPath + '/admin/news/toAddNews' + ($('#type').val()==1?"/news":"/class" )+ '.html'
        ,shade: 0.3
    });
}

function deployNewsOrNot(data, layEvent, obj){
    var desc = "发布新闻";
    if(data.status==1){
        desc = "取消新闻发布";
    }
    layer.confirm('确认' + desc + '？', {icon: 3, title:'警告'}, function(index) {
        $.ajax({
            type: "POST",
            url: contextPath + "/admin/news/deployNewsOrNot.html",
            data: {
                titleId: data.titleId,
                oper: (data.status?0:1)
            },
            dataType: "json",
            success: function (result) {
                obj.update({
                    status: (data.status?0:1)
                });
                layer.msg(result.msg, {
                    time: 1500 //1.5秒关闭（如果不配置，默认是3秒）
                }, function(){
                    if(result.code == 1){
                        searchList();
                    }
                });
            }
        });
    });
}

function editNews(data, layEvent, obj) {
    var editLayer = layer.open({
        type: 2
        //,offset: '80px' //具体配置参考：http://www.layui.com/doc/modules/layer.html#offset
        ,id: layEvent //防止重复弹出
        ,area: ['1000px', '600px']
        ,content: contextPath + '/admin/news/toEditNews.html?titleId=' + data.titleId
        ,maxmin:true
        ,shade: 0.3
    });
}

function deleteNews(data, layEvent, obj) {
    layer.confirm('不建议删除，建议取消发布，如果删除，分享出去的文章将不可读！', {icon: 3, title:'警告'}, function(index){
        $.ajax({
            type: "POST",
            url:contextPath + "/admin/news/deleteNews.html",
            data : {
                titleId : data.titleId
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
    layer.confirm('确认推送新闻到微信？', {icon: 3, title:'警告'}, function(index) {
        $.ajax({
            type: "POST",
            url: contextPath + "/admin/news/sendWeiXin.html",
            dataType: "json",
            data: {titleId:data.titleId},
            success: function (result) {
                if (result.code == 1) {
                    layer.msg("推送成功");
                } else {
                    layer.msg(result.msg);
                }
            }
        });
    });
}

function searchList(){
    $("#searchList").click();
}

function preView(data, layEvent, obj){
    var preViewLayer = layer.open({
        type: 2
        //,offset: '80px' //具体配置参考：http://www.layui.com/doc/modules/layer.html#offset
        ,id: layEvent //防止重复弹出
        ,area: ['460px', '750px']
        ,content: contextPath + '/admin/news/toPreView.html?titleId=' + data.titleId
        ,maxmin:true
        ,shade: 0.3
    });
}