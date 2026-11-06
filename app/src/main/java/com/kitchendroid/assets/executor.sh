#!/system/bin/sh

# Environment variables
export PATH="$({TOOLKIT}):$PATH"
export START_DIR="$({START_DIR})"
export TEMP_DIR="$({TEMP_DIR})"
export TMPDIR="$TEMP_DIR"
export APP_USER_ID="$({APP_USER_ID})"

# Jump to start directory if valid
if [[ "$START_DIR" != "" ]] && [[ -d "$START_DIR" ]]; then
    cd "$START_DIR"
fi

# Tool directory (rebranded: tidak pakai /sdcard/DNA)
export KITCHEN_DIR="$({START_DIR})/KitchenDroid"
if [ ! -d $KITCHEN_DIR ]; then
    mkdir -p $KITCHEN_DIR
fi

# Temp dir: pakai internal path (no-root)
if [ "$({ROOT_PERMISSION})" = "true" ]; then
    export KITCHEN_TMP=/data/KitchenDroid
else
    export KITCHEN_TMP="$({START_DIR})/KitchenDroid"
fi

# Project config
if [ -f $TMPDIR/KitchenDroid.ini ]; then
    project=$(cat $TMPDIR/KitchenDroid.ini)
    export KITCHEN_PRO=$KITCHEN_DIR/$project
    export KITCHEN_DRO=$KITCHEN_TMP/$project
fi

# Fix ownership
chown -R $APP_USER_ID:$APP_USER_ID $START_DIR

# Run script
if [[ -f "$1" ]]; then
    chmod 755 "$1"
    source "$1"
else
    echo "${1} Lost" 1>&2
fi

exit 0
