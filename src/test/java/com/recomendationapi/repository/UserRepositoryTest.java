package com.recomendationapi.repository;

import com.recomendationapi.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.recomendationapi.TestUtils.USER_MEDIA_ID_VALID;
import static com.recomendationapi.TestUtils.buildUserValid;
import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ExtendWith(SpringExtension.class)
public class UserRepositoryTest {

    @Autowired
    UserRepository repository;

    @Test()
    @DisplayName("should find the user by mediaId")
    public void testValid() {
        User dbUser = repository.findUserByMediaId(USER_MEDIA_ID_VALID).block();
        assertNull(dbUser);

        User user = buildUserValid();
        repository.save(user).block();


        dbUser = repository.findUserByMediaId(USER_MEDIA_ID_VALID).block();
        assertNotNull(dbUser);
        assertEquals(USER_MEDIA_ID_VALID, dbUser.getMediaId());
    }

    @Test()
    @DisplayName("should find the user by mediaId updated")
    public void testValidUpdated() {
        User user = buildUserValid();
        repository.save(user).block();

        User dbUser = repository.findUserByMediaId(USER_MEDIA_ID_VALID).block();
        assertNotNull(dbUser);
        assertEquals(USER_MEDIA_ID_VALID, dbUser.getMediaId());
        assertFalse(dbUser.isActive());

        dbUser.setActive(true);
        repository.save(user).block();

        assertNotNull(dbUser);
        assertEquals(USER_MEDIA_ID_VALID, dbUser.getMediaId());
        assertTrue(dbUser.isActive());
    }

    @Test()
    @DisplayName("should NOT find the user by mediaId when user do not exists")
    public void testInvalid() {
        User user = buildUserValid();
        repository.save(user).block();

        User dbUser = repository.findUserByMediaId("invalid").block();
        assertNull(dbUser);
    }
}
