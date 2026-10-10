package com.br.application.rest;

import com.br.application.dto.AnimalResumoDTO;
import com.br.core.domain.enums.Categoria;
import com.br.core.domain.enums.FaseLote;
import com.br.core.domain.enums.OrigemAnimal;
import com.br.core.domain.enums.Sexo;
import com.br.core.domain.enums.Status;
import com.br.core.domain.model.Animal;
import com.br.core.domain.model.Lote;
import com.br.core.domain.model.Pagina;
import com.br.core.domain.model.Pesagem;
import com.br.core.domain.repository.AnimalRepository;
import com.br.core.domain.repository.LoteRepository;
import com.br.core.domain.repository.PesagemRepository;
import com.br.usecase.manejo.ReverterMorteAnimalUseCase;
import com.br.usecase.manejo.AtualizarAnimalUseCase;
import com.br.usecase.dto.AtualizarAnimalCommand;
import com.br.usecase.manejo.RegistrarAnimalInicialUseCase;
import com.br.usecase.manejo.RegistrarCompraAnimalUseCase;
import com.br.usecase.dto.RegistrarAnimalInicialCommand;
import org.mockito.ArgumentCaptor;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

class AnimalControllerTest {

    @Test
    @DisplayName("Endpoint de reversao de morte chama o caso de uso e retorna 204")
    void reverterMorteRetornaNoContent() throws Exception {
        UUID animalId = UUID.randomUUID();
        ReverterMorteAnimalUseCase reverterMorte = mock(ReverterMorteAnimalUseCase.class);
        AnimalController controller = new AnimalController(
                null, null, null, null, null, null, null, null, null, null, null, reverterMorte, null
        );
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        mockMvc.perform(post("/api/v1/animais/{id}/reverter-morte", animalId))
                .andExpect(status().isNoContent());

        verify(reverterMorte).executar(animalId);
    }

