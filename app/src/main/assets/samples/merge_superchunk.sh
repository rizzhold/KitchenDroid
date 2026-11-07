project=$(cat $TMPDIR/KitchenDroid.ini)
KD_PRO=$KITCHEN_DIR/$project
if [ ! -d $KD_PRO/out ];then
  mkdir -p $KD_PRO/out
fi

for prefix in $IMG; do
    safe_prefix=$(printf "%s" "$prefix" | sed 's/[.[*^$+(){}|]/\\&/g')
    pattern="^${safe_prefix}\.[0-9]\\{1,\\}$"
    find "$KD_PRO" -maxdepth 1 -type f -name "${prefix}*" -exec basename {} \; |
    grep "$pattern" |
    tr '\n' ' ' |
    {
        read -r files
        cd $KD_PRO
        echo "> Starting merging files into：$prefix"
        simg2img ${files% } $KD_PRO/out/$prefix
        cd
        if [ -f $KD_PRO/out/$prefix ];then
            echo "> Conversion completed, file located at：$KD_PRO/out/$prefix"
        else
            echo "DEBUG: > processing independent prefix '$prefix'" >&2
            echo "DEBUG: > generated regex pattern '$pattern'" >&2
            echo "DEBUG: > processing '${files% }' failed, please screenshot and contact developer to fix" >&2
        fi
    }
done
