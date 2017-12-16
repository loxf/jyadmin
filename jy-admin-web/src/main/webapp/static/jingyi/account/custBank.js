
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
    url: 'queryBankcardList.html',
    method: 'POST',
    elem: '#dataTable', //指定原始表格元素选择器（推荐id选择器）
    height: 435, //容器高度
    page: true,
    cols: [[
        {
            field: 'userName',
            title: '客户姓名',
            width: 120
        },
        {
            field: 'bank',
            title: '银行',
            width: 120
        },
        {
            field: 'bankNo',
            title: '卡号',
            width: 240
        },
        {
            field: 'province',
            title: '省',
            width: 100
        },
        {
            field: 'city',
            title: '市',
            width: 100
        },
        {
            field: 'zhName',
            title: '支行名称',
            width: 100,
            templet : '#statusTpl'
        },
        {
            field: 'remark',
            title: '备注',
            width: 240
        },
        {
            field: 'phone',
            title: '银行关联电话',
            width: 180
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
function searchList(){
    $("#searchList").click();
}