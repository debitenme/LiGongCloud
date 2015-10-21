# LiGongCloud

当年的毕业设计，保存下来，是个回忆。</br>
也是我程序猿路上第一个独立完成的项目，沈阳理工大学网盘系统</br>
这个项目上学的时候黑天白天的写代码，不会就自己查资料，</br>
一个多月，真的让我学到了太多太多的东西。</br>
原来的代码没有用maven，为了方便，改了一下原来的结构，</br>
变成了多项目结构的maven工程</br>

记录一下运行的过程，希望以后想起来大学毕设的时候，能够不忘记</br>
系统分管理员登录和普通用户登录</br>
管理员登录账号：admin</br>
管理员登录密码：admin</br>
管理员登录的账号密码是在程序中写的，哎。。要不怎么说那时候年轻呢！</br>
在mysql数据库中创建一个名为networkdisk的数据库</br>
将spring-common配置文件中的update改成create，</br>
这样，就能够在networkdisk数据库中自动建表了，</br>
然后在user表中，编写一个管理员的账号，关键就是这个管理员的ID，</br>
要和disk表中的userID对应上，</br>
对了，然后就是在disk表中，编写管理员的网盘信息，</br>
关键的说过了，就是disk表中的userID要和user表中的id对应上。</br>
做完这些，把spring-common配置中的update改回来，</br>
这样管理员就能正常登陆了。</br>
在浏览器中，用管理员账号注册普通用户账号，</br>
这样就有普通用户了</br>
只有建立管理员时是需要弄弄数据库的，</br>
建立普通用户就直接用管理员账号了，是不用配置数据库的</br>
要不说当时年轻，知识少呢，现在想想......可以更好的</br>
![image](https://github.com/hejiawang/LiGongCloud/raw/master/ligongcloudImage/ligongcloud-login.png)
![image](https://github.com/hejiawang/LiGongCloud/raw/master/ligongcloudImage/main.png)
![image](https://github.com/hejiawang/LiGongCloud/raw/master/ligongcloudImage/main.png)
