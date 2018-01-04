package gui.draft;

import java.util.Arrays;
import java.util.Vector;

/**
 * @Author by XuLiang
 * @Date 2018/01/04 11:59
 * @Email stanxu526@gmail.com
 */
public class TestVection {
    public static void main(String[] args) {
        Integer[] object1={0,10,20,30,40,50,60,70,80,90,100};//数组定义
        Vector<Integer> object2;//Vector定义
        object2=new Vector<Integer>(Arrays.asList(object1));//[] -> array -> vector
        System.err.println(Arrays.toString(object1));
    }
}
