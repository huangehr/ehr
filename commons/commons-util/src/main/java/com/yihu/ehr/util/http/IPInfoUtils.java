package com.yihu.ehr.util.http;

import javax.servlet.http.HttpServletRequest;

/**
 * Utils - ip信息辅助工具类
 * Created by progr1mmer on 2018/1/18.
 */
public class IPInfoUtils {

    private static final long a1 = getIpNum("10.0.0.0");
    private static final long a2 = getIpNum("10.255.255.255");
    private static final long b1 = getIpNum("172.16.0.0");
    private static final long b2 = getIpNum("172.31.255.255");
    private static final long c1 = getIpNum("192.168.0.0");
    private static final long c2 = getIpNum("192.168.255.255");
    private static final long d1 = getIpNum("10.44.0.0");
    private static final long d2 = getIpNum("10.69.0.255");

    private static long getIpNum(String ipAddress) {
        String [] ip = ipAddress.split("\\.");
        long a = Integer.parseInt(ip[0]);
        long b = Integer.parseInt(ip[1]);
        long c = Integer.parseInt(ip[2]);
        long d = Integer.parseInt(ip[3]);
        return a * 256 * 256 * 256 + b * 256 * 256 + c * 256 + d;
    }

    public static boolean isInnerIP(String ip){
        long n = getIpNum(ip);
        return (n >= a1 && n <= a2) || (n >= b1 && n <= b2) || (n >= c1 && n <= c2) || (n >= d1 && n <= d2);
    }

    public static String getIPAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

}
