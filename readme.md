## 项目名称
基于云平台的人工智能语音交互系统

## 项目介绍
使用 Android 和人工智能语音技术开发 APP

集成百度语音识别、语音合成、语音翻译等技术简单的完了一个使用语音直接操控手机的APP。

运用智能语音技术，分别实现语音人机智能聊天、语音打开特定APP、中-英语音翻译以及语音备忘录功能。

## 开发特点
界面层和逻辑代码层分割开，界面层只处理数据显示，逻辑代码层主要处理业务代码如发送请求和接收回调消息。

使用懒汉式单例设计模式获取唯一对象，如语音识别类、语音合成类等，都只需要一次初始化后继使用同一对象。

使用门面设计模式，每一模块都有专门的后台工具类处理所有流程，界面层只需要将文本传递给工具类即可

## APP主要界面
#### 语音聊天
<img width="200" height="400" src="./app/src/main/res/readmepicture/readmepicture1.jpg "/>
<img width="200" height="400" src="./app/src/main/res/readmepicture/readmepicture2.jpg "/>
<img width="200" height="400" src="./app/src/main/res/readmepicture/readmepicture3.jpg "/>
<img width="200" height="400" src="./app/src/main/res/readmepicture/readmepicture4.jpg "/>

#### 语音备忘
<img width="200" height="400" src="./app/src/main/res/readmepicture/readmepicture5.jpg "/>

#### 语音翻译
<img width="200" height="400" src="./app/src/main/res/readmepicture/readmepicture6.jpg "/>

#### 语音设置
<img width="200" height="400" src="./app/src/main/res/readmepicture/readmepicture7.jpg "/>