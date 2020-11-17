var vm = new Vue({
    el: '#app',
    data: {
        split: {
            horizontal: 0.3,
            vertical : 0.5
        },
        search: {
            filename: null,
            tags: null
        },
        columns: [
            {
                title: 'id',
                key: 'id',
                width: 80,
                align: 'center'
            },
            {
                title: '文件名称',
                key: 'filename',
                align: 'center'
            },
            {
                title: '相对路径',
                key: 'relativePath',
                align: 'center'
            },
            {
                title: '标签',
                key: 'tags',
                render: (h, params) => {
                    var arr = params.row.tags;
                    return h('ul', arr.map(function(item, index){
                        return h('Tag', item);
                    }))
                },
                align: 'center'
            },
            {
                title: '创建时间',
                key: 'createTime',
                width: 180,
                render: (h, params) => {
                    return h('i-time', {props: {time: params.row.createTime, type: 'datetime'}});
                },
                align: 'center'
            },
            {
                title: '最后打开时间',
                key: 'lastOpenTime',
                width: 180,
                render: (h, params) => {
                    return h('i-time', {props: {time: params.row.lastOpenTime, type: 'datetime'}});
                },
                align: 'center'
            }
        ],
        files: [{"id":1,"filename":"设计文档.md","relativePath":"\\2020-11\\设计文档.md","tags":["RECENT_MODIFIED","doing","todo"],"createTime":1605420837931,"lastOpenTime":1605420843068}],
        file: {id: null, filename: null, tags: []},
        tagToAdd: null,
        modal: false,
        loading: true
    },
    methods: {
        uploadSuccess: function(response) {
            if (response.code === 1) {
                this.$Message.success('Success');
            } else {
                this.$Message.error(response.message);
            }
        },
        queryData: function() {
            var filename = this.search.filename;
            var tags = this.search.tags;
            axios.get('/query', {
                params: {
                    filename: filename,
                    tags: tags
                }
            })
            .then(function(response) {
                var data = response.data;
                if (data.code === 1) {
                    vm.files = data.result;
                } else {
                    vm.$Message.error(data.message);
                }
            })
            .catch(function(error) {
                console.log(error);
            })
        },
        openFile: function(row) {
            var fileId = row.id;
            axios.get('/open/' + fileId)
            .then(function(response) {
                var data = response.data;
                if (data.code === 1) {
                    vm.$Message.success('Success');
                } else {
                    vm.$Message.error(data.message);
                }
            })
            .catch(function(error) {
                console.log(error);
            })
        },
        handleContextMenu (row) {
            this.file = row;
        },
        handleContextMenuEdit () {
            this.tagToAdd = null;
            this.modal=true;
        },
        handleContextMenuDelete () {
            var fileId = vm.file.id;
            axios.delete('/delete/' + fileId)
            .then(function(response) {
                var data = response.data;
                if (data.code === 1) {
                    vm.$Message.success('Success');
                    vm.modal=false;
                } else {
                    vm.$Message.error(data.message);
                }
            })
            .catch(function(error) {
                console.log(error);
            })
        },
        updateFile () {
            axios.put('/update',{
                id: vm.file.id,
                newName: vm.file.filename,
                tags: vm.file.tags
            })
            .then(function(response) {
                var data = response.data;
                if (data.code === 1) {
                    vm.$Message.success('Success');
                    vm.modal=false;
                } else {
                    vm.$Message.error(data.message);
                }
            })
            .catch(function(error) {
                console.log(error);
            })
        },
        handleTagRemove (event, name) {
            const index = this.file.tags.indexOf(name);
            this.file.tags.splice(index, 1);
        },
        handleTagAdd() {
            this.file.tags.push(this.tagToAdd);
            this.tagToAdd = null;
        }
    }
})

var dom = document.getElementById("container");
var myChart = echarts.init(dom);
var app = {};
option = null;
myChart.showLoading();
axios.get('/tagMap')
.then(function(response) {
    var data = response.data;
    myChart.hideLoading();
    var graph = echarts.dataTool.gexf.parse(data);

    graph.nodes.forEach(function (node) {
        node.itemStyle = null;
        node.value = node.symbolSize;
        node.symbolSize /= 1.5;
        node.label = {
            normal: {
                show: node.symbolSize > 10
            }
        };
        node.category = node.attributes.modularity_class;
    });
    option = {
        title: {
            text: 'Les Miserables',
            subtext: 'Circular layout',
            top: 'bottom',
            left: 'right'
        },
        tooltip: {},
        animationDurationUpdate: 1500,
        animationEasingUpdate: 'quinticInOut',
        series: [
            {
                name: 'Les Miserables',
                type: 'graph',
                layout: 'circular',
                circular: {
                    rotateLabel: true
                },
                data: graph.nodes,
                links: graph.links,
                roam: true,
                label: {
                    position: 'right',
                    formatter: '{b}'
                },
                lineStyle: {
                    color: 'source',
                    curveness: 0.3
                }
            }
        ]
    };

    myChart.setOption(option);
})
.catch(function(error) {
    console.log(error);
});
if (option && typeof option === "object") {
    myChart.setOption(option, true);
}