# GetImg
从相册，文件系统，拍照中获取单张图片并裁剪压缩图片
## Gradle依赖配置
### 在工程的根目录下配置
```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
		}
}
```
### 在app Module中配置
```
dependencies {
	...
	implementation 'com.github.ContextDong:GetImg:v1.0.0'
}
````

## 用法

## 注意事项
1. android6 动态申请权限需要自己实现
2. 可选择实现压缩图片的回调接口
