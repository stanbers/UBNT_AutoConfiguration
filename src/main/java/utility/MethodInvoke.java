package utility;

import java.lang.reflect.Method;

public class MethodInvoke {

    public void invokeMethod(){
        String method = ExcelUtils.getMethodFromExcel("TrainScheduling_ltrailways_login_master");
        try {
            Class cls = Class.forName("Login");
            Class partypes[] = new Class[2];
            partypes[0] = String.class;
            partypes[1] = String.class;
            try {
                Method meth = cls.getMethod(method, partypes);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

