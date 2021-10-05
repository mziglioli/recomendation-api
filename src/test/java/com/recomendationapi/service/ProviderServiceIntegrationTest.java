package com.recomendationapi.service;

import com.recomendationapi.form.ProviderForm;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.recomendationapi.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProviderServiceIntegrationTest {

  @Autowired
  private ProviderService service;
  @Autowired
  private UserService userService;

  @Test
  @DisplayName("Should NOT save the provider when user do not exists")
  @Order(1)
  void test__invalidUser() {
    long count = service.getAll().count().block();
    assertEquals(0, count);
    ProviderForm form = buildProviderFormValid();
    assertThrows(RuntimeException.class, () -> service.add(form).block());
    count = service.getAll().count().block();
    assertEquals(0, count);
  }

  @Test
  @DisplayName("Should save the provider when valid provider")
  @Order(2)
  void test__validProvider() {
    long count = service.getAll().count().block();
    assertEquals(0, count);

    // add an user
    userService.save(buildUserValid()).block();

    ProviderForm form = buildProviderFormValid();
    service.save(form).subscribe();
    count = service.getAll().count().block();
    assertEquals(1, count);
  }

  @Test
  @DisplayName("Should NOT save the provider when provider already exists")
  @Order(3)
  void test__invalidProviderExists() {
    long count = service.getAll().count().block();
    assertEquals(1, count);

    ProviderForm form = buildProviderFormValid();

    // save again the provider should throw
    assertThrows(RuntimeException.class, () -> service.add(form).block());


    // save again the provider with slight dif name should throw
    form.setName("test  provider ");
    assertThrows(RuntimeException.class, () -> service.add(form).block());

    count = service.getAll().count().block();
    assertEquals(1, count);
  }
}
