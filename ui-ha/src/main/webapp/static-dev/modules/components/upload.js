/*!
 * upload -v1.0.0 (2015/8/17)
 * 
 * @description:
 *
 *      文件上传
 *
 * @example:
 *  html代码：
        <div class="u-upload alone f-ib f-tac">
             <!--用来存放item-->
             <div id="fileList" class="uploader-list"></div>
             <div id="filePicker" class="f-mt10">选择文件</div>
         </div>
 *  js代码：
       new Upload({aloneFile: true});
 * @author:      yezehua
 * 
 * @copyright:   2015 www.yihu.com
 */
define(function (require, exports, module) {

    var $ = require('base').$,
        context = require('base').context,
        Component = require('component'),
        WebUploader = require('modules/upload/webuploader');

    require('modules/components/upload.css');
    return Component.create({
        uploader: null, // Web Uploader实例
        init: function (opts) {

            this.opts = $.extend({},{
                fileList:  '#fileList', // 文件列表(缩略图容器)
                aloneFile: false,
                thumbWidth: 88,
                thumbHeight: 110
            },opts);

            var opt = $.extend({

                // 自动上传。
                auto: false,

                // swf文件路径
                swf: context.path+'/static-dev/modules/upload/Uploader.swf',

                // 文件接收服务端。
                server: context.path+'/fileupload.php',

                // 选择文件的按钮。可选。
                // 内部根据当前运行是创建，可能是input元素，也可能是flash.
                pick: '#filePicker',

                // 只允许选择文件，可选。
                /*
                 accept: {
                 title: 'Images',
                 extensions: 'gif,jpg,jpeg,bmp,png',
                 mimeTypes: 'image/*'
                 }*/
            },this.opts.cfg);

            this.uploader = WebUploader.create(opt);

            this.addEvents();

        },
        addEvents: function () {

            var self = this,
                uploader = this.uploader,
                opts = this.opts;

            var $list = $(opts.fileList),
                // 优化retina, 在retina下这个值是2
                ratio = window.devicePixelRatio || 1,
                // 缩略图大小
                thumbnailWidth = opts.thumbWidth * ratio,
                thumbnailHeight = opts.thumbHeight * ratio;

            // 当有文件添加进来的时候
            uploader.on( 'fileQueued', function( file ) {
                if(opts.aloneFile) {
                    $(opts.fileList).empty();
                    uploader.reset();
                }
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
            });

            // 文件上传成功，给item添加成功class, 用样式标记上传成功。
            uploader.on( 'uploadSuccess', function( file ) {
                $( '#'+file.id ).addClass('upload-state-done');
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
            });

            // 完成上传完了，成功或者失败，先删除进度条。
            uploader.on( 'uploadComplete', function( file ) {
                $( '#'+file.id ).find('.progress').remove();
            });
        }
    });
})