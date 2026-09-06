import { useEffect, useState, useCallback } from 'react';
import { isAxiosError } from 'axios';
import { rebanhoService } from '../api/rebanhoService';
import type { Animal, CadastrarAnimalInput, Lote } from '../types';
import { AnimalModalForm } from '../components/AnimalModalForm';
import { Users, Plus, Tag, AlertCircle } from 'lucide-react';

export const RebanhoPage = () => {
    const [animais, setAnimais] = useState<Animal[]>([]);
    const [lotes, setLotes] = useState<Lote[]>([]);
    const [loading, setLoading] = useState(true);
    const [erroBanco, setErroBanco] = useState<string | null>(null);
    const [filtroCategoria, setFiltroCategoria] = useState<string>('TODOS');
    const [isModalOpen, setIsModalOpen] = useState(false);

    const carregarDados = useCallback(async () => {
        setLoading(true);
        try {
            const [dadosAnimais, dadosLotes] = await Promise.all([
                rebanhoService.listarAnimais(),
                rebanhoService.listarLotes(),
            ]);

            // BLINDAGEM CRUCIAL: Assegura que o estado seja sempre um array, mesmo se a API falhar
            setAnimais(Array.isArray(dadosAnimais) ? dadosAnimais : []);
            setLotes(Array.isArray(dadosLotes) ? dadosLotes : []);
            setErroBanco(null);
        } catch (err: unknown) {
            console.error('Falha ao comunicar com o backend:', err);
            const mensagem = isAxiosError<{ mensagem?: string }>(err) ? err.response?.data?.mensagem : undefined;
            setErroBanco(mensagem || 'Erro ao carregar dados do banco PostgreSQL.');
            setAnimais([]);
            setLotes([]);
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

    // Garante segurança caso animais não seja um array
    const listaAnimaisSegura = Array.isArray(animais) ? animais : [];
    const animaisFiltrados = filtroCategoria === 'TODOS'
        ? listaAnimaisSegura
        : listaAnimaisSegura.filter(a => a?.categoria === filtroCategoria);

    // Garante segurança caso lotes não seja um array
    const listaLotesSegura = Array.isArray(lotes) ? lotes : [];

    if (loading && listaAnimaisSegura.length === 0 && listaLotesSegura.length === 0) {
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

            {/* Cards de Resumo de Lotes */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                {listaLotesSegura.length === 0 ? (
                    <div className="col-span-3 p-6 bg-stone-900/40 border border-stone-800 rounded-2xl text-center text-stone-500 font-mono text-xs">
                        Nenhum lote cadastrado ou endpoint `/api/v1/lotes` retornou vazio.
                    </div>
                ) : (
                    listaLotesSegura.map((lote) => (
                        <div key={lote.id} className="border border-stone-800 bg-stone-900/80 rounded-2xl p-6 shadow-xl hover:border-emerald-500/50 transition-all">
                            <div className="flex items-center justify-between mb-3">
                <span className="text-xs font-bold uppercase tracking-widest text-emerald-400">
                  {lote?.fase || 'SEM FASE'}
                </span>
                                <span className="px-2.5 py-0.5 bg-stone-950 border border-stone-800 rounded-full text-xs font-mono text-stone-300">
                  {lote?.quantidadeAnimais ?? 0} cabeças
                </span>
                            </div>
                            <h3 className="text-lg font-bold text-white mb-1">{lote?.nome || 'Lote sem nome'}</h3>
                            <p className="text-xs text-stone-400 mb-4">Lote em fase de {lote?.fase?.toLowerCase() || 'manejo'}.</p>
                            <div className="flex items-center justify-between pt-3 border-t border-stone-800 text-xs font-mono">
                                <span className="text-stone-400">Peso Médio Atual:</span>
                                <span className="text-emerald-400 font-bold">{lote?.pesoMedio ?? 0} kg</span>
                            </div>
                        </div>
                    ))
                )}
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
                        </tr>
                        </thead>
                        <tbody className="divide-y divide-stone-800 text-stone-300">
                        {animaisFiltrados.length === 0 ? (
                            <tr>
                                <td colSpan={6} className="text-center py-8 text-stone-500 font-mono">
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
                      <span className="px-2.5 py-1 bg-emerald-950/60 border border-emerald-800 text-emerald-400 rounded-full font-mono text-[10px]">
                        {animal?.status || 'ATIVO'}
                      </span>
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
        </div>
    );
};
