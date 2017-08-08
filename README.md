# zkconfigserver说明文档 #

## 登陆
    地址 :
    	环境IP ：10012
    用户名 :
    	admin
    密码 :
    	123456

## 关于 connected (服务连接状态)
	用途 : 观察各服务的连接与运行状态
	注册方式 : 各服务在当前环境zookeeper中按照约定的规则注册 “永久节点”
	注册路径 :
		connected
			|--> 需要注册的服务节点
						|--> 服务节点1
						|--> 服务节点2
						......

		例：
		connected
			|--> authoService
					|--> authoService_1
					|--> authoService_2
				    ......

		说明:
			服务创建的节点必须是 "永久节点"
			服务节点下的子节点名称唯一，可以自己维护
			例如:
				需要在     /dm2/connected/authoService 节点下创建一个被用于服务发现的永久节点 authoService_1
				只需要定义 /dm2/connected/authoService/authoService
				会出现    /dm2/connected/authoService/authoService_1 这样的结果

			code:
				client.create().withMode(CreateMode.PERSISTENT).forPath(path, new byte[]{});
				client.create().withMode(CreateMode.PERSISTENT).forPath(path, data.getBytes("UTF-8"));

    注册数据 :
        类型 : string
	    格式 : json
	    编码 : UTF-8
		结构 : {"Ip":"","Port":"","State":""}
		    例：
		    	{"Ip":"10.10.10.51","Port":"9099","State":"Runing"}

		    说明:
			    Ip & Port  : 对外提供服务的 ip 和 port
			    State      : 枚举 int或string 都可以 请从以下枚举中挑选

			    	// 准备阶段
                    Preparing,
                    // 等待其他服务端的进入确认
                    WaitLoadingEnsure,
                    // 加载阶段
                    Loading,
                    // 运行中
                    Runing,
                    // 进行负载添加
                    InsertLoading,
                    // 进行负载部分解除
                    DeleteLoading,
                    // 退出清理阶段
                    ClearPreExit

## 关于infrastructure (基础服务配置)
	用途 : 用于读取基础服务配置
	说明 : 在服务启动后,一些基础服务配置已经随docker环境配置好了.如果需要可自行进入zkconfigservice修改
	结构 : 服务节点下的 子节点名为配置的key 节点的数据为配置的value

	例:
		infrastructure
				|--> mysql
					   |--> username --> admin
					   |--> password --> rootroot
					   |--> url	     --> 10.10.10.51:3306 (看作 mysql url配置为10.10.10.61:3306  类型为string)

## 关于serviceConfig (服务配置)
	用途 : 用于读取各个服务自己的配置
	说明 : 用户不能随意添加或删除配置,只能修改已有配置的value
	结构 : 服务节点下的 子节点名为配置的key 节点的数据为配置的value

	例:
		serviceConfig
			  |--> masterService
			  			|--> MaxCommitTime --> 6000  (看作 master 服务下 有一个配置 MaxCommitTime=6000  类型为string)



## 关于zkconfigserver
	细节说明 :
		这个服务主要通过/resources 下的 zookeeper-config.properties 和 zookeeper-node.properties
	两个配置文件完成zookeeper中节点结构与默认数据的创建.
		如果规定的节点结构被破坏可以重新启动该服务还原,此时需配置一个值为true的环境变量INITIAL_FLAG.
		如果该服务在启动中出现错误会删除已创建的节点结构,直到下一次服务被正确启动.
		如果需要测试新添加的配置 可以直接修改zookeeper-config.properties并重新启动即可.
