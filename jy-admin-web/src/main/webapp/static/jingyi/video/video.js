
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
    cols: [[
        {
            field: 'videoName',
            title: '视频名称',
            width: 380
        },
        {
            field: 'videoOutId',
            title: '外部ID',
            width: 210
        },
        {
            field: 'videoUnique',
            title: '视频唯一码',
            width: 210
        },
        {
            field: 'status',
            title: '状态',
            width: 210 ,
            templet : '#statusTpl'
        },
        /*{
            field: 'metaData',
            title: '参数',
            width: 300
        },*/
        {
            field: 'videoId',
            title: '操作',
            width: 310,
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

    if (layEvent === 'editVideo') { //编辑视频
        editVideo(data, layEvent, obj);
    } else if (layEvent === 'deleteVideo') {// 删除视频
        deleteVideo(data, layEvent, obj);
    }
});

function addVideo() {
    var addLayer = layer.open({
        type: 2
        ,offset: '80px' //具体配置参考：http://www.layui.com/doc/modules/layer.html#offset
        ,id: 'addVideo' //防止重复弹出
        ,area: ['1000px', '600px']
        ,content: 'toAddVideo.html'
        ,shade: 0.3
        ,cancel: function(index, layero){
            searchList();
        }
    });
}

function updateVideo() {
    $.ajax({
        type: "POST",
        url:"editVideo.html",
        data : {
            videoId : $("#J_videoId").val(),
            videoOutId : $("#J_videoOutId").val(),
            videoName : $("#J_videoName").val()
        },
        dataType:"json",
        success: function(data) {
            layer.msg(data.msg, {
                time: 1500 //1.5秒关闭（如果不配置，默认是3秒）
            }, function(){
                searchList();
                layer.closeAll();
            });
        }
    });
}

function editVideo(data, layEvent, obj) {
    var getTpl = $("#M_editVideo").html()
    laytpl(getTpl).render(data, function(html){
        var editLayer = layer.open({
            type: 1
            //,offset: '80px' //具体配置参考：http://www.layui.com/doc/modules/layer.html#offset
            ,id: layEvent //防止重复弹出
            ,title: '修改视频名称'
            ,area: ['500px', '300px']
            ,content: html
            ,maxmin:true
            ,shade: 0.3
        });
    });
}

function deleteVideo(data, layEvent, obj) {
    layer.confirm('确认删除视频？', {icon: 3, title:'警告'}, function(index){
        $.ajax({
            type: "POST",
            url:"delVideo.html",
            data : {
                videoId : data.videoId
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