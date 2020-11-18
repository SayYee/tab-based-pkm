Vue.prototype.$echarts = echarts;

// 禁用右键菜单
document.oncontextmenu=function (ev){
    return false;
};

var vm = new Vue({
    el: '#app',
    data: {
        split: {
            horizontal: 0.3,
            vertical : 0.5
        },
        uploadFilePath: null,
        search: {
            filename: null,
            tags: "RECENT_MODIFIED"
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
        loading: true,
        myChart: null,
        tagMenu: {
            visible: false,
            posX: 0,
            posY: 0,
            locator: null,
            tag: null,
            newTag: null,
            modal: false,
            loading: true
        }
    },
    mounted() {
        this.drawTagMap();
    },
    methods: {
        refresh: function() {
            vm.queryData();
            vm.loadTagMap();
        },
        uploadByPath: function() {
            axios.get('/copy', {
                params: {
                    path: this.uploadFilePath
                }
            })
            .then(function(response) {
                var data = response.data;
                if (data.code === 1) {
                    vm.uploadFilePath = null;
                    vm.$Message.success('Success');
                    vm.refresh();
                } else {
                    vm.$Message.error(data.message);
                }
            })
            .catch(function(error) {
                vm.$Message.error("error");
                console.log(error);
            })
        },
        uploadSuccess: function(response) {
            if (response.code === 1) {
                this.$Message.success('Success');
                this.search.filename = null;
                this.search.tags = 'RECENT_MODIFIED';
                vm.refresh();
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
                vm.refresh();
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
                vm.refresh();
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
        },
        drawTagMap() {
            this.myChart= this.$echarts.init(document.getElementById("container"));
            this.myChart.on('contextmenu', {dataType : 'node'}, function(params) {
                vm.handleTagMenuCancel();
                vm.tagMenu.tag = params.name;
                const clientX = params.event.event.clientX;
                const clientY = params.event.event.clientY;
                vm.handleContextmenu(clientX, clientY);
            })
            this.loadTagMap();
        },
        loadTagMap() {
            axios.get('/tagMap')
            .then(function(response) {
                var data = response.data;
                vm.myChart.hideLoading();
                var graph = echarts.dataTool.gexf.parse(data);

                graph.nodes.forEach(function (node) {
                    node.itemStyle = null;
                    node.value = node.symbolSize;
                    node.symbolSize *= 5;
                    node.label = {
                        normal: {
                            show: true
                        }
                    };
                });
                option = {
                    title: {
                        text: 'tag map',
                        subtext: 'Circular layout',
                        top: 'bottom',
                        left: 'right'
                    },
                    tooltip: {},
                    toolbox: {
                        feature: {
                            myTool1: {
                                show: true,
                                title: "刷新",
                                icon: "image://styles/imgs/refresh.svg",
                                onclick: function() {
                                    vm.loadTagMap();
                                }
                            }
                        }
                    },
                    animationDurationUpdate: 1500,
                    animationEasingUpdate: 'quinticInOut',
                    series: [
                        {
                            name: 'tag map',
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

                vm.myChart.setOption(option);
            })
            .catch(function(error) {
                console.log(error);
            });
        },
        createLocator() {
               // 获取Dropdown
            const contextmenu = this.$refs.contextMenu
            // 创建locator
            const locator = document.createElement('div')
            locator.style.cssText = `position:fixed;left:${this.tagMenu.posX}px;top:${this.tagMenu.posY}px`
            document.body.appendChild(locator)
            // 将locator绑定到Dropdown的reference上
            contextmenu.$refs.reference = locator
            this.tagMenu.locator = locator
        },
        removeLocator () {
            if (this.tagMenu.locator) document.body.removeChild(this.tagMenu.locator)
            this.tagMenu.locator = null
        },
        handleContextmenu (clientX, clientY) {
            if (this.tagMenu.posX !== clientX) this.tagMenu.posX = clientX
            if (this.tagMenu.posY !== clientY) this.tagMenu.posY = clientY
            if (this.trigger !== 'custom') {
                this.createLocator()
                this.tagMenu.visible = true
            }
        },
        handleTagMenuCancel: function() {
            this.tagMenu.visible = false;
            this.removeLocator();
        },
        handleTagMenuRename: function() {
            this.tagMenu.newTag = null;
            this.tagMenu.modal = true;
        },
        handleTagRename: function() {
            axios.put('/renameTag',{
                tag: vm.tagMenu.tag,
                newTag: vm.tagMenu.newTag
            })
            .then(function(response) {
                var data = response.data;
                if (data.code === 1) {
                    vm.$Message.success('Success');
                    vm.tagMenu.modal=false;
                } else {
                    vm.$Message.error(data.message);
                }
                vm.refresh();
            })
            .catch(function(error) {
                console.log(error);
            })
        },
        handleTagMenuDelete: function() {
            axios.delete('/deleteTag/' + vm.tagMenu.tag)
            .then(function(response) {
                var data = response.data;
                if (data.code === 1) {
                    vm.$Message.success('Success');
                } else {
                    vm.$Message.error(data.message);
                }
                vm.refresh();
            })
            .catch(function(error) {
                console.log(error);
            })
        }
    }
})