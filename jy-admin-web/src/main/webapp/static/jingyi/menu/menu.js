var element = layui.element;
// 菜单配置
var menu = {
    VIP: [
        {
            menuName: '首页',
            menuHref: "/admin/index.html"
        },
        {
            menuName: '会员管理',
            menuHref: contextPath + '/admin/cust/index.html'
        },
        {
            menuName: '合伙人管理',
            menuHref: 'javascript:void(0)',
            child: [{
                menuName: '合伙人列表',
                menuHref: contextPath + '/admin/partner/index.html'
            }, {
                menuName: '合伙人审核',
                menuHref: contextPath + '/admin/partner/toPend.html'
            }]
        }
    ],
    ORDER: [
        {
            menuName: '全部订单',
            menuHref: contextPath + '/admin/error.html?errorMsg=This page is not valid. Please wait!'
        },
        {
            menuName: '余额管理',
            menuHref: contextPath + '/admin/error.html?errorMsg=This page is not valid. Please wait!'
        },
        {
            menuName: '积分管理',
            menuHref: contextPath + '/admin/error.html?errorMsg=This page is not valid. Please wait!'
        },
        {
            menuName: '银行卡管理',
            menuHref: contextPath + '/admin/error.html?errorMsg=This page is not valid. Please wait!'
        },
        {
            menuName: '提现管理',
            menuHref: contextPath + '/admin/error.html?errorMsg=This page is not valid. Please wait!'
        },
        {
            menuName: '公司收入',
            menuHref: contextPath + '/admin/error.html?errorMsg=This page is not valid. Please wait!'
        },
        {
            menuName: '用户收入',
            menuHref: contextPath + '/admin/error.html?errorMsg=This page is not valid. Please wait!'
        }
    ],
    OFFER: [
        {
            menuName: '商品分类',
            menuHref: contextPath + '/admin/offerCatalog/index.html'
        },
        {
            menuName: '商品管理',
            menuHref: contextPath + '/admin/offer/index.html'
        },
        {
            menuName: '活动管理',
            menuHref: contextPath + '/admin/active/index.html'
        },
        {
            menuName: '视频管理',
            menuHref: contextPath + '/admin/video/index.html'
        }
    ],
    CONFIG: [
        {
            menuName: '支付设置',
            menuHref: contextPath + '/admin/config/index.html?catalog=PAY'
        },
        {
            menuName: '积分规则',
            menuHref: contextPath + '/admin/config/index.html?catalog=BP'
        },
        {
            menuName: '公司信息',
            menuHref: contextPath + '/admin/config/index.html?catalog=COM'
        },
        {
            menuName: '运行配置',
            menuHref: contextPath + '/admin/config/index.html?catalog=RUNTIME'
        }
    ]
}

function getLeftMenu(type) {
    var menuArr = eval("menu." + type);
    if (menuArr) {
        var laytpl = layui.laytpl;
        laytpl($("#left_menu").html()).render(menuArr, function (html) {
            // console.log(html);
            $("#div_left_menu").html(html);
            element.init();
        });
    } else {
        layer.msg("暂未开通");
    }
}

function locationMenu() {
    var href = location.href;
    var arr = ["VIP", "ORDER", "OFFER", "CONFIG"];
    for (var i = 0; i < arr.length; i++) {
        var item = arr[i];
        var menuArr = eval("menu." + item);
        var flag = parseMenuArr(menuArr, 1);
        if (flag) {
            var laytpl = layui.laytpl;
            laytpl($("#left_menu").html()).render(menuArr, function (html) {
                // console.log(html);
                $("#div_left_menu").html(html);
                element.init();
            });
            break;
        }
    }
}

function parseMenuArr(menuArr, deep) {
    var href = location.href;
    var flag = false;
    for (var index in menuArr) {
        var menuItem = menuArr[index];
        if (href.indexOf(menuItem.menuHref) > 0) {
            flag = true;
        } else if (menuItem.child) {
            flag = parseMenuArr(menuItem.child, 2);
        }
        if (flag) {
            if (deep == 1) {
                // 添加属性 layui-nav-itemed
                menuItem.classInfo = "layui-nav-itemed";
            } else {
                // 添加属性 layui-this
                menuItem.classInfo = "layui-this";
            }
            break;
        }
    }
    return flag;
}

locationMenu();