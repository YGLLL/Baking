优达学城
纳米学位Android开发进阶
烘培应用

这是一款 Android 烘培应用，用户能够选择一个食谱，并观看视频教程

界面展示
![](https://github.com/YGLLL/Baking/blob/master/screenshot/1.jpg)
![](https://github.com/YGLLL/Baking/blob/master/screenshot/2.jpg)
![](https://github.com/YGLLL/Baking/blob/master/screenshot/3.jpg)

共有3个Activity
	MainActivity
	StepActivity
	DescriptionActivity
以及2个Fragment
	StepFragment
	DescriptionFragment
	
正常模式下启用3个Activity：（括号内表示的是此Activity拥有的Fragment）
	MainActivity
	StepActivity(StepFragment)
	DescriptionActivity(DescriptionFragment)
平板模式下启用2个Activity：
	MainActivity
	StepActivity(StepFragment,DescriptionFragment)
	
使用到的开源库：
	compile 'com.android.support:appcompat-v7:25.2.0'
    compile files('libs/gson-2.8.1.jar')
    compile 'com.squareup.okhttp3:okhttp:3.8.1'
    compile 'org.litepal.android:core:1.5.1'（注意，使用了litepal操作数据库）
    compile 'com.android.support:recyclerview-v7:25.2.0'
    compile 'com.android.support:cardview-v7:25.2.0'
    compile 'com.android.support:design:25.2.0'
    compile 'com.github.bumptech.glide:glide:3.7.0'