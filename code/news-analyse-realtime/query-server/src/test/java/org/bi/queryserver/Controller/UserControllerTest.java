package org.bi.queryserver.Controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserControllerTest {
    @Autowired
    private UserController userController;

    @Test
    public void testGetUserFavors() throws Exception {
        userController.getUserFavors("U103269");
    }

    @Test
    public void testGetRecommendations() throws Exception {
        userController.getRecommendations("U100515");
    }
}
