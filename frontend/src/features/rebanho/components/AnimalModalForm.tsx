import { useState, type FormEvent } from 'react';
import type { Animal, CadastrarAnimalInput, CategoriaAnimal, Lote } from '../types';
import { X, Tag } from 'lucide-react';

interface AnimalModalFormProps {
    lotes: Lote[];
    matrizes: Animal[];
    isOpen: boolean;
    onClose: () => void;
    onSuccess: () => void;
    onCadastrar: (dados: CadastrarAnimalInput) => Promise<unknown>;
}

export const AnimalModalForm = ({
                                    lotes,
                                    matrizes,
                                    isOpen,
                                    onClose,
                                    onSuccess,
                                    onCadastrar,
                                }: AnimalModalFormProps) => {
    const [origem, setOrigem] = useState<'COMPRA' | 'NASCIMENTO'>('COMPRA');
    const [brincoRgd, setBrincoRgd] = useState('');
    const [categoria, setCategoria] = useState<CategoriaAnimal>('GARROTE');
    const [sexo, setSexo] = useState<'MACHO' | 'FEMEA'>('MACHO');
    const [peso, setPeso] = useState<number>(200);

    const hoje = new Date().toISOString().split('T')[0];
    const [dataNascimento, setDataNascimento] = useState(hoje);
    const [dataEntrada, setDataEntrada] = useState(hoje);

    const [loteId, setLoteId] = useState<string>(lotes[0]?.id || '');
    const [maeId, setMaeId] = useState('');
    const [carregando, setCarregando] = useState(false);
    const [erro, setErro] = useState<string | null>(null);

    if (!isOpen) return null;

    const handleOrigemChange = (novaOrigem: 'COMPRA' | 'NASCIMENTO') => {
        setOrigem(novaOrigem);
        if (novaOrigem === 'NASCIMENTO') {
            setCategoria(sexo === 'MACHO' ? 'BEZERRO' : 'BEZERRA');
            setPeso(35);
        }
    };

    const handleSubmit = async (e: FormEvent) => {
        e.preventDefault();
        setErro(null);
        setCarregando(true);

        try {
            await onCadastrar({
                origem,
                brincoRgd,
                categoria,
                sexo,
                peso: Number(peso),
                dataNascimento,
                dataEntrada: origem === 'NASCIMENTO' ? dataNascimento : dataEntrada,
                maeId: maeId || undefined,
                loteId,
            });
            onSuccess();
            onClose();
        } catch (err: unknown) {
            console.error(err);
            setErro('Erro ao registrar animal no banco de dados. Verifique os dados informados.');
        } finally {
            setCarregando(false);
        }
    };

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/80 backdrop-blur-sm p-4">
            <div className="bg-stone-900 border border-stone-800 rounded-2xl max-w-lg w-full p-6 shadow-2xl">
                <div className="flex items-center justify-between pb-4 border-b border-stone-800 mb-6">
                    <h2 className="text-lg font-black text-white flex items-center gap-2">
                        <Tag className="w-5 h-5 text-emerald-500" />
                        REGISTRO DE ENTRADA NO REBANHO
                    </h2>
                    <button onClick={onClose} className="text-stone-400 hover:text-white transition-colors cursor-pointer">
                        <X className="w-5 h-5" />
                    </button>
                </div>

                {erro && (
                    <div className="mb-4 p-3 bg-red-950/50 border border-red-800 text-red-300 rounded-xl text-xs">
                        {erro}
                    </div>
                )}

                <form onSubmit={handleSubmit} className="space-y-4">

                    {/* Seletor de Origem (Nascimento vs Compra) */}
                    <div className="flex gap-2 p-1 bg-stone-950 rounded-xl border border-stone-800">
                        <button
                            type="button"
                            onClick={() => handleOrigemChange('COMPRA')}
                            className={`flex-1 py-2 text-xs font-bold uppercase rounded-lg transition-all cursor-pointer ${
                                origem === 'COMPRA' ? 'bg-emerald-600 text-stone-950' : 'text-stone-400 hover:text-white'
                            }`}
                        >
                            Compra / Aquisição
                        </button>
                        <button
                            type="button"
                            onClick={() => handleOrigemChange('NASCIMENTO')}
                            className={`flex-1 py-2 text-xs font-bold uppercase rounded-lg transition-all cursor-pointer ${
                                origem === 'NASCIMENTO' ? 'bg-emerald-600 text-stone-950' : 'text-stone-400 hover:text-white'
                            }`}
                        >
                            Nascimento na Fazenda
                        </button>
                    </div>

                    <div className="grid grid-cols-2 gap-4">
                        <div>
                            <label className="block text-xs font-bold uppercase tracking-wider text-stone-300 mb-1">
                                Brinco / RGD *
                            </label>
                            <input
                                type="text"
                                required
                                value={brincoRgd}
                                onChange={(e) => setBrincoRgd(e.target.value)}
                                placeholder="Ex: BR-9500"
                                className="w-full px-3.5 py-2.5 bg-stone-950 border border-stone-800 rounded-xl text-white placeholder-stone-600 focus:outline-none focus:border-emerald-500 font-mono"
                            />
                        </div>
                        <div>
                            <label className="block text-xs font-bold uppercase tracking-wider text-stone-300 mb-1">
                                Categoria Zootécnica *
                            </label>
                            <select
                                value={categoria}
                                onChange={(e) => setCategoria(e.target.value as CategoriaAnimal)}
                                disabled={origem === 'NASCIMENTO'}
                                className="w-full px-3.5 py-2.5 bg-stone-950 border border-stone-800 rounded-xl text-white focus:outline-none focus:border-emerald-500 disabled:opacity-60"
                            >
                                {origem === 'NASCIMENTO' ? (
                                    <>
                                        <option value="BEZERRO">Bezerro</option>
                                        <option value="BEZERRA">Bezerra</option>
                                    </>
                                ) : (
                                    <>
                                        <option value="BEZERRO">Bezerro</option>
                                        <option value="BEZERRA">Bezerra</option>
                                        <option value="GARROTE">Garrote</option>
                                        <option value="NOVILHA">Novilha</option>
                                        <option value="BOI">Boi</option>
                                        <option value="VACA">Vaca</option>
                                        <option value="TOURO">Touro</option>
                                    </>
                                )}
                            </select>
                        </div>
                    </div>

                    <div className="grid grid-cols-2 gap-4">
                        <div>
                            <label className="block text-xs font-bold uppercase tracking-wider text-stone-300 mb-1">
                                Sexo *
                            </label>
                            <select
                                value={sexo}
                                onChange={(e) => setSexo(e.target.value as 'MACHO' | 'FEMEA')}
                                className="w-full px-3.5 py-2.5 bg-stone-950 border border-stone-800 rounded-xl text-white focus:outline-none focus:border-emerald-500"
                            >
                                <option value="MACHO">Macho</option>
                                <option value="FEMEA">Fêmea</option>
                            </select>
                        </div>
                        <div>
                            <label className="block text-xs font-bold uppercase tracking-wider text-stone-300 mb-1">
                                {origem === 'NASCIMENTO' ? 'Peso ao Nascer (kg) *' : 'Peso de Entrada (kg) *'}
                            </label>
                            <input
                                type="number"
                                step="0.1"
                                required
                                value={peso}
                                onChange={(e) => setPeso(Number(e.target.value))}
                                className="w-full px-3.5 py-2.5 bg-stone-950 border border-stone-800 rounded-xl text-white focus:outline-none focus:border-emerald-500 font-mono"
                            />
                        </div>
                    </div>

                    <div className="grid grid-cols-2 gap-4">
                        <div>
                            <label className="block text-xs font-bold uppercase tracking-wider text-stone-300 mb-1">
                                {origem === 'COMPRA' ? 'Data de Nascimento *' : 'Data do Parto *'}
                            </label>
                            <input
                                type="date"
                                required
                                value={dataNascimento}
                                onChange={(e) => {
                                    setDataNascimento(e.target.value);
                                    if (origem === 'NASCIMENTO') setDataEntrada(e.target.value);
                                }}
                                className="w-full px-3.5 py-2.5 bg-stone-950 border border-stone-800 rounded-xl text-white focus:outline-none focus:border-emerald-500 font-mono"
                            />
                        </div>

                        {origem === 'COMPRA' ? (
                            <div>
                                <label className="block text-xs font-bold uppercase tracking-wider text-stone-300 mb-1">
                                    Data de Chegada *
                                </label>
                                <input
                                    type="date"
                                    required
                                    value={dataEntrada}
                                    onChange={(e) => setDataEntrada(e.target.value)}
                                    className="w-full px-3.5 py-2.5 bg-stone-950 border border-stone-800 rounded-xl text-white focus:outline-none focus:border-emerald-500 font-mono"
                                />
                            </div>
                        ) : (
                            <div>
                                <label className="block text-xs font-bold uppercase tracking-wider text-stone-300 mb-1">
                                    Lote de Manejo (Opcional)
                                </label>
                                <select
                                    value={loteId || ''}
                                    onChange={(e) => setLoteId(e.target.value)}
                                    className="w-full px-3.5 py-2.5 bg-stone-950 border border-stone-800 rounded-xl text-white focus:outline-none focus:border-emerald-500"
                                >
                                    <option value="">Nenhum lote atribuído (Avulso)</option>
                                    {lotes.map((lote) => (
                                        <option key={lote.id} value={lote.id}>
                                            {lote.nome}
                                        </option>
                                    ))}
                                </select>
                            </div>
                        )}
                    </div>

                    {origem === 'NASCIMENTO' && (
                        <div>
                            <label className="block text-xs font-bold uppercase tracking-wider text-stone-300 mb-1">
                                Matriz (opcional)
                            </label>
                            <select
                                value={maeId}
                                onChange={(e) => setMaeId(e.target.value)}
                                className="w-full px-3.5 py-2.5 bg-stone-950 border border-stone-800 rounded-xl text-white focus:outline-none focus:border-emerald-500"
                            >
                                <option value="">Não informar matriz</option>
                                {matrizes.map((matriz) => (
                                    <option key={matriz.id} value={matriz.id}>
                                        {matriz.brincoRgd} — {matriz.categoria}
                                    </option>
                                ))}
                            </select>
                        </div>
                    )}

                    {origem === 'COMPRA' && (
                        <div>
                            <label className="block text-xs font-bold uppercase tracking-wider text-stone-300 mb-1">
                                Lote de Manejo *
                            </label>
                            <select
                                value={loteId}
                                onChange={(e) => setLoteId(e.target.value)}
                                className="w-full px-3.5 py-2.5 bg-stone-950 border border-stone-800 rounded-xl text-white focus:outline-none focus:border-emerald-500"
                            >
                                {lotes.map((lote) => (
                                    <option key={lote.id} value={lote.id}>
                                        {lote.nome}
                                    </option>
                                ))}
                            </select>
                        </div>
                    )}

                    <div className="pt-4 flex items-center justify-end gap-3">
                        <button
                            type="button"
                            onClick={onClose}
                            className="px-4 py-2.5 bg-stone-800 hover:bg-stone-700 text-stone-300 font-bold text-xs uppercase rounded-xl transition-colors cursor-pointer"
                        >
                            Cancelar
                        </button>
                        <button
                            type="submit"
                            disabled={carregando}
                            className="px-5 py-2.5 bg-emerald-600 hover:bg-emerald-500 text-stone-950 font-black text-xs uppercase tracking-wider rounded-xl transition-all cursor-pointer shadow-lg shadow-emerald-950/30 disabled:opacity-50"
                        >
                            {carregando ? 'Salvando...' : 'Salvar Animal'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};
