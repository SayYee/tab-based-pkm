# tbp：基于标签的个人知识库管理系统



## 动机

作为开发人员，会接触到很多的新知识，需要存储起来，并且在需要的时候检索这些数据进行主题研究，这个过程的载体往往是各种笔记软。

在这个过程中，认知会不断发生变化，原有的体系结构需要进行调整变动，这要求作为载体的笔记能够很方便的调整结构。

现有的笔记软件，大多会提供基于层级目录的文件管理方式，但是这种方式并不灵活。如果一开始没有建立好合适的层级结构，很容易导致笔记内容东一块儿西一块儿，散落的到处都是，因为一个文件具体该怎么分类，也是个不断变动的过程。查找笔记也往往是基于内容进行检索，但是全文检索功能过于强大，会把很多无关紧要的信息拉取出来。

我个人在使用现有笔记软件的时候，随着笔记数量的增大，总会感觉不适应：结构变动不易，检索困难。当然这可能也是我个人的问题，不太会组织内容。

另外笔记软件为了提供强大的功能，往往是把数据以自己的格式存储起来，一旦离开这个软件，这些数据就很难解读出来。对笔记软件越深度的使用，对于这个软件的依赖就越高，最后就很难离开，甚至不能离开。作为程序员，把数据存储在别人手里，不能在感觉不适合的时候切换其他方案，心里还是会有些膈应。

因此我希望的软件满足这些功能：

- 层级结构易于变动，可以很轻松的进行调整，不用有太大的心理负担。

  文件可以按照多个维度进行分类，轻松进行调整。

- 目的性更强的检索功能，高性能。

  基于更加精炼的内容进行检索，用户容易搜索到自己想要的东西

- 所有数据本地存储。数据的同步功能交给其他软件实现。

- 所有资源归用户所有，即使离开这个软件，也最大程度的方便用户的后续使用。

  文件的一些操作，可以依赖本地的软件进行，比如对md文件的编辑，可以使用typora或者任何用户喜欢的软件。系统本身只负责元数据的维护，其他的功能交给更适合的软件完成。

- 提供一定程度的其他笔记软件的兼容性，方便用户将其他笔记资源纳入管理

  最方便的方式方法就是使用url资源定位，将对应笔记内容的url放入系统管理

  

