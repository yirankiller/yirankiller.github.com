---
layout: post
description: Mysql中文乱码 默认编码设置
title: Linux安装Mysql 中文乱码
keywords: Mysql Linux中文乱码 默认编码设置 utf8
category : gibberish
tagline: "Focus on web develop"
tags : [Mysql,CharacterSet]
---
{% include JB/setup %}

##Mysql 中文乱码
安装前可以先登录Mysql查看一下当前数据库的编码: `show variables like 'character%'`

    +--------------------------+------------------------------+
    | Variable_name            | Value                        |
    +--------------------------+------------------------------+
    | character_set_client     | latin1                       |
    | character_set_connection | latin1                       |
    | character_set_database   | latin1                       |
    | character_set_filesystem | binary                       |
    | character_set_results    | latin1                       |
    | character_set_server     | latin1                       |
    | character_set_system     | utf8                         |
    | character_sets_dir       | /usr/share/mysql/charsets/   |
    +--------------------------+------------------------------+

在网上查资料后发现要修改 `/etc/mysql/my.cnf` 文件

    [client] 下添加
    default-character-set=utf8
    [mysqld_safe] 下添加
    default-character-set=utf8
    [mysql] 下添加
    default-character-set=utf8
    再找到[mysqld] 添加
    character-set-server=utf8 或者 default-character-set=utf8
    我自己试验在[mysqld]下用default-character-set=utf8 重启Mysql会失败，所以我用character-set-server=utf8能修改成功

注意是`utf8` 而不是utf-8.
修改好后，重新启动Mysql `/etc/init.d/mysql restart`.再查询一下 `show variables like 'character%'`

    +--------------------------+----------------------------+
    | Variable_name            | Value                      |
    +--------------------------+----------------------------+
    | character_set_client     | utf8                       |
    | character_set_connection | utf8                       |
    | character_set_database   | utf8                       |
    | character_set_filesystem | binary                     |
    | character_set_results    | utf8                       |
    | character_set_server     | utf8                       |
    | character_set_system     | utf8                       |
    | character_sets_dir       | /usr/share/mysql/charsets/ |
    +--------------------------+----------------------------+

确认修改成功.但是请注意
如果你在之前已经创建数据库,请删除再新建一个 <br />
`drop database xxx;`
`create database xxx;` 如果数据重要 请先备份



