import { useState, type FormEvent } from 'react';
import type {
    Animal,
    CadastroAnimalFormInput,
    CategoriaAnimal,
    FluxoCadastroAnimal,
    Lote,
    OrigemAnimal,
} from '../types';
import { X, Tag } from 'lucide-react';

interface AnimalModalFormProps {
    lotes: Lote[];
    matrizes: Animal[];
    isOpen: boolean;
    onClose: () => void;
    onCadastrar: (cadastro: CadastroAnimalFormInput) => Promise<unknown>;
}

const hojeLocal = (): string => {
    const agora = new Date();
    return `${agora.getFullYear()}-${String(agora.getMonth() + 1).padStart(2, '0')}-${String(agora.getDate()).padStart(2, '0')}`;
};

const categoriasNascimento: CategoriaAnimal[] = ['BEZERRO', 'BEZERRA'];
const categoriasCadastro: CategoriaAnimal[] = [
    'BEZERRO',
    'BEZERRA',
    'GARROTE',
    'NOVILHA',
    'BOI',
    'VACA',
    'TOURO',
];

export const AnimalModalForm = ({
                                    lotes,
                                    matrizes,
                                    isOpen,
                                    onClose,
                                    onCadastrar,
                                }: AnimalModalFormProps) => {
    const [fluxo, setFluxo] = useState<FluxoCadastroAnimal>('EXISTENTE');
    const [origem, setOrigem] = useState<OrigemAnimal>('DESCONHECIDO');
    const [brincoRgd, setBrincoRgd] = useState('');
    const [categoria, setCategoria] = useState<CategoriaAnimal>('GARROTE');
    const [sexo, setSexo] = useState<'MACHO' | 'FEMEA'>('MACHO');
    const [pesoAtual, setPesoAtual] = useState('');
    const [dataPesagem, setDataPesagem] = useState('');
    const [dataCompraHistorica, setDataCompraHistorica] = useState('');
    const [valorCompraHistorico, setValorCompraHistorico] = useState('');
    const [pesoNascimento, setPesoNascimento] = useState('35');
    const [dataNascimentoExistente, setDataNascimentoExistente] = useState('');
    const [dataNascimentoNascimento, setDataNascimento] = useState(hojeLocal);
    const [loteId, setLoteId] = useState('');
    const [maeId, setMaeId] = useState('');
    const [carregando, setCarregando] = useState(false);
    const [erro, setErro] = useState<string | null>(null);

    const resetForm = () => {
        setFluxo('EXISTENTE');
        setOrigem('DESCONHECIDO');
        setBrincoRgd('');
        setCategoria('GARROTE');
        setSexo('MACHO');
        setPesoAtual('');
        setDataPesagem('');
        setDataCompraHistorica('');
        setValorCompraHistorico('');
        setPesoNascimento('35');
        setDataNascimentoExistente('');
        setDataNascimento(hojeLocal());
        setLoteId('');
        setMaeId('');
        setCarregando(false);
        setErro(null);
    };

    const handleClose = () => {
        resetForm();
        onClose();
    };

    if (!isOpen) return null;

    const handleFluxoChange = (novoFluxo: FluxoCadastroAnimal) => {
        setFluxo(novoFluxo);
        setDataCompraHistorica('');
        setValorCompraHistorico('');
        if (novoFluxo === 'NASCIMENTO') {
            if (!dataNascimento) {
                setDataNascimento(hojeLocal());
            }
            setCategoria(sexo === 'MACHO' ? 'BEZERRO' : 'BEZERRA');
        }
    };

    const handleSexoChange = (novoSexo: 'MACHO' | 'FEMEA') => {
        setSexo(novoSexo);
        if (fluxo === 'NASCIMENTO') {
            setCategoria(novoSexo === 'MACHO' ? 'BEZERRO' : 'BEZERRA');
        }
    };

    const handleOrigemChange = (novaOrigem: OrigemAnimal) => {
        setOrigem(novaOrigem);
        if (novaOrigem !== 'COMPRA') {
            setDataCompraHistorica('');
            setValorCompraHistorico('');
        }
    };

    const handleSubmit = async (e: FormEvent) => {
        e.preventDefault();
        setErro(null);

        if (fluxo === 'EXISTENTE') {
            const temPeso = pesoAtual.trim() !== '';
            const temDataPesagem = dataPesagem !== '';
            if (temPeso !== temDataPesagem) {
                setErro('Peso atual e data da pesagem devem ser informados juntos.');
                return;
            }
            if (dataPesagem && dataPesagem > hojeLocal()) {
                setErro('A data da pesagem não pode ser futura.');
                return;
            }
            if (dataPesagem && dataNascimentoExistente && dataPesagem < dataNascimentoExistente) {
                setErro('A data da pesagem não pode ser anterior à data de nascimento.');
                return;
            }

            if (origem === 'COMPRA') {
                const valorInformado = valorCompraHistorico.trim() !== '';
                if (valorInformado && (!Number.isFinite(Number(valorCompraHistorico)) || Number(valorCompraHistorico) <= 0)) {
                    setErro('O valor da compra histórica deve ser maior que zero.');
                    return;
                }
                if (dataCompraHistorica && dataCompraHistorica > hojeLocal()) {
                    setErro('A data da compra histórica não pode ser futura.');
                    return;
                }
                if (dataCompraHistorica && dataNascimentoExistente && dataCompraHistorica < dataNascimentoExistente) {
                    setErro('A data da compra histórica não pode ser anterior à data de nascimento.');
                    return;
                }
            }
        }

        setCarregando(true);

        try {
            const lote = loteId || undefined;
            const cadastro: CadastroAnimalFormInput = fluxo === 'EXISTENTE'
                ? {
                    fluxo,
                    dados: {
                        origem,
                        brincoRgd,
                        categoria,
                        sexo,
                        dataNascimento: dataNascimentoExistente || null,
                        ...(lote ? { loteId: lote } : {}),
                        ...(pesoAtual.trim() !== '' && dataPesagem
                            ? { pesoAtual: Number(pesoAtual), dataPesagem }
                            : {}),
                        ...(origem === 'COMPRA' && dataCompraHistorica
                            ? { dataCompraHistorica }
                            : {}),
                        ...(origem === 'COMPRA' && valorCompraHistorico.trim() !== ''
                            ? { valorCompraHistorico: Number(valorCompraHistorico) }
                            : {}),
                    },
                }
                : {
                    fluxo,
                    dados: {
                        origem: 'NASCIMENTO',
                        brincoRgd,
                        categoria,
                        sexo,
                        peso: Number(pesoNascimento),
                        dataNascimento: dataNascimento,
                        dataEntrada: dataNascimento,
                        ...(maeId ? { maeId } : {}),
                        ...(lote ? { loteId: lote } : {}),
                    },
                };

            await onCadastrar(cadastro);
            handleClose();
        } catch (err: unknown) {
            console.error(err);
            setErro('Erro ao registrar animal no banco de dados. Verifique os dados informados.');
        } finally {
            setCarregando(false);
        }
    };

    const isNascimento = fluxo === 'NASCIMENTO';
    const dataNascimento = isNascimento ? dataNascimentoNascimento : dataNascimentoExistente;

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/80 backdrop-blur-sm p-4">
            <div className="bg-stone-900 border border-stone-800 rounded-2xl max-w-lg w-full p-6 shadow-2xl">
                <div className="flex items-center justify-between pb-4 border-b border-stone-800 mb-6">
                    <h2 className="text-lg font-black text-white flex items-center gap-2">
                        <Tag className="w-5 h-5 text-emerald-500" />
                        CADASTRAR ANIMAL
                    </h2>
                    <button onClick={handleClose} className="text-stone-400 hover:text-white transition-colors cursor-pointer">
                        <X className="w-5 h-5" />
                    </button>
                </div>

                {erro && (
                    <div className="mb-4 p-3 bg-red-950/50 border border-red-800 text-red-300 rounded-xl text-xs">
                        {erro}
                    </div>
                )}

                <form onSubmit={handleSubmit} className="space-y-4">
                    <div className="flex gap-2 p-1 bg-stone-950 rounded-xl border border-stone-800">
                        <button
                            type="button"
                            onClick={() => handleFluxoChange('EXISTENTE')}
                            aria-pressed={fluxo === 'EXISTENTE'}
                            className={`flex-1 py-2 text-xs font-bold uppercase rounded-lg transition-all cursor-pointer ${
                                fluxo === 'EXISTENTE' ? 'bg-emerald-600 text-stone-950' : 'text-stone-400 hover:text-white'
                            }`}
                        >
                            Animal já existente
                        </button>
                        <button
                            type="button"
                            onClick={() => handleFluxoChange('NASCIMENTO')}
                            aria-pressed={isNascimento}
                            className={`flex-1 py-2 text-xs font-bold uppercase rounded-lg transition-all cursor-pointer ${
                                isNascimento ? 'bg-emerald-600 text-stone-950' : 'text-stone-400 hover:text-white'
                            }`}
                        >
                            Novo nascimento
                        </button>
                    </div>

                    {!isNascimento && (
                        <div>
                            <label htmlFor="origem-historica" className="block text-xs font-bold uppercase tracking-wider text-stone-300 mb-1">
                                Origem histórica *
                            </label>
                            <select
                                id="origem-historica"
                                value={origem}
                                onChange={(e) => handleOrigemChange(e.target.value as OrigemAnimal)}
                                className="w-full px-3.5 py-2.5 bg-stone-950 border border-stone-800 rounded-xl text-white focus:outline-none focus:border-emerald-500"
                            >
                                <option value="DESCONHECIDO">Desconhecido</option>
                                <option value="COMPRA">Compra histórica</option>
                                <option value="NASCIMENTO">Nascimento na fazenda</option>
                            </select>
                        </div>
                    )}

                    <div className="grid grid-cols-2 gap-4">
                        <div>
                            <label htmlFor="brinco-rgd" className="block text-xs font-bold uppercase tracking-wider text-stone-300 mb-1">
                                Brinco / RGD *
                            </label>
                            <input
                                id="brinco-rgd"
                                type="text"
                                required
                                value={brincoRgd}
                                onChange={(e) => setBrincoRgd(e.target.value)}
                                placeholder="Ex: BR-9500"
                                className="w-full px-3.5 py-2.5 bg-stone-950 border border-stone-800 rounded-xl text-white placeholder-stone-600 focus:outline-none focus:border-emerald-500 font-mono"
                            />
                        </div>
                        <div>
                            <label htmlFor="categoria-animal" className="block text-xs font-bold uppercase tracking-wider text-stone-300 mb-1">
                                Categoria Zootécnica *
                            </label>
                            <select
                                id="categoria-animal"
                                value={categoria}
                                onChange={(e) => setCategoria(e.target.value as CategoriaAnimal)}
                                disabled={isNascimento}
                                className="w-full px-3.5 py-2.5 bg-stone-950 border border-stone-800 rounded-xl text-white focus:outline-none focus:border-emerald-500 disabled:opacity-60"
                            >
                                {(isNascimento ? categoriasNascimento : categoriasCadastro).map((opcao) => (
                                    <option key={opcao} value={opcao}>{opcao}</option>
                                ))}
                            </select>
                        </div>
                    </div>

                    {!isNascimento && origem === 'COMPRA' && (
                        <div className="space-y-3 rounded-xl border border-stone-800 bg-stone-950/60 p-4">
                            <div>
                                <h3 className="text-xs font-bold uppercase tracking-wider text-stone-300">
                                    Dados históricos da compra
                                </h3>
                                <p className="mt-1 text-xs text-stone-500">
                                    Informe apenas se esses dados históricos forem conhecidos.
                                </p>
                            </div>
                            <div className="grid grid-cols-2 gap-4">
                                <div>
                                    <label htmlFor="data-compra-historica" className="block text-xs font-bold uppercase tracking-wider text-stone-300 mb-1">
                                        Data da compra
                                    </label>
                                    <input
                                        id="data-compra-historica"
                                        type="date"
                                        min={dataNascimentoExistente || undefined}
                                        max={hojeLocal()}
                                        value={dataCompraHistorica}
                                        onChange={(e) => setDataCompraHistorica(e.target.value)}
                                        className="w-full px-3.5 py-2.5 bg-stone-950 border border-stone-800 rounded-xl text-white focus:outline-none focus:border-emerald-500 font-mono"
                                    />
                                </div>
                                <div>
                                    <label htmlFor="valor-compra-historico" className="block text-xs font-bold uppercase tracking-wider text-stone-300 mb-1">
                                        Valor da compra (R$)
                                    </label>
                                    <input
                                        id="valor-compra-historico"
                                        type="number"
                                        min="0.01"
                                        step="0.01"
                                        value={valorCompraHistorico}
                                        onChange={(e) => setValorCompraHistorico(e.target.value)}
                                        className="w-full px-3.5 py-2.5 bg-stone-950 border border-stone-800 rounded-xl text-white focus:outline-none focus:border-emerald-500 font-mono"
                                    />
                                </div>
                            </div>
                        </div>
                    )}

                    <div className="grid grid-cols-2 gap-4">
                        <div>
                            <label htmlFor="sexo-animal" className="block text-xs font-bold uppercase tracking-wider text-stone-300 mb-1">
                                Sexo *
                            </label>
                            <select
                                id="sexo-animal"
                                value={sexo}
                                onChange={(e) => handleSexoChange(e.target.value as 'MACHO' | 'FEMEA')}
                                className="w-full px-3.5 py-2.5 bg-stone-950 border border-stone-800 rounded-xl text-white focus:outline-none focus:border-emerald-500"
                            >
                                <option value="MACHO">Macho</option>
                                <option value="FEMEA">Fêmea</option>
                            </select>
                        </div>
                        {isNascimento ? (
                            <div>
                                <label htmlFor="peso-nascimento" className="block text-xs font-bold uppercase tracking-wider text-stone-300 mb-1">
                                    Peso ao nascer (kg) *
                                </label>
                                <input
                                    id="peso-nascimento"
                                    type="number"
                                    min="0.1"
                                    step="0.1"
                                    required
                                    value={pesoNascimento}
                                    onChange={(e) => setPesoNascimento(e.target.value)}
                                    className="w-full px-3.5 py-2.5 bg-stone-950 border border-stone-800 rounded-xl text-white focus:outline-none focus:border-emerald-500 font-mono"
                                />
                            </div>
                        ) : (
                            <div>
                                <label htmlFor="peso-atual" className="block text-xs font-bold uppercase tracking-wider text-stone-300 mb-1">
                                    Peso atual (kg)
                                </label>
                                <input
                                    id="peso-atual"
                                    type="number"
                                    min="0.1"
                                    step="0.1"
                                    value={pesoAtual}
                                    onChange={(e) => setPesoAtual(e.target.value)}
                                    className="w-full px-3.5 py-2.5 bg-stone-950 border border-stone-800 rounded-xl text-white focus:outline-none focus:border-emerald-500 font-mono"
                                />
                            </div>
                        )}
                    </div>

                    <div className="grid grid-cols-2 gap-4">
                        <div>
                            <label htmlFor="data-nascimento" className="block text-xs font-bold uppercase tracking-wider text-stone-300 mb-1">
                                {isNascimento ? 'Data de nascimento *' : 'Data de nascimento'}
                            </label>
                            <input
                                id="data-nascimento"
                                type="date"
                                required={isNascimento}
                                value={dataNascimento}
                                max={hojeLocal()}
                                onChange={(e) => {
                                    if (isNascimento) {
                                        setDataNascimento(e.target.value);
                                    } else {
                                        setDataNascimentoExistente(e.target.value);
                                    }
                                }}
                                className="w-full px-3.5 py-2.5 bg-stone-950 border border-stone-800 rounded-xl text-white focus:outline-none focus:border-emerald-500 font-mono"
                            />
                        </div>
                        {!isNascimento && (
                            <div>
                                <label htmlFor="data-pesagem" className="block text-xs font-bold uppercase tracking-wider text-stone-300 mb-1">
                                    Data da pesagem
                                </label>
                                <input
                                    id="data-pesagem"
                                    type="date"
                                    max={hojeLocal()}
                                    value={dataPesagem}
                                    onChange={(e) => setDataPesagem(e.target.value)}
                                    className="w-full px-3.5 py-2.5 bg-stone-950 border border-stone-800 rounded-xl text-white focus:outline-none focus:border-emerald-500 font-mono"
                                />
                            </div>
                        )}
                    </div>

                    <div>
                        <label htmlFor="lote-animal" className="block text-xs font-bold uppercase tracking-wider text-stone-300 mb-1">
                            Lote (opcional)
                        </label>
                        <select
                            id="lote-animal"
                            value={loteId}
                            onChange={(e) => setLoteId(e.target.value)}
                            className="w-full px-3.5 py-2.5 bg-stone-950 border border-stone-800 rounded-xl text-white focus:outline-none focus:border-emerald-500"
                        >
                            <option value="">Sem lote</option>
                            {lotes.map((lote) => (
                                <option key={lote.id} value={lote.id}>
                                    {lote.nome}
                                </option>
                            ))}
                        </select>
                    </div>

                    {isNascimento && (
                        <div>
                            <label htmlFor="matriz-animal" className="block text-xs font-bold uppercase tracking-wider text-stone-300 mb-1">
                                Matriz (opcional)
                            </label>
                            <select
                                id="matriz-animal"
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

                    <div className="pt-4 flex items-center justify-end gap-3">
                        <button
                            type="button"
                            onClick={handleClose}
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
