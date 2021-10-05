package com.recomendationapi.service;

import com.recomendationapi.model.User;
import com.recomendationapi.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import reactor.core.publisher.Mono;

import static com.recomendationapi.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class UserServiceTest {

  @SpyBean
  private UserService service;
  @MockBean
  private UserRepository repository;

  @Test
  @DisplayName("given an existing user with mediaId should return the correct user")
  void test__validUser() {
    mock();
    User user = service.getUserByMediaId(USER_MEDIA_ID_VALID).block();
    assertNotNull(user);
    assertEquals("test", user.getName());
    assertEquals(USER_MEDIA_ID_VALID, user.getMediaId());
  }

  @Test
  @DisplayName("given an NOT existing user with mediaId should return an empty user")
  void test__invalidUser() {
    mock();
    User user = service.getUserByMediaId(USER_MEDIA_ID_INVALID).block();
    assertNotNull(user);
    assertNull(user.getEmail());
    assertNull(user.getMediaId());
  }

  private void mock() {
    given(repository.findUserByMediaId(USER_MEDIA_ID_VALID))
        .willReturn(Mono.just(buildUserValid()));
    given(repository.findUserByMediaId(USER_MEDIA_ID_INVALID))
            .willReturn(Mono.empty());
  }
}
