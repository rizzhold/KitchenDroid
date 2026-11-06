if [ ! -d $KD_PRO/out ];then
  mkdir -p $KD_PRO/out
fi
for i in $IMG ;do
info=$(kd_tool gettype $KD_PRO/$i)
if [ "$info" = "ext" ] || [ "$info" = "erofs" ]; then
  echo "> Starting Conversion：$i"
    img2simg $KD_PRO/$i $KD_PRO/out/$i
elif [ "$info" = "sparse" ]; then
  echo "> Starting Conversion：$i"
  simg2img $KD_PRO/$i $KD_PRO/out/$i
else
  echo "> No conversion support：$i"
fi
if [[ $silence = 1 ]]; then
  echo "> Deleting：$i"
  rm -rf $KD_PRO/$i
fi
if [ -f $KD_PRO/out/$i ];then
  echo "> Conversion completed and file is saved at：$KD_PRO/out"
else
  echo "> Conversion failed, take a screenshot and contact the developer to fix it!!!"
fi
done
