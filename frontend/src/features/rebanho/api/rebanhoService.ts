import { api } from '../../../shared/api/client';
import type { Animal, AtualizarAnimalInput, CadastrarAnimalInput, Lote, Pagina, ResumoRebanho, StatusAnimal } from '../types';

export const rebanhoService = {
    async listarAnimaisPaginado(status?: StatusAnimal, pagina = 0, tamanho = 100): Promise<Pagina<Animal>> {
        const response = await api.get<Pagina<Animal>>('v1/animais', {
            params: {
                pagina,
                tamanho,
                ...(status ? { status } : {}),
            },
        });
        return response.data;
    },

    async listarAnimais(status?: StatusAnimal): Promise<Animal[]> {
        const pagina = await this.listarAnimaisPaginado(status);
        return pagina.conteudo;
    },

    async listarMatrizes(): Promise<Animal[]> {
        const primeiraPagina = await this.listarAnimaisPaginado('ATIVO');
        const paginasRestantes = await Promise.all(
            Array.from(
                { length: Math.max(primeiraPagina.totalPaginas - 1, 0) },
                (_, indice) => this.listarAnimaisPaginado('ATIVO', indice + 1, primeiraPagina.tamanhoPagina),
            ),
        );

        return [primeiraPagina, ...paginasRestantes].flatMap((pagina) => pagina.conteudo);
    },

    async obterResumo(): Promise<ResumoRebanho> {
        const response = await api.get<ResumoRebanho>('v1/animais/resumo');
        return response.data;
    },

    async cadastrarAnimal(animal: CadastrarAnimalInput): Promise<string>{
        const response = await api.post<string>('v1/animais', animal);
        return response.data;
    },

    async atualizarAnimal(id: string, animal: AtualizarAnimalInput): Promise<void> {
        await api.put(`v1/animais/${id}`, animal);
    },

    async registrarMorte(id: string, dataMorte: string): Promise<void> {
        await api.delete(`v1/animais/${id}/baixa-morte`, {
            params: { dataMorte },
        });
    },

    async reverterMorte(id: string): Promise<void> {
        await api.post(`v1/animais/${id}/reverter-morte`);
    },

    async excluirAnimal(id: string): Promise<void> {
        await api.delete(`v1/animais/${id}`);
    },

    async listarLotes(): Promise<Lote[]> {
        const response = await api.get<Pagina<Lote>>('v1/lotes');
        return response.data.conteudo;
    },
};
