/*!
 * upload -v1.0.0 (2015/8/17)
 * 
 * @description:
 *
 *      文件上传
 *
 * @example:
 *  html代码：
        <div class="u-upload alone f-ib f-tac"
            data-alone-file=true data-file-list="#fileList" data-thumb-width="88" data-thumb-height="110">
             <!--用来存放item-->
             <div id="fileList" class="uploader-list"></div>
             <div id="filePicker" class="f-mt10">选择文件</div>
         </div>
 *  js代码：
       $('.u-upload').upload()
 * @author:      yezehua
 * 
 * @copyright:   2015 www.yihu.com
 */

(function ($, win) {
    var Util = $.Util;
    var defaults = {
        // 自动上传。
        auto: false,
        // swf文件路径
        swf: $.Context.STATIC_PATH+'/lib/plugin/upload/Uploader.swf',
        // 文件接收服务端。
        server: $.Context.PATH+'/fileupload.php',
        // 选择文件的按钮。可选。
        // 内部根据当前运行是创建，可能是input元素，也可能是flash.
        pick: {id:'#div_file_picker'},
        sendAsBinary: true
        // 只允许选择文件，可选。
        /*
         accept: {
         title: 'Images',
         extensions: 'gif,jpg,jpeg,bmp,png',
         mimeTypes: 'image/*'
         }*/
    };

    function bindEvents() {

        var  uploader = this;
        var $list = $(this.fileList),
        // 优化retina, 在retina下这个值是2
            ratio = window.devicePixelRatio || 1,
        // 缩略图大小
            thumbnailWidth = this.thumbWidth * ratio,
            thumbnailHeight = this.thumbHeight * ratio;

        uploader.on( 'beforeFileQueued', function( file ) {
            if(uploader.aloneFile===true || Util.isStrEquals("true", uploader.aloneFile)) {
                $(uploader.fileList).empty();
                uploader.reset();
            }
            uploader.trigger('beforeQueued',file);
        });
        // 当有文件添加进来的时候
        uploader.on( 'fileQueued', function( file ) {

            var $li = $(
                    '<div id="' + file.id + '" class="file-item thumbnail">' +
                    '<img>' +
                    '<div class="info">' + file.name + '</div>' +
                    '</div>'
                ),
                $img = $li.find('img');

            $list.append( $li );

            // 创建缩略图
            uploader.makeThumb( file, function( error, src ) {
                if ( error ) {
                    $img.replaceWith('<span>不能预览</span>');
                    return;
                }

                $img.attr( 'src', src );
            }, thumbnailWidth, thumbnailHeight );
            uploader.trigger('queued',file);
        });
        uploader.on( 'uploadBeforeSend', function( obj, data, headers ) {
            uploader.trigger('beforeSend',obj, data, headers);
        });
        // 文件上传过程中创建进度条实时显示。
        uploader.on( 'uploadProgress', function( file, percentage ) {
            var $li = $( '#'+file.id ),
                $percent = $li.find('.progress span');

            // 避免重复创建
            if ( !$percent.length ) {
                $percent = $('<p class="progress"><span></span></p>')
                    .appendTo( $li )
                    .find('span');
            }

            $percent.css( 'width', percentage * 100 + '%' );
            uploader.trigger('progress',file, percentage);
        });

        // 文件上传成功，给item添加成功class, 用样式标记上传成功。
        uploader.on( 'uploadSuccess', function( file ) {
            $( '#'+file.id ).addClass('upload-state-done');
            uploader.trigger('success',file);
        });

        // 文件上传失败，现实上传出错。
        uploader.on( 'uploadError', function( file ) {
            var $li = $( '#'+file.id ),
                $error = $li.find('div.error');

            // 避免重复创建
            if ( !$error.length ) {
                $error = $('<div class="error"></div>').appendTo( $li );
            }

            $error.text('上传失败');
            uploader.trigger('error',file);
        });

        // 完成上传完了，成功或者失败，先删除进度条。
        uploader.on( 'uploadComplete', function( file ) {
            $( '#'+file.id ).find('.progress').remove();
            uploader.trigger('complete',file);
        });
    }
    function getAttrs() {
        return {
            aloneFile: this.attr('data-alone-file'),
            fileList: this.attr("data-file-list"),
            thumbWidth: this.attr("data-thumb-width"),
            thumbHeight: this.attr("data-thumb-height")
        }
    }
    $.fn.extend({
        webupload: function (opts) {
            opts = $.extend(true,{},defaults,opts);
            var attrs = getAttrs.call(this);
            if(attrs.aloneFile) {
                opts = $.extend(true,{},opts,{
                    pick: {
                        multiple: false
                    }
                });
            }
            var uploader = WebUploader.create(opts);
            $.extend(uploader,{
                aloneFile: false,
                fileList: '#div_file_list',
                thumbWidth: 88,
                thumbHeight: 110
            }, attrs);
            bindEvents.call(uploader);
            return uploader;
        }
    });
})(jQuery, window);