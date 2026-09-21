import assert from 'node:assert/strict';
import fs from 'node:fs';
import { it } from 'node:test';

it('@spec:AC-329 documentação distingue histórico existente e evolução futura', () => {
    const dominio = fs.readFileSync(new URL('../../docs/product/rebanho.md', import.meta.url), 'utf8');
    const banco = fs.readFileSync(new URL('../../docs/architecture/database.md', import.meta.url), 'utf8');
    assert.match(dominio, /Cadastro inicial \/ Nascimento \/ Compra/);
    assert.doesNotMatch(dominio, /Reversão de Morte \/ Venda/);
    assert.match(dominio, /sem registrar histórico individual de transferências/);
    assert.match(banco, /V9__Add_Origem_Animal.sql/);
    assert.match(banco, /DESCONHECIDO/);
});
