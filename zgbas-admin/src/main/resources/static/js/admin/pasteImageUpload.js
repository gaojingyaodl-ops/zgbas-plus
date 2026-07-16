var PasteUpload = {
    init : function (){
        document.addEventListener('paste', function (event) {
            var items = (event.clipboardData || window.clipboardData).items;
            var file = null;
            if (items && items.length) {
                // 搜索剪切板items
                for (var i = 0; i < items.length; i++) {
                    if (items[i].type.indexOf('image') !== -1) {
                        file = items[i].getAsFile();
                        break;
                    }
                }
            } else {
                $.messager.alert('提示','当前浏览器不支持粘贴图片上传！','info');
                return;
            }
            if (!file) {
                $.messager.alert('提示','粘贴内容非图片！','info');
                return;
            }
            console.log("======" + file.name);
            this.uploaderFile();
        });
    },
    uploaderFile: function (){

    }
}