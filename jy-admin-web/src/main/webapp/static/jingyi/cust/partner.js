
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
    height: 490, //容器高度
    page: true,
    cols: [[
        {
            field: 'realName',
            title: '姓名',
            width: 120
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
            field: 'isAgent',
            title: '代理级别',
            width: 100
            ,templet : '#agentTpl'
        },
        {
            field: 'firstLvNbr',
            title: '一级',
            width: 60,
            templet : '#firstLvNbrTpl'
        },
        {
            field: 'secondLvNbr',
            title: '二级',
            width: 60,
            templet : '#secondLvNbrTpl'
        },
        {
            field: 'metaData',
            title: '赠送名额',
            width: 180,
            templet : '#totalVipNbrTpl'
        },
        {
            field: 'metaData',
            title: '已用名额',
            width: 180,
            templet : '#useVipNbrTpl'
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

    if (layEvent === 'editVipNbr') { //赠送名额
        editVipNbr(data, layEvent, obj);
    } else if (layEvent === 'editAgent') {// 修改代理身份
        editAgent(data, layEvent, obj);
    } else if (layEvent === 'cancelAgent') {// 取消代理
        cancelAgent(data, layEvent, obj);

    }
});

function openChildCustList(type, custId) {
    var title = '一级同学';
    if(type==2){
        title = '二级同学';
    }
    layer.open({
        type: 2
        ,offset: '80px' //具体配置参考：http://www.layui.com/doc/modules/layer.html#offset
        ,id: 'openChildCustList' //防止重复弹出
        ,title : title
        ,area: ['750px', '500px']
        ,content: contextPath + '/admin/cust/toChildList.html?type=' + type + '&custId=' + custId
        ,maxmin:true
        ,shade: 0.3
    });
}


function editVipNbr(data, layEvent, obj){
    layer.open({
        type: 1
        ,offset: '200px' //具体配置参考：http://www.layui.com/doc/modules/layer.html#offset
        ,title : '赠送VIP'
        ,id: layEvent //防止重复弹出
        ,content: '<div style="padding: 20px 100px;"><input type="hidden" id="J_R_custId" value="' + data.custId + '">'
        + '<input type="text" id="J_R_recommend" placeholder="推荐人手机号码/邮箱"></div>'
        ,btn: '提交'
        ,yes: function(index, layero){
            // 修改逻辑
            var _recommend = $("#J_R_recommend").val();
            if(_recommend==''){
                layer.msg('请填写推荐人');
                return false;
            }
            $.ajax({
                type: "POST",
                url:"modifyRecommend.html",
                data : {
                    custId : $("#J_R_custId").val(),
                    recommend : _recommend
                },
                dataType:"json",
                async:true,
                success: function(data) {
                    layer.msg(data.msg, {
                        time: 2000 //2秒关闭（如果不配置，默认是3秒）
                    }, function(){
                        if(data.code == 1){
                            obj.update({
                                recomend: _recommend
                            });
                            layer.closeAll();
                        }else{
                            return false;
                        }
                    });
                }
            });
        }
        ,btnAlign: 'c' //按钮居中
        ,shade: 0.3
    });
}

function editAgent(data, layEvent, obj){
    //do something
    layer.open({
        type: 1
        ,offset: '200px' //具体配置参考：http://www.layui.com/doc/modules/layer.html#offset
        ,id: layEvent //防止重复弹出
        ,title : '修改VIP等级'
        ,content: '<div style="padding: 20px 100px;"><input type="hidden" id="J_R_custId" value="' + data.custId + '">'
        + '<input type="radio" value="0" name="vip_lv" title="无">无身份'
        + ' <input type="radio" value="1" name="vip_lv" title="代理商">代理商'
        + ' <input type="radio" value="2" name="vip_lv" title="合伙人">合伙人'
        + ' <input type="radio" value="3" name="vip_lv" title="分公司">分公司' + '</div>'
        ,btn: '提交'
        ,success: function(layero, index){
            form.render();
        }
        ,yes: function(index, layero){
            // 修改逻辑
            var _userLevel = $("input[name='vip_lv']:checked").val();
            if(_userLevel==''){
                layer.msg('请选择用户等级');
                return false;
            }
            $.ajax({
                type: "POST",
                url:"modifyUserLevel.html",
                data : {
                    custId : $("#J_R_custId").val(),
                    userLevel : _userLevel
                },
                dataType:"json",
                success: function(data) {
                    layer.msg(data.msg, {
                        time: 1500 //1.5秒关闭（如果不配置，默认是3秒）
                    }, function(){
                        if(data.code == 1){
                            obj.update({
                                userLevel: _userLevel
                            });
                            layer.closeAll();
                        }else{
                            return false;
                        }
                    });
                }
            });
        }
        ,btnAlign: 'c' //按钮居中
        ,shade: 0.3
    });
}

function cancelAgent(data, layEvent, obj){
    layer.confirm('确认取消代理商？', {icon: 3, title:'提示'}, function(index){
        // TODO 取消代理商

        layer.close(index);
    });
}