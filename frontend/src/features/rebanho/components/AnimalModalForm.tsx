import { useState, type FormEvent } from 'react';
import type { CategoriaAnimal, Lote } from '../types';
import { X, Tag } from 'lucide-react';

interface AnimalInputData {
    brinco: string;
    categoria: CategoriaAnimal;
    sexo: 'MACHO' | 'FEMEA';
    pesoEntrada: number;
    dataNascimentoOrEntrada: string;
    loteId: number;
}

interface AnimalModalFormProps {
    lotes: Lote[];
    isOpen: boolean;
    onClose: () => void;
    onSuccess: () => void;
    onCadastrar: (dados: AnimalInputData) => Promise<any>;
}

export const AnimalModalForm = ({
                                    lotes,
                                    isOpen,
                                    onClose,
                                    onSuccess,
                                    onCadastrar,
                                }: AnimalModalFormProps) => {
    const [brinco, setBrinco] = useState('');
    const [brincoEletronica, setBrincoEletronica] = useState('');
    const [categoria, setCategoria] = useState<CategoriaAnimal>('GARROTE');
    const [sexo, setSexo] = useState<'MACHO' | 'FEMEA'>('MACHO');
    const [pesoEntrada, setPesoEntrada] = useState<number>(200);
    const [dataEntrada, setDataEntrada] = useState(new Date().toISOString().split('T')[0]);
    const [loteId, setLoteId] = useState<number>(lotes[0]?.id || 1);
    const [carregando, setCarregando] = useState(false);
    const [erro, setErro] = useState<string | null>(null);

    if (!isOpen) return null;

    const handleSubmit = async (e: FormEvent) => {
        e.preventDefault();
        setErro(null);
        setCarregando(true);

        try {
            await onCadastrar({
                brinco,
                categoria,
                sexo,
                pesoEntrada: Number(pesoEntrada),
                dataNascimentoOrEntrada: dataEntrada,
                loteId: Number(loteId),
            });
            onSuccess();
            onClose();
        } catch (err) {
            console.error(err);
            setErro('Erro ao registrar animal no banco de dados. Verifique se o brinco já existe.');
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
                        REGISTRAR NOVO ANIMAL / NASCIMENTO
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
                    <div className="grid grid-cols-2 gap-4">
                        <div>
                            <label className="block text-xs font-bold uppercase tracking-wider text-stone-300 mb-1">
                                Brinco Visual *
                            </label>
                            <input
                                type="text"
                                required
                                value={brinco}
                                onChange={(e) => setBrinco(e.target.value)}
                                placeholder="Ex: BR-9500"
                                className="w-full px-3.5 py-2.5 bg-stone-950 border border-stone-800 rounded-xl text-white placeholder-stone-600 focus:outline-none focus:border-emerald-500"
                            />
                        </div>
                        <div>
                            <label className="block text-xs font-bold uppercase tracking-wider text-stone-300 mb-1">
                                Brinco Eletrônico (RFID)
                            </label>
                            <input
                                type="text"
                                value={brincoEletronica}
                                onChange={(e) => setBrincoEletronica(e.target.value)}
                                placeholder="Opcional"
                                className="w-full px-3.5 py-2.5 bg-stone-950 border border-stone-800 rounded-xl text-white placeholder-stone-600 focus:outline-none focus:border-emerald-500"
                            />
                        </div>
                    </div>

                    <div className="grid grid-cols-2 gap-4">
                        <div>
                            <label className="block text-xs font-bold uppercase tracking-wider text-stone-300 mb-1">
                                Categoria Zootécnica *
                            </label>
                            <select
                                value={categoria}
                                onChange={(e) => setCategoria(e.target.value as CategoriaAnimal)}
                                className="w-full px-3.5 py-2.5 bg-stone-950 border border-stone-800 rounded-xl text-white focus:outline-none focus:border-emerald-500"
                            >
                                <option value="BEZERRO">Bezerro</option>
                                <option value="BEZERRA">Bezerra</option>
                                <option value="GARROTE">Garrote</option>
                                <option value="NOVILHA">Novilha</option>
                                <option value="BOI">Boi</option>
                                <option value="VACA_REPRODUTORA">Vaca Reprodutora</option>
                                <option value="TOURO">Touro</option>
                            </select>
                        </div>
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
                    </div>

                    <div className="grid grid-cols-2 gap-4">
                        <div>
                            <label className="block text-xs font-bold uppercase tracking-wider text-stone-300 mb-1">
                                Peso Inicial / Entrada (kg) *
                            </label>
                            <input
                                type="number"
                                step="0.1"
                                required
                                value={pesoEntrada}
                                onChange={(e) => setPesoEntrada(Number(e.target.value))}
                                className="w-full px-3.5 py-2.5 bg-stone-950 border border-stone-800 rounded-xl text-white focus:outline-none focus:border-emerald-500 font-mono"
                            />
                        </div>
                        <div>
                            <label className="block text-xs font-bold uppercase tracking-wider text-stone-300 mb-1">
                                Data de Entrada *
                            </label>
                            <input
                                type="date"
                                required
                                value={dataEntrada}
                                onChange={(e) => setDataEntrada(e.target.value)}
                                className="w-full px-3.5 py-2.5 bg-stone-950 border border-stone-800 rounded-xl text-white focus:outline-none focus:border-emerald-500 font-mono"
                            />
                        </div>
                    </div>

                    <div>
                        <label className="block text-xs font-bold uppercase tracking-wider text-stone-300 mb-1">
                            Lote de Manejo *
                        </label>
                        <select
                            value={loteId}
                            onChange={(e) => setLoteId(Number(e.target.value))}
                            className="w-full px-3.5 py-2.5 bg-stone-950 border border-stone-800 rounded-xl text-white focus:outline-none focus:border-emerald-500"
                        >
                            {lotes.map((lote) => (
                                <option key={lote.id} value={lote.id}>
                                    {lote.nome} ({lote.categoriaPredominante})
                                </option>
                            ))}
                        </select>
                    </div>

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
                            {carregando ? 'Salvando no Banco...' : 'Salvar Animal'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};