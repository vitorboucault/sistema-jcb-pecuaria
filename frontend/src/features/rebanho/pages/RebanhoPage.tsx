import { useEffect, useState, useCallback } from 'react';
import { isAxiosError } from 'axios';
import { rebanhoService } from '../api/rebanhoService';
import type { Animal, AtualizarAnimalInput, CadastrarAnimalInput, CategoriaAnimal, Lote, ResumoRebanho } from '../types';
import { AnimalModalForm } from '../components/AnimalModalForm';
import { Users, Plus, Tag, AlertCircle, Activity, Pencil, Skull, Trash2, X, Save } from 'lucide-react';

const categoriasResumo: CategoriaAnimal[] = ['BEZERRO', 'BEZERRA', 'GARROTE', 'NOVILHA', 'BOI', 'VACA', 'TOURO'];

export const RebanhoPage = () => {
    const [animais, setAnimais] = useState<Animal[]>([]);
    const [lotes, setLotes] = useState<Lote[]>([]);
    const [resumo, setResumo] = useState<ResumoRebanho | null>(null);
    const [loading, setLoading] = useState(true);
    const [erroBanco, setErroBanco] = useState<string | null>(null);
    const [filtroCategoria, setFiltroCategoria] = useState<string>('TODOS');
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [animalEmEdicao, setAnimalEmEdicao] = useState<Animal | null>(null);
    const [formEdicao, setFormEdicao] = useState<AtualizarAnimalInput>({
        brincoRgd: '',
        dataNascimento: '',
        sexo: 'MACHO',
        categoria: 'BEZERRO',
    });

    const carregarDados = useCallback(async () => {
        setLoading(true);
        try {
            const [dadosAnimais, dadosLotes, dadosResumo] = await Promise.all([
                rebanhoService.listarAnimais(),
                rebanhoService.listarLotes(),
                rebanhoService.obterResumo(),
            ]);

            // BLINDAGEM CRUCIAL: Assegura que o estado seja sempre um array, mesmo se a API falhar
            setAnimais(Array.isArray(dadosAnimais) ? dadosAnimais : []);
            setLotes(Array.isArray(dadosLotes) ? dadosLotes : []);
            setResumo(dadosResumo ?? null);
            setErroBanco(null);
        } catch (err: unknown) {
            console.error('Falha ao comunicar com o backend:', err);
            const mensagem = isAxiosError<{ mensagem?: string }>(err) ? err.response?.data?.mensagem : undefined;
            setErroBanco(mensagem || 'Erro ao carregar dados do banco PostgreSQL.');
            setAnimais([]);
            setLotes([]);
            setResumo(null);
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        const timeout = window.setTimeout(() => void carregarDados(), 0);
        return () => window.clearTimeout(timeout);
    }, [carregarDados]);

    const handleCadastrarAnimal = async (dados: CadastrarAnimalInput) => {
        await rebanhoService.cadastrarAnimal(dados);
        await carregarDados();
    };

    const abrirEdicao = (animal: Animal) => {
        setAnimalEmEdicao(animal);
        setFormEdicao({
            brincoRgd: animal.brincoRgd,
            dataNascimento: animal.dataNascimento,
            sexo: animal.sexo,
            categoria: animal.categoria,
        });
    };

    const salvarEdicao = async () => {
        if (!animalEmEdicao) return;

        try {
            await rebanhoService.atualizarAnimal(animalEmEdicao.id, formEdicao);
            setAnimalEmEdicao(null);
            await carregarDados();
        } catch (err: unknown) {
            console.error(err);
            const mensagem = isAxiosError<{ mensagem?: string }>(err) ? err.response?.data?.mensagem : undefined;
            setErroBanco(mensagem || 'Erro ao atualizar animal.');
        }
    };

    const registrarMorte = async (animal: Animal) => {
        const dataMorte = window.prompt('Informe a data da morte (YYYY-MM-DD):', new Date().toISOString().split('T')[0]);
        if (!dataMorte) return;
        if (!window.confirm(`Confirmar baixa por morte do animal ${animal.brincoRgd}?`)) return;

        try {
            await rebanhoService.registrarMorte(animal.id, dataMorte);
            await carregarDados();
        } catch (err: unknown) {
            console.error(err);
            const mensagem = isAxiosError<{ mensagem?: string }>(err) ? err.response?.data?.mensagem : undefined;
            setErroBanco(mensagem || 'Erro ao registrar morte do animal.');
        }
    };

    const excluirAnimal = async (animal: Animal) => {
        if (!window.confirm(`Excluir fisicamente o animal ${animal.brincoRgd}? Use apenas para cadastro feito por engano.`)) return;

        try {
            await rebanhoService.excluirAnimal(animal.id);
            await carregarDados();
        } catch (err: unknown) {
            console.error(err);
            const mensagem = isAxiosError<{ mensagem?: string }>(err) ? err.response?.data?.mensagem : undefined;
            setErroBanco(mensagem || 'Erro ao excluir animal. Verifique se ele possui histórico ou vínculos.');
        }
    };

    // Garante segurança caso animais não seja um array
    const listaAnimaisSegura = Array.isArray(animais) ? animais : [];
    const animaisFiltrados = filtroCategoria === 'TODOS'
        ? listaAnimaisSegura
        : listaAnimaisSegura.filter(a => a?.categoria === filtroCategoria);

    // Garante segurança caso lotes não seja um array
    const listaLotesSegura = Array.isArray(lotes) ? lotes : [];

    if (loading && listaAnimaisSegura.length === 0 && listaLotesSegura.length === 0 && !resumo) {
        return (
            <div className="flex h-96 items-center justify-center text-emerald-400 font-mono text-sm tracking-wider">
                SINCRONIZANDO CURRAL E DADOS DO SERVIDOR...
            </div>
        );
    }

    return (
        <div className="space-y-8 pb-12">
            {/* Cabeçalho */}
            <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 border-b border-stone-800 pb-6">
                <div>
                    <h1 className="text-2xl font-black text-white tracking-tight flex items-center gap-3">
                        <Users className="w-6 h-6 text-emerald-500" />
                        GESTÃO DE REBANHO E LOTES
                    </h1>
                    <p className="text-stone-400 text-xs uppercase tracking-widest mt-1">
                        Rastreabilidade Individual • Ciclo Pecuário
                    </p>
                </div>
                <button
                    onClick={() => setIsModalOpen(true)}
                    className="px-4 py-2.5 bg-emerald-600 hover:bg-emerald-500 text-stone-950 font-bold text-xs uppercase tracking-wider rounded-xl transition-all flex items-center gap-2 cursor-pointer shadow-lg shadow-emerald-950/30"
                >
                    <Plus className="w-4 h-4" /> Novo Animal / Nascimento
                </button>
            </div>

            {erroBanco && (
                <div className="p-4 bg-amber-950/40 border border-amber-700 text-amber-300 rounded-xl text-sm font-medium flex items-center gap-3">
                    <AlertCircle className="w-5 h-5 flex-shrink-0" />
                    <span>{erroBanco}</span>
                </div>
            )}

            {/* Resumo do Rebanho */}
            <div className="grid grid-cols-2 lg:grid-cols-8 gap-4">
                <div className="col-span-2 lg:col-span-1 border border-emerald-700/60 bg-emerald-950/30 rounded-xl p-5 shadow-xl">
                    <div className="flex items-center justify-between mb-3">
                        <span className="text-xs font-bold uppercase tracking-widest text-emerald-300">Rebanho Total</span>
                        <Activity className="w-4 h-4 text-emerald-400" />
                    </div>
                    <div className="text-3xl font-black text-white font-mono">{resumo?.total ?? 0}</div>
                    <div className="text-xs text-stone-400 mt-1">Animais ativos</div>
                </div>

                {categoriasResumo.map((categoria) => (
                    <div key={categoria} className="border border-stone-800 bg-stone-900/80 rounded-xl p-5 shadow-xl hover:border-emerald-500/50 transition-all">
                        <div className="text-[11px] font-bold uppercase tracking-widest text-stone-400 mb-3">
                            {categoria}
                        </div>
                        <div className="text-2xl font-black text-white font-mono">
                            {resumo?.porCategoria?.[categoria] ?? 0}
                        </div>
                        <div className="text-xs text-stone-500 mt-1">Ativos</div>
                    </div>
                ))}
            </div>

            {/* Tabela de Animais */}
            <div className="bg-stone-900/60 border border-stone-800 rounded-2xl p-6 shadow-xl">
                <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-6">
                    <h2 className="text-sm font-bold uppercase tracking-wider text-white">Inventário Individual de Brincos</h2>
                    <div className="flex items-center gap-2">
                        <span className="text-xs text-stone-400">Filtrar Categoria:</span>
                        <select
                            value={filtroCategoria}
                            onChange={(e) => setFiltroCategoria(e.target.value)}
                            className="bg-stone-950 border border-stone-800 text-stone-200 text-xs rounded-xl px-3 py-2 focus:outline-none focus:border-emerald-500"
                        >
                            <option value="TODOS">Todas as Categorias</option>
                            <option value="BEZERRO">Bezerro</option>
                            <option value="BEZERRA">Bezerra</option>
                            <option value="GARROTE">Garrote</option>
                            <option value="NOVILHA">Novilha</option>
                            <option value="BOI">Boi</option>
                            <option value="VACA">Vaca</option>
                            <option value="TOURO">Touro</option>
                        </select>
                    </div>
                </div>

                <div className="overflow-x-auto">
                    <table className="w-full text-left text-xs">
                        <thead className="bg-stone-950 text-stone-400 uppercase tracking-wider border-b border-stone-800 font-mono">
                        <tr>
                            <th className="px-4 py-3">Brinco Visual</th>
                            <th className="px-4 py-3">Categoria</th>
                            <th className="px-4 py-3">Sexo</th>
                            <th className="px-4 py-3">Lote Atual</th>
                            <th className="px-4 py-3">Última Pesagem</th>
                            <th className="px-4 py-3">Status</th>
                            <th className="px-4 py-3 text-right">Ações</th>
                        </tr>
                        </thead>
                        <tbody className="divide-y divide-stone-800 text-stone-300">
                        {animaisFiltrados.length === 0 ? (
                            <tr>
                                <td colSpan={7} className="text-center py-8 text-stone-500 font-mono">
                                    Nenhum animal cadastrado ou endpoint `/api/v1/animais` indisponível.
                                </td>
                            </tr>
                        ) : (
                            animaisFiltrados.map((animal) => (
                                <tr key={animal.id} className="hover:bg-stone-800/40 transition-colors">
                                    <td className="px-4 py-3.5 font-mono font-bold text-white flex items-center gap-2">
                                        <Tag className="w-3.5 h-3.5 text-emerald-500" />
                                        {animal?.brincoRgd || 'S/N'}
                                    </td>
                                    <td className="px-4 py-3.5 font-medium">{animal?.categoria || 'N/D'}</td>
                                    <td className="px-4 py-3.5 text-stone-400">{animal?.sexo || 'N/D'}</td>
                                    <td className="px-4 py-3.5 text-stone-300">{animal?.nomeLote || 'Sem Lote'}</td>
                                    <td className="px-4 py-3.5 font-mono text-emerald-400 font-bold">
                                        {animal?.pesoAtual == null ? 'Sem pesagem' : `${animal.pesoAtual} kg`}
                                    </td>
                                    <td className="px-4 py-3.5">
                      <span className={`px-2.5 py-1 rounded-full font-mono text-[10px] border ${
                          animal?.status === 'MORTO'
                              ? 'bg-stone-950 border-stone-700 text-stone-400'
                              : 'bg-emerald-950/60 border-emerald-800 text-emerald-400'
                      }`}>
                        {animal?.status || 'ATIVO'}
                      </span>
                                    </td>
                                    <td className="px-4 py-3.5">
                                        <div className="flex items-center justify-end gap-2">
                                            <button
                                                type="button"
                                                onClick={() => abrirEdicao(animal)}
                                                title="Editar"
                                                className="p-2 bg-stone-950 border border-stone-800 text-stone-300 hover:text-white hover:border-emerald-600 rounded-lg transition-colors cursor-pointer"
                                            >
                                                <Pencil className="w-3.5 h-3.5" />
                                            </button>
                                            {animal.status === 'ATIVO' && (
                                                <>
                                                    <button
                                                        type="button"
                                                        onClick={() => registrarMorte(animal)}
                                                        title="Morte"
                                                        className="p-2 bg-stone-950 border border-stone-800 text-amber-300 hover:border-amber-600 rounded-lg transition-colors cursor-pointer"
                                                    >
                                                        <Skull className="w-3.5 h-3.5" />
                                                    </button>
                                                    <button
                                                        type="button"
                                                        onClick={() => excluirAnimal(animal)}
                                                        title="Excluir"
                                                        className="p-2 bg-stone-950 border border-stone-800 text-red-300 hover:border-red-600 rounded-lg transition-colors cursor-pointer"
                                                    >
                                                        <Trash2 className="w-3.5 h-3.5" />
                                                    </button>
                                                </>
                                            )}
                                        </div>
                                    </td>
                                </tr>
                            ))
                        )}
                        </tbody>
                    </table>
                </div>
            </div>

            {/* Modal de Cadastro */}
            <AnimalModalForm
                lotes={listaLotesSegura}
                matrizes={listaAnimaisSegura.filter((animal) => animal.sexo === 'FEMEA')}
                isOpen={isModalOpen}
                onClose={() => setIsModalOpen(false)}
                onSuccess={carregarDados}
                onCadastrar={handleCadastrarAnimal}
            />

            {animalEmEdicao && (
                <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/80 backdrop-blur-sm p-4">
                    <div className="bg-stone-900 border border-stone-800 rounded-2xl max-w-lg w-full p-6 shadow-2xl">
                        <div className="flex items-center justify-between pb-4 border-b border-stone-800 mb-6">
                            <h2 className="text-lg font-black text-white flex items-center gap-2">
                                <Pencil className="w-5 h-5 text-emerald-500" />
                                EDITAR ANIMAL
                            </h2>
                            <button onClick={() => setAnimalEmEdicao(null)} className="text-stone-400 hover:text-white transition-colors cursor-pointer">
                                <X className="w-5 h-5" />
                            </button>
                        </div>

                        <div className="space-y-4">
                            <div className="grid grid-cols-2 gap-4">
                                <div>
                                    <label className="block text-xs font-bold uppercase tracking-wider text-stone-300 mb-1">
                                        Brinco / RGD *
                                    </label>
                                    <input
                                        type="text"
                                        value={formEdicao.brincoRgd}
                                        onChange={(e) => setFormEdicao((atual) => ({ ...atual, brincoRgd: e.target.value }))}
                                        className="w-full px-3.5 py-2.5 bg-stone-950 border border-stone-800 rounded-xl text-white focus:outline-none focus:border-emerald-500 font-mono"
                                    />
                                </div>
                                <div>
                                    <label className="block text-xs font-bold uppercase tracking-wider text-stone-300 mb-1">
                                        Data de Nascimento *
                                    </label>
                                    <input
                                        type="date"
                                        value={formEdicao.dataNascimento}
                                        onChange={(e) => setFormEdicao((atual) => ({ ...atual, dataNascimento: e.target.value }))}
                                        className="w-full px-3.5 py-2.5 bg-stone-950 border border-stone-800 rounded-xl text-white focus:outline-none focus:border-emerald-500 font-mono"
                                    />
                                </div>
                            </div>

                            <div className="grid grid-cols-2 gap-4">
                                <div>
                                    <label className="block text-xs font-bold uppercase tracking-wider text-stone-300 mb-1">
                                        Sexo *
                                    </label>
                                    <select
                                        value={formEdicao.sexo}
                                        onChange={(e) => setFormEdicao((atual) => ({ ...atual, sexo: e.target.value as 'MACHO' | 'FEMEA' }))}
                                        className="w-full px-3.5 py-2.5 bg-stone-950 border border-stone-800 rounded-xl text-white focus:outline-none focus:border-emerald-500"
                                    >
                                        <option value="MACHO">Macho</option>
                                        <option value="FEMEA">Fêmea</option>
                                    </select>
                                </div>
                                <div>
                                    <label className="block text-xs font-bold uppercase tracking-wider text-stone-300 mb-1">
                                        Categoria *
                                    </label>
                                    <select
                                        value={formEdicao.categoria}
                                        onChange={(e) => setFormEdicao((atual) => ({ ...atual, categoria: e.target.value as CategoriaAnimal }))}
                                        className="w-full px-3.5 py-2.5 bg-stone-950 border border-stone-800 rounded-xl text-white focus:outline-none focus:border-emerald-500"
                                    >
                                        {categoriasResumo.map((categoria) => (
                                            <option key={categoria} value={categoria}>{categoria}</option>
                                        ))}
                                    </select>
                                </div>
                            </div>

                            <div className="pt-4 flex items-center justify-end gap-3">
                                <button
                                    type="button"
                                    onClick={() => setAnimalEmEdicao(null)}
                                    className="px-4 py-2.5 bg-stone-800 hover:bg-stone-700 text-stone-300 font-bold text-xs uppercase rounded-xl transition-colors cursor-pointer"
                                >
                                    Cancelar
                                </button>
                                <button
                                    type="button"
                                    onClick={salvarEdicao}
                                    className="px-5 py-2.5 bg-emerald-600 hover:bg-emerald-500 text-stone-950 font-black text-xs uppercase tracking-wider rounded-xl transition-all cursor-pointer shadow-lg shadow-emerald-950/30 flex items-center gap-2"
                                >
                                    <Save className="w-4 h-4" />
                                    Salvar
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};
