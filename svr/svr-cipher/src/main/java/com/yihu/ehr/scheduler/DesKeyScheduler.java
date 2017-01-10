package com.yihu.ehr.scheduler;

import com.yihu.ehr.foreign.IDictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;
import java.util.Random;

/**
 * DES KEY更新任务
 *
 * @author lyr
 * @version 1.0
 * @created on 2016/5/10.
 */
@Component
public class DesKeyScheduler {

    @Autowired
    private IDictService dictService; //字典服务

    /**
     * 每天4点执行一次DES密钥更新
     *
     * @throws Exception
     */
    @Scheduled(cron = "0 0 4 * * ?")
    public void updateDesKey() throws Exception{
        String key = generateKey();

        dictService.updateDictEntry("{\"dictId\":28,\"code\":\"DES_KEY\",\"value\":\""
                + key +"\",\"sort\":1,\"phoneticCode\":\"\",\"catalog\":\"\"}");
    }

    /**
     * 随机生成DES密钥
     *
     * @return String DES密钥
     * @throws Exception
     */
    public String generateKey() throws Exception
    {
        int _len = 8;
        //以当前时间为种子创建随机数
        Random random = new Random(new Date().getTime());

        byte[] keys = new byte[_len];

        for (int i = 0; i < _len; i++)
        {
            //生成65-122范围的随机数
            keys[i] = (byte)Math.round(random.nextDouble()*57 + 65);
        }

        //返回密钥的ASCII字符串
        return new String(keys,"ASCII");
    }
}
