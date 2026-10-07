export type CategoriaAnimal =
    | 'BEZERRO'
    | 'BEZERRA'
    | 'GARROTE'
    | 'NOVILHA'
    | 'VACA'
    | 'BOI'
    | 'TOURO';

export type StatusAnimal = 'ATIVO' | 'VENDIDO' | 'MORTO';

export type OrigemAnimal = 'COMPRA' | 'NASCIMENTO' | 'DESCONHECIDO';

export type FluxoCadastroAnimal = 'EXISTENTE' | 'NASCIMENTO';

export interface Animal {
    id: string;
    brincoRgd: string;
    categoria: CategoriaAnimal;
    sexo: 'MACHO' | 'FEMEA';
    loteId: string | null;
    nomeLote?: string;
    pesoAtual: number | null;
    status: StatusAnimal;
    dataNascimento: string | null;
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

export interface CadastrarAnimalInicialInput {
    origem: OrigemAnimal;
    brincoRgd: string;
    categoria: CategoriaAnimal;
    sexo: 'MACHO' | 'FEMEA';
    dataNascimento: string | null;
    loteId?: string;
    pesoAtual?: number;
    dataPesagem?: string;
    dataCompraHistorica?: string;
    valorCompraHistorico?: number;
}

export interface CadastrarNascimentoInput {
    origem: 'NASCIMENTO';
    brincoRgd: string;
    categoria: CategoriaAnimal;
    sexo: 'MACHO' | 'FEMEA';
    peso: number;
    dataNascimento: string;
    dataEntrada: string;
    maeId?: string;
    loteId?: string;
}

export type CadastroAnimalFormInput =
    | { fluxo: 'EXISTENTE'; dados: CadastrarAnimalInicialInput }
    | { fluxo: 'NASCIMENTO'; dados: CadastrarNascimentoInput };

export type CadastrarAnimalInput = CadastrarNascimentoInput;

export interface AtualizarAnimalInput {
    brincoRgd: string;
    dataNascimento: string | null;
    sexo: 'MACHO' | 'FEMEA';
    categoria: CategoriaAnimal;
}
