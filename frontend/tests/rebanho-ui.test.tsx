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

    it('@spec:AC-370 cadastro existente inicia nascimento vazio e nascimento operacional continua obrigatório', async () => {
        const user = userEvent.setup();
        render(<RebanhoPage />);
        await screen.findByText(ativo.brincoRgd);
        await user.click(screen.getByRole('button', { name: /Novo Animal/ }));

        const nascimentoHistorico = screen.getByLabelText('Data de nascimento (opcional)') as HTMLInputElement;
        expect(nascimentoHistorico.value).toBe('');
        expect(nascimentoHistorico.required).toBe(false);
        expect(screen.getByLabelText('Data da pesagem')).toHaveProperty('max');

        await user.type(screen.getByLabelText('Brinco / RGD *'), 'HIST-SEM-NASCIMENTO');
        await user.click(screen.getByRole('button', { name: 'Salvar Animal' }));
        await waitFor(() => expect(rebanhoService.cadastrarAnimalInicial).toHaveBeenCalledOnce());
        expect(rebanhoService.cadastrarAnimalInicial).toHaveBeenCalledWith(expect.objectContaining({
            dataNascimento: null,
        }));

        await user.click(screen.getByRole('button', { name: /Novo Animal/ }));
        await user.click(screen.getByRole('button', { name: 'Novo nascimento' }));
        const nascimentoOperacional = screen.getByLabelText('Data de nascimento *') as HTMLInputElement;
        expect(nascimentoOperacional.required).toBe(true);
        expect(nascimentoOperacional.value).not.toBe('');

        await user.click(screen.getByRole('button', { name: 'Animal já existente' }));
        expect((screen.getByLabelText('Data de nascimento (opcional)') as HTMLInputElement).value).toBe('');
    });

    it('@spec:AC-370 aquisição histórica não aplica nascimento mínimo quando ele é desconhecido', async () => {
        const user = userEvent.setup();
        render(<RebanhoPage />);
        await screen.findByText(ativo.brincoRgd);
        await user.click(screen.getByRole('button', { name: /Novo Animal/ }));
        await user.selectOptions(screen.getByRole('combobox', { name: 'Origem histórica *' }), 'COMPRA');

        expect((screen.getByLabelText('Data da compra') as HTMLInputElement).min).toBe('');
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

    it('@spec:AC-357 exibe dados históricos somente para origem COMPRA', async () => {
        const user = userEvent.setup();
        render(<RebanhoPage />);
        await screen.findByText(ativo.brincoRgd);
        await user.click(screen.getByRole('button', { name: /Novo Animal/ }));

        expect(screen.queryByLabelText('Data da compra')).toBeNull();
        expect(screen.queryByLabelText('Valor da compra (R$)')).toBeNull();

        const origem = screen.getByRole('combobox', { name: 'Origem histórica *' });
        await user.selectOptions(origem, 'COMPRA');
        expect(screen.getByLabelText('Data da compra')).toBeDefined();
        expect(screen.getByLabelText('Valor da compra (R$)')).toBeDefined();

        await user.selectOptions(origem, 'NASCIMENTO');
        expect(screen.queryByLabelText('Data da compra')).toBeNull();
        expect(screen.queryByLabelText('Valor da compra (R$)')).toBeNull();
    });

    it('@spec:AC-358 aceita somente a data ou somente o valor histórico', async () => {
        const user = userEvent.setup();
        render(<RebanhoPage />);
        await screen.findByText(ativo.brincoRgd);
        await user.click(screen.getByRole('button', { name: /Novo Animal/ }));
        await user.clear(screen.getByLabelText('Data de nascimento (opcional)'));
        await user.type(screen.getByLabelText('Data de nascimento (opcional)'), '2019-04-10');
        await user.selectOptions(screen.getByRole('combobox', { name: 'Origem histórica *' }), 'COMPRA');
        await user.type(screen.getByLabelText('Brinco / RGD *'), 'HIST-DATA');
        await user.type(screen.getByLabelText('Data da compra'), '2021-05-10');
        await user.click(screen.getByRole('button', { name: 'Salvar Animal' }));

        await waitFor(() => expect(rebanhoService.cadastrarAnimalInicial).toHaveBeenCalledOnce());
        const somenteData = vi.mocked(rebanhoService.cadastrarAnimalInicial).mock.calls[0][0];
        expect(somenteData).toMatchObject({ dataCompraHistorica: '2021-05-10' });
        expect(somenteData).not.toHaveProperty('valorCompraHistorico');
    });

    it('@spec:AC-358 aceita somente o valor histórico', async () => {
        const user = userEvent.setup();
        render(<RebanhoPage />);
        await screen.findByText(ativo.brincoRgd);
        await user.click(screen.getByRole('button', { name: /Novo Animal/ }));
        await user.clear(screen.getByLabelText('Data de nascimento (opcional)'));
        await user.type(screen.getByLabelText('Data de nascimento (opcional)'), '2019-04-10');
        await user.selectOptions(screen.getByRole('combobox', { name: 'Origem histórica *' }), 'COMPRA');
        await user.type(screen.getByLabelText('Brinco / RGD *'), 'HIST-VALOR');
        await user.type(screen.getByLabelText('Valor da compra (R$)'), '3200');
        await user.click(screen.getByRole('button', { name: 'Salvar Animal' }));

        await waitFor(() => expect(rebanhoService.cadastrarAnimalInicial).toHaveBeenCalledOnce());
        const somenteValor = vi.mocked(rebanhoService.cadastrarAnimalInicial).mock.calls[0][0];
        expect(somenteValor).toMatchObject({ valorCompraHistorico: 3200 });
        expect(somenteValor).not.toHaveProperty('dataCompraHistorica');
    });

    it('@spec:AC-359 envia data e valor históricos ao cadastro inicial', async () => {
        const user = userEvent.setup();
        render(<RebanhoPage />);
        await screen.findByText(ativo.brincoRgd);
        await user.click(screen.getByRole('button', { name: /Novo Animal/ }));
        await user.clear(screen.getByLabelText('Data de nascimento (opcional)'));
        await user.type(screen.getByLabelText('Data de nascimento (opcional)'), '2019-04-10');
        await user.selectOptions(screen.getByRole('combobox', { name: 'Origem histórica *' }), 'COMPRA');
        await user.type(screen.getByLabelText('Brinco / RGD *'), 'HIST-COMPRA');
        await user.type(screen.getByLabelText('Data da compra'), '2021-05-10');
        await user.type(screen.getByLabelText('Valor da compra (R$)'), '3200');
        await user.click(screen.getByRole('button', { name: 'Salvar Animal' }));

        await waitFor(() => expect(rebanhoService.cadastrarAnimalInicial).toHaveBeenCalledOnce());
        expect(rebanhoService.cadastrarAnimalInicial).toHaveBeenCalledWith(expect.objectContaining({
            origem: 'COMPRA',
            brincoRgd: 'HIST-COMPRA',
            dataCompraHistorica: '2021-05-10',
            valorCompraHistorico: 3200,
        }));
    });

    it('@spec:AC-360 omite dados históricos quando nenhum deles é conhecido', async () => {
        const user = userEvent.setup();
        render(<RebanhoPage />);
        await screen.findByText(ativo.brincoRgd);
        await user.click(screen.getByRole('button', { name: /Novo Animal/ }));
        await user.selectOptions(screen.getByRole('combobox', { name: 'Origem histórica *' }), 'COMPRA');
        await user.type(screen.getByLabelText('Brinco / RGD *'), 'HIST-SEM-DADOS');
        await user.click(screen.getByRole('button', { name: 'Salvar Animal' }));

        await waitFor(() => expect(rebanhoService.cadastrarAnimalInicial).toHaveBeenCalledOnce());
        const semDados = vi.mocked(rebanhoService.cadastrarAnimalInicial).mock.calls[0][0];
        expect(semDados).not.toHaveProperty('dataCompraHistorica');
        expect(semDados).not.toHaveProperty('valorCompraHistorico');
        expect(Object.values(semDados)).not.toContain('');
        expect(Object.values(semDados)).not.toContain(0);
    });

    it('@spec:AC-361 remove dados ao trocar COMPRA para DESCONHECIDO', async () => {
        const user = userEvent.setup();
        render(<RebanhoPage />);
        await screen.findByText(ativo.brincoRgd);
        await user.click(screen.getByRole('button', { name: /Novo Animal/ }));
        const origem = screen.getByRole('combobox', { name: 'Origem histórica *' });
        await user.selectOptions(origem, 'COMPRA');
        await user.type(screen.getByLabelText('Data da compra'), '2021-05-10');
        await user.type(screen.getByLabelText('Valor da compra (R$)'), '3200');
        await user.selectOptions(origem, 'DESCONHECIDO');
        await user.type(screen.getByLabelText('Brinco / RGD *'), 'HIST-TROCA');
        await user.click(screen.getByRole('button', { name: 'Salvar Animal' }));

        await waitFor(() => expect(rebanhoService.cadastrarAnimalInicial).toHaveBeenCalledOnce());
        const desconhecido = vi.mocked(rebanhoService.cadastrarAnimalInicial).mock.calls[0][0];
        expect(desconhecido).not.toHaveProperty('dataCompraHistorica');
        expect(desconhecido).not.toHaveProperty('valorCompraHistorico');
    });

    it('@spec:AC-361 remove dados ao trocar COMPRA para NASCIMENTO histórico', async () => {
        const user = userEvent.setup();
        render(<RebanhoPage />);
        await screen.findByText(ativo.brincoRgd);
        await user.click(screen.getByRole('button', { name: /Novo Animal/ }));
        const origem = screen.getByRole('combobox', { name: 'Origem histórica *' });
        await user.selectOptions(origem, 'COMPRA');
        await user.type(screen.getByLabelText('Data da compra'), '2021-05-10');
        await user.type(screen.getByLabelText('Valor da compra (R$)'), '3200');
        await user.selectOptions(origem, 'NASCIMENTO');
        await user.type(screen.getByLabelText('Brinco / RGD *'), 'HIST-NASC');
        await user.click(screen.getByRole('button', { name: 'Salvar Animal' }));

        await waitFor(() => expect(rebanhoService.cadastrarAnimalInicial).toHaveBeenCalledOnce());
        const nascimentoHistorico = vi.mocked(rebanhoService.cadastrarAnimalInicial).mock.calls[0][0];
        expect(nascimentoHistorico).not.toHaveProperty('dataCompraHistorica');
        expect(nascimentoHistorico).not.toHaveProperty('valorCompraHistorico');
    });

    it('@spec:AC-362 novo nascimento nunca recebe dados de aquisição histórica', async () => {
        const user = userEvent.setup();
        render(<RebanhoPage />);
        await screen.findByText(ativo.brincoRgd);
        await user.click(screen.getByRole('button', { name: /Novo Animal/ }));
        await user.selectOptions(screen.getByRole('combobox', { name: 'Origem histórica *' }), 'COMPRA');
        await user.type(screen.getByLabelText('Data da compra'), '2021-05-10');
        await user.type(screen.getByLabelText('Valor da compra (R$)'), '3200');
        await user.click(screen.getByRole('button', { name: 'Novo nascimento' }));
        await user.type(screen.getByLabelText('Brinco / RGD *'), 'BEZ-SEM-COMPRA');
        await user.click(screen.getByRole('button', { name: 'Salvar Animal' }));

        await waitFor(() => expect(rebanhoService.cadastrarNascimento).toHaveBeenCalledOnce());
        const nascimento = vi.mocked(rebanhoService.cadastrarNascimento).mock.calls[0][0];
        expect(nascimento).not.toHaveProperty('dataCompraHistorica');
        expect(nascimento).not.toHaveProperty('valorCompraHistorico');
    });

    it('@spec:AC-363 limpa os dados de aquisição ao cancelar e reabrir', async () => {
        const user = userEvent.setup();
        render(<RebanhoPage />);
        await screen.findByText(ativo.brincoRgd);
        await user.click(screen.getByRole('button', { name: /Novo Animal/ }));
        await user.selectOptions(screen.getByRole('combobox', { name: 'Origem histórica *' }), 'COMPRA');
        await user.type(screen.getByLabelText('Data da compra'), '2021-05-10');
        await user.type(screen.getByLabelText('Valor da compra (R$)'), '3200');
        await user.click(screen.getByRole('button', { name: 'Cancelar' }));
        await user.click(screen.getByRole('button', { name: /Novo Animal/ }));

        expect((screen.getByRole('combobox', { name: 'Origem histórica *' }) as HTMLSelectElement).value).toBe('DESCONHECIDO');
        await user.selectOptions(screen.getByRole('combobox', { name: 'Origem histórica *' }), 'COMPRA');
        expect((screen.getByLabelText('Data da compra') as HTMLInputElement).value).toBe('');
        expect((screen.getByLabelText('Valor da compra (R$)') as HTMLInputElement).value).toBe('');
    });

    it('@spec:AC-344 rejeita peso atual informado sem data da pesagem', async () => {
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

    it('@spec:AC-344 rejeita data da pesagem informada sem peso atual', async () => {
        const user = userEvent.setup();
        render(<RebanhoPage />);
        await screen.findByText(ativo.brincoRgd);
        await user.click(screen.getByRole('button', { name: /Novo Animal/ }));
        await user.type(screen.getByLabelText('Brinco / RGD *'), 'HIST-103');
        await user.type(screen.getByLabelText('Data da pesagem'), '2025-01-15');
        await user.click(screen.getByRole('button', { name: 'Salvar Animal' }));

        expect(await screen.findByText('Peso atual e data da pesagem devem ser informados juntos.')).toBeDefined();
        expect(rebanhoService.cadastrarAnimalInicial).not.toHaveBeenCalled();
    });

    it('@spec:AC-340 @spec:AC-341 reseta o formulário ao cancelar e ao reabrir após sucesso', async () => {
        const user = userEvent.setup();
        const lote = { id: 'lote-1', nome: 'Lote 1', fase: 'CRIA' as const, quantidadeAnimais: 0, pesoMedio: 0 };
        vi.mocked(rebanhoService.listarLotes).mockResolvedValue([lote]);
        render(<RebanhoPage />);
        await screen.findByText(ativo.brincoRgd);

        await user.click(screen.getByRole('button', { name: /Novo Animal/ }));
        await user.click(screen.getByRole('button', { name: 'Novo nascimento' }));
        await user.type(screen.getByLabelText('Brinco / RGD *'), 'NAO-PERSISTIR');
        await user.selectOptions(screen.getByRole('combobox', { name: 'Matriz (opcional)' }), ativo.id);
        await user.selectOptions(screen.getByRole('combobox', { name: 'Lote (opcional)' }), lote.id);
        await user.click(screen.getByRole('button', { name: 'Cancelar' }));

        await user.click(screen.getByRole('button', { name: /Novo Animal/ }));
        expect(screen.getByRole('button', { name: 'Animal já existente' }).getAttribute('aria-pressed')).toBe('true');
        expect((screen.getByRole('combobox', { name: 'Origem histórica *' }) as HTMLSelectElement).value).toBe('DESCONHECIDO');
        expect((screen.getByLabelText('Brinco / RGD *') as HTMLInputElement).value).toBe('');
        expect((screen.getByLabelText('Peso atual (kg)') as HTMLInputElement).value).toBe('');
        expect((screen.getByLabelText('Data da pesagem') as HTMLInputElement).value).toBe('');
        expect((screen.getByRole('combobox', { name: 'Lote (opcional)' }) as HTMLSelectElement).value).toBe('');
        expect(screen.queryByRole('combobox', { name: 'Matriz (opcional)' })).toBeNull();

        await user.type(screen.getByLabelText('Brinco / RGD *'), 'SUCESSO-RESET');
        await user.click(screen.getByRole('button', { name: 'Salvar Animal' }));
        await waitFor(() => expect(rebanhoService.cadastrarAnimalInicial).toHaveBeenCalledOnce());
        await waitFor(() => expect(screen.queryByRole('heading', { name: 'CADASTRAR ANIMAL' })).toBeNull());

        await user.click(screen.getByRole('button', { name: /Novo Animal/ }));
        expect((screen.getByLabelText('Brinco / RGD *') as HTMLInputElement).value).toBe('');
        expect((screen.getByRole('combobox', { name: 'Lote (opcional)' }) as HTMLSelectElement).value).toBe('');
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

    it('@spec:AC-371 edição de animal sem nascimento abre vazia e salva null', async () => {
        const user = userEvent.setup();
        const animalSemNascimento: Animal = { ...ativo, id: 'sem-nascimento', brincoRgd: 'SEM-NASCIMENTO', dataNascimento: null };
        vi.mocked(rebanhoService.listarAnimais).mockResolvedValue([animalSemNascimento]);
        render(<RebanhoPage />);
        await screen.findByText(animalSemNascimento.brincoRgd);

        await user.click(linha(animalSemNascimento.brincoRgd).getByTitle('Editar'));
        const dataNascimento = screen.getByLabelText('Data de Nascimento (opcional)') as HTMLInputElement;
        expect(dataNascimento.value).toBe('');
        await user.click(screen.getByRole('button', { name: 'Salvar' }));

        await waitFor(() => expect(rebanhoService.atualizarAnimal).toHaveBeenCalledWith(
            animalSemNascimento.id,
            expect.objectContaining({ dataNascimento: null }),
        ));
    });
});
