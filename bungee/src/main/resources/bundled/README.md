# 使用文档

### 注意
所有命令权限仅在 BC 端可用，即 Bukkit 端的 OP 并不列入 BC 端的 ADMIN 权限组内。

### 命令列表
```shell
# 重载配置
/${command}:${aliasCommand} reload
```

```shell
# 显示定时任务列表以查看 id（子服控制台不可用）
/${command}:${aliasCommand} job list
```

```shell
# 移除所有定时任务（子服控制台不可用）
/${command}:${aliasCommand} job clear
```

```shell
# 移除指定 id 的定时任务（子服控制台不可用）
/${command}:${aliasCommand} job remove <id>
```

```shell
# 主要命令
/${command}:${aliasCommand} commit <crontab> <target> <action> {arguments}
```

> ### 参数说明
> #### 操作（action）的可选值为：
> - ##### `tip` 发送提示（短消息）
>   ##### `消息内容` arguments
> 
> - ##### `notice` 发送通告（长消息）
>   ##### `通告文本文件的名称（须包含文件名称后缀，如： '.txt'，'.yml' 等）` arguments
> 
> - ##### `script` 执行脚本（由事件驱动三方脚本引擎，即只发送要求执行脚本的事件） 
>   ##### `脚本名称` arguments
> #### 

### 配置文件（示例）
```yaml
# config.yml
# 启用调试环境的日志输出
debug: true

# 定时计划（由第三方库 Quartz Jobs 提供支持）
# cron 表达式在线生成工具：
#   https://cron.qqe2.com/
# 本插件已提供的定时计划：
#   now: 即时执行
crontab:
  # 测试用定时计划
  # 每 5 秒执行一次
  test: '0/5 * * * * ?'

# 目标服务器组（优先从服务器组内进行匹配并返回，其次才是匹配单个服务器的名称）
# 本插件已提供的目标服务器组：
#   all: 所有服务器
target:
  # 测试用目标服务器
  test:
  - 'test'
  - 'test1'
  - 'test2'

# 自定义占位符（用于替换待发送的文本消息中的指定内容）
# 占位符使用格式：${example_name}
# 本插件已提供的占位符：
#   sender: 发送者名称
#   target: 目标服务器名称（多个名称之间使用 ',' 隔开）
#   target_first: 排在首位的目标服务器名称
#   target_last: 排在末位的目标服务器名称
#   time: 发送消息的时间
placeholder:
  # 设置占位符 time 的时间文本格式
  time: 'yyyy-MM-dd HH:mm:ss'
  # 自定义占位符
  custom:
    # 测试用占位符
    test: '这是一个测试用占位符'
    # 测试用占位符（根据环境动态选择）
    test_env:
      # 【必选】当发送者为服务器时
      server: '服务器'
      # 【必选】当发送者为玩家时
      player: '玩家'
      # 【可选】当发送者为管理员（前提是发送者为玩家）时
      admin: '管理员'
```
