import assert from 'node:assert/strict';
import fs from 'node:fs';
import { describe, it } from 'node:test';

const pageSource = fs.readFileSync(new URL('../src/features/rebanho/pages/RebanhoPage.tsx', import.meta.url), 'utf8');
const serviceSource = fs.readFileSync(new URL('../src/features/rebanho/api/rebanhoService.ts', import.meta.url), 'utf8');

describe('contrato do Rebanho', () => {
    it('@spec:AC-205 envia status por params e chama a reversão de morte', () => {
        assert.match(serviceSource, /listarAnimais\(status\?: StatusAnimal\)/);
        assert.match(serviceSource, /api\.get<Pagina<Animal>>\('v1\/animais', \{\s*params:/s);
        assert.match(serviceSource, /tamanho: 100/);
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
});
