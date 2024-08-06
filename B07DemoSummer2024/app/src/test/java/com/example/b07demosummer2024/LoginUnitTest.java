package com.example.b07demosummer2024;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.CompletableFuture;





@RunWith(MockitoJUnitRunner.class)
public class LoginUnitTest {
    @Mock
    LoginActivityModel model;
    @Mock
    LoginActivityView view;
    @Mock
    LoginActivityPresenter presenter;

    @Before
    public void setUp() {
        presenter = new LoginActivityPresenter(view, model);
    }

    @Test
    public void testLoginSuccess() throws Exception{
        String user = "john";
        String pass = "password";

        CompletableFuture<Boolean> future = CompletableFuture.completedFuture(true);

        when(model.queryDB(presenter, user, pass)).thenReturn(future);

        // Act
        CompletableFuture<Boolean> result = presenter.checkDB(user, pass);

        // Assert
        result.thenAccept(isAuthenticated -> {
            assertTrue(isAuthenticated);
        });
    }
    @Test
    public void testCheckDBFailure() throws Exception {
        // Arrange
        String username = "user";
        String password = "pass";
        CompletableFuture<Boolean> future = CompletableFuture.completedFuture(false);

        when(model.queryDB(presenter, username, password)).thenReturn(future);

        // Act
        CompletableFuture<Boolean> result = presenter.checkDB(username, password);

        // Assert
        assertFalse(result.get());
    }

}
