package gui;

import java.io.*;

/**
 * @Author by XuLiang
 * @Date 2017/12/23 10:07
 * @Email stanxu526@gmail.com
 */
public class UpdateConfigFile {

    private static String originalSSID = "wireless.1.ssid=ubnt_m2";
    private static String IP = "netconf.3.ip=10.1.2.103";
    private static String netmask1 = "netconf.1.netmask=255.255.255.0";
    private static String netmask2 = "netconf.2.netmask=255.255.255.0";
    private static String netmask3 = "netconf.3.netmask=255.255.255.0";
    private static String gatewayIP = "route.1.gateway=10.1.2.100";

    public static String read(String updatedSSID,String updatedIP,String updatedNetmask1,String updatedNetmask2,String updatedNetmask3,String gatewayIP){
        BufferedReader br = null;
        String line = null;
        StringBuffer buf = new StringBuffer();
        try {
            br = new BufferedReader(new FileReader(System.getProperty("user.dir")+"\\src\\main\\java\\configData\\aa.cfg"));
            while ((line = br.readLine()) != null) {
                if (line.trim().equals(originalSSID)) {
                    buf.delete(0,originalSSID.length());
                    buf.replace(0,originalSSID.length(),updatedSSID);
                }
                else if (line.trim().equals(IP)) {
                    buf.delete(0,IP.length());
                    buf.replace(0,IP.length(),updatedIP);
                }
                else if (line.trim().equals(netmask1)) {
                    buf.delete(0,netmask1.length());
                    buf.replace(0,netmask1.length(),updatedNetmask1);
                }
                else if (line.trim().equals(netmask2)) {
                    buf.delete(0,netmask2.length());
                    buf.replace(0,netmask2.length(),updatedNetmask2);
                }
                else if (line.trim().equals(netmask3)) {
                    buf.delete(0,netmask3.length());
                    buf.replace(0,netmask3.length(),updatedNetmask3);
                }
                else if (line.trim().equals(gatewayIP)) {
                    buf.delete(0,gatewayIP.length());
                    buf.replace(0,gatewayIP.length(),gatewayIP);
                }
                // 如果不用修改, 则按原来的内容回写
                else {
                    buf.append(line);
                }
                buf.append(System.getProperty("line.separator"));
            }

        }catch (Exception e){
            e.printStackTrace();
        } finally {
            // 关闭流
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    br = null;
                }
            }
        }

        return buf.toString();
    }

    public static void write(String content) {
        BufferedWriter bw = null;

        try {
            // 根据文件路径创建缓冲输出流
            bw = new BufferedWriter(new FileWriter(System.getProperty("user.dir")+"\\src\\main\\java\\configData\\aa.cfg"));
            // 将内容写入文件中
            bw.write(content);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭流
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    bw = null;
                }
            }
        }
    }

}
