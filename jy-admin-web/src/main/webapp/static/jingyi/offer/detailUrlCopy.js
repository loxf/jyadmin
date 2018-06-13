function getCopyData(offerId, offerType, type) {
    var result = "";
    $.ajax({
        type: "POST",
        async:false,
        url: contextPath + "/admin/offer/getDetailUrl.html",
        data : {
            offerId : offerId,
            offerType : offerType,
            type : type
        },
        dataType:"json",
        success: function(data) {
            if(data.code==1){
                result = data.data;
                $("#copyTarget").text(data.data);  /*放入目标对象*/
            } else {
                layer.msg(data.msg);
            }
        }
    });
    return result;
}

var clipboard = new Clipboard('.copy-btn', {
    text: function(trigger) {
        var offerId = $(trigger).data("offerid");
        var offerType = $(trigger).data("offertype"); /*获取目标对象上挂载的参数offerType*/
        var type = $(trigger).data("type"); /*获取目标对象上挂载的参数type*/
        var data = getCopyData(offerId, offerType, type);
        return data;
    }
});
clipboard.on('success', function(e) {
    layer.msg("复制成功");
});
clipboard.on('error', function(e) {
    layer.msg("复制失败");
});