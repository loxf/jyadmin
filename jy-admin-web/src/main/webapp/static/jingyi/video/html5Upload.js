/**
 * Created by 郝少禅
 * Email:sxchying@126.com
 * QQ:490746237
 */
window.html5Upload = (function () {
    function html5Upload() {
    }
    //添加文件的Option对象
    var addFileOption = {};
    //点击上传的Option对象
    var uploadOption = {};
    //拓展函数
    (function () {
        //使用jq拓展文件添加函数
        (function ($) {
            var defaults = {
                addFile: function (data) {

                },
                addFileError: function (data) {

                }
            };
            $.fn.addFile = function (options) {
                addFileOption = $.extend(defaults, options || {});
                return this.each(function () {
                    $(this).click(uploadFileOperation.addFile);
                });
            };
        })(jQuery);
        //使用jq拓展上传函数
        (function ($) {
            var defaults = {
                uploadInitUrl: "",
                status : 0,
                uploadFinish: function (data) {

                },
                uploadProgress: function (data) {

                },
                uploadError: function (data) {

                },
                uploadAbort: function (data) {

                }
            };
            $.fn.upload = function (options) {
                uploadOption = $.extend(defaults, options || {});
                return this.each(function () {
                    $(this).click(function () {
                        //上传
                        videoUpload.tryUpload();
                    });
                });
            };
        })(jQuery);
    })();
    //ajax
    var uploadAjax = (function () {
        function uploadAjax() {
        }
        uploadAjax.post = function (url, params, success, error) {
            $.ajax({
                url: url,
                type: 'post',
                data: params,
                dataType: "json",
                success: function (data) {
                    success(data);
                },
                error: function (data) {
                    if (error) {
                        error(data);
                    }
                }
            });
        };
        return uploadAjax;
    })();

    //上传回调函数
    var xhrEventCallback = (function () {
        function xhrEventCallback() {
        }
        xhrEventCallback.finish = function (e) {
            //文件上传完成
            uploadOption.uploadFinish({ code: 0, msg: e.msg });
            uploadOption.status = 3;
            videoUpload.xhrAbort();
        };
        xhrEventCallback.progress = function (e) {
            uploadOption.uploadProgress({ progress: e.curr + "%"});
        };
        xhrEventCallback.error = function (e) {
            uploadOption.uploadError({ code: 404, msg: e.msg });
            uploadOption.status = -3;
            videoUpload.xhrAbort();
        };
        return xhrEventCallback;
    })();
    //上传
    var videoUpload = (function () {
        function videoUpload() {
        }
        var xhr = null;
        var tryNum = 0;
        videoUpload.xhrAbort = function () {
            xhr && xhr.abort();
        };
        videoUpload.tryUpload = function () {
            if(html5Upload.exportObject.selectFile.file==undefined){
                parent.layer.msg("请先添加视频");
                return;
            }
            if(uploadOption.status==1){
                parent.layer.msg("视频正在上传中，请耐心等候");
                return;
            } else if(uploadOption.status==3){
                parent.layer.msg("视频已经上传完成，不能重复上传");
                return;
            } else if(uploadOption.status==-3){
                parent.layer.msg("请稍后重试");
                return;
            } else {
                // 上传
                uploadOption.status = 1;
            }
            // 获取上传签名
            var video_name = encodeURIComponent(html5Upload.exportObject.selectFile.file.name);
            var token = html5Upload.exportObject.selectFile.fileKey;
            uploadAjax.post(uploadOption.uploadInitUrl, {video_name : video_name, token : token}, function (data) {
                var uploadSign = data.sign;
                jyVideoId = data.videoId;
                if(data.code==1){
                    try {
                        var resultMsg = qcVideo.ugcUploader.start({
                            videoFile: html5Upload.exportObject.selectFile.file,
                            // coverFile: coverFileList[0],
                            getSignature: uploadSign,
                            allowAudio: 1,
                            success: function(result){
                                if(result.type == 'video') {
                                    xhrEventCallback.progress({curr:0});
                                }
                            },
                            error: function(result){
                                if(result.type == 'video') {
                                    xhrEventCallback.error(result);
                                }
                            },
                            progress: function(result){
                                if(result.type == 'video') {
                                    xhrEventCallback.progress({curr:Math.floor(result.curr*100), sha: Math.floor(result.shacurr*100)});
                                }
                            },
                            finish: function(result){
                                var fileId = result.fileId;
                                var videoUrl = result.videoUrl;
                                // result.coverUrl;
                                var msg ;
                                if(result.message) {
                                    msg = result.message;
                                } else {
                                    msg = "上传成功";
                                }
                                xhrEventCallback.finish({msg: msg, fileId: fileId, videoUrl : videoUrl});
                            }
                        });
                        if(resultMsg){
                            alert(resultMsg);
                        }
                    } catch (e) {
                        uploadOption.uploadAbort({ code: 207, msg: e.message });
                        uploadOption.status = -3;
                    }
                }
            });
        };
        return videoUpload;
    })();
    //文件操作
    var uploadFileOperation = (function () {
        function uploadFileOperation() {
        }
        //允许上传的文件类型
        var fileTypes = "wmv|avi|dat|asf|rm|rmvb|ram|mpg|mpeg|mp4|mov|m4v|mkv|flv|vob|qt|divx|cpk|fli|flc|mod|dvix|dv|ts";
        //获取文件的类型
        var getFileType = function (file) {
            return file.name.split(".").pop();
        };
        //获取文件的key
        var getFileKey = function (file) {
            return [getFileType(file), file.size, file.lastModifiedDate || file.name].join('_');
        };
        //选中文件的时候
        var inputFileChange = function (e) {
            var file = e.target.files[0];
            var fType = getFileType(file);
            if (file.size < 0) {
                addFileOption.addFileError({ code: 100, msg: "文件大小为0" });
            } else if (!eval("/" + fileTypes + "$/i").test(fType)) {
                addFileOption.addFileError({ code: 101, msg: "不支持此视频格式" });
            } else {
                addFileOption.addFile({ code: 0, msg: "成功", data: { fileName: file.name, fileSize: file.size, fileType: fType } });
                html5Upload.exportObject.selectFile.file = file;
                html5Upload.exportObject.selectFile.fileKey = getFileKey(file);
            }
        };
        //添加文件
        uploadFileOperation.addFile = function (e) {
            if(uploadOption.status==1){
                parent.layer.msg("视频正在上传中，请耐心等候");
                return;
            } else {
                uploadOption.status = 0;
            }
            var inpfile = document.getElementById("fileUploadId_Hsc");
            if (inpfile) {
                inpfile.click && e.target != inpfile && inpfile.click();
            } else {
                inpfile = document.createElement("input");
                $(inpfile).append(inpfile);
                inpfile.setAttribute("id", "fileUploadId_Hsc");
                inpfile.setAttribute("type", "file");
                inpfile.style.display = "none";
                inpfile.addEventListener('change', inputFileChange, !1);
                inpfile.click && e.target != inpfile && inpfile.click();
            }
        };
        return uploadFileOperation;
    })();
    //可供用户访问的属性
    html5Upload.exportObject = (function () {
        function exportObject() {
        }
        //选中的文件对象
        exportObject.selectFile = {};
        return exportObject;
    })();
    return html5Upload;
})();