
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
            field: 'nickName',
            title: '用户昵称',
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
            field: 'userLevel',
            title: '会员等级',
            width: 100
            ,templet : '#vipTpl'
        },
        {
            field: 'isAgent',
            title: '代理级别',
            width: 100
            ,templet : '#agentTpl'
        },
        {
            field: 'recomend',
            title: '推荐人',
            width: 120
        },
        {
            field: 'firstLvNbr',
            title: '一级同学',
            width: 100,
            templet : '#firstLvNbrTpl'
        },
        {
            field: 'secondLvNbr',
            title: '二级同学',
            width: 100,
            templet : '#secondLvNbrTpl'
        },
        {
            field: 'province',
            title: '省',
            width: 80
        },
        {
            field: 'city',
            title: '市',
            width: 80
        },
        {
            field: 'address',
            title: '地址',
            width: 120
        },
        {
            field: 'createdAt',
            title: '注册时间',
            width: 180
        },
        {
            field: 'custId',
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
    var tr = obj.tr; //获得当前行 tr 的DOM对象

    if (layEvent === 'editRecommend') { //修改推荐人
        editRecommend(data, layEvent, tr);
    } else if (layEvent === 'editVip') {// 修改VIP等级
        editVip(data, layEvent, tr);
    } else if(layEvent === "deleteCust"){// 删除客户
        delCust(data, layEvent, tr);
    }
});

function editRecommend(data, layEvent, tr) {
    //修改推荐人
    layer.open({
        type: 1
        ,offset: '200px' //具体配置参考：http://www.layui.com/doc/modules/layer.html#offset
        ,title : '修改推荐人'
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

function editVip(data, layEvent, tr) {
    layer.open({
        type: 1
        ,offset: '200px' //具体配置参考：http://www.layui.com/doc/modules/layer.html#offset
        ,id: layEvent //防止重复弹出
        ,title : '修改VIP等级'
        ,content: '<div style="padding: 20px 100px;"><input type="hidden" id="J_R_custId" value="' + data.custId + '">'
        + '<input type="radio" value="NONE" name="vip_lv" title="普通">普通'
        + ' <input type="radio" value="VIP" name="vip_lv" title="VIP">VIP'
        + ' <input type="radio" value="SVIP" name="vip_lv" title="SVIP">SVIP' + '</div>'
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
                        time: 2000 //2秒关闭（如果不配置，默认是3秒）
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
function delCust(data, layEvent, tr) {
    layer.confirm('删除客户不可恢复，确认删除？', {icon: 3, title:'警告'}, function(index){
        $.ajax({
            type: "POST",
            url:"delCust.html",
            data : {
                custId : $("#J_R_custId").val(),
            },
            dataType:"json",
            success: function(data) {
                layer.msg(data.msg, {
                    time: 2000 //2秒关闭（如果不配置，默认是3秒）
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
    });
}
function openChildCustList(type, custId) {
    var title = '一级同学';
    if(type==2){
        title = '二级同学';
    }
    layer.open({
        type: 2
        ,offset: '80px' //具体配置参考：http://www.layui.com/doc/modules/layer.html#offset
        ,id: 'childCustList' //防止重复弹出
        ,title : title
        ,area: ['750px', '500px']
        ,content: 'toChildList.html?type=' + type + '&custId=' + custId
        ,maxmin:true
        ,shade: 0.3
    });
}
