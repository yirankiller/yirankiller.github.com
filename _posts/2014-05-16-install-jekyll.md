---
layout: post
description: 本地安装Jekyll
title: Jekyll本地环境
keywords: 本地安装Jekyll
category : jekyll
tagline: "Focus on web develop"
tags : [Jekyll]
---
{% include JB/setup %}

##本地安装Jekyll  [Ubuntu 系统]

首先安装Ruby,现在Ruby版本有1.9.1

`sudo apt-get install ruby1.9.1-dev` 注意要安装ruby1.9.1-dev 而不是ruby1.9.1

安装Jekyll

`sudo gem install jekyll`

安装Node.js

`sudo apt-get install nodejs` 如果不安装Node.js,启动Jekyll会报错

进Git Pages主目录

`jekyll serve`  启动Jekyll 默认地址是 http://localhost:4000

-w 或者 -watch是启动监听,相当于热部署.



