
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
    url: 'queryCashList.html',
    method: 'POST',
    elem: '#dataTable', //指定原始表格元素选择器（推荐id选择器）
    height: 435, //容器高度
    page: true,
    cols: [[
        {
            field: 'custName',
            title: '客户姓名',
            width: 120
        },
        {
            field: 'balance',
            title: '提现金额(元)',
            width: 120
        },
        {
            field: 'type',
            title: '提现类型',
            width: 120,
            templet : '#typeTpl'
        },
        {
            field: 'objId',
            title: '银行卡/微信ID',
            width: 240
        },
        {
            field: 'status',
            title: '状态',
            width: 100,
            templet : '#statusTpl'
        },
        {
            field: 'remark',
            title: '备注',
            width: 240
        },
        {
            field: 'createdAt',
            title: '创建时间',
            width: 180
        },
        {
            field: 'custId',
            title: '操作',
            width: 180,
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

    if(data.status==1) {
        if (layEvent === 'passPending') { // 已打款
            passPending(data, layEvent, obj);
        } else if (layEvent === 'notpassPending') {// 拒绝打款
            notpassPending(data, layEvent, obj);
        }
    } else {
        layer.msg("记录已处理");
    }
});

function notpassPending(data, layEvent, obj) {
    //修改推荐人
    layer.open({
        type: 1
        ,offset: '200px' //具体配置参考：http://www.layui.com/doc/modules/layer.html#offset
        ,title : '拒绝打款'
        ,id: layEvent //防止重复弹出
        ,content: '<div style="padding: 20px 100px;"><input type="text" style="padding: 10px 10px" id="J_remark" placeholder="请输入拒绝原因" /></div>'
        ,btn: '提交'
        ,yes: function(index, layero){
            if($("#J_remark").val()==''){
                layer.msg("请输入拒绝打款原因")
                return false;
            }
            // 修改逻辑
            $.ajax({
                type: "POST",
                url:"pendingCustCash.html",
                data : {
                    id : data.id,
                    remark : $("#J_remark").val(),
                    status : -3
                },
                dataType:"json",
                success: function(data) {
                    layer.msg(data.msg, {
                        time: 1500 //1.5秒关闭（如果不配置，默认是3秒）
                    }, function(){
                        if(data.code == 1){
                            obj.update({
                                status : -3
                            });
                        }
                        layer.close(index);
                    });
                }
            });
        }
        ,btnAlign: 'c' //按钮居中
        ,shade: 0.3
    });
}

function passPending(data, layEvent, obj) {
    layer.confirm('确认已打款？', {icon: 3, title:'警告'}, function(index){
        $.ajax({
            type: "POST",
            url:"pendingCustCash.html",
            data : {
                id : data.id,
                status : 3
            },
            dataType:"json",
            success: function(data) {
                layer.msg(data.msg, {
                    time: 1500 //1.5秒关闭（如果不配置，默认是3秒）
                }, function(){
                    if(data.code == 1){
                        obj.update({
                            status : 3
                        });
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