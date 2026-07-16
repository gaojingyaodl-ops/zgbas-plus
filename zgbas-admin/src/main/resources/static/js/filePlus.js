// var filePlugin = new FilePlugin('#fileId',$('#entityId').val(),'/bs/company/updateFileId',true);
FilePluginPlus = function (domId, bizId, url_updateFileId, hasEditFile) {
    this.domFileId = domId;
    this.bizId = bizId;
    this.url_updateFileId = url_updateFileId;
    if (hasEditFile == undefined) {
        hasEditFile = false;
    }
    this.hasEditFile = hasEditFile;
};
FilePluginPlus.prototype = {
    load: function (fileIds, _showDomId) {
		if (fileIds && !fileIds.endsWith(",")){
			fileIds = fileIds + ",";
		}
        var _url = this.url_updateFileId;
        var _domId = this.domFileId;
        var _bizId = this.bizId;
        if (!_bizId){
            _bizId = 0;
        }
        // console.log(fileIds, _showDomId);
        _domId = _domId.replace("#", "");
        var url = _ctx + '/file/loadFiles?hasEditFile=' + this.hasEditFile + '&url=' + _url + '&domId=' + _domId + '&bizId='+_bizId;
        $.post(url, {
            fileIds: fileIds
        }, function (data) {
            // console.log("load - function : ", _showDomId, " - ", data);
            $(_showDomId).html(data);
        });
    },
    uploadSuccess: function (fileId) {
        var _domFileId = this.domFileId;
        var _url_updateFileId = this.url_updateFileId;
        var _bizId = this.bizId;
        var oldFileId = $(_domFileId).val();
        if(oldFileId && !oldFileId.endsWith(",")){
            oldFileId = oldFileId+",";
        }
        var newFileId = oldFileId + fileId;
        // console.log("uploadSuccess : ", oldFileId,newFileId);
        $(_domFileId).val(newFileId);

        var _domId = this.domFileId;
        _domId = _domId.replace("#", "");

        var parString = '{"id":' + _bizId + ', "' + _domId + '":"' + newFileId + '"}';
        console.log("parString - ", parString);
        // console.log("uploadSuccess : ", _ctx+_url_updateFileId+"?id="+_bizId+"&fileId="+newFileId);
        $.post(_ctx + _url_updateFileId, JSON.parse(parString), function (d) {
            // console.log("uploadSuccess - post : ", d);
        })
    },
    del: function (ths, fileId) {
        var _url_updateFileId = this.url_updateFileId;
        var _domFileId = this.domFileId;
        var _bizId = this.bizId;
        var updateUrl = $(ths).attr("updateUrl");
        var domId = $(ths).attr("domId");
        var _$bizd = $(ths).attr("bizId");
        if (!_$bizd){
            _$bizd = 0;
        }
        if (updateUrl != null && updateUrl != undefined && updateUrl != "") {
            _url_updateFileId = updateUrl;
        }
        if (domId != null && domId != undefined && domId != "") {
            _domFileId = "#" + domId;
        }
        //删除确认弹窗
        var new_file_id = "";
        $.messager.confirm({
            width: 388,
            height: 170,
            title: '删除提示',
            msg: '确定要删除该信息吗？',
            ok: "确定",
            cancel: "取消",
            cls: 'c9',
            border: 'thin',
            fn: function (r) {
                if (r) {
                    $.post(_ctx + '/file/delete', {
                        fileId: fileId,
                        domId: domId,
                        bizId:_$bizd
                    }, function (r) {
                        if (r.success) {
                            setTimeout(function(){
                                var oldFileId = $(_domFileId).val();
                                var newFileId = oldFileId.replace(fileId + ",", '');
                                $(_domFileId).val(newFileId);
                                new_file_id = newFileId;
                                $('#file'+fileId).remove();
                                $.post(_ctx+_url_updateFileId,{id:_bizId,fileId:newFileId},function(d){
                                })
                            },200);
                        }
                    });
                }
            }
        });
    }
}

function fileUploadPlus(fileBtn, fileId, filePath, filePlugin) {
    var $this = $('#' + fileId);
    // fileBtn:点击弹出选择文件事件的dom对象
    // filePath:文件上传成功后，保存的服务文件Id
    // --start-文件上传---
    var uploader = WebUploader.create({
        auto: true,
        // swf文件路径
        swf: _ctx + '/static/webuploader-0.1.5/Uploader.swf',
        // 文件接收服务端。
        server: _ctx + '/file/uploadFile',
        // 选择文件的按钮。可选。
        // 内部根据当前运行是创建，可能是input元素，也可能是flash.
        pick: '#' + fileBtn,
        // 不压缩image, 默认如果是jpeg，文件上传前会压缩一把再上传！
        resize: false,
        duplicate: true,//true可重复上传，默认不可多次重复上传
        // 只允许选择图片文件。
        accept: {
            title: 'file',
            extensions: 'gif,jpg,jpeg,bmp,png,pdf,rar,zip,doc,docx,xlsx,xls',
            mimeTypes: "*"
        }
    });
    // 去掉文件上传组件样式，使用上传dom 自己样式
    $(".webuploader-pick").removeClass();
    uploader.on('beforeFileQueued', function (file) {
        $.messager.progress('close');
        if (file && file.size > 104857600) {
            $.messager.alert('tip', "上传文件不能超过100M!", 'info');
            return false;
        }
    })

    // 当有文件添加进来的时候
    uploader.on('fileQueued', function (file) {
        $.messager.progress();
        //var $li = $('<div id="' + file.id + '" class="file-item thumbnail">' + '<img>' + '<div class="info">' + file.name + '</div>' + '</div>'), $img = $li.find('img');
    });

    // 文件上传成功，给item添加成功class, 用样式标记上传成功。
    uploader.on('uploadSuccess', function (file, response) {
        $.messager.progress('close');
        if (file && file.size > 104857600) {
            $.messager.alert('tip', "上传文件不能超过100M!", 'info');
            return;
        }
        if (response && response.result) {//上传成功
            // console.log("uploadSuccess", response);
            var fileId = response.fileId;
            // console.log("fileId", fileId);
            filePlugin.uploadSuccess(fileId);
            //加载附件列表
            filePlugin.load($this.val(), '#' + filePath);
        } else {
            $.messager.alert('tip', "上传文件失败，失败信息：" + response._raw, 'info');
        }
    });
    uploader.on('error', function (file) {
        $.messager.progress('close');
        $.messager.alert('tip', '文件格式不支持!', 'info');
    })
    // 文件上传失败，显示上传出错。
    uploader.on('file', function (file) {
        $.messager.progress('close');
        $.messager.alert('tip', '文件上传失败,请重试!', 'info');
    });
    // --end-文件上传---
}
