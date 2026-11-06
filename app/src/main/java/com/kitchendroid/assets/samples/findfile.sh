val=$1
case ${val} in
dir)
ls -F $KD_PRO | sed 's/\/$//g' | grep -v "config"
;;
zip1)
ls $KITCHEN_DIR | grep -v "/$" | grep \."zip"$
;;
img1)
ls $KITCHEN_DIR | grep -v "/$" | grep \."img"$
;;
br)
ls $KD_PRO | grep -v "/$" | grep \."br"$
;;
dat)
ls $KD_PRO | grep -v "/$" | grep \."new.dat"$
;;
img)
for i in $(ls $KD_PRO | grep -v "/$" | grep \."img"$)
do
    if [ -f $KD_PRO/$i ];then
        info=$(kd_tool gettype $KD_PRO/$i)
        echo "$i|$i ($info)"
    fi
done
;;
split_sparse)
find "$KD_PRO" -maxdepth 1 -type f -exec basename {} \; |
    sed -n 's/\(.*\)\.[0-9]\{1,\}$/\1/p; t; d' |
    sort | uniq -c |
    while read -r count prefix; do
        clean_prefix=$(echo "$prefix" | sed 's/[^a-zA-Z0-9._-]$//')
        printf "%s|%s Total %d files\n" "$clean_prefix" "$clean_prefix" "$count"
    done
;;
zst_img)
ls $KD_PRO | grep -v "/$" | grep -E "\.(zst|zstd|img)$"
;;
*)
ls $KD_PRO | grep -v "/$" | grep \."$1"$
;;
esac
