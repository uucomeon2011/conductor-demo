# 验证netflix 的conductor框架，conductor+mysql配置，验证流程的执行
## 1.创建conductor数据库
创建mysql数据库 conductor 并集成 mysql 配置 config.properties
```
db=mysql
jdbc.url=jdbc:mysql://localhost:3306/conductor?useSSL=false&useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&serverTimezone=UTC
jdbc.username=root
jdbc.password=123456
conductor.mysql.connection.pool.size.max=10
conductor.mysql.connection.pool.idle.min=3
```



## 2.启动conductor server 
## conductor server 启动
```
java -jar conductor-server-2.8.0-SNAPSHOT-all.jar config.properties
```
## 3.conductor ui 启动

生成Ui页面，ui依赖于nodejs,以及gulp的插件。
下载安装nodejs安装包，要求高于8以上版本。
全局安装gulp 命令：
```
$ npm install --global gulp
```
cd 进入项目conductor/ui 命令：
```
$ npm install --save-dev gulp
```
生成项目依赖的gulp。
执行命令：
```
gulp watch
```
等待构建成功，然后访问localhost:3000页面查看Conductor的管理界面。

## 4.task 定义dsl
POST /metadata/taskdefs
```
[
  {
    "name":"mytask1",
    "retryCount":3,
    "timeoutSeconds":1200,
    "inputKeys":["ai1","ai2"],
    "outputKeys":["ao1","ao2"],
    "timeoutPolicy":"TIME_OUT_WF",
    "retryLogic":"FIXED",
    "retryDelaySeconds":600,
    "responseTimeoutSeconds":600
  },
  {
    "name":"mytask2",
    "retryCount":3,
    "timeoutSeconds":1200,
    "inputKeys":["bi1","bi2"],
    "outputKeys":["bo1","bo2"],
    "timeoutPolicy":"TIME_OUT_WF",
    "retryLogic":"FIXED",
    "retryDelaySeconds":600,
    "responseTimeoutSeconds":600
  }
]
```
## 5.workflow 定义dsl
POST /metadata/workflow
```
  {
    "name":"myworkflow1",
    "description":"my workflow for test",
    "version":1,
    "tasks":[
      {
        "name":"mytask1",
        "taskReferenceName":"node1",
        "type":"SIMPLE",
        "inputParameters":{
          "ai1":"${workflow.input.wi1}",
          "ai2":"${workflow.input.wi2}"
        }
      },
      {
        "name":"mytask2",
        "taskReferenceName":"node2",
        "type":"SIMPLE",
        "inputParameters":{
          "bi1":"${node1.output.ao1}",
          "bi2":"${node1.output.ao2}"
        }
      }
    ],
    "outputParameters":{
      "ao1":"${node1.output.ao1}",
      "ao2":"${node1.output.ao2}",
      "bo1":"${node2.output.bo1}",
      "bo2":"${node2.output.bo2}"
    },
    "schemaVersion":2
  }
```
## 6.写task worker 执行任务
参考src源码 启动任务执行服务
## 7.创建流程实例，启动流程
/workflow/{name}
name:myworkflow1
body:{"wi1": "param1","wi2": "param2"}
## 7.通过ui界面查看执行结果

参考:
[conductor github](https://github.com/uucomeon2011/conductor)
[示例参考](https://blog.csdn.net/mpren/article/details/86495608)