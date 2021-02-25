//一般直接写在一个js文件中
layui.config({
    base: './modules/'
}).extend({
    tag: 'tag'
    , pkm: 'pkm'
}).use(['layer', 'form', 'table', 'util', 'tag', 'jquery', 'laytpl', 'pkm', 'element', 'dropball', 'slider'], function () {
    var layer = layui.layer
        , form = layui.form
        , table = layui.table
        , util = layui.util
        , tag = layui.tag
        , $ = layui.jquery
        , laytpl = layui.laytpl
        , pkm = layui.pkm
        , element = layui.element
        , dropball = layui.dropball
        , slider = layui.slider;

    //列表实例
    var tableIns = table.render({
        elem: '#demo'
        , height: 'full-120'
//         , url: 'http://localhost:9000/query' //数据接口
       , url: '/query' //数据接口
        , parseData: function (res) {
            return {
                "code": res.code === 1 ? 0 : 1,
                "msg": res.message,
                "data": res.result
            }
        }
        , page: false //开启分页
        , cols: [[ //表头
            { field: 'id', title: 'ID', width: 80 }
            , { field: 'filename', title: '文件名称', sort: true}
            , { field: 'resourceType', title: '类型', align: 'center', width: 80, sort: true, templet: function(d){
                return d.resourceType === 1 ? '<i class="layui-icon">&#xe621;</i>' : '<i class="layui-icon">&#xe64c;</i>'
            }}
            , { field: 'resourcePath', title: '资源定位' }
            , { field: 'tags', title: '标签', align: 'center', templet: '#tagList' }
            , { field: 'createTime', title: '创建时间', width: 180, sort: true, templet: '<div>{{layui.util.toDateString(d.createTime, "yyyy-MM-dd HH:mm:ss")}}</div>' }
            , { field: 'lastOpenTime', title: '最后打开时间', width: 180, sort: true, templet: '<div>{{layui.util.toDateString(d.createTime, "yyyy-MM-dd HH:mm:ss")}}</div>' }
            , { field: 'right', title: '操作', width: 180, align: 'center', toolbar: '#colTool' }
        ]]
    });
    //监听工具条 
    table.on('tool(test)', function (obj) { //注：tool 是工具条事件名，test 是 table 原始容器的属性 lay-filter="对应的值"
        var data = obj.data; //获得当前行数据
        var layEvent = obj.event; //获得 lay-event 对应的值（也可以是表头的 event 参数对应的值）
        var tr = obj.tr; //获得当前行 tr 的 DOM 对象（如果有的话）

        if (layEvent === 'open') { //打开
            pkm.open(data.id);
            //do somehing
        } else if (layEvent === 'del') { //删除
            layer.confirm('真的删除么', function (index) {
                pkm.delete(data.id, function (res) {
                    obj.del(); //删除对应行（tr）的DOM结构，并更新缓存
                    layer.close(index);
                });
            });
        } else if (layEvent === 'edit') { //编辑
            // 初始化表单
            form.val("edit", {
                "id": data.id
                , "filename": data.filename // "name": "value"
            });
            // 初始化标签：动态渲染内容，然后渲染标签
            var getTpl = editTagList.innerHTML
                , view = document.getElementById('tagContainer');
            laytpl(getTpl).render(data, function (html) {
                view.innerHTML = html;
            });
            tag.render('tags');

            // 开启弹窗
            layer.open({
                type: 1,
                title: '编辑',
                skin: 'layui-layer-rim', //加上边框
                area: '800px',
                content: $('#edit') //这里content是一个DOM，注意：最好该元素要存放在body最外层，否则可能被其它的相对元素所影响
            });
        }
    });
    //监听行双击事件
    table.on('rowDouble(test)', function (obj) {
        var data = obj.data; //获得当前行数据
        pkm.open(data.id);
    });

    // 查询表单
    form.on('submit(search)', function (data) {
        var params = data.field.search.split(" ");
        var tags = params[0];
        var filename = params[1] ? params[1] : "";
        tableIns.reload({
            where: {
                tags: tags,
                filename: filename
            }
        });
        return false;
    });

    form.on('submit(edit)', function (data) {
        var id = data.field.id;
        var filename = data.field.filename;
        var tags = tag.data('tags');

        pkm.update(id, filename, tags, function (res) {
            layer.closeAll();
            layer.msg('操作成功', { icon: 1 });
            $("#search").click();
        });
        // 编辑按钮提交
        return false;
    });

    // 初始化悬浮球，并添加点击事件
    dropball.init('#neko', '0px', '50%', function () {
        $("#addFile")[0].reset();
        tag.render('addFileTags');
        $("#addUrl")[0].reset();
        tag.render('addUrlTags');
        $("#copyFile")[0].reset();
        tag.render('copyFileTags');

        layer.open({
            type: 1,
            title: '新增元素',
            skin: 'layui-layer-rim', //加上边框
            area: '800px',
            maxmin: true,
            content: $('#add') //这里content是一个DOM，注意：最好该元素要存放在body最外层，否则可能被其它的相对元素所影响
        });
    });
    // 创建文件
    form.on('submit(addFile)', function (data) {
        var filename = data.field.filename;
        var tagStr = tag.data('addFileTags').join('.');

        pkm.create(filename, tagStr, function (res) {
            layer.closeAll();
            layer.msg('操作成功', { icon: 1 });
            $("#search").click();
        });
        // 编辑按钮提交
        return false;
    });
    // 添加url
    form.on('submit(addUrl)', function (data) {
        var url = data.field.url;
        var filename = data.field.filename;
        var tagStr = tag.data('addUrlTags').join('.');

        pkm.url(filename, url, tagStr, function (res) {
            layer.closeAll();
            layer.msg('操作成功', { icon: 1 });
            $("#search").click();
        });
        // 编辑按钮提交
        return false;
    });
    // 复制文件
    form.on('submit(copyFile)', function (data) {
        var path = data.field.path;
        // 通过 shift+右键 复制的路径带有引号，需要去掉
        path = path.replaceAll("\"", "");
        var tagStr = tag.data('copyFileTags').join('.');

        pkm.copy(path, tagStr, function (res) {
            layer.closeAll();
            layer.msg('操作成功', { icon: 1 });
            $("#search").click();
        });
        // 编辑按钮提交
        return false;
    });

    //	// ajax demo
    $('#jstree_demo_div')
    // 点击节点进行搜索
        .on("activate_node.jstree", function (e, data) {
            var array = [];
            array.unshift(data.node.text);
            data.node.parents.forEach(function(item, index){
                var text = data.instance.get_node(item).text;
                if (text) {
                    array.unshift(text);
                }
            })
            // array.shift(); // 这个看情况，如果不要根节点，这个就干掉好了
            form.val("search", {
                "search": array.join('.'), "filename": data.filename // "name": "value"
            });
            $('#search').click();
        })
        // 进行修改时，展示小黄点 对应的事件说明：https://www.jstree.com.cn/api.html#/?q=.jstree%20Event&f=create_node.jstree
        .on("create_node.jstree rename_node.jstree delete_node.jstree move_node.jstree copy_node.jstree cut.jstree copy.jstree paste.jstree", function(e, data){
            $("#saveTree").children("span").css("display", "inherit");
            $("#saveTree").attr('disabled', false);
        })
        // 数据加载完成后，展开所有节点
        .on("ready.jstree", function(e, data){
            $('#jstree_demo_div').jstree(true).open_all()
        })
        .jstree({
            // 变更事件，拖拽功能，搜索功能（需要配输入框使用，右键按钮，节点图标设置
            'plugins': ['changed', 'dnd', 'search', 'contextmenu', 'types'],
            'types': {
                'default': {
                    'icon': 'layui-icon layui-icon-note'
                }
            },
            'core': {
                'check_callback': true,
                'data': function(obj, callback) {
                    var value;
                    // 这个方法，改成同步的。
                    pkm.currentTree(function(res){
                        value = res;
                    });
                    callback.call(this, JSON.parse(value.result));
                }
            }
        });
    // 树搜索功能
    var to = false;
    $('#tree-search').keyup(function () {
        if (to) { clearTimeout(to); }
        to = setTimeout(function () {
            var v = $('#tree-search').val();
            $('#jstree_demo_div').jstree(true).search(v, true, true);
        }, 250);
    });

    // 树操作功能按钮
    $("#saveTree").attr('disabled',true);
    $('#saveTree').click(function() {
        $('#jstree_demo_div').jstree(true).open_all()
        var content = $('#jstree_demo_div').jstree(true).get_json('#',{
            no_state: true,
            no_data: true,
            no_li_attr: true,
            no_a_attr: true,
            flat: false
        });
        pkm.saveTree(JSON.stringify(content), function(res) {
            $("#saveTree").children("span").css("display", "none");
            $("#saveTree").attr('disabled', true);
            $('#jstree_demo_div').jstree(true).refresh();
        });
    });
    $("#refreshTree").click(function() {
        $("#saveTree").children("span").css("display", "none");
        $("#saveTree").attr('disabled', true);
        $('#jstree_demo_div').jstree(true).refresh();
    })

    // TODO 后续实现根据滑块调整tree版本的功能
    var sliderIns = slider.render({
        elem: '#slideTest'
        ,min: 1 //最小值
        ,max: 10 //最大值
        ,showstep: true
        ,change: function(value) {
            console.log(value);
        }
    });
});