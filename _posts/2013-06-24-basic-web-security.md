---
layout: post
title: Web 安全问题总结
description: HttpOnly Secure Cookie Cross-site Request Forgery Session Fixation Prevent site from framing the page
keywords: HttpOnly,Secure Cookie,Cross-site Request Forgery,Session Fixation,Prevent site from framing the page
category : web
tagline: "Focus on web develop"
tags : [java,web,security,javascript]
---
{% include JB/setup %}

##Security Parameter

###HttpOnly
前段时间，web项目提出了一些安全问题，第一个就是cookie的HttpOnly.
先来了解以下cookie的格式：

	Set-Cookie: <name>=<value>[; <Max-Age>=<age>]
	[; expires=<date>][; domain=<domain_name>]
	[; path=<some_path>][; secure][; HttpOnly]

主要就是为了来防止XSS攻击，如果浏览器支持这个属性的话，在客户端你将不能通过像javascript这样的脚本得到这个cookie <br />
下面是我找到的解决httponly的办法，很详细。
Using Java to Set HttpOnly

Since Sun Java Enterprise Edition 6 (JEE 6), that adopted Java Servlet 3.0 technology, it's programmatically easy setting HttpOnly flag in a cookie.
In fact setHttpOnly and isHttpOnly methods are available in the Cookie interface, and also for session cookies (JSESSIONID):

{% highlight java %}
	Cookie cookie = getMyCookie("myCookieName");
	cookie.setHttpOnly(true);
{% endhighlight %}
		

Moreover since JEE 6 it's also declaratively easy setting HttpOnly flag in session cookie, by applying the following configuration in the deployment descriptor `WEB-INF/web.xml`:
{% highlight xml %}
	<session-config>
		<cookie-config>
			<http-only>true</http-only>
		</cookie-config>
	<session-config>
{% endhighlight %}
For Java Enterprise Edition versions prior to JEE 6 a common workaround is to overwrite the SET-COOKIE http response header with a session cookie value that explicitly appends the HttpOnly flag:
{% highlight java %}
	String sessionid = request.getSession().getId();
	//be careful overwriting: JSESSIONID may have been set with other flags
	response.setHeader("SET-COOKIE", "JSESSIONID=" + sessionid + "; HttpOnly");
{% endhighlight %}
Some web application servers, that implements JEE 5, and servlet container that implements Java Servlet 2.5 (part of the JEE 5), also allow creating HttpOnly session cookies:
Tomcat 6 In `conf/context.xml` set the context tag's attribute useHttpOnly as follow:
{% highlight xml %}
	<?xml version="1.0" encoding="UTF-8"?>
	<Context path="/myWebApplicationPath" useHttpOnly="true">
{% endhighlight %}
JBoss 5.0.1 and JBOSS EAP 5.0.1 In `\server\<myJBossServerInstance>\deploy\jbossweb.sar\context.xml` set the SessionCookie tag 8 as follow:
{% highlight xml %}
	<?xml version="1.0" encoding="UTF-8"?>
	<Context cookies="true" crossContext="true">
    	<SessionCookie httpOnly="true" />
{% endhighlight %}    	

