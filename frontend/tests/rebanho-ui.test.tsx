import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { cleanup, render, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { RebanhoPage } from '../src/features/rebanho/pages/RebanhoPage';
import { rebanhoService } from '../src/features/rebanho/api/rebanhoService';
import type { Animal } from '../src/features/rebanho/types';

vi.mock('../src/features/rebanho/api/rebanhoService', () => ({
    rebanhoService: {
        listarAnimais: vi.fn(), listarLotes: vi.fn(), obterResumo: vi.fn(), listarMatrizes: vi.fn(),
        cadastrarAnimalInicial: vi.fn(), cadastrarNascimento: vi.fn(), atualizarAnimal: vi.fn(), registrarMorte: vi.fn(),
        reverterMorte: vi.fn(), excluirAnimal: vi.fn(),
    },
}));

const ativo: Animal = {
    id: 'ativo', brincoRgd: 'VACA-ATIVA', categoria: 'VACA', sexo: 'FEMEA',
    loteId: null, pesoAtual: null, status: 'ATIVO', dataNascimento: '2020-05-10',
};
const morto: Animal = { ...ativo, id: 'morto', brincoRgd: 'VACA-MORTA', status: 'MORTO' };
const vendido: Animal = { ...ativo, id: 'vendido', brincoRgd: 'VACA-VENDIDA', status: 'VENDIDO' };

beforeEach(() => {
    vi.mocked(rebanhoService.listarAnimais).mockResolvedValue([ativo, morto, vendido]);
    vi.mocked(rebanhoService.listarLotes).mockResolvedValue([]);
    vi.mocked(rebanhoService.obterResumo).mockResolvedValue({
        total: 1, porCategoria: { BEZERRO: 0, BEZERRA: 0, GARROTE: 0, NOVILHA: 0, BOI: 0, VACA: 1, TOURO: 0 },
    });
    vi.mocked(rebanhoService.listarMatrizes).mockResolvedValue([ativo]);
    vi.mocked(rebanhoService.cadastrarAnimalInicial).mockResolvedValue('animal-inicial');
    vi.mocked(rebanhoService.cadastrarNascimento).mockResolvedValue('nascimento');
    vi.mocked(rebanhoService.atualizarAnimal).mockResolvedValue(undefined);
    vi.mocked(rebanhoService.reverterMorte).mockResolvedValue(undefined);
});
afterEach(cleanup);

function linha(brinco: string) {
    const row = screen.getByText(brinco).closest('tr');
    if (!row) throw new Error(`Linha não encontrada: ${brinco}`);
    return within(row);
}

describe('Rebanho renderizado', () => {
    it('@spec:AC-331 @spec:AC-207 mostra somente ações permitidas por status e ausência de pesagem', async () => {
        render(<RebanhoPage />);
        await screen.findByText(ativo.brincoRgd);
        expect(linha(ativo.brincoRgd).getByTitle('Morte')).toBeDefined();
        expect(linha(ativo.brincoRgd).getByTitle('Excluir')).toBeDefined();
        expect(linha(ativo.brincoRgd).getByText('Sem pesagem')).toBeDefined();
        expect(linha(ativo.brincoRgd).queryByTitle('Reverter morte')).toBeNull();
        expect(linha(morto.brincoRgd).getByTitle('Reverter morte')).toBeDefined();
        expect(linha(morto.brincoRgd).queryByTitle('Excluir')).toBeNull();
        expect(linha(morto.brincoRgd).queryByTitle('Morte')).toBeNull();
        expect(linha(vendido.brincoRgd).getAllByRole('button')).toHaveLength(1);
        expect(linha(vendido.brincoRgd).getByTitle('Editar')).toBeDefined();
    });

    it('@spec:AC-331 @spec:AC-206 consulta status no backend e aplica categoria na lista recebida', async () => {
        const user = userEvent.setup();
        vi.mocked(rebanhoService.listarAnimais).mockImplementation(async (status) =>
            [ativo, morto, vendido].filter((animal) => !status || animal.status === status));
        render(<RebanhoPage />);
        await screen.findByText(ativo.brincoRgd);
        await user.selectOptions(screen.getAllByRole('combobox')[0], 'MORTO');
        await waitFor(() => expect(screen.queryByText(ativo.brincoRgd)).toBeNull());
        expect(rebanhoService.listarAnimais).toHaveBeenLastCalledWith('MORTO');
        expect(screen.getByText(morto.brincoRgd)).toBeDefined();
        const consultas = vi.mocked(rebanhoService.listarAnimais).mock.calls.length;
        await user.selectOptions(screen.getAllByRole('combobox')[1], 'BOI');
        expect(screen.getByText('Nenhum animal encontrado para os filtros selecionados.')).toBeDefined();
        expect(rebanhoService.listarAnimais).toHaveBeenCalledTimes(consultas);
    });

    it('@spec:AC-331 @spec:AC-301 @spec:AC-346 matrizes elegíveis independem da tabela de mortos', async () => {
        const user = userEvent.setup();
        vi.mocked(rebanhoService.listarAnimais).mockResolvedValue([morto]);
        vi.mocked(rebanhoService.listarMatrizes).mockResolvedValue([
            ativo, morto, vendido,
            { ...ativo, id: 'macho', brincoRgd: 'MACHO', sexo: 'MACHO' },
            { ...ativo, id: 'bezerra', brincoRgd: 'BEZERRA', categoria: 'BEZERRA' },
        ]);
        render(<RebanhoPage />);
        await screen.findByText(morto.brincoRgd);
        await user.click(screen.getByRole('button', { name: /Novo Animal/ }));
        await user.click(screen.getByRole('button', { name: 'Novo nascimento' }));
        expect(await screen.findByRole('option', { name: 'VACA-ATIVA — VACA' })).toBeDefined();
        for (const brinco of ['VACA-MORTA', 'VACA-VENDIDA', 'MACHO', 'BEZERRA']) {
            expect(screen.queryByRole('option', { name: new RegExp(`${brinco} —`) })).toBeNull();
        }
    });

    it('@spec:AC-331 @spec:AC-312 @spec:AC-315 preserva matrizes na falha e remove aviso após recuperação', async () => {
        const user = userEvent.setup();
        const consoleError = vi.spyOn(console, 'error').mockImplementation(() => {});
        vi.mocked(rebanhoService.listarMatrizes)
            .mockResolvedValueOnce([ativo]).mockRejectedValueOnce(new Error('rede')).mockResolvedValueOnce([]);
        render(<RebanhoPage />);
        await screen.findByText(ativo.brincoRgd);
        await waitFor(() => expect(rebanhoService.listarMatrizes).toHaveBeenCalledTimes(1));
        await user.click(linha(ativo.brincoRgd).getByTitle('Editar'));
        await user.click(screen.getByRole('button', { name: 'Salvar' }));
        await screen.findByText(/Não foi possível atualizar a lista de matrizes/);
        expect(consoleError).toHaveBeenCalled();
        await user.click(screen.getByRole('button', { name: /Novo Animal/ }));
        await user.click(screen.getByRole('button', { name: 'Novo nascimento' }));
        expect(screen.getByRole('option', { name: 'VACA-ATIVA — VACA' })).toBeDefined();
        await user.click(screen.getByRole('button', { name: 'Cancelar' }));
        await user.click(linha(ativo.brincoRgd).getByTitle('Editar'));
        await user.click(screen.getByRole('button', { name: 'Salvar' }));
        await waitFor(() => expect(screen.queryByText(/Não foi possível atualizar a lista de matrizes/)).toBeNull());
        expect(rebanhoService.listarMatrizes).toHaveBeenCalledTimes(3);
        await user.click(screen.getByRole('button', { name: /Novo Animal/ }));
        await user.click(screen.getByRole('button', { name: 'Novo nascimento' }));
        expect(screen.queryByRole('option', { name: 'VACA-ATIVA — VACA' })).toBeNull();
    });

    it('@spec:AC-331 @spec:AC-312 reversão confirmada atualiza tabela e matrizes uma vez', async () => {
        const user = userEvent.setup();
        vi.spyOn(window, 'confirm').mockReturnValue(true);
        render(<RebanhoPage />);
        await screen.findByText(morto.brincoRgd);
        await waitFor(() => expect(rebanhoService.listarMatrizes).toHaveBeenCalledTimes(1));
        await user.click(linha(morto.brincoRgd).getByTitle('Reverter morte'));
        await waitFor(() => expect(rebanhoService.listarMatrizes).toHaveBeenCalledTimes(2));
        expect(rebanhoService.reverterMorte).toHaveBeenCalledExactlyOnceWith(morto.id);
        expect(rebanhoService.listarAnimais).toHaveBeenCalledTimes(2);
    });

    it('@spec:AC-340 @spec:AC-341 exibe os dois fluxos, origem histórica e lote sem seleção automática', async () => {
        const user = userEvent.setup();
        const lotes = [{ id: 'lote-1', nome: 'Lote 1', fase: 'CRIA' as const, quantidadeAnimais: 0, pesoMedio: 0 }];
        vi.mocked(rebanhoService.listarLotes).mockResolvedValue(lotes);
        render(<RebanhoPage />);
        await screen.findByText(ativo.brincoRgd);

        await user.click(screen.getByRole('button', { name: /Novo Animal/ }));

        expect(screen.getByRole('heading', { name: 'CADASTRAR ANIMAL' })).toBeDefined();
        expect(screen.getByRole('button', { name: 'Animal já existente' })).toBeDefined();
        expect(screen.getByRole('button', { name: 'Novo nascimento' })).toBeDefined();
        expect(screen.queryByText('Compra / Aquisição')).toBeNull();
        expect((screen.getByRole('combobox', { name: 'Origem histórica *' }) as HTMLSelectElement).value).toBe('DESCONHECIDO');
        const lote = screen.getByRole('combobox', { name: 'Lote (opcional)' });
        expect((lote as HTMLSelectElement).value).toBe('');
        expect(within(lote).getAllByRole('option')[0].textContent).toContain('Sem lote');
    });

    it('@spec:AC-343 envia peso atual e data da pesagem juntos no cadastro histórico', async () => {
        const user = userEvent.setup();
        render(<RebanhoPage />);
        await screen.findByText(ativo.brincoRgd);
        await user.click(screen.getByRole('button', { name: /Novo Animal/ }));
        await user.type(screen.getByLabelText('Brinco / RGD *'), 'HIST-100');
        await user.type(screen.getByLabelText('Peso atual (kg)'), '200');
        await user.type(screen.getByLabelText('Data da pesagem'), '2025-01-15');
        await user.click(screen.getByRole('button', { name: 'Salvar Animal' }));

        await waitFor(() => expect(rebanhoService.cadastrarAnimalInicial).toHaveBeenCalledOnce());
        expect(rebanhoService.cadastrarAnimalInicial).toHaveBeenCalledWith(expect.objectContaining({
            origem: 'DESCONHECIDO',
            brincoRgd: 'HIST-100',
            pesoAtual: 200,
            dataPesagem: '2025-01-15',
        }));
    });

    it('@spec:AC-344 rejeita peso atual ou data da pesagem isolados', async () => {
        const user = userEvent.setup();
        render(<RebanhoPage />);
        await screen.findByText(ativo.brincoRgd);
        await user.click(screen.getByRole('button', { name: /Novo Animal/ }));
        await user.type(screen.getByLabelText('Brinco / RGD *'), 'HIST-101');
        await user.type(screen.getByLabelText('Peso atual (kg)'), '200');
        await user.click(screen.getByRole('button', { name: 'Salvar Animal' }));

        expect(await screen.findByText('Peso atual e data da pesagem devem ser informados juntos.')).toBeDefined();
        expect(rebanhoService.cadastrarAnimalInicial).not.toHaveBeenCalled();
    });

    it('@spec:AC-345 @spec:AC-347 usa nascimento operacional e isola payloads ao alternar fluxos', async () => {
        const user = userEvent.setup();
        render(<RebanhoPage />);
        await screen.findByText(ativo.brincoRgd);

        await user.click(screen.getByRole('button', { name: /Novo Animal/ }));
        await user.click(screen.getByRole('button', { name: 'Novo nascimento' }));
        await user.type(screen.getByLabelText('Brinco / RGD *'), 'BEZ-100');
        await user.selectOptions(screen.getByRole('combobox', { name: 'Matriz (opcional)' }), ativo.id);
        await user.click(screen.getByRole('button', { name: 'Salvar Animal' }));

        await waitFor(() => expect(rebanhoService.cadastrarNascimento).toHaveBeenCalledOnce());
        const nascimento = vi.mocked(rebanhoService.cadastrarNascimento).mock.calls[0][0];
        expect(nascimento).toMatchObject({ origem: 'NASCIMENTO', maeId: ativo.id, peso: 35 });
        expect(nascimento).not.toHaveProperty('pesoAtual');
        expect(nascimento).not.toHaveProperty('dataPesagem');
        expect(nascimento).not.toHaveProperty('dataCompra');
        expect(nascimento).not.toHaveProperty('valorCompra');

        await user.click(screen.getByRole('button', { name: /Novo Animal/ }));
        await user.click(screen.getByRole('button', { name: 'Animal já existente' }));
        await user.clear(screen.getByLabelText('Brinco / RGD *'));
        await user.type(screen.getByLabelText('Brinco / RGD *'), 'HIST-102');
        await user.click(screen.getByRole('button', { name: 'Salvar Animal' }));

        await waitFor(() => expect(rebanhoService.cadastrarAnimalInicial).toHaveBeenCalledOnce());
        const existente = vi.mocked(rebanhoService.cadastrarAnimalInicial).mock.calls[0][0];
        expect(existente).toMatchObject({ origem: 'DESCONHECIDO', brincoRgd: 'HIST-102' });
        expect(existente).not.toHaveProperty('maeId');
        expect(existente).not.toHaveProperty('peso');
        expect(existente).not.toHaveProperty('dataEntrada');
        expect(existente).not.toHaveProperty('dataCompra');
        expect(existente).not.toHaveProperty('valorCompra');
    });
});
