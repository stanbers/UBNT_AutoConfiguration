package UITesting;

import org.junit.Test;
import pageObject.Login;

public class AddEventTest {
    @Test
    public void addEvent(){
        Login.login("http://train.ltrailways.com/");



    }

}
