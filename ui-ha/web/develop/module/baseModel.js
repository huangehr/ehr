/*!
 * BaseModel -v1.0.0 (2015/7/27)
 *
 * @description: 基模型对象（类）
 *
 *      该对象（类），继承于BaseObject，由BaseObject.create调用产生，是BaseObject的子类（预留扩展需要）。
 *
 * @author:      yezehua
 *
 * @copyright:   2015 www.yihu.com
 */

define(['module/baseObject'],function (BaseObject) {

    var BaseModel = BaseObject.create();

    return BaseModel;
})