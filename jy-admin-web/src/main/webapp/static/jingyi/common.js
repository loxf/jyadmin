function playMedia(media, type) {
    if (type === 'PIC') {
        var imgPath = basePic + media;
        layer.open({
            type: 1
            , title: "图片"
            , offset: '80px' //具体配置参考：http://www.layui.com/doc/modules/layer.html#offset
            , id: 'playMediaLayer' //防止重复弹出
            , content: '<img src="' + imgPath + '" style="max-width: 350px; min-width: 200px; min-height: 100px" />'
            , shade: 0.3
        });
    } else if (type === 'MP4') {
        // 根据media 获取视频链接
        $.ajax({
            type: "POST",
            url: contextPath + "/admin/video/getUrl.html",
            data : {
                videoId : media
            },
            dataType:"json",
            success: function(data) {
                if(data.code==1){
                    var videoPath = data.data;
                    layer.open({
                        type: 2
                        , title: "视频"
                        , offset: '80px' //具体配置参考：http://www.layui.com/doc/modules/layer.html#offset
                        , id: 'playMediaLayer' //防止重复弹出
                        , area: ['680px', '430px']
                        , content: videoPath
                        , maxmin: true
                        , shade: 0.3
                    });
                } else {
                    layer.msg(data.msg);
                }
         }
     });
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
                        , title: "详情"
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
    } else if (type === 'URL') {
        if(media.startWith("http")){
            window.open(media, "_blank");
        } else {
            window.open("http://" + media, "_blank");
        }
    } else {
        layer.msg(media);
    }
}

String.prototype.startWith=function(str){
    var reg=new RegExp("^"+str);
    return reg.test(this);
}

String.prototype.endWith=function(str){
    var reg=new RegExp(str+"$");
    return reg.test(this);
}