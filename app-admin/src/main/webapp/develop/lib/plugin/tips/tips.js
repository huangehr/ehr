/**
 * Tips obj
 */

(function ($, win) {
    $.extend({
        Tips: {

            _init:function(jqSelector,options){

                if(jqSelector){

                    var that = this;

                    jqSelector.each(function(){

                        var $t = {
                            tipWrap : $('<div class="SD-tipbox"></div>'),
                            tipContent : $('<div class="cntBox"></div>'),
                            tipArrow : $('<div class="SD-tipbox-direction"><em>&#9670;</em><span>&#9670;</span></div>'),
                            tipClose : $('<a title="关闭" onclick="return false" class="close-ico" href="#">关闭</a>'),
                            options : {}
                        };

                        $.extend($t.options,that._defaultOptions,options);

                        //show arrow
                        if($t.options.showArrow){
                            $t.tipWrap.append($t.tipContent).append($t.tipArrow).appendTo(document.body).attr("id","tip"+that._generalId());
                            $t.tipArrow.addClass("SD-tipbox-"+$t.options.position);
                        }else{
                            $t.tipWrap.append($t.tipContent).appendTo(document.body).attr("id","tip"+that._generalId());
                        }


                        if($t.options.content){
                            $t.tipContent.html($t.options.content);
                        }else{
                            $t.tipContent.html($(this).data("tips"));
                        }



                        $(this).data("jtip",$t);

                        if($t.options.show){
                            that._show($(this));
                        }

                        if($t.options.closeAble){
                            $t.tipWrap.append($t.tipClose);
                            var _tipObj = $(this);
                            $t.tipClose.on("click", function(){
                                that._hide(_tipObj);
                                _tipObj.unbind("mouseover");
                            });

                            $(this).one("mouseover",function(){
                                that._show($(this));
                            });

                        }else{

                            $(this).bind("mouseover focus",function(){
                                that._show($(this));
                            });

                            $(this).bind("mouseout blur",function(){
                                that._hide($(this));
                            });

                        }

                    });

                }

            },
            _generalId:function(){
                return $.now();
            },
            /**
             *public method
             *@param jqSelector jquery对象
             *@param options tip设置对象
             */
            tip:function(jqSelector,options){
                this._init(jqSelector,options);
            },

            /**
             * @param message 提示信息
             * @param width 提示宽度 可缺省
             * @param delayTime 提示耽搁时间 可缺省
             */
            error:function(message,width,delayTime){
                this._initTipDiv(message,'error',width,delayTime);
            },

            warn:function(message,width,delayTime){
                this._initTipDiv(message,'warn',width,delayTime);
            },

            success:function(message,width,delayTime){
                this._initTipDiv(message,'success',width,delayTime);
            },

            _initTipDiv:function(message,type,width,delayTime){
                var id = this._tipHtml.lastId;
                if(id){
                    $("#"+id).remove();
                }
                var jTip = '';
                if('error' === type){
                    jTip= $(this._tipHtml.error);
                }else if('warn' === type){
                    jTip= $(this._tipHtml.warn);
                }else if('success' === type){
                    jTip= $(this._tipHtml.success);
                }
                id = $.now()+"wTip";
                this._tipHtml.lastId = id;
                jTip.attr("id",id);
                jTip.appendTo(document.body);
                jTip.html(message);
                if(width){
                    jTip.css("width",width+"px");
                }
                if(delayTime){
                    jTip.slideDown(400).delay(delayTime).slideUp(400);
                }else{
                    jTip.slideDown(400).delay(3000).slideUp(400);
                }

            },

            _show:function(jElm){
                var width = jElm.outerWidth(),
                    height = jElm.outerHeight(),
                    left = jElm.offset().left,
                    top = jElm.offset().top,
                    $t = jElm.data("jtip"),
                    position = $t.options.position,
                    showArrow = $t.options.showArrow,
                    tHeight = $t.tipWrap.outerHeight(),
                    tWidth = $t.tipWrap.outerWidth();

                if(position == 'top'){
                    if(showArrow){
                        $t.tipWrap.css("left",left+(width-tWidth)/2+"px");
                        $t.tipWrap.css("top",top-tHeight-9+"px");
                    }else{
                        $t.tipWrap.css("left",left+(width-tWidth)/2+"px");
                        $t.tipWrap.css("top",top-tHeight+"px");
                    }
                }else if(position == 'right'){
                    if(showArrow){
                        $t.tipWrap.css("left",left+width+9+"px");
                        $t.tipWrap.css("top",top+(height-tHeight)/2+"px");
                    }else{
                        $t.tipWrap.css("left",left+width+"px");
                        $t.tipWrap.css("top",top+(height-tHeight)/2+"px");
                    }
                }else if(position == 'bottom'){
                    if(showArrow){
                        $t.tipWrap.css("left",left+(width-tWidth)/2+"px");
                        $t.tipWrap.css("top",top+height+9+"px");
                    }else{
                        $t.tipWrap.css("left",left+(width-tWidth)/2+"px");
                        $t.tipWrap.css("top",top+height+"px");
                    }
                }else if(position == 'left'){
                    if(showArrow){
                        $t.tipWrap.css("left",left-tWidth-9+"px");
                        $t.tipWrap.css("top",top+(height-tHeight)/2+"px");
                    }else{
                        $t.tipWrap.css("left",left-tWidth+"px");
                        $t.tipWrap.css("top",top+(height-tHeight)/2+"px");
                    }
                }
                $t.tipWrap.show();
            },
            /**
             * 如果空间不足重新计算tips位置
             */
            //TODO
            _recalPostion:function(){

            },

            _hide:function(jElm){
                var $t = jElm.data("jtip");
                $t.tipWrap.hide();
            },


            _defaultOptions:{
                position:'right',
                closeAble:false,
                show:false,
                content:'',
                showArrow:true
            },

            _tipHtml:{
                success:'<div class="tip-success tips"></div>',
                warn:'<div class="tip-warn tips"></div>',
                error:'<div class="tip-error tips"></div>',
                lastId:''
            }
        }
    });
})(jQuery, window);