在探索的过程中，我接触到了[dendron](https://github.com/dendronhq/dendron)这个项目。这个项目试图构建一个个人的知识库管理系统，这个项目基于vscode进行构建，使用层级标签作为核心概念，作者详细描述了自己的知识库探索过程，提及了知识库背后的一些理论，在阅读了他的博客之后，我决定以标签作为系统的核心元数据，为此需要提供基于标签的检索、修改标签、标签批处理功能。

与dendron的作者不同的是，我认为基于vscode太过局限，vscode更多是一个编辑器的功能，可以管理写作的内容，但是知识库的管理内容不应该被限制于文本，应该更加强大；标签之间应该是平等的，可以随意组合的，文件的名称也可以更加有意义一些，进行文件内容的补充说明。



因为是java技术栈，对于前端的东西接触有限，因此最终选择使用spring boot开发javaweb程序。但是为了提供更多的可能，同时也提供了命令行程序，为了能够同时基于命令行和web进行操作数据，将核心功能拆离出来，可以作为jar依赖，也可以独立作为服务启动，通过自定义的tcp协议进行交互。相对而言还是web页面使用较多一些。

在开发过程中，借鉴了zookeeper中的一些设计，包括命令序列化、数据全量增量存储、项目打包构建。



## 介绍



为本地文件额外维护标签元数据，可以通过标签组合、文件名称快速检索。

特点：

- 所有的数据都存储在本地，所有的文件都通过本地软件进行访问、编辑。
- 支持文件、文件夹、url的管理。
- 快速高效：所有元数据在启动时全部加载到内存当中，高速处理
- 可靠：基于增量+全量的元数据存储方式，操作生效便会被立刻持久化，增量的存储方式保证每次持久化操作在几毫秒内完成。
- 简洁：基于二进制存储元数据，每个文件的元数据在300字节左右



## 模块介绍

tbp-assembly：项目打包模块

tbp-cli：命令行客户端

tbp-common：model、常量类、操作类

tbp-core：核心jar

tbp-nio：tbp的tcp客户端、服务端实现

tbp-web：spring boot模块



## 打包



拉取代码，在项目根目录下执行

```
mvn clean package
```

命令会在tbp-assembly下的target中创建`tbp-1.0-SNAPSHOT-bin.tar.gz`文件。



## 运行

将打包阶段构建的压缩文件放置到合适路径下解压。

```
├─bin
│      tbpCli.cmd			# tcp client启动命令
│      tbpEnv.cmd			
│      tbpServer.cmd		# tcp server启动命令
│      tbpWeb.cmd			# web项目启动命令
│      
├─conf
│      application.yml		# web项目配置文件
│      log4j2.xml			# tcp模式下的服务端日志配置
│      tbp.cfg				# tcp模式下服务端配置
│      
├─lib						# tcp、cli依赖lib
│      gexf4j-1.0.0.jar
│      guava-18.0.jar
│      jansi-1.18.jar
│      jline-3.16.0.jar
│      jline-builtins-3.16.0.jar
│      jline-console-3.16.0.jar
│      jline-reader-3.16.0.jar
│      jline-style-3.16.0.jar
│      jline-terminal-3.16.0.jar
│      log4j-api-2.13.3.jar
│      log4j-core-2.13.3.jar
│      log4j-slf4j-impl-2.13.3.jar
│      picocli-4.5.2.jar
│      picocli-shell-jline3-4.5.2.jar
│      slf4j-api-1.7.25.jar
│      stax-api-1.0-2.jar
│      stax2-api-3.1.4.jar
│      tbp-cli-1.0-SNAPSHOT.jar
│      tbp-common-1.0-SNAPSHOT.jar
│      tbp-core-1.0-SNAPSHOT.jar
│      tbp-nio-1.0-SNAPSHOT.jar
│      woodstox-core-asl-4.4.1.jar
│      
└─web					# web独立jar
        tbp-web.jar
```



### web模式启动（建议）

#### 配置

修改`conf/application.yml`中的配置。一般只需要修改snap和store两个配置就可以

```yaml
tbp:
  # 元数据存储目录
  snap-dir: D:\\pkm\\snap
  # 文件存储目录
  store-dir: D:\\pkm\\store
```

#### 启动

进入bin目录，调用`tbpWeb.cmd`。mac用户可以执行`cat tbpWeb.cmd|sh`启动

#### 使用

访问<http://localhost:8080/static/index.html>，其中端口号是application.yml中配置的`server.port`参数



### tcp模式启动

tcp模式主要是为了cli、web同时操作而开发的，目前cli使用不多，没有做界面优化，使用起来没有web舒适，推荐还是使用web。



#### 配置

修改`conf/tbp.cfg`，主要还是snap、store属性修改

```properties
# 服务端监听端口
port=9000

# 文件存储路径
storeDir=D:\\pkm\\store
# 元数据存储路径
snapDir=D:\\pkm\\snap

# tree数据历史版本保留数量
treeRetainNum=10
```



修改`conf/application.yml`，只需要启用，并配置server监听端口就可以

```yaml
tbp:
  # nio配置。如果启用nio，需要先启动tbpServer.cmd
  nio:
    enable: true
    # tcp server 监听端口，需要和tbp.cfg中配置的端口相同
    port: 9000
```





#### 启动

启动tbpServer.cmd，启动tcp server

启动tbpWeb.cmd，启动web server

启动tbpCli.cmd，启动cli client



#### 使用

web：

访问<http://localhost:8080/static/index.html>，其中端口号是application.yml中配置的`server.port`参数

cli：

启动tbpCli.cmd时会自动打开cmd交互窗口。



## 使用建议（Web）

### chrome app

使用chrome app，把<http://localhost:8080/static/index.html>安装成本地应用，可以避免和其他web页面混合在一起



### 数据同步

系统的数据全部存储在snap、store目录中，只要将数据同步到其他设备上，就可以在其他设备上继续工作。

我个人使用的是 坚果云 进行数据同步，在win和mac上使用。