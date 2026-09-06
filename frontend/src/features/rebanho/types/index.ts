export type CategoriaAnimal =
    | 'BEZERRO'
    | 'BEZERRA'
    | 'GARROTE'
    | 'NOVILHA'
    | 'VACA'
    | 'BOI'
    | 'TOURO';

export type StatusAnimal = 'ATIVO' | 'VENDIDO' | 'MORTO';

export interface Animal {
    id?: number;
    brinco: string;
    brincoRgd?: string;
    categoria: CategoriaAnimal;
    sexo: 'MACHO' | 'FEMEA';
    pesoEntrada: number;
    pesoAtual?: number;
    dataNascimentoOrEntrada: string;
    loteId: number;
    nomeLote?: string;
    status?: StatusAnimal;
}

export interface Lote {
    id: number;
    nome: string;
    descricao: string;
    quantidadeAnimais: number;
    pesoMedio: number;
    categoriaPredominante: CategoriaAnimal;
}