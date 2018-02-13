#Create by anxi.xue at 2017-09-06

#这个脚本的作用是自动编译APK,然后自动签名，然后自动安装到手机上，然后启动主界面

#使用注意点1 (根据自己的项目需求可以修改以下几个配置)
#包名(根据自己的需求修改)
packageName=com.tct.nowkey
#主界面(根据自己的需求修改)
mainActivity=.NowKeyActivity
#签名之后的apk文件位置(根据自己的需求修改，可以不改，直接使用)
signedApkPath=/local/release.apk

#使用注意点2
#需要把签名包放在指定的位置，可以自己调整，没有这个签名包可以到windows系统的共享路径下获取：\\rdshare\temp\anxi\releasekey(下载下来是一个压缩包，需要解压)
signApkPath=/local/tools/google_platform_release

#使用注意点3
#为了提高效率，编译apk过程是使用了--offline模式，这就要求使用这个脚本之前至少需要用以下命令成功编译一次，以后就可以离线了
#在线编译指令 gradle :app:assembleDebug

currentPath=$(pwd)
debugApkPath=$currentPath"/app/build/outputs/apk/app-debug.apk"
echo "=== workPath:"$currentPath
echo "=== debugApkPath Path:"$debugApkPath
echo "=== signApkPath:"$signApkPath

cd $currentPath
echo "=== delete old apk ..."
rm $debugApkPath

echo "=== build apk ..."
gradle :app:assembleDebug --offline  
cd $signApkPath

echo "=== sign apk ..."
java -jar signapk.jar platform.x509.pem platform.pk8 $debugApkPath  $signedApkPath

echo "=== uninstall old apk ..."
adb uninstall $packageName 

echo "=== install new apk ..."
adb install $signedApkPath 

echo "=== Start apk ..."
adb shell am start -n $packageName/$mainActivity

cd $currentPath