    @Test
    @DisplayName("@spec:AC-107 @spec:AC-369 Listagem de animais preserva nascimento nulo")
    void listarBuscaLotesEUltimasPesagensEmLote() {
        UUID loteId = UUID.randomUUID();
        UUID animalComLoteId = UUID.randomUUID();
        UUID animalSemLoteId = UUID.randomUUID();
        Animal animalComLote = new Animal(animalComLoteId, "A-001", LocalDate.now().minusYears(1), Sexo.MACHO,
                Categoria.BEZERRO, Status.ATIVO, null, loteId);
        Animal animalSemLote = new Animal(animalSemLoteId, "A-002", null, Sexo.FEMEA,
                Categoria.NOVILHA, Status.ATIVO, null, null);
        Lote lote = new Lote(loteId, "Lote 01", FaseLote.RECRIA, LocalDate.now().minusMonths(1), null);
        Pesagem ultimaPesagem = new Pesagem(UUID.randomUUID(), animalComLoteId, LocalDate.now().minusDays(2), 245.5, false);

        AnimalRepositoryFake animalRepository = new AnimalRepositoryFake(
                new Pagina<>(List.of(animalComLote, animalSemLote), 0, 10, 2, 1)
        );
        LoteRepositoryFake loteRepository = new LoteRepositoryFake(Map.of(loteId, lote));
        PesagemRepositoryFake pesagemRepository = new PesagemRepositoryFake(Map.of(animalComLoteId, ultimaPesagem));
        AnimalController controller = new AnimalController(
                null,
                null,
                animalRepository,
                loteRepository,
                pesagemRepository,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        Pagina<AnimalResumoDTO> pagina = controller.listar(0, 10, null).getBody();

        assertThat(pagina).isNotNull();
        assertThat(pagina.conteudo()).hasSize(2);
        assertThat(pagina.conteudo().getFirst().nomeLote()).isEqualTo("Lote 01");
        assertThat(pagina.conteudo().getFirst().pesoAtual()).isEqualTo(245.5);
        assertThat(pagina.conteudo().get(1).nomeLote()).isNull();
        assertThat(pagina.conteudo().get(1).pesoAtual()).isNull();
        assertThat(pagina.conteudo().get(1).dataNascimento()).isNull();
        assertThat(loteRepository.buscarPorIdsChamadas).isEqualTo(1);
        assertThat(loteRepository.buscarPorIdChamadas).isZero();
        assertThat(loteRepository.idsBuscados).containsExactly(loteId);
        assertThat(pesagemRepository.buscarUltimasEmLoteChamadas).isEqualTo(1);
        assertThat(pesagemRepository.buscarUltimaIndividualChamadas).isZero();
        assertThat(pesagemRepository.animalIdsBuscados).containsExactly(animalComLoteId, animalSemLoteId);
    }

    @Test
    @DisplayName("@spec:AC-101 Sem status usa somente a consulta padrão")
    void listarSemStatusUsaConsultaPadrao() {
        AnimalRepositoryFake animalRepository = new AnimalRepositoryFake(paginaVazia());
        AnimalController controller = criarController(animalRepository);

        controller.listar(0, 10, null);

        assertThat(animalRepository.buscarTodosPaginadoChamadas).isEqualTo(1);
        assertThat(animalRepository.buscarPorStatusPaginadoChamadas).isZero();
        assertThat(animalRepository.paginaConsultada).isEqualTo(0);
        assertThat(animalRepository.tamanhoConsultado).isEqualTo(10);
    }

    @Test
    @DisplayName("@spec:AC-102 Status ATIVO usa a consulta específica")
    void listarPorStatusAtivo() {
        AnimalRepositoryFake animalRepository = new AnimalRepositoryFake(
                paginaVazia(), Map.of(Status.ATIVO, paginaVazia()));
        AnimalController controller = criarController(animalRepository);

        controller.listar(0, 10, Status.ATIVO);

        assertThat(animalRepository.statusConsultado).isEqualTo(Status.ATIVO);
        assertThat(animalRepository.buscarPorStatusPaginadoChamadas).isEqualTo(1);
        assertThat(animalRepository.buscarTodosPaginadoChamadas).isZero();
        assertThat(animalRepository.paginaConsultada).isEqualTo(0);
        assertThat(animalRepository.tamanhoConsultado).isEqualTo(10);
    }

    @Test
    @DisplayName("@spec:AC-103 Status MORTO usa a consulta específica")
    void listarPorStatusMorto() {
        AnimalRepositoryFake animalRepository = new AnimalRepositoryFake(
                paginaVazia(), Map.of(Status.MORTO, paginaVazia()));
        AnimalController controller = criarController(animalRepository);

        controller.listar(0, 10, Status.MORTO);

        assertThat(animalRepository.statusConsultado).isEqualTo(Status.MORTO);
        assertThat(animalRepository.buscarPorStatusPaginadoChamadas).isEqualTo(1);
        assertThat(animalRepository.buscarTodosPaginadoChamadas).isZero();
        assertThat(animalRepository.paginaConsultada).isEqualTo(0);
        assertThat(animalRepository.tamanhoConsultado).isEqualTo(10);
    }

    @Test
    @DisplayName("@spec:AC-104 Status VENDIDO usa a consulta específica")
    void listarPorStatusVendido() {
        AnimalRepositoryFake animalRepository = new AnimalRepositoryFake(
                paginaVazia(), Map.of(Status.VENDIDO, paginaVazia()));
        AnimalController controller = criarController(animalRepository);

        controller.listar(0, 10, Status.VENDIDO);

        assertThat(animalRepository.statusConsultado).isEqualTo(Status.VENDIDO);
        assertThat(animalRepository.buscarPorStatusPaginadoChamadas).isEqualTo(1);
        assertThat(animalRepository.buscarTodosPaginadoChamadas).isZero();
        assertThat(animalRepository.paginaConsultada).isEqualTo(0);
        assertThat(animalRepository.tamanhoConsultado).isEqualTo(10);
    }

    @Test
    @DisplayName("@spec:AC-105 Status inválido retorna 400 sem consultar o repositório")
    void listarComStatusInvalidoRetornaBadRequestSemConsultarRepositorio() throws Exception {
        AnimalRepositoryFake animalRepository = new AnimalRepositoryFake(paginaVazia());
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(criarController(animalRepository)).build();

        mockMvc.perform(get("/api/v1/animais").param("status", "INVALIDO"))
                .andExpect(status().isBadRequest());

        assertThat(animalRepository.buscarTodosPaginadoChamadas).isZero();
        assertThat(animalRepository.buscarPorStatusPaginadoChamadas).isZero();
    }

    @Test
    @DisplayName("@spec:AC-106 Consulta filtrada preserva metadados de paginação")
    void listarPorStatusPreservaMetadadosDePaginacao() {
        Pagina<Animal> paginaFiltrada = new Pagina<>(List.of(), 2, 5, 11, 3);
        AnimalRepositoryFake animalRepository = new AnimalRepositoryFake(
                paginaVazia(), Map.of(Status.MORTO, paginaFiltrada));
        AnimalController controller = criarController(animalRepository);

        Pagina<AnimalResumoDTO> resultado = controller.listar(2, 5, Status.MORTO).getBody();

        assertThat(resultado).isNotNull();
        assertThat(resultado.numeroPagina()).isEqualTo(2);
        assertThat(resultado.tamanhoPagina()).isEqualTo(5);
        assertThat(resultado.totalElementos()).isEqualTo(11);
        assertThat(resultado.totalPaginas()).isEqualTo(3);
        assertThat(animalRepository.paginaConsultada).isEqualTo(2);
        assertThat(animalRepository.tamanhoConsultado).isEqualTo(5);
    }

    @Test
    @DisplayName("@spec:AC-325 @spec:AC-334 Cadastro inicial pela API retorna 201 e delega os dados informados")
    void cadastrarAnimalInicialRetornaCreatedEDelegaCommand() throws Exception {
        UUID animalId = UUID.randomUUID();
        UUID loteId = UUID.randomUUID();
        RegistrarAnimalInicialUseCase useCase = mock(RegistrarAnimalInicialUseCase.class);
        when(useCase.executar(any())).thenReturn(animalId);
        MockMvc mockMvc = criarMockMvcCadastro(criarControllerCadastro(useCase, null));

        mockMvc.perform(post("/api/v1/animais/cadastrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"brincoRgd":"VACA-001","dataNascimento":"2020-05-10","sexo":"FEMEA","categoria":"VACA","loteId":"%s","origem":"NASCIMENTO","pesoAtual":385.5,"dataPesagem":"2026-09-20"}
                                """.formatted(loteId)))
                .andExpect(status().isCreated())
                .andExpect(content().string("\"" + animalId + "\""));

        ArgumentCaptor<RegistrarAnimalInicialCommand> captor = ArgumentCaptor.forClass(RegistrarAnimalInicialCommand.class);
        verify(useCase).executar(captor.capture());
        RegistrarAnimalInicialCommand command = captor.getValue();
        assertThat(command.brincoRgd()).isEqualTo("VACA-001");
        assertThat(command.dataNascimento()).isEqualTo(LocalDate.of(2020, 5, 10));
        assertThat(command.sexo()).isEqualTo(Sexo.FEMEA);
        assertThat(command.categoria()).isEqualTo(Categoria.VACA);
        assertThat(command.loteId()).isEqualTo(loteId);
        assertThat(command.origem()).isEqualTo(OrigemAnimal.NASCIMENTO);
        assertThat(command.pesoAtual()).isEqualTo(385.5);
        assertThat(command.dataPesagem()).isEqualTo(LocalDate.of(2026, 9, 20));
    }

    @Test
    @DisplayName("@spec:AC-364 API de cadastro inicial aceita nascimento nulo")
    void cadastrarAnimalInicialAceitaNascimentoNulo() throws Exception {
        UUID animalId = UUID.randomUUID();
        RegistrarAnimalInicialUseCase useCase = mock(RegistrarAnimalInicialUseCase.class);
        when(useCase.executar(any())).thenReturn(animalId);
        MockMvc mockMvc = criarMockMvcCadastro(criarControllerCadastro(useCase, null));

        mockMvc.perform(post("/api/v1/animais/cadastrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"brincoRgd":"HIST-SEM-NASCIMENTO","dataNascimento":null,"sexo":"FEMEA","categoria":"VACA","origem":"DESCONHECIDO"}
                                """))
                .andExpect(status().isCreated());

        ArgumentCaptor<RegistrarAnimalInicialCommand> captor = ArgumentCaptor.forClass(RegistrarAnimalInicialCommand.class);
        verify(useCase).executar(captor.capture());
        assertThat(captor.getValue().dataNascimento()).isNull();
    }

    @Test
    @DisplayName("@spec:AC-365 Endpoint operacional continua exigindo nascimento")
    void endpointOperacionalRejeitaNascimentoNulo() throws Exception {
        var nascimentoUseCase = mock(com.br.usecase.manejo.RegistrarNascimentoUseCase.class);
        AnimalController controller = new AnimalController(
                nascimentoUseCase, null, null, null, null, null, null, null, null, null, null, null, null
        );
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setMessageConverters(new JacksonJsonHttpMessageConverter())
                .build();

        mockMvc.perform(post("/api/v1/animais")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"origem":"NASCIMENTO","brincoRgd":"BEZ-SEM-DATA","categoria":"BEZERRO","sexo":"MACHO","dataNascimento":null}
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(nascimentoUseCase);
    }

    @Test
    @DisplayName("@spec:AC-371 API de edição aceita nascimento nulo")
    void editarAnimalAceitaNascimentoNulo() throws Exception {
        UUID animalId = UUID.randomUUID();
        AtualizarAnimalUseCase useCase = mock(AtualizarAnimalUseCase.class);
        AnimalController controller = new AnimalController(
                null, null, null, null, null, null, null, null, null, useCase, null, null, null
        );
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setMessageConverters(new JacksonJsonHttpMessageConverter())
                .build();

        mockMvc.perform(put("/api/v1/animais/{id}", animalId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"brincoRgd":"HIST-SEM-DATA","dataNascimento":null,"sexo":"FEMEA","categoria":"VACA"}
                                """))
                .andExpect(status().isNoContent());

        ArgumentCaptor<AtualizarAnimalCommand> captor = ArgumentCaptor.forClass(AtualizarAnimalCommand.class);
        verify(useCase).executar(captor.capture());
        assertThat(captor.getValue().dataNascimento()).isNull();
    }

    @Test
    @DisplayName("@spec:AC-326 Cadastro inicial pela API preserva COMPRA sem acionar compra operacional")
    void cadastrarAnimalInicialComCompraUsaSomenteCasoDeUsoInicial() throws Exception {
        RegistrarAnimalInicialUseCase useCase = mock(RegistrarAnimalInicialUseCase.class);
        RegistrarCompraAnimalUseCase compraUseCase = mock(RegistrarCompraAnimalUseCase.class);
        when(useCase.executar(any())).thenReturn(UUID.randomUUID());
        MockMvc mockMvc = criarMockMvcCadastro(criarControllerCadastro(useCase, compraUseCase));

        mockMvc.perform(post("/api/v1/animais/cadastrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"brincoRgd":"NOVILHA-001","dataNascimento":"2022-03-20","sexo":"FEMEA","categoria":"NOVILHA","origem":"COMPRA"}
                                """))
                .andExpect(status().isCreated());

        ArgumentCaptor<RegistrarAnimalInicialCommand> captor = ArgumentCaptor.forClass(RegistrarAnimalInicialCommand.class);
        verify(useCase).executar(captor.capture());
        assertThat(captor.getValue().origem()).isEqualTo(OrigemAnimal.COMPRA);
        verifyNoInteractions(compraUseCase);
    }

