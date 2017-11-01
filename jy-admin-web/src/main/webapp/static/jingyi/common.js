function playMedia(media, type) {
    if (type === 'PIC') {
        var imgPath = basePic + media;
        layer.open({
            type: 1
            , offset: '80px' //具体配置参考：http://www.layui.com/doc/modules/layer.html#offset
            , id: 'playMediaLayer' //防止重复弹出
            , content: '<img src="' + imgPath + '" width="350px" height="180px" />'
            , shade: 0.3
        });
    } else if (type === 'MP4') {
        // 根据media 获取视频链接
        /*$.ajax({
            type: "POST",
            url:"queryVideoPath.html",
            data : {
                videoId : media
            },
            dataType:"json",
            success: function(data) {*/
        // TODO 获取视频链接
        var videoPath = media;
        layer.open({
            type: 2
            , offset: '80px' //具体配置参考：http://www.layui.com/doc/modules/layer.html#offset
            , id: 'playMediaLayer' //防止重复弹出
            , area: ['750px', '500px']
            , content: videoPath
            , maxmin: true
            , shade: 0.3
        });
        /* }
     });*/
    } else if (type === 'HTML') {
        // 根据media 获取html_info
        $.ajax({
            type: "POST",
            url: contextPath + "/admin/htmlInfo/get.html",
            data: {
                htmlId: media
            },
            dataType: "json",
            success: function (data) {
                if(data.code===1) {
                    var videoPath = media;
                    layer.open({
                        type: 1
                        , offset: '80px' //具体配置参考：http://www.layui.com/doc/modules/layer.html#offset
                        , id: 'playMediaLayer' //防止重复弹出
                        , area: ['750px', '500px']
                        , content: data.data
                        , maxmin: true
                        , shade: 0.3
                    });
                } else {
                    layer.msg(data.msg);
                }
            }
        });
    }
}

