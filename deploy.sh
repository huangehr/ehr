#! /bin/sh

function wagon_jar() {
    local cur_dir workdir

    cur_dir=$1
    workdir=$1
    cd ${workdir}

    for dir in $(ls ${cur_dir})
    do
        if test -d ${dir};then

            if (echo ${dir} | grep '^svr-') then
                cd ${dir}

                mvn wagon:upload-single

                cd ..
            fi
        fi
    done
}

function wagon_src(){
    echo "wagon src"
}

while [[ $# > 1 ]]
do
key="$1"

case $key in
    -p|--profile)
    PROFILE="$2"
    shift # past argument
    ;;
    -t|--TEST)
    TEST="$2"
    shift # past argument
    ;;
    -c|--COMPILE_COMMONS)
    COMPILE_COMMONS="$2"
    shift # past argument
    ;;
    --*)
    PROFILE="dev"
    TEST=false
    COMPILE_COMMONS=true
    ;;
esac
shift # past argument or value
done
echo Maven profile                  = "${PROFILE}"
echo Test before deploy             = "${TEST}"
echo Compile commons dependencies   = "${COMPILE_COMMONS}"



#cd commons-dependencies
#mvn compile install #-Dmaven.test.skip=true 

#cd ../commons-dependencies-native
#mvn compile package -Dmaven.test.skip=true 

#cd ..
#wagon_jar .

#if test -d $1
#then

#else
#    echo "Missing directory."
#    exit 1
#fi