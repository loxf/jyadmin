
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
    url: 'queryCompanyIncomeList.html',
    method: 'POST',
    elem: '#dataTable', //指定原始表格元素选择器（推荐id选择器）
    height: 435, //容器高度
    page: true,
    cols: [[
        {
            field: 'custName',
            title: '消费者',
            width: 150
        },
        {
            field: 'type',
            title: '收入类型',
            width: 150,
            templet : '#typeTpl'
        },
        {
            field: 'detailName',
            title: '明细内容',
            width: 450
        },
        {
            field: 'amount',
            title: '收入(元)',
            width: 100
        },
        {
            field: 'createdAt',
            title: '收入时间',
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