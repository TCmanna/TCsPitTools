TCmanna's Pit Tools
===

## 功能实现

1. 游戏内显示事件列表
2. 快速获取房间内玩家持有gold排行
3. 添加神秘物品 T3附魔颜色需求 与 唯一码 预览
4. 可查询任意玩家穿戴装备和手持物品 用于Nick玩家

## 下载及使用方法

**安装**  
1. 在此处 https://github.com/TCmanna/TCsPitTools/releases 下载最新版本jar文件
2. 确保你的Minecraft版本为1.8.9并且安装了Forge
3. 将jar文件放入同材质包根目录下的mods文件夹中
4. 启动你的游戏

**功能使用**  
* 使用`/pittools`打开配置GUI
* 使用`/check <玩家名>`快速查询当前装备和手持物品
* 使用`/getgold`激活自动查询玩家gold 再次使用则中断执行并输出已有结果

## 特点

* 使用国内仓库同步事件列表 进行事件查询 有效解决brookeafk API无法连接的问题  
  特别感谢 https://github.com/BrookeAFK/brookeafk-api  
  国内仓库地址 https://gitee.com/tcmanna/brookeafk-api

* 全部功能实现为合法操作 理论上不会有违规行为

## 注意事项

查询gold建议使用默认值 延迟过小会导致hyp发包太快的检测并t出大厅  
如网络延迟过大请视情况调大查询延迟  
  
如果事件列表没有加载成功尝试重启游戏 多次未成功请发送issues我会尽快修复  
  
如遇BUG或者崩溃的情况请提交issues 带上崩溃报告 和使用的mod版本  

---
本mod开源允许二次分发 修改 无需任何申请
