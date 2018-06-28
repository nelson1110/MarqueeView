# MarqueeView
新的里程表式垂直跑马灯，基于viewgroup的自定义控件

## 效果图
<img src="./images/20180628.gif" height="650"/>

## 用法：
### Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:
```groovy
    allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
	
### Step 2. Add the dependency
```groovy
	dependencies {
	        implementation 'com.github.nelson1110:MarqueeView:0.1.0-release'
	}
```
### Step 3. 在xml中添加该控件
```xml
<com.libs.nelson.marqueeviewlib.MarqueeView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        />
```
 `xml`中的一些可用属性
 
参数名 | 意义
- | :-
orientation | 动画滚动方向
animator_duration | 动画时间
stay_duration | 每个item动画结束后停留的时间
reverse_animator | 是否反向动画，默认↑或←
