# CuiTrip
citric android source code 

#Todo 
1. 接入drag2 
2. 上一位同志的AsyncHttpResponseHandler很蛋疼 ，改http通讯模块，至少支持test
3. fresco
4. 统一 启动组建使用静态方法 activity.start(args)
5. actvitiyresult －>eventBus


#RongIm
* 周期	
	- 启动indexactivity 检测是否用户登录－>y/(请求融云登录)；n/(融云登录->y/登出)
	- 退出登录&&token失效退出登录 融云登出
* 创建聊天
 	- 旅行者创建订单成功后，创建聊天，传聊天id给订单
 	- 跳转到订单详情页面 如果:订单的聊天id为空｜订单聊天组信息(@1)异常（聊天组不包含自己｜聊天组人数不为2)->设置默认聊天页面_创建聊天_传聊天id给订单_替换默认聊天页面
* 融云异常
    - 经常出现的是 ipc not connected 30001 ;于是在 创建聊天和查看聊天组信息(@1) 错误的时候检测错误码并强制重新连接融云
* 发送消息
    - 发送消息成功后，使用老聊天接口发送给服务器老聊天记录。 考虑 保存hash的tartgetid和orderid表， 
* 聊天列表
    - 使用了订单列表api ，然后获取融云聊天数据，填充最后消息和未读消息数，并根据最后消息时间大到小排序，然并卵<---订单列表并未根据消息时间排序并且做了分页。

#版本
* MainApplication is_dev 属性
* BusinessHelper BASE_URL
* BusinessHelper API_VERSION 
* build.gradle (TripApp) version**

#代码混淆
	-keep class com.cuitrip.model.**{*;}		 
	-keep class com.cuitrip.business.**{*;}	
	


 