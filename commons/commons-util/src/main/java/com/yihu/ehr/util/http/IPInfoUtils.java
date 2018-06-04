package com.yihu.ehr.util.http;

import javax.servlet.http.HttpServletRequest;

/**
 * Utils - ip信息辅助工具类
 * Created by progr1mmer on 2018/1/18.
 */
public class IPInfoUtils {

    private static final long A1 = getIpNum("10.0.0.0");
    private static final long A2 = getIpNum("10.255.255.255");
    private static final long B1 = getIpNum("172.16.0.0");
    private static final long B2 = getIpNum("172.31.255.255");
    private static final long C1 = getIpNum("192.168.0.0");
    private static final long C2 = getIpNum("192.168.255.255");
    private static final long D1 = getIpNum("10.44.0.0");
    private static final long D2 = getIpNum("10.69.0.255");
    private static final long E1 = getIpNum("1.0.0.0");
    private static final long E2 = getIpNum("1.255.255.255");

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
        return (n >= A1 && n <= A2) || (n >= B1 && n <= B2) || (n >= C1 && n <= C2) || (n >= D1 && n <= D2) || (n >= E1 && n<=E2);
    }

    public static String getIPAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("proxy-client-iP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("wl-proxy-client-ip");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("http_client_ip");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("http_x_forwarded_for");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

}
