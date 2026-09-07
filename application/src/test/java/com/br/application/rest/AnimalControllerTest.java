package com.br.application.rest;

import com.br.application.dto.AnimalResumoDTO;
import com.br.core.domain.enums.Categoria;
import com.br.core.domain.enums.FaseLote;
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
import org.springframework.http.ResponseEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AnimalControllerTest {

    @Test
    @DisplayName("Endpoint de reversao de morte chama o caso de uso e retorna 204")
    void reverterMorteRetornaNoContent() {
        UUID animalId = UUID.randomUUID();
        ReverterMorteAnimalUseCaseSpy reverterMorte = new ReverterMorteAnimalUseCaseSpy();
        AnimalController controller = new AnimalController(
                null, null, null, null, null, null, null, null, null, null, null, reverterMorte
        );

        ResponseEntity<Void> response = controller.reverterMorte(animalId);

        assertThat(response.getStatusCode().value()).isEqualTo(204);
        assertThat(reverterMorte.animalIdRecebido).isEqualTo(animalId);
    }

    @Test
    @DisplayName("Listagem de animais busca lotes e ultimas pesagens em lote")
    void listarBuscaLotesEUltimasPesagensEmLote() {
        UUID loteId = UUID.randomUUID();
        UUID animalComLoteId = UUID.randomUUID();
        UUID animalSemLoteId = UUID.randomUUID();
        Animal animalComLote = new Animal(animalComLoteId, "A-001", LocalDate.now().minusYears(1), Sexo.MACHO,
                Categoria.BEZERRO, Status.ATIVO, null, loteId);
        Animal animalSemLote = new Animal(animalSemLoteId, "A-002", LocalDate.now().minusYears(2), Sexo.FEMEA,
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
                null
        );

        Pagina<AnimalResumoDTO> pagina = controller.listar(0, 10).getBody();

        assertThat(pagina).isNotNull();
        assertThat(pagina.conteudo()).hasSize(2);
        assertThat(pagina.conteudo().getFirst().nomeLote()).isEqualTo("Lote 01");
        assertThat(pagina.conteudo().getFirst().pesoAtual()).isEqualTo(245.5);
        assertThat(pagina.conteudo().get(1).nomeLote()).isNull();
        assertThat(pagina.conteudo().get(1).pesoAtual()).isNull();
        assertThat(loteRepository.buscarPorIdsChamadas).isEqualTo(1);
        assertThat(loteRepository.buscarPorIdChamadas).isZero();
        assertThat(loteRepository.idsBuscados).containsExactly(loteId);
        assertThat(pesagemRepository.buscarUltimasEmLoteChamadas).isEqualTo(1);
        assertThat(pesagemRepository.buscarUltimaIndividualChamadas).isZero();
        assertThat(pesagemRepository.animalIdsBuscados).containsExactly(animalComLoteId, animalSemLoteId);
    }

    private static class ReverterMorteAnimalUseCaseSpy extends ReverterMorteAnimalUseCase {
        private UUID animalIdRecebido;

        private ReverterMorteAnimalUseCaseSpy() {
            super(null);
        }

        @Override
        public void executar(UUID animalId) {
            animalIdRecebido = animalId;
        }
    }

    private static class AnimalRepositoryFake implements AnimalRepository {
        private final Pagina<Animal> pagina;

        private AnimalRepositoryFake(Pagina<Animal> pagina) {
            this.pagina = pagina;
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
            return this.pagina;
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
