
var form = layui.form
    ,layer = layui.layer
    ,upload = layui.upload;

//同时绑定多个元素，并将属性设定在元素上
upload.render({
    elem: '.uploadExcel'
    ,size : 10240
    ,accept: 'file'
    ,exts: 'xlsx|xls'
    ,done: function(res, index, upload){
        layer.msg(res.msg);
    }
});