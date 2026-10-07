import { describe, expect, it, vi } from 'vitest';
import { rebanhoService } from '../src/features/rebanho/api/rebanhoService';
import { api } from '../src/shared/api/client';
import type { Animal, Pagina } from '../src/features/rebanho/types';

vi.mock('../src/shared/api/client', () => ({ api: { get: vi.fn(), post: vi.fn() } }));

describe('paginação existente de matrizes', () => {
    it('@spec:AC-331 @spec:AC-313 consulta todas as páginas e mantém matrizes além da primeira', async () => {
        const animais = Array.from({ length: 205 }, (_, i): Animal => ({
            id: String(i), brincoRgd: `MATRIZ-${i}`, sexo: 'FEMEA', categoria: 'VACA',
            status: 'ATIVO', loteId: null, pesoAtual: null, dataNascimento: '2020-01-01',
        }));
        vi.mocked(api.get).mockImplementation(async (_url, config) => {
            const params = config?.params as Record<string, unknown> | undefined;
            const pagina = Number(params?.pagina);
            const data: Pagina<Animal> = {
                conteudo: animais.slice(pagina * 100, (pagina + 1) * 100),
                numeroPagina: pagina, tamanhoPagina: 100, totalElementos: 205, totalPaginas: 3,
            };
            return { data };
        });
        expect(await rebanhoService.listarMatrizes()).toEqual(animais);
        expect(api.get).toHaveBeenCalledTimes(3);
        for (const pagina of [0, 1, 2]) {
            expect(api.get).toHaveBeenCalledWith('v1/animais', { params: { pagina, tamanho: 100, status: 'ATIVO' } });
        }
    });

    it('@spec:AC-331 @spec:AC-315 falha em página adicional rejeita carga sem entregar lista parcial', async () => {
        vi.mocked(api.get).mockResolvedValueOnce({
            data: { conteudo: [], numeroPagina: 0, tamanhoPagina: 100, totalElementos: 101, totalPaginas: 2 },
        }).mockRejectedValueOnce(new Error('falha da página 2'));
        await expect(rebanhoService.listarMatrizes()).rejects.toThrow('falha da página 2');
    });
});

describe('cadastro do animal', () => {
    it('@spec:AC-342 cadastra animal existente no endpoint dedicado', async () => {
        vi.mocked(api.post).mockResolvedValue({ data: 'animal-inicial' });

        const dados = {
            origem: 'COMPRA' as const,
            brincoRgd: 'BR-100',
            categoria: 'VACA' as const,
            sexo: 'FEMEA' as const,
            dataNascimento: '2020-01-01',
        };

        await expect(rebanhoService.cadastrarAnimalInicial(dados)).resolves.toBe('animal-inicial');
        expect(api.post).toHaveBeenCalledWith('v1/animais/cadastrar', dados);
    });

    it('@spec:AC-359 preserva dados de aquisição histórica no POST do cadastro inicial', async () => {
        vi.mocked(api.post).mockResolvedValue({ data: 'animal-compra-historica' });

        const dados = {
            origem: 'COMPRA' as const,
            brincoRgd: 'BR-COMPRA-HISTORICA',
            categoria: 'VACA' as const,
            sexo: 'FEMEA' as const,
            dataNascimento: '2019-04-10',
            dataCompraHistorica: '2021-05-10',
            valorCompraHistorico: 3200,
        };

        await expect(rebanhoService.cadastrarAnimalInicial(dados)).resolves.toBe('animal-compra-historica');
        expect(api.post).toHaveBeenCalledWith('v1/animais/cadastrar', dados);
    });

    it('@spec:AC-345 preserva nascimento no endpoint operacional', async () => {
        vi.mocked(api.post).mockResolvedValue({ data: 'bezerro' });

        const dados = {
            origem: 'NASCIMENTO' as const,
            brincoRgd: 'BEZ-100',
            categoria: 'BEZERRO' as const,
            sexo: 'MACHO' as const,
            peso: 35,
            dataNascimento: '2026-01-10',
            dataEntrada: '2026-01-10',
            maeId: 'matriz-1',
            loteId: 'lote-1',
        };

        await expect(rebanhoService.cadastrarNascimento(dados)).resolves.toBe('bezerro');
        expect(api.post).toHaveBeenCalledWith('v1/animais', dados);
    });
});
