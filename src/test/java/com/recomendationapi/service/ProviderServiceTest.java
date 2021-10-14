package com.recomendationapi.service;

import com.recomendationapi.model.Provider;
import com.recomendationapi.repository.ProviderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.Optional;

import static com.recomendationapi.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class ProviderServiceTest {

  @SpyBean
  private ProviderService service;
  @MockBean
  private ProviderRepository repository;

  @Test
  @DisplayName("Should return the correct provider by name")
  void test__validProvider() {
    mock();
    Provider provider = service.getProvider(PROVIDER_NAME_VALID);
    assertNotNull(provider);
    assertEquals(PROVIDER_NAME_VALID, provider.getName());
  }

  @Test
  @DisplayName("should return empty provider when not find by name")
  void test__invalidProvider() {
    mock();
    Provider provider = service.getProvider("any");
    assertNotNull(provider);
    assertNull(provider.getId());
  }

  private void mock() {
    given(repository.findProviderByName(PROVIDER_NAME_VALID))
          .willReturn(Optional.of(buildProviderValid()));
    given(repository.findProviderByName("any"))
            .willReturn(Optional.empty());
  }
}
