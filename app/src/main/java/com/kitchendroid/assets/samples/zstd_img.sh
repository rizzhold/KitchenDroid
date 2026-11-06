if [ ! -d $KD_PRO/out ];then
  mkdir -p $KD_PRO/out
fi

for i in $ZST; do
    if [ ! -f "$KD_PRO/$i" ]; then
        echo "Error: file $KD_PRO/$i does not exist!"
        continue
    fi
    if expr "$i" : '.*\.zst$' >/dev/null || expr "$i" : '.*\.zstd$' >/dev/null; then
        echo "> Starting decompression: $i"
        line=$(echo "$i" | cut -d"." -f1)
        zstd -d -k -f $KD_PRO/$i -o $KD_PRO/out/$line.img
        echo "> Decompression completed, file located at：$KD_PRO/out/$line.img"
    fi
    if expr "$i" : '.*\.img$' >/dev/null; then
        echo "> Starting compression: $i "
        zstd -$level -T4 -f $KD_PRO/$i -o $KD_PRO/out/$i.zst
        echo "> Compression completed, file located at：$KD_PRO/out/$i.zst"
    fi
    if [[ $silence = 1 ]]; then
        echo "> Deleting: $i"
        rm -rf "$KD_PRO/$i"
    fi
done
