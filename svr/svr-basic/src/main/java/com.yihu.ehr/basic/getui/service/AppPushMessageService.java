package com.yihu.ehr.basic.getui.service;

import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.AppMessage;
import com.gexin.rp.sdk.base.impl.ListMessage;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.base.uitls.AppConditions;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.LinkTemplate;
import com.gexin.rp.sdk.template.NotificationTemplate;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.gexin.rp.sdk.template.style.Style0;
import com.yihu.ehr.basic.getui.ConstantUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author litaohong on 2018/4/16
 * @project ehr
 * 消息推送服务
 */
@Service
public class AppPushMessageService {

    Logger logger = LoggerFactory.getLogger(AppPushMessageService.class);

    @Autowired
    private GeTuiClientService geTuiClientService;

    /**
     * 向单个用户推送消息
     * @param userId
     * @param message
     * @param targetUrl
     */
    public String pushMessageToSingle(String userId,String title,String message,String targetUrl){
        if(StringUtils.isEmpty(userId) || StringUtils.isEmpty(title) || StringUtils.isEmpty(message)){
            return "params[userId|title|message] must have a value !";
        }
        String clientId = geTuiClientService.getClientIdByUserId(userId);
        if(StringUtils.isEmpty(clientId)){
            return "this user is no such clientId:"+ userId;
        }
        IGtPush iGetPush = new IGtPush(ConstantUtil.appKey,ConstantUtil.masterSecret);

        //获取推送模板
        NotificationTemplate notificationTemplate = getNotificationTemplate(title,message);

        //消息对象
        SingleMessage singleMessage = new SingleMessage();
        // 离线依然触发
        singleMessage.setOffline(true);
        // 离线有效时间，单位为毫秒，可选
        singleMessage.setOfflineExpireTime(24 * 3600 * 1000);
        // 可选，1为wifi，0为不限制网络环境。根据手机处于的网络情况，决定是否下发
        singleMessage.setPushNetWorkType(0);
        singleMessage.setData(notificationTemplate);

        //组装推送目标
        Target target = new Target();
        target.setAppId(ConstantUtil.appId);
        target.setClientId(clientId);

        IPushResult pushResult = null;
        pushResult = iGetPush.pushMessageToSingle(singleMessage,target);
        if(null != pushResult){
            logger.info(pushResult.getResponse().toString());
        }else{
            logger.error("个推异常，发送失败，回调结果为null！");
        }
        return pushResult.getResponse().toString();
    }

    /**
     * 推送消息到多个用户
     * @param userIds
     * @param title
     * @param message
     */
    public String pushMessageToList(String userIds,String title,String message){
        if(StringUtils.isEmpty(userIds) || StringUtils.isEmpty(title) || StringUtils.isEmpty(message)){
            return "params[userId|title|message] must have a value !";
        }
        IGtPush iGtPush = new IGtPush(ConstantUtil.appKey,ConstantUtil.masterSecret);

        List<String> clientIdList = geTuiClientService.getListClientIdByUserId(userIds);
        NotificationTemplate notificationTemplate = getNotificationTemplate(title,message);
        ListMessage listMessage = new ListMessage();
        listMessage.setData(notificationTemplate);
        listMessage.setOffline(true);
        listMessage.setOfflineExpireTime(24 * 1000 * 3600);
        List targets = new ArrayList();
        clientIdList.forEach(
                one -> {
                    Target target = new Target();
                    target.setAppId(ConstantUtil.appId);
                    target.setClientId(one);
                    targets.add(target);
                }
        );
        String taskId = iGtPush.getContentId(listMessage);
        IPushResult pushResult = null;
        try {
            pushResult = iGtPush.pushMessageToList(taskId,targets);
            logger.info(pushResult.getResponse().toString());
        }catch (Exception e){
            e.printStackTrace();
        }
        return pushResult.getResponse().toString();
    }


