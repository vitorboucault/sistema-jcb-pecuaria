import { api } from '../../../shared/api/client';
import type { Animal, AtualizarAnimalInput, CadastrarAnimalInput, Lote, Pagina, ResumoRebanho } from '../types';

export const rebanhoService = {
    async listarAnimais(): Promise<Animal[]> {
        const response = await api.get<Pagina<Animal>>('v1/animais?tamanho=100');
        return response.data.conteudo;
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

    async excluirAnimal(id: string): Promise<void> {
        await api.delete(`v1/animais/${id}`);
    },

    async listarLotes(): Promise<Lote[]> {
        const response = await api.get<Pagina<Lote>>('v1/lotes');
        return response.data.conteudo;
    },
};
