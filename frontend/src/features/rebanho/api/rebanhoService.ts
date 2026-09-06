import { api } from '../../../shared/api/client';
import type { Animal, Lote } from '../types';

export const rebanhoService = {
    async listarAnimais(): Promise<Animal[]> {
        const response = await api.get<Animal[]>('v1/animais');
        return response.data;
    },

    async cadastrarAnimal(animal: Omit<Animal, 'id' | 'pesoAtual'>): Promise<Animal>{
        const response = await api.post<Animal>('v1/animais', animal);
        return response.data;
    },

    async listarLotes(): Promise<Lote[]> {
        const response = await api.get<Lote[]>('v1/lotes');
        return response.data;
    },
};