    /**
     * 推送消息到指定应用群app,即各类标签用户，可按手机，地区等划分，无需clientId
     * @param appidList
     * @param phoneList
     * @param cityList
     * @param tagList
     * @param title
     * @param message
     */
    public String pushMessageToAPP(List<String> appidList,List<String> phoneList,List<String> cityList,List<String> tagList,String title, String message) {
        if(CollectionUtils.isEmpty(appidList) || StringUtils.isEmpty(title) || StringUtils.isEmpty(message)){
            return "params[appidList|title|message] must have a value !";
        }
        IGtPush iGtPush = new IGtPush(ConstantUtil.appKey,ConstantUtil.masterSecret);

        NotificationTemplate notificationTemplate = getNotificationTemplate(title,message);

        //组装群内消息对象
        AppMessage appMessage = new AppMessage();
        //推送给哪些应用
        appMessage.setAppIdList(appidList);
        //推送模板
        appMessage.setData(notificationTemplate);
        //离线推送有效期
        appMessage.setOfflineExpireTime(24 * 1000 * 3600);

        //组装群内标签分类，要推送给哪些类型的用户
        AppConditions appConditions = new AppConditions();
        if(!CollectionUtils.isEmpty(phoneList)){
            appConditions.addCondition(AppConditions.PHONE_TYPE,phoneList);
        }
        if(!CollectionUtils.isEmpty(cityList)){
            appConditions.addCondition(AppConditions.REGION,cityList);
        }
        if(!CollectionUtils.isEmpty(tagList)){
            appConditions.addCondition(AppConditions.TAG,tagList);
        }
        appMessage.setConditions(appConditions);

        IPushResult pushResult = null;
        pushResult = iGtPush.pushMessageToApp(appMessage);
        logger.info(pushResult.getResponse().toString());
        return pushResult.getResponse().toString();
    }


    /**
     * 打开网页
     * @param userIds
     * @param title
     * @param message
     * @param targetUrl
     * @return
     */
    public String pushMessageToSingleLink(String userIds,String title,String message,String targetUrl){
        if(StringUtils.isEmpty(userIds) || StringUtils.isEmpty(title) || StringUtils.isEmpty(message)){
            return "params[userId|title|message] must have a value !";
        }
        String clientId = "";
        List<String> clientIdList = geTuiClientService.getListClientIdByUserId(userIds);
        if(CollectionUtils.isEmpty(clientIdList)){
            return "this user is no such clientId:"+ userIds;
        }
        clientId = clientIdList.get(0);
        IGtPush iGetPush = new IGtPush(ConstantUtil.appKey,ConstantUtil.masterSecret);

        //获取推送模板
        LinkTemplate linkTemplate = getLinkTemplate(title,message,targetUrl);

        //消息对象
        SingleMessage singleMessage = new SingleMessage();
        // 离线依然触发
        singleMessage.setOffline(true);
        // 离线有效时间，单位为毫秒，可选
        singleMessage.setOfflineExpireTime(24 * 3600 * 1000);
        // 可选，1为wifi，0为不限制网络环境。根据手机处于的网络情况，决定是否下发
        singleMessage.setPushNetWorkType(0);
        singleMessage.setData(linkTemplate);

        //组装推送目标
        Target target = new Target();
        target.setAppId(ConstantUtil.appId);
        target.setClientId(clientId);

        IPushResult pushResult = null;
        pushResult = iGetPush.pushMessageToSingle(singleMessage,target);
        if(null != pushResult){
            logger.info(pushResult.getResponse().toString());
        }else{
            logger.error("个推异常，发送失败，回调结果为null！");
        }
        return pushResult.getResponse().toString();
    }

