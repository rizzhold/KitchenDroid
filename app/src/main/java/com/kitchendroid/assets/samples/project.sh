#查看项目
CK () {
for i in $(ls -d $KITCHEN_DIR/KD_*);do
  echo "${i##*/}"
done
}
#新建项目
XJ () {
if [ -d $KITCHEN_DIR/KD_"$T" ]; then
  echo "> Project already exists and will be renamed automatically!"
  T="$T"-`date "+%Y%m%d%H%M%S"`
  echo "> Creating：KD_${T}"
  mkdir -p $KITCHEN_DIR/KD_"$T"
else
  echo "> Creating：KD_${T}"
  mkdir -p $KITCHEN_DIR/KD_"$T"
fi
echo "> Created!"
echo "KD_${T}" > $TMPDIR/KitchenDroid.ini
}
#删除项目
SC () {
for i in ${TSS};do
  echo "> Deleting：${i}"
  rm -rf $KITCHEN_DIR/$i
  rm -rf $KITCHEN_TMP/$i
  if [ "$i" = "$project" ];then
    rm -rf $TMPDIR/KitchenDroid.ini
  fi
  echo "> Deleted！"
done
}
#删除插件
sub (){
for i in ${sub};do
  echo "> Deleting：$i"
  rm -rf $START_DIR/module/$i
  echo "> Deleted！"
done
}

$1
