package com.recomendationapi.form;

import com.recomendationapi.model.Provider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.recomendationapi.TestUtils.USER_MEDIA_ID_VALID;
import static com.recomendationapi.TestUtils.buildProviderFormValid;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ProviderFormTest {

  @Test
  @DisplayName("build a valid entity")
  void testValidEntity() {
    ProviderForm form = buildProviderFormValid();
    Provider entity = form.convertToEntity();
    assertEquals("TEST PROVIDER", entity.getName());
    assertEquals("1 Test Street", entity.getAddress());
    assertEquals("provider@test.com", entity.getEmail());
    assertEquals("Manchester", entity.getCity());
    assertEquals("01234567", entity.getPhone());
    assertEquals("M1 1TT", entity.getPostCode());
    assertEquals(USER_MEDIA_ID_VALID, entity.getCreatorId());
  }

  @Test
  @DisplayName("build a valid entity name")
  void testEntityName() {
    ProviderForm form = buildProviderFormValid();
    Provider entity = form.convertToEntity();
    assertEquals("TEST PROVIDER", entity.getName());

    form.setName("Test provider");
    entity = form.convertToEntity();
    assertEquals("TEST PROVIDER", entity.getName());

    form.setName("   Test     Provider    ");
    entity = form.convertToEntity();
    assertEquals("TEST PROVIDER", entity.getName());

    form.setName("   test     prOvider    ");
    entity = form.convertToEntity();
    assertEquals("TEST PROVIDER", entity.getName());
  }
}