本文摘自[OWASP HttpOnly](https://www.owasp.org/index.php/HTTPOnly)

###Secure Cookie

A secure cookie has the secure attribute enabled and is only used via HTTPS, ensuring that the cookie is always encrypted when transmitting from client to server. This makes the cookie less likely to be exposed to cookie theft via eavesdropping. [Wiki](http://en.wikipedia.org/wiki/HTTP_cookie#Secure_cookie)

secure表示创建的cookie只能在HTTPS协议下被浏览器传递到服务器端进行会话验证如果是HTTP协议则不会传递该cookie
  服务器端的Cookie对象是java中的对象，请不要和浏览器端的cookie文件混淆了。服务器端的Cookie对象是方便java程序员包装 一个浏览器端的cookie文件。一但包装好，就放到response对象中，在转换成http头文件。在传递到浏览器端。这时就会在浏览器的临时文件中 创建一个cookie文件。
  但我们再次访问网页时，才查看浏览器端的cookie文件中的secure值，如果是true，但是http连接。这个cookie就不会传到服务器端。当然这个过程对浏览器是透明的。其他人是不会知道的。
在java代码中可以在Cookie对象中直接设置
{% highlight java %}
	Cookie cookie = getMyCookie("myCookieName");
	cookie.setSecure(true);
{% endhighlight %}
也可以在HttpResponse的中设置
{% highlight java %}
	String sessionid = request.getSession().getId();
	//be careful overwriting: JSESSIONID may have been set with other flags
	response.setHeader("SET-COOKIE", "JSESSIONID=" + sessionid + "; HttpOnly;secure");
{% endhighlight %}

在Servlet 3.0以后，可以在`WEB-INF/web.xml`中配置
{% highlight xml %}
	<session-config>
		<cookie-config>
			<secure>true</secure>
		</cookie-config>
	</session-config>
{% endhighlight %}
在Tomcat6.x 或者 jboss5.x 容器的 `context.xml`中，我们也可以用修改HttpOnly的方式设置secure
{% highlight xml %}
	<?xml version="1.0" encoding="UTF-8"?>
	<Context cookies="true" crossContext="true">
    	<SessionCookie secure="true"/>
{% endhighlight %}

###Prevent site from framing the page
防止页面被嵌入iframe放到其他页面，我们可以在HttpResponse的Header中加入：
{% highlight java %}
	/*
	X-Frame-Options:
 		DENY         prevent any site from framing the page.
		SAMEORIGIN   allows only sites from the same domain to frame the page.
	*/
	response.setHeader("X-Frame-Options","SAMEORIGIN");
{% endhighlight %}

***

##Cross-site Request Forgery

Cross-site request forgery：跨站请求伪造，也被称成为“one click attack”或者session riding，通常缩写为CSRF或者XSRF，是一种对网站的恶意利用。听起来像跨站脚本（XSS），但它与XSS不同。XSS利用站点内的信任用户，而CSRF则通过伪装来自受信任用户的请求来利用受信任的网站。与XSS攻击相比，CSRF攻击往往不大流行（因此对其进行防范的资源也相当稀少）和难以防范，所以被认为比XSS更具危险性。

在每一个页面的form中，都加入一个hidden，我们用下面代码的formtoken作为这个hidden的值
{% highlight java %}
	String secret = "1a2b3c4d5e6f7g1a2b3c4d5e6f7g1a2b3c4d5e6f7g1a2b3c4d5e6f7g1a2b3c4d5e6f7g";
	String sessionId = session.getId();
	String servletpath = request.getServletPath();
	byte[] plaintext = (sessionId + secret).getBytes("UTF-8");
	String formtoken = new sun.misc.BASE64Encoder().encode(java.security.MessageDigest.getInstance("SHA1").digest(plaintext));
{% endhighlight %}   
在我们每次接收request之前，都要先验证formtoken的值，用同样的secret和算法。这样可以确保请求不是恶意伪造的

##Session Fixation

首先我来模拟一个场景让大家更了解这个问题
假如有一天你收到一封这样的邮件
Hi Ethan!
you had new message on you XXX account,please login and check that.
[Login here.](http://www.justkiller.info/loginAccount;JSESSIONID=0285E5822849FE1417600E7B62B16511)

神经大条的人可能就会直接登录,没有任何异常.
但实际上在这封邮件的链接上自带了JSESSIONID,当你用Attacker给你的JSESSIONID登录了系统，而系统又没有在登录成功之后重新生成新的Session Id.
这时候Attacker在不需要任何用户名和密码的情况下就已经可以查看你的任何信息了.如果有不明白，可以先了解一下JSESSIONID.
所以强烈建议任何系统在登录成功后一定要重新生成新的Session Id.在Java中就是调用 `session.invalidate();` 方法.
最后用Jboss 的同学请注意,Jboss在某些版本中 `session.invalidate()` 方法是不会重新生成Session Id 的.
[session.getSession(true) does not create new sessionID after invalidation](https://issues.jboss.org/browse/JBAS-4436)
对于Jboss这种情况,需要修改server.xml中emptySessionPath=false,默认应该是true.


