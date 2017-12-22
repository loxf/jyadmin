/**
 * 实例化一个饼图
 * @param data 数据格式[["分公司",2],["合伙人",6],["代理商",17],["SVIP",24],["VIP",128],["普通会员", 1402]];
 * @param title 饼图名称
 * @param obj dom对象id
 */
function getPie(data, title, obj) {
    var chart = echarts.init(document.getElementById(obj), "walden");
    var dateList = data.map(function (item) {
        return item[0];
    });
    var valueList = data.map(function (item) {
        var tmp = {
            value:item[1],
            name:item[0]
        }
        return tmp;
    });
    // 指定图表的配置项和数据
    var option = {
        tooltip: {
            trigger: 'item',
            formatter: "{a} <br/>{b}: {c} ({d}%)",
            saveAsImage: {}
        },
        toolbox: {
            show : true,
            feature : {
                mark : {show: true},
                dataView : {show: true, readOnly: false},
                saveAsImage : {show: true}
            }
        },
        legend: {
            orient: 'vertical',
            x: 'left',
            data:dateList
        },
        series: [
            {
                name:title,
                type:'pie',
                radius: ['50%', '70%'],
                avoidLabelOverlap: false,
                label: {
                    normal: {
                        show: false,
                        position: 'center'
                    },
                    emphasis: {
                        show: true,
                        formatter: "{b}: {c}位\n({d}%)",
                        textStyle: {
                            fontSize: '20',
                            fontWeight: 'bold'
                        }
                    }
                },
                labelLine: {
                    normal: {
                        show: false
                    }
                },
                data:valueList
            }
        ]
    };
    // 使用刚指定的配置项和数据显示图表。
    chart.setOption(option);
}
/**
 * 实例化一个折线图/柱状图
 * @param data 数据格式[["分公司",2],["合伙人",6],["代理商",17],["SVIP",24],["VIP",128],["普通会员", 1402]];
 * @param title 饼图名称
 * @param type 折线图line;柱状图bar
 * @param obj dom对象id
 */
function getLine(data, title, type, obj) {
    var chart = echarts.init(document.getElementById(obj), "walden");
    var dateList = data.map(function (item) {
        return item[0];
    });
    var valueList = data.map(function (item) {
        return item[1];
    });
    var option = {
        title: {
            text: title
        },
        tooltip: {
            trigger: 'axis'
        },
        legend: {
            data:['最高','最低']
        },
        toolbox: {
            show: true,
            feature: {
                /*dataZoom: {
                    yAxisIndex: 'none'
                },*/
                dataView: {readOnly: false},
                magicType: {type: ['line', 'bar']},
                saveAsImage: {}
            }
        },
        xAxis:  {
            type: 'category',
            boundaryGap: type!=='line',
            data: dateList
        },
        yAxis: {
            type: 'value'
        },
        series: [
            {
                name:title,
                type:type,
                data:valueList,
                markPoint: {
                    data: [
                        {type: 'max', name: '最大值'},
                        {type: 'min', name: '最小值'}
                    ]
                },
                markLine: {
                    data: [
                        {type: 'average', name: '平均值'}
                    ]
                }
            }
        ]
    };
    chart.setOption(option);
}