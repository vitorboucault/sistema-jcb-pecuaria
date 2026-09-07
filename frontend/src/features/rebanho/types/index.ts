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
    id: string;
    brincoRgd: string;
    categoria: CategoriaAnimal;
    sexo: 'MACHO' | 'FEMEA';
    loteId: string | null;
    nomeLote?: string;
    pesoAtual: number | null;
    status: StatusAnimal;
    dataNascimento: string;
    dataMorte?: string | null;
}

export interface Lote {
    id: string;
    nome: string;
    fase: 'CRIA' | 'RECRIA' | 'ENGORDA';
    quantidadeAnimais: number;
    pesoMedio: number;
}

export interface Pagina<T> {
    conteudo: T[];
    numeroPagina: number;
    tamanhoPagina: number;
    totalElementos: number;
    totalPaginas: number;
}

export interface ResumoRebanho {
    total: number;
    porCategoria: Record<CategoriaAnimal, number>;
}

export interface CadastrarAnimalInput {
    origem: 'COMPRA' | 'NASCIMENTO';
    brincoRgd: string;
    categoria: CategoriaAnimal;
    sexo: 'MACHO' | 'FEMEA';
    peso: number;
    dataNascimento: string;
    dataEntrada: string;
    dataCompra?: string;
    valorCompra?: number;
    maeId?: string;
    loteId: string;
}

export interface AtualizarAnimalInput {
    brincoRgd: string;
    dataNascimento: string;
    sexo: 'MACHO' | 'FEMEA';
    categoria: CategoriaAnimal;
}
