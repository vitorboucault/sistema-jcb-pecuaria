import assert from 'node:assert/strict';
import fs from 'node:fs';
import { describe, it } from 'node:test';

const pageSource = fs.readFileSync(new URL('../src/features/rebanho/pages/RebanhoPage.tsx', import.meta.url), 'utf8');
const serviceSource = fs.readFileSync(new URL('../src/features/rebanho/api/rebanhoService.ts', import.meta.url), 'utf8');

describe('contrato do Rebanho', () => {
    it('@spec:AC-301 carrega matrizes ativas independente do filtro da tabela', () => {
        assert.match(pageSource, /const \[matrizes, setMatrizes\]/);
        assert.match(pageSource, /rebanhoService\.listarMatrizes\(\)/);
        assert.match(pageSource, /animal\.sexo === 'FEMEA'/);
        assert.match(pageSource, /animal\.categoria === 'VACA' \|\| animal\.categoria === 'NOVILHA'/);
        assert.match(pageSource, /matrizes=\{matrizes\}/);
    });

    it('@spec:AC-205 envia status por params e chama a reversão de morte', () => {
        assert.match(serviceSource, /listarAnimais\(status\?: StatusAnimal\)/);
        assert.match(serviceSource, /api\.get<Pagina<Animal>>\('v1\/animais', \{\s*params:/s);
        assert.match(serviceSource, /listarAnimaisPaginado\(status\?: StatusAnimal, pagina = 0, tamanho = 100\)/);
        assert.match(serviceSource, /\.\.\.\(status \? \{ status \} : \{\}\)/);
        assert.match(serviceSource, /reverterMorte\(id: string\)/);
        assert.match(serviceSource, /api\.post\(`v1\/animais\/\$\{id\}\/reverter-morte`\)/);
    });

    it('@spec:AC-206 troca status via backend e mantém categoria como filtro local', () => {
        assert.match(pageSource, /rebanhoService\.listarAnimais\(filtroStatus\)/);
        assert.match(pageSource, /filtroCategoria === 'TODOS'/);
        assert.match(pageSource, /listaAnimaisSegura\.filter\(a => a\?\.categoria === filtroCategoria\)/);
    });

    it('@spec:AC-207 mostra apenas as ações permitidas por status', () => {
        assert.match(pageSource, /animal\.status === 'ATIVO'/);
        assert.match(pageSource, /animal\.status === 'MORTO'/);
        assert.match(pageSource, /reverterMorte/);
        assert.match(pageSource, /excluirAnimal/);
    });

    it('@spec:AC-208 diferencia badges verde, cinza e âmbar', () => {
        assert.match(pageSource, /bg-emerald/);
        assert.match(pageSource, /bg-stone/);
        assert.match(pageSource, /bg-amber/);
    });

    it('@spec:AC-209 gera a data de morte com calendário local', () => {
        assert.doesNotMatch(pageSource, /toISOString\(\)/);
        assert.match(pageSource, /getFullYear\(\)/);
        assert.match(pageSource, /getMonth\(\)/);
        assert.match(pageSource, /getDate\(\)/);
    });

    it('@spec:AC-210 usa mensagem neutra para lista vazia', () => {
        assert.match(pageSource, /Nenhum animal encontrado para os filtros selecionados\./);
        assert.doesNotMatch(pageSource, /endpoint `\/api\/v1\/animais` indisponível/);
    });

    it('@spec:AC-211 não adiciona reversão de venda nem dependência de frontend', () => {
        assert.doesNotMatch(pageSource, /reverterVenda/);
        assert.doesNotMatch(serviceSource, /reverterVenda/);
    });

    it('@spec:AC-312 sincroniza matrizes após cada mutação bem-sucedida do rebanho', () => {
        assert.match(pageSource, /const recarregarDados = useCallback\(async \(\) => \{\s*await Promise\.all\(\[carregarDados\(\), carregarMatrizes\(\)\]\);/s);

        for (const mutacao of [
            'handleCadastrarAnimal',
            'salvarEdicao',
            'registrarMorte',
            'reverterMorte',
            'excluirAnimal',
        ]) {
            const inicio = pageSource.indexOf(`const ${mutacao}`);
            const fim = pageSource.indexOf('\n    };', inicio);
            assert.notEqual(inicio, -1, `mutação ausente: ${mutacao}`);
            assert.notEqual(fim, -1, `fim ausente: ${mutacao}`);
            assert.match(pageSource.slice(inicio, fim), /await recarregarDados\(\);/);
        }
    });

    it('@spec:AC-313 disponibiliza matrizes elegíveis de todas as páginas', () => {
        assert.match(serviceSource, /async listarMatrizes\(\): Promise<Animal\[\]>/);
        assert.match(serviceSource, /primeiraPagina\.totalPaginas/);
        assert.match(serviceSource, /Array\.from\(\s*\{ length: Math\.max\(primeiraPagina\.totalPaginas - 1, 0\) \}/s);
        assert.match(serviceSource, /this\.listarAnimaisPaginado\('ATIVO', indice \+ 1, primeiraPagina\.tamanhoPagina\)/);
        assert.match(serviceSource, /\[primeiraPagina, \.\.\.paginasRestantes\]\.flatMap/);
        assert.match(pageSource, /rebanhoService\.listarMatrizes\(\)/);
    });

    it('@spec:AC-314 @spec:AC-348 recarrega o rebanho uma única vez após qualquer cadastro', () => {
        assert.doesNotMatch(pageSource, /onSuccess=\{recarregarDados\}/);

        const inicio = pageSource.indexOf('const handleCadastrarAnimal');
        const fim = pageSource.indexOf('\n    };', inicio);
        assert.notEqual(inicio, -1, 'handleCadastrarAnimal ausente');
        assert.notEqual(fim, -1, 'fim de handleCadastrarAnimal ausente');

        const cadastroSource = pageSource.slice(inicio, fim);
        assert.match(cadastroSource, /rebanhoService\.cadastrarAnimalInicial\(cadastro\.dados\)/);
        assert.match(cadastroSource, /rebanhoService\.cadastrarNascimento\(cadastro\.dados\)/);
        assert.match(cadastroSource, /await recarregarDados\(\);/);
        assert.equal([...cadastroSource.matchAll(/recarregarDados\(\)/g)].length, 1);
    });

    it('@spec:AC-342 separa os contratos dos endpoints de cadastro histórico e nascimento', () => {
        assert.match(serviceSource, /cadastrarAnimalInicial\(animal: CadastrarAnimalInicialInput\)/);
        assert.match(serviceSource, /api\.post<string>\('v1\/animais\/cadastrar', animal\)/);
        assert.match(serviceSource, /cadastrarNascimento\(animal: CadastrarNascimentoInput\)/);
        assert.match(serviceSource, /api\.post<string>\('v1\/animais', animal\)/);
    });

    it('@spec:AC-340 @spec:AC-341 não expõe campos da compra operacional no modal', () => {
        const modalSource = fs.readFileSync(new URL('../src/features/rebanho/components/AnimalModalForm.tsx', import.meta.url), 'utf8');
        assert.match(modalSource, /Animal já existente/);
        assert.match(modalSource, /Novo nascimento/);
        assert.match(modalSource, /DESCONHECIDO/);
        assert.match(modalSource, /<option value="">Sem lote<\/option>/);
        assert.doesNotMatch(modalSource, /Compra \/ Aquisição/);
        assert.doesNotMatch(modalSource, /Data da Compra/);
        assert.doesNotMatch(modalSource, /Valor da Compra/);
    });

    it('@spec:AC-315 preserva matrizes em erro e limpa o aviso após sucesso', () => {
        const inicio = pageSource.indexOf('const carregarMatrizes');
        const fim = pageSource.indexOf('\n    };', inicio);
        assert.notEqual(inicio, -1, 'carregarMatrizes ausente');
        assert.notEqual(fim, -1, 'fim de carregarMatrizes ausente');

        const matrizesSource = pageSource.slice(inicio, fim);
        assert.doesNotMatch(matrizesSource, /catch[\s\S]*setMatrizes\(\[\]\)/);
        assert.match(matrizesSource, /setErroMatrizes\(null\)/);
        assert.match(matrizesSource, /catch[\s\S]*setErroMatrizes\(/);
        assert.match(pageSource, /\{erroMatrizes && \(/);
        assert.match(pageSource, /Não foi possível atualizar a lista de matrizes/);
    });
});
