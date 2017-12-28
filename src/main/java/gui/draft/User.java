package gui.draft;

/**
 * @Author by XuLiang
 * @Date 2017/12/28 8:52
 * @Email stanxu526@gmail.com
 */
public class User {
    private String name;
    private String name2;
    public User(String name,String name2){
        this.name = name;
        this.name2 = name2;
    }
    public String getName(){
        return this.name;
    }
    public String getPass(){
        return this.name2;
    }
}

