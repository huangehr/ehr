#! /bin/sh

function scan_dir() {
    local cur_dir parent_dir workdir

    workdir=$1
    cd ${workdir}

    if [ ${workdir} = "/" ]
    then
        cur_dir=""
    else
        cur_dir=$(pwd)
    fi

    for dirlist in $(ls ${cur_dir})
    do
        if test -d ${dirlist};then
            cd ${dirlist}

            mvn compile

            cd ..
        fi
    done
}


#if test -d $1
#then
#    scan_dir $1
#else
#    echo "the Directory isn't exist which you input,pls input a new one!!"
#    exit 1
#fi

cd commons-dependencies
mvn compile install

cd ../commons-dependencies-native
mvn compile package -Dmaven.test.skip=true
