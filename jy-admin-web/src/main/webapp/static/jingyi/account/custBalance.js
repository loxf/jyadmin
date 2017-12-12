
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
    url: 'queryCustBalanceList.html',
    method: 'POST',
    elem: '#dataTable', //指定原始表格元素选择器（推荐id选择器）
    height: 435, //容器高度
    page: true,
    cols: [[
        {
            field: 'nickName',
            title: '客户昵称',
            width: 150
        },
        {
            field: 'realName',
            title: '真实姓名',
            width: 150,
            templet : '#typeTpl'
        },
        {
            field: 'userLevel',
            title: '用户等级',
            width: 120,
            templet : '#vipTpl'
        },
        {
            field: 'isAgent',
            title: '代理商',
            width: 120,
            templet : '#agentTpl'
        },
        {
            title: '联系方式',
            width: 150,
            templet : '#phoneOrEmailTpl'
        },
        {
            field: 'balance',
            title: '余额(元)',
            width: 100
        },
        {
            field: 'bp',
            title: '积分',
            width: 100
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

function searchList(){
    $("#searchList").click();
}