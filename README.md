# xmile-getData
使用http Client4.4模拟请求
并且对验证码处理
验证码处理涉及得比较多图像处理
在噪点处理上 使用中值滤波
切割验证码 平均切割
二值化验证码 黑白二值
在登陆密码上面 使用了 MD5加密 并且是大写


重点问题：
1.json返回的数据中文乱码  通过			String content = EntityUtils.toString(r.getEntity(), "UTF-8"); 设置编码格式解决
2.sessionID问题 在cookie中添加sessionID才能登陆成功
3.验证码处理达到80%的准确率， 后面需要加强--未解决
4.一旦发起请求需要对响应码进行处理，登陆不成功需重新登陆--已解决（用自动登陆ID代替）
5.添加swing框架来获取用户的输入--还没done（已解决 但渲染层较慢 ）--已经替换日期渲染控件

# 最后修复本-FINAL VERSION
   每次统计都重新登陆获得http上下文 通过上下文进行url的访问
