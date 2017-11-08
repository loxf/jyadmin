
var form = layui.form;
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
    url: 'pendList.html',
    method: 'POST',
    elem: '#dataTable', //指定原始表格元素选择器（推荐id选择器）
    height: 490, //容器高度
    page: true,
    where:{status:0},
    cols: [[
        {
            field: 'realName',
            title: '姓名',
            width: 120
        },
        {
            field: 'isAgent',
            title: '代理级别',
            width: 100
            ,templet : '#agentTpl'
        },
        {
            field: 'isChinese',
            title: '国内/海外',
            width: 100,
            templet : '#isChineseTpl'
        },
        {
            field: 'phone',
            title: '手机/邮箱',
            width: 120
            ,templet : '#phoneOrEmailTpl'
        },
        {
            field: 'province',
            title: '省',
            width: 80
        },
        {
            field: 'city',
            title: '市',
            width: 70
        },
        {
            field: 'custId',
            title: '操作',
            width: 250,
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
    var tr = obj.tr; //获得当前行 tr 的DOM对象

    if (layEvent === 'pass') { // 通过
        pass(data, layEvent, obj);
    } else if (layEvent === 'notPass') {// 不通过
        notPass(data, layEvent, obj);
    }
});

function pass(data, layEvent, obj){
    isPass(data.custId, data.realName, 1);
}

function notPass(data, layEvent, obj){
    isPass(data.custId, data.realName, 2);
}

function isPass(custId, username, type) {
    layer.confirm('确认' + (type==1?"通过": "拒绝") + '代理商【' + username + '】的申请？', {icon: 3, title:'提示'}, function(index){
        $.ajax({
            type: "POST",
            url:"isPassAgent.html",
            data : {
                custId : custId,
                status: type
            },
            dataType:"json",
            success: function(data) {
                layer.msg(data.msg, {
                    time: 1500 //1.5秒关闭（如果不配置，默认是3秒）
                }, function(){
                    if(data.code == 1){
                        searchList();
                        layer.close(index);
                    }else{
                        return false;
                    }
                });
            }
        });
    });
}

function searchList() {
    $("#searchList").click();
}