    @Test
    @DisplayName("@spec:AC-350 Cadastro inicial pela API encaminha data e valor da aquisição histórica")
    void cadastrarAnimalInicialEncaminhaDadosDeAquisicaoHistorica() throws Exception {
        RegistrarAnimalInicialUseCase useCase = mock(RegistrarAnimalInicialUseCase.class);
        when(useCase.executar(any())).thenReturn(UUID.randomUUID());
        MockMvc mockMvc = criarMockMvcCadastro(criarControllerCadastro(useCase, null));

        mockMvc.perform(post("/api/v1/animais/cadastrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"brincoRgd":"NOVILHA-002","dataNascimento":"2019-04-10","sexo":"FEMEA","categoria":"VACA","origem":"COMPRA","dataCompraHistorica":"2021-05-10","valorCompraHistorico":3200.00}
                                """))
                .andExpect(status().isCreated());

        ArgumentCaptor<RegistrarAnimalInicialCommand> captor = ArgumentCaptor.forClass(RegistrarAnimalInicialCommand.class);
        verify(useCase).executar(captor.capture());
        assertThat(captor.getValue().dataCompraHistorica()).isEqualTo(LocalDate.of(2021, 5, 10));
        assertThat(captor.getValue().valorCompraHistorico()).isEqualByComparingTo("3200.00");
    }

    @Test
    @DisplayName("@spec:AC-327 @spec:AC-335 Cadastro inicial pela API aceita lote e peso ausentes")
    void cadastrarAnimalInicialAceitaLoteAusente() throws Exception {
        RegistrarAnimalInicialUseCase useCase = mock(RegistrarAnimalInicialUseCase.class);
        when(useCase.executar(any())).thenReturn(UUID.randomUUID());
        MockMvc mockMvc = criarMockMvcCadastro(criarControllerCadastro(useCase, null));

        mockMvc.perform(post("/api/v1/animais/cadastrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"brincoRgd":"BOI-001","dataNascimento":"2021-01-15","sexo":"MACHO","categoria":"BOI","origem":"DESCONHECIDO"}
                                """))
                .andExpect(status().isCreated());

        ArgumentCaptor<RegistrarAnimalInicialCommand> captor = ArgumentCaptor.forClass(RegistrarAnimalInicialCommand.class);
        verify(useCase).executar(captor.capture());
        assertThat(captor.getValue().loteId()).isNull();
        assertThat(captor.getValue().pesoAtual()).isNull();
        assertThat(captor.getValue().dataPesagem()).isNull();
    }

