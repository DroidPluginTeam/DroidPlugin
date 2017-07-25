Droid Plugin
======

[中文文档](readme_cn.md "中文文档")

We just transfer the project to a new address for more no-official contributors
The new address: [DroidPlugin](https://github.com/DroidPluginTeam/DroidPlugin "DroidPlugin")

[Fllow me at github](https://github.com/cmzy)

DroidPlugin is a new **Plugin Framework** developed and maintained by Andy Zhang( [Fllow me at github](https://github.com/cmzy) ).
It enables the host app run any third-party apk without installation, modification and repackage, which benefit a lot for collaborative development on Android.

-------



## Problems to be solved:
    
 1. Unable to send `Notification` with custom Resources，eg：
 
     a.  Notification with custom RemoteLayout, which means `Notification`'s `contentView`，`tickerView`，
     `bigContentView` and `headsUpContentView` must be null.

     b.  Notification with icon customized by R.drawable.XXX. The framework will transform it to Bitmap instead.

 2. Unable to define specified `Intent Filter` for the plugged app's `Service`、`Activity`、`BroadcastReceiver`
 and `ContentProvider`. So the plugged app is invisible for the outside system and app.

 3. Lack of `Hook` to the `Native` layer, thus apk (e.g. a majority of game apps) with `native` code cannot be loaded as plugin.
    
## Features：
  1. Compatible to Android 2.3 and later versions
  2. Given its .apk file, the plugged app could be run either independently or as plugin of the host, **NO** source code needed.
  3. Unnecessary to register the plugged app's `Service`、`Activity`、`BroadcastReceiver`、`ContentProvider` in the host.
  4. The plugged app are recognized as *Installed* by the host and other plugged apps
  5. Very low level of code invasion, in deed just one line code to integrate DroidPlugin into the host app.
  6. Complete code level separation between host and plugged apps, only system level message passing method provide by Android allowed.
  7. All system API supported
  8. Resources management are also completely separated between host and plugged apps.
  9. Process management for plugged apps, idle processed of the plugged app will be timely recycled to guarantee minimum memory usage.
  10. Static broadcast of plugged app will be treated as dynamic, thus the static broadcasting will never be trigger if
  the plugged app are not activated.
    
## Usage：

#### Integrate with the host apps

It is very simple integrate Droid Plugin to your proejct：

1. Import Droid Plugin project to your project as a lib.

2. Include following attributes in host's `AndroidManifest.xml`：
	
		<application android:name="com.morgoo.droidplugin.PluginApplication" 
			android:label="@string/app_name"
			android:icon="@drawable/ic_launcher" >

           
3. Or, if you use customized `Application`，add following code in the methods `onCreate` and `attachBaseContext`:
    
		@Override
		public void onCreate() {
			super.onCreate();
			PluginHelper.getInstance().applicationOnCreate(getBaseContext()); //must be after super.onCreate()
		}
        
		@Override
		protected void attachBaseContext(Context base) {
			PluginHelper.getInstance().applicationAttachBaseContext(base);
            super.attachBaseContext(base);
		}

4. Modify the `authorityName` value in `Libraries\DroidPlugin\build.gradle` (suggested use your package name)

#### Install、Uninstall or Upgrade the plugged app：

1. **Install/Upgrade**, use this method：
 
		int PluginManager.getInstance().installPackage(String filepath, int flags);
   
	For installation, `filepath` set to path of the .apk file, and `flags` set to 0.

	For upgrade, `filepath` set to path of the .apk file, and  `flags` set to `PackageManagerCompat.INSTALL_REPLACE_EXISTING`.
        
    
2. **Uninstall**, use this method：

		int PluginManager.getInstance().deletePackage(String packageName,int flags);

	`packageName` is package name of the plugged app，`flags = 0`。

3. **Activate**

    Just use android's API, same for communication between components.
	
## FAQ
	
 [FAQ](https://github.com/DroidPluginTeam/DroidPlugin/wiki/FAQ "FAQ")
	
## Remark：

Please feel free to [report bugs](https://github.com/Qihoo360/DroidPlugin/issues) or ask for help via email.
QQ Group:318901026

##Who is using Droid Plugin?
	
 [360 App Store](http://sj.360.cn "360 App Store")

    
### Thanks：
    
    Translated by Ming Song（gnosoir@hotmail.com）    
