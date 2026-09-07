package com.br.application.rest;

import com.br.usecase.manejo.RegistrarVendaAnimalUseCase;
import com.br.usecase.manejo.ReverterVendaAnimalUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class VendaControllerTest {

    @Test
    void reverterVendaRetornaNoContentEChamaCasoDeUso() throws Exception {
        UUID animalId = UUID.randomUUID();
        RegistrarVendaAnimalUseCase registrarVenda = mock(RegistrarVendaAnimalUseCase.class);
        ReverterVendaAnimalUseCase reverterVenda = mock(ReverterVendaAnimalUseCase.class);
        VendaController controller = new VendaController(registrarVenda, reverterVenda);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        mockMvc.perform(post("/api/v1/vendas/animais/{animalId}/reverter", animalId))
                .andExpect(status().isNoContent());

        verify(reverterVenda).executar(animalId);
    }
}
