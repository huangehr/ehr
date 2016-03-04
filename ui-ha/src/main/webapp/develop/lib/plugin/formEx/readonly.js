/*!
 * readonly -v1.0.0 (2015/11/24)
 * 
 * @description:
 *
 * @author:      yezehua
 * 
 * @copyright:   2015 www.yihu.com
 */
$(function () {
    $('.u-ui-readonly input').on('focus',function(){
        if($(this).closest('.u-ui-readonly').length){
            this.blur();
        }
    });
});