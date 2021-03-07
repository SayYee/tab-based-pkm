layui.define(['jquery', 'layer'], function (exports) {
    var $ = layui.jquery, layer = layui.layer

    // var basePath = "http://localhost:9000";
    var basePath = "";

    var pkm = {
        cmd: function(cmd, fn) {
            var self = this;
            self.sendRequest({
                url: basePath + "/cmd",
                type: "get",
                data: {
                    cmdStr: cmd
                },
                callback: fn
            })
        },
        cmdInfo: function(fn) {
            var self = this;
            self.sendRequest({
                url: basePath + "/cmdInfo",
                type: "get",
                callback: fn
            })
        },
        assignTree: function (id, fn) {
            var self = this;
            self.sendRequest({
                url: basePath + "/getAssignTree",
                type: "get",
                data: {
                    id: id
                },
                callback: fn
            })
        },
        currentTree: function (fn) {
            var self = this;
            self.sendRequest({
                url: basePath + "/getCurrentTree",
                type: "get",
                async: false,
                callback: fn
            })
        },
        saveTree: function (treeStr, fn) {
            var self = this;
            self.sendRequest({
                url: basePath + "/setTree",
                type: "put",
                data: {
                    treeStr: treeStr
                },
                callback: fn
            })
        },
        // 将本地文件纳入pkm
        copy: function (path, tagStr, fn) {
            var self = this;
            self.sendRequest({
                url: basePath + "/copy",
                type: "post",
                data: {
                    path: path,
                    tagStr: tagStr
                },
                callback: fn
            })
        },
        // 创建并打开文件
        create: function (filename, tagStr, fn) {
            var self = this;
            self.sendRequest({
                url: basePath + "/create",
                type: "post",
                data: {
                    filename: filename,
                    tagStr: tagStr
                },
                callback: fn
            })
        },
        // url加入pkm
        url: function (name, url, tagStr, fn) {
            var self = this;
            self.sendRequest({
                url: basePath + "/url",
                type: "post",
                data: {
                    name: name,
                    url: url,
                    tagStr: tagStr
                },
                callback: fn
            })
        },
        // 打开文件
        open: function (id, fn) {
            var self = this;
            self.sendRequest({
                url: basePath + "/open/" + id,
                type: "get",
                callback: fn
            })
        },
        // 打开文件所在文件夹，并选中文件
        select: function (id, fn) {
            var self = this;
            self.sendRequest({
                url: basePath + "/select/" + id,
                type: "get",
                callback: fn
            })
        },
        // 更新元数据
        update: function (id, newName, tags, fn) {
            var self = this;
            self.sendRequest({
                url: basePath + "/update",
                type: "put",
                contentType: 'application/json',
                data: JSON.stringify({
                    id: id,
                    newName: newName,
                    tags: tags
                }),
                callback: fn
            })
        },
        // 删除数据
        delete: function (id, fn) {
            var self = this;
            self.sendRequest({
                url: basePath + "/delete/" + id,
                type: "delete",
                callback: fn
            })
        },
        // 重命名标签
        modifyTag: function (tag, newTag, fn) {
            var self = this;
            self.sendRequest({
                url: basePath + "/modifyTag",
                type: "put",
                data: {
                    tag: tag,
                    newTag: newTag
                },
                callback: fn
            })
        },
        // 删除标签
        deleteTag: function (tag, fn) {
            var self = this;
            self.sendRequest({
                url: basePath + "/deleteTag/" + tag,
                type: "delete",
                callback: fn
            });
        },
        // ajax发送方法
        sendRequest: function (options, fn) {
            var self = this;
            $.ajax({
                url: options.url,
                type: options.type,
                data: options.data,
                contentType: options.contentType,
                async: options.async,
                beforeSend: function () {
                    layer.load(2);
                    return true;
                },
                complete: function () {
                    layer.closeAll('loading');
                },
                success: function (res) {
                    self.successResDeal(res, options.callback);
                },
                error: function (res) {
                    layer.msg("操作异常", { icon: 5 });
                }
            });
        },
        // 处理ajax成功回调事件
        successResDeal: function (res, fn) {
            fn = fn || function (res) { layer.msg('操作成功', { icon: 1 }) };
            if (res.code === 1) {
                fn(res);
            } else {
                layer.msg("操作异常：" + res.message, { icon: 5 });
            }
        }
    }
    exports('pkm', pkm);
});    