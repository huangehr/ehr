package com.yihu.ehr.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constrant.Services;
import org.springframework.stereotype.Service;

/**
 * ����һ��ObjectMapper�����࣬����ע��Bean�Ĵ�����
 *
 * ��Ϊ����ԭ�򣬲��Ҵ������̰߳�ȫ�ģ����౻���档�μ���http://wiki.fasterxml.com/JacksonBestPracticesPerformance
 *
 * @implNote ��bean���������ֹ���ȡ�ĳ�Ա�����С�
 *
 * @author Sand
 * @version 1.0
 * @created 2015.08.14 13:45
 */
@Service(Services.ObjectMapper)
public class ObjectMapperExt extends ObjectMapper {
}
