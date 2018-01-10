package utility;

import com.google.common.io.Files;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author by XuLiang
 * @Date 2017/12/25 12:43
 * @Email stanxu526@gmail.com
 */
public class UpdateConfigFile {

    //the following fields standby to be updated
    private static final String originalSSID = "wireless.1.ssid=";
    private static final String originalIP = "netconf.3.ip=";
    private static final String originalNetmask1 = "netconf.1.netmask=";
    private static final String originalNetmask2 = "netconf.2.netmask=";
    private static final String originalNetmask3 = "netconf.3.netmask=";
    private static final String originalGateway = "route.1.gateway=";

    private static final String originalFruq = "radio.1.freq=";
    private static final String originalMACAddress = "wireless.1.ap=";

    /**
     * Update the specific config file based on swing GUI input values.
     * @param updatedSSID  the input SSID text
     * @param updatedIP   the input IP address text
     * @param updatedNetmask   the input netmask text
     * @param updateGatewayIP   the input gateway IP text
     * @param updatedFruq    the input frequency text , for M5
     * @param updatedMACAddress   the input MAC address text, for M5_ST
     * @param targetFile   the target config file
     */
    public static void updateFile(String updatedSSID,String updatedIP,String updatedNetmask,String updateGatewayIP,
                                  String updatedFruq, String updatedMACAddress,String targetFile){
        String relativePath = "D:\\ConfigFile\\"+targetFile+"_Config.cfg";
//        String relativePath = System.getProperty("user.dir")+"\\ConfigFile\\"+targetFile+"_Config.cfg";
        //update the above fields which passed from swing GUI input box in the config file
        List<String> newLines = new ArrayList<String>();
        try {
            for (String line : Files.readLines(new File(relativePath), StandardCharsets.UTF_8)) {
                if (line.contains(originalSSID)){
                    newLines.add(line.replace(line,originalSSID+updatedSSID));
                }
                else if(line.contains(originalIP)){
                    newLines.add(line.replace(line,originalIP+updatedIP));
                }
                else if (line.contains(originalNetmask1)){
                    newLines.add(line.replace(line,originalNetmask1+updatedNetmask));
                }
                else if (line.contains(originalNetmask2)){
                    newLines.add(line.replace(line,originalNetmask2+updatedNetmask));
                }
                else if (line.contains(originalNetmask3)){
                    newLines.add(line.replace(line,originalNetmask3+updatedNetmask));
                }
                else if (line.contains(originalGateway)){
                    newLines.add(line.replace(line,originalGateway+updateGatewayIP));
                }
                else if (updatedFruq != null && line.contains(originalFruq)){
                    newLines.add(line.replace(line,originalFruq+updatedFruq));
                }
                else if (updatedMACAddress != null && line.contains(originalMACAddress)){
                    newLines.add(line.replace(line,originalMACAddress+updatedMACAddress));
                }
                else{
                    newLines.add(line);
                }
            }

            //write into the same file
            FileWriter writer = new FileWriter(relativePath);
            for (String newLine: newLines) {
                writer.write(newLine);
                writer.append(System.getProperty("line.separator"));
            }
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
