import { api } from '../../../shared/api/client';
import type { Animal, CadastrarAnimalInput, Lote, Pagina } from '../types';

export const rebanhoService = {
    async listarAnimais(): Promise<Animal[]> {
        const response = await api.get<Pagina<Animal>>('v1/animais?tamanho=100');
        return response.data.conteudo;
    },

    async cadastrarAnimal(animal: CadastrarAnimalInput): Promise<string>{
        const response = await api.post<string>('v1/animais', animal);
        return response.data;
    },

    async listarLotes(): Promise<Lote[]> {
        const response = await api.get<Pagina<Lote>>('v1/lotes');
        return response.data.conteudo;
    },
};