    /**
     * 透传消息
     * @param userIds
     * @param title
     * @param message
     * @return
     */
    public String pushMessageTransimssion(String userIds,String title,String message){
        if(StringUtils.isEmpty(userIds) || StringUtils.isEmpty(title) || StringUtils.isEmpty(message)){
            return "params[userId|title|message] must have a value !";
        }
        String clientId = "";
        List<String> clientIdList = geTuiClientService.getListClientIdByUserId(userIds);
        if(CollectionUtils.isEmpty(clientIdList)){
            return "this user is no such clientId:"+ userIds;
        }
        clientId = clientIdList.get(0);
        IGtPush iGetPush = new IGtPush(ConstantUtil.appKey,ConstantUtil.masterSecret);

        //获取推送模板
        TransmissionTemplate transmissionTemplate = getTransmissionTemplate(message);

        //消息对象
        SingleMessage singleMessage = new SingleMessage();
        // 离线依然触发
        singleMessage.setOffline(true);
        // 离线有效时间，单位为毫秒，可选
        singleMessage.setOfflineExpireTime(24 * 3600 * 1000);
        // 可选，1为wifi，0为不限制网络环境。根据手机处于的网络情况，决定是否下发
        singleMessage.setPushNetWorkType(0);
        singleMessage.setData(transmissionTemplate);

        //组装推送目标
        Target target = new Target();
        target.setAppId(ConstantUtil.appId);
        target.setClientId(clientId);

        IPushResult pushResult = null;
        pushResult = iGetPush.pushMessageToSingle(singleMessage,target);
        if(null != pushResult){
            logger.info(pushResult.getResponse().toString());
        }else{
            logger.error("个推异常，发送失败，回调结果为null！");
        }
        return pushResult.getResponse().toString();
    }

    /**
     * 网页消息发送模板，用户点击消息后跳转至相应的网页
     * @return
     */
    public LinkTemplate getLinkTemplate(String title,String text,String url){
        LinkTemplate linkTemplate = new LinkTemplate();
        linkTemplate.setAppId(ConstantUtil.appId);
        linkTemplate.setAppkey(ConstantUtil.appKey);

        Style0 style = new Style0();
        style.setTitle(title);
        style.setText(text);

        linkTemplate.setStyle(style);
        linkTemplate.setUrl(url);
        return linkTemplate;
    }


    /**
     * 点击通知打开应用模板
     * @return
     */
    public NotificationTemplate getNotificationTemplate(String title, String text){
        NotificationTemplate notificationTemplate = new NotificationTemplate();
        notificationTemplate.setAppId(ConstantUtil.appId);
        notificationTemplate.setAppkey(ConstantUtil.appKey);

        Style0 style = new Style0();
        style.setTitle(title);
        style.setText(text);
        style.setLogo("notifyLogo.png");
        style.setLogoUrl("http://image.baidu.com/search/detail?ct=503316480&z=0&ipn=d&word=%E5%9B%BE%E7%89%87&hs=0&pn=0&spn=0&di=97987891320&pi=0&rn=1&tn=baiduimagedetail&is=0%2C0&ie=utf-8&oe=utf-8&cl=2&lm=-1&cs=3588772980%2C2454248748&os=1031665791%2C326346256&simid=0%2C0&adpicid=0&lpn=0&ln=30&fr=ala&fm=&sme=&cg=&bdtype=0&oriquery=&objurl=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F0142135541fe180000019ae9b8cf86.jpg%401280w_1l_2o_100sh.png&fromurl=ippr_z2C%24qAzdH3FAzdH3Fooo_z%26e3Bzv55s_z%26e3Bv54_z%26e3BvgAzdH3Fo56hAzdH3FZNzMxNTI8M2%3D%3D_z%26e3Bip4s&gsm=0&islist=&querylist=");
        notificationTemplate.setStyle(style);
        // 收到消息是否立即启动应用： 1为立即启动，2则广播等待客户端自启动
        notificationTemplate.setTransmissionType(1);
        return notificationTemplate;
    }

    public TransmissionTemplate getTransmissionTemplate(String message){
        TransmissionTemplate transmissionTemplate = new TransmissionTemplate();
        transmissionTemplate.setAppId(ConstantUtil.appId);
        transmissionTemplate.setAppkey(ConstantUtil.appKey);
        transmissionTemplate.setTransmissionContent(message);
//        transmissionTemplate.setDuration(DateFormatUtils.format(new Date(),DateFormatUtils.ISO_DATETIME_FORMAT.toString()));
        return transmissionTemplate;
    }
}