    @ParameterizedTest
    @MethodSource("requestsInvalidosParaCadastroInicial")
    @DisplayName("@spec:AC-328 Cadastro inicial inválido retorna 400 sem executar o caso de uso")
    void cadastrarAnimalInicialInvalidoRetornaBadRequestSemExecutarCasoDeUso(String requestInvalido) throws Exception {
        RegistrarAnimalInicialUseCase useCase = mock(RegistrarAnimalInicialUseCase.class);
        MockMvc mockMvc = criarMockMvcCadastro(criarControllerCadastro(useCase, null));

        mockMvc.perform(post("/api/v1/animais/cadastrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestInvalido))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(useCase);
    }

    private static Stream<String> requestsInvalidosParaCadastroInicial() {
        return Stream.of(
                "{\"brincoRgd\":\" \" ,\"dataNascimento\":\"2020-05-10\",\"sexo\":\"FEMEA\",\"categoria\":\"VACA\",\"origem\":\"NASCIMENTO\"}",
                "{\"brincoRgd\":\"VACA-001\",\"dataNascimento\":\"2020-05-10\",\"categoria\":\"VACA\",\"origem\":\"NASCIMENTO\"}",
                "{\"brincoRgd\":\"VACA-001\",\"dataNascimento\":\"2020-05-10\",\"sexo\":\"FEMEA\",\"origem\":\"NASCIMENTO\"}",
                "{\"brincoRgd\":\"VACA-001\",\"dataNascimento\":\"2020-05-10\",\"sexo\":\"FEMEA\",\"categoria\":\"VACA\"}",
                "{\"brincoRgd\":\"VACA-001\",\"dataNascimento\":\"2020-05-10\",\"sexo\":\"FEMEA\",\"categoria\":\"VACA\",\"origem\":\"INVALIDA\"}"
        );
    }

    private static Pagina<Animal> paginaVazia() {
        return new Pagina<>(List.of(), 0, 10, 0, 0);
    }

    private static AnimalController criarController(AnimalRepository animalRepository) {
        return new AnimalController(
                null, null, animalRepository, new LoteRepositoryFake(Map.of()),
                new PesagemRepositoryFake(Map.of()), null, null, null, null, null, null, null, null
        );
    }

    private static AnimalController criarControllerCadastro(
            RegistrarAnimalInicialUseCase useCase,
            RegistrarCompraAnimalUseCase compraUseCase
    ) {
        return new AnimalController(
                null, null, null, null, null, null, null, compraUseCase, null, null, null, null, useCase
        );
    }

    private static MockMvc criarMockMvcCadastro(AnimalController controller) {
        return MockMvcBuilders.standaloneSetup(controller)
                .setMessageConverters(new JacksonJsonHttpMessageConverter())
                .build();
    }

    private static class AnimalRepositoryFake implements AnimalRepository {
        private final Pagina<Animal> pagina;
        private final Map<Status, Pagina<Animal>> paginasPorStatus;
        private int buscarTodosPaginadoChamadas;
        private int buscarPorStatusPaginadoChamadas;
        private int paginaConsultada;
        private int tamanhoConsultado;
        private Status statusConsultado;

        private AnimalRepositoryFake(Pagina<Animal> pagina) {
            this(pagina, Map.of());
        }

        private AnimalRepositoryFake(Pagina<Animal> pagina, Map<Status, Pagina<Animal>> paginasPorStatus) {
            this.pagina = pagina;
            this.paginasPorStatus = paginasPorStatus;
        }

        @Override
        public Animal salvar(Animal animal) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<Animal> buscarPorId(UUID id) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<Animal> buscarPorBrinco(String brinco) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<Animal> buscarAnimaisElegiveisParaEvolucao() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Pagina<Animal> buscarTodosPaginado(int pagina, int tamanho) {
            buscarTodosPaginadoChamadas++;
            paginaConsultada = pagina;
            tamanhoConsultado = tamanho;
            return this.pagina;
        }

        @Override
        public Pagina<Animal> buscarPorStatusPaginado(Status status, int pagina, int tamanho) {
            buscarPorStatusPaginadoChamadas++;
            paginaConsultada = pagina;
            tamanhoConsultado = tamanho;
            statusConsultado = status;
            return paginasPorStatus.get(status);
        }

        @Override
        public List<Animal> buscarPorLote(UUID loteId) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long contarAnimaisAtivos() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Map<Categoria, Long> contarAtivosPorCategoria() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void excluirPorId(UUID id) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean existeFilhoComMaeId(UUID maeId) {
            return false;
        }
    }

    private static class LoteRepositoryFake implements LoteRepository {
        private final Map<UUID, Lote> lotes;
        private int buscarPorIdsChamadas;
        private int buscarPorIdChamadas;
        private List<UUID> idsBuscados = List.of();

        private LoteRepositoryFake(Map<UUID, Lote> lotes) {
            this.lotes = lotes;
        }

        @Override
        public void salvar(Lote lote) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<Lote> buscarPorId(UUID id) {
            buscarPorIdChamadas++;
            throw new UnsupportedOperationException();
        }

        @Override
        public Map<UUID, Lote> buscarPorIds(List<UUID> ids) {
            buscarPorIdsChamadas++;
            idsBuscados = ids;
            return lotes;
        }

        @Override
        public List<Lote> buscarAtivos() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Pagina<Lote> buscarTodosPaginado(int pagina, int tamanho) {
            throw new UnsupportedOperationException();
        }
    }

    private static class PesagemRepositoryFake implements PesagemRepository {
        private final Map<UUID, Pesagem> pesagens;
        private int buscarUltimasEmLoteChamadas;
        private int buscarUltimaIndividualChamadas;
        private List<UUID> animalIdsBuscados = List.of();

        private PesagemRepositoryFake(Map<UUID, Pesagem> pesagens) {
            this.pesagens = pesagens;
        }

        @Override
        public void salvar(Pesagem pesagem) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void excluirPorId(UUID id) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<Pesagem> buscarPesagemPorId(UUID id) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<Pesagem> buscarUltimaPesagemDoAnimal(UUID animalId) {
            buscarUltimaIndividualChamadas++;
            throw new UnsupportedOperationException();
        }

        @Override
        public Map<UUID, Pesagem> buscarUltimasPesagensPorAnimalIds(List<UUID> animalIds) {
            buscarUltimasEmLoteChamadas++;
            animalIdsBuscados = animalIds;
            return pesagens;
        }

        @Override
        public List<Pesagem> buscarHistoricoPorAnimal(UUID id) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean existePorAnimalId(UUID animalId) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Double calcularGanhoPesoTotalNoPeriodo(LocalDate inicioSafra, LocalDate fimSafra) {
            throw new UnsupportedOperationException();
        }
    }
}
