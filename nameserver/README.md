# nameserver cluster模式

## 采用协议：

gossip

## 实现：

1. nameserver启动时读取默认路径下的namserverAddress.conf配置，用户可在配置中任意配置3~5个nameserver种子节点地址。
2. 向这些地址定时发送namserver上线信息（udp协议），直到回复ack
3. namserver接收彼此的上线信息，如果接收到了nameserver上线信息则向自身配置的几个nameserver节点发送上线信息，接收到broker注册信息也向配置的节点发送broker注册信息，更新broker表
4. 对broker使用定时任务监测，一段时间未更新则剔除
5. 如果producer拉取broker地址信息先在本机进行查找，如果没找到再向配置文件中的目标server发起查找请求

