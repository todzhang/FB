# README

## 概述

为了更好理解equation group的fuzzbunch，特别将相关代码进行反编译，简化其模块依赖。
目前完成了dander spritz的Java GUI反编译和DSz的Python代码反编译。

为了更好的理解其核心，精简了fuzzbunch的模块，构造了一个最小执行环境。
这个环境主要是完成C2的功能，包括Server的运行，Payload的生成。

由于Equation比较复杂，这里的分析肯定有不少错误，需要进一步分析。


## 使用技巧

### Java调试

执行ddb.start.Start的main函数即可


### python调试

在Terminal的命令行执行pdb，就会进入到python的调试模式。
这时可以访问 dsz，_dsz模块，以及_dsz.dszObj.
完成调试的时候，只要执行c，就会退出调试。


## TODO任务

1. DszLp.exe, DszLpCore.exe的逆向分析
1. Dsz_Implant_Pc.dll的逆向分析
1. Resources\Dsz\Modules\Files-dsz\i386-winnt-vc9\release下的dll逆向分析
1. 其它模块与Dsz的交互逻辑
1. 隧道的建立，维护和使用
