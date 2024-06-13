package org.bi.queryserver.Controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserControllerTest {
    @Autowired
    private UserController userController;

    @Test
    public void testGetHistory() throws Exception {
        userController.getUserHistory("U103269");
    }
}
