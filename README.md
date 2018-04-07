# AndroidPlugin

这个是插件化开发的一个入门的教程与demo展示。

插件化原理介绍：

[插件化开发的入门](markdown/插件化开发的入门.md)


工程介绍：

* DexClassLoader：介绍对于未安装apk（插件）的怎么实现加载，工程里已经提供了测试的插件apk
* PathClassLoader：介绍对于已经安装的apk（插件）的怎么实现加载，这是宿主工程
   * PathClassLoaderPlugin：针对上面的PathClassLoader宿主工程，提供的插件工程
* 针对**插件化开发入门**这边文章的demo 
   * app：宿主工程
   * iplugin：接口定义（插件实现，宿主用于反射）
   * plugin：插件工程
