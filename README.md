# HaE - Highlighter and Extractor

根据自身情况作了一些修改

## config
配置文件默认读取的是burpsuite.jar文件所在路径的PluginConfig文件夹内

## processHttpMessage
每条请求会做一个正则判断，正则匹配会在注释里添加Rules Type的类型，防止Rules Type内存在相同name的Rule。

## 正则
Rule配置时存在Engine属性，分为nfa和dfa，如果是nfa使用的是jregex，只要不是nfa，则使用的是dk.brics.automaton.RegExp。
另外删除了正则内一定存在括号的要求。