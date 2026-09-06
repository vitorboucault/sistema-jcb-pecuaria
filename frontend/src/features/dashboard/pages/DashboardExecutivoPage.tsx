import { useEffect, useState } from 'react';
import { api } from '../../../shared/api/client.ts';
import { KpiCard } from '../../auth/components/kpiCard.tsx';
import {TrendingUp, DollarSign, Scale, Activity, Percent, MapPin, ShieldAlert, Layers
} from 'lucide-react';

interface DashboardData {
    margemBrutaHectare: number;
    pontoEquilibrioArrobas: number;
    desembolsoCabecaMes: number;
    custoArrobaProduzida: number;
    ganhoMedioDiarioGlobal: number;
    conversaoAlimentarMedia: number;
    taxaPrenhez: number;
    taxaDesmame: number;
    taxaLotacao: number;
}

export const DashboardExecutivoPage = () => {
    const [data, setData] = useState<DashboardData | null>(null);
    const [loading, setLoading] = useState(true);
    const [, setErroBanco] = useState<string | null>(null);

    useEffect(() => {
        api.get<DashboardData>('/v1/dashboard/executivo')
            .then((response) => {
                setData(response.data);
                setErroBanco(null);
            })
            .catch((err) => {
                console.error('Falha ao comunicar com o banco de dados:', err);
                setErroBanco('Não foi possível carregar os dados consolidados do banco. Verifique a conexão com o Spring Boot.');
            })
            .finally(() => {
                setLoading(false);
            });
    }, []);

    if (loading) {
        return (
            <div className="flex h-96 items-center justify-center text-emerald-400 font-mono text-sm tracking-wider">
                CARREGANDO INDICADORES DA FAZENDA...
            </div>
        );
    }

    return (
        <div className="space-y-8 pb-12">
            {/* Cabeçalho do Painel */}
            <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 border-b border-stone-800 pb-6">
                <div>
                    <h1 className="text-2xl font-black text-white tracking-tight flex items-center gap-3">
                        <span className="w-3 h-3 rounded-full bg-emerald-500 animate-pulse"></span>
                        PAINEL EXECUTIVO DA SAFRA
                    </h1>
                    <p className="text-stone-400 text-xs uppercase tracking-widest mt-1">
                        Consolidado Econômico e Zootécnico • Ciclo Completo
                    </p>
                </div>
                <div className="flex items-center gap-3">
          <span className="px-3 py-1.5 bg-stone-900 border border-stone-800 text-stone-300 rounded-xl text-xs font-mono">
            Arroba Referência: <strong className="text-emerald-400">R$ 310,00</strong>
          </span>
                </div>
            </div>

            {/* Grid dos 8 KPIs */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">

                <KpiCard
                    title="Margem Bruta / Hectare"
                    value={`R$ ${data?.margemBrutaHectare.toLocaleString('pt-BR', { minimumFractionDigits: 2 }) ?? '0,00'}`}
                    unit="/ ha"
                    description="Retorno financeiro líquido gerado por hectare ao longo da safra ativa."
                    icon={<DollarSign className="w-5 h-5 text-emerald-400" />}
                    statusColor="green"
                />

                <KpiCard
                    title="Ponto de Equilíbrio"
                    value={data?.pontoEquilibrioArrobas.toFixed(1) ?? '0'}
                    unit="@ / cab"
                    description="Quantidade mínima de arrobas necessárias por cabeça para cobrir o custo total."
                    icon={<ShieldAlert className="w-5 h-5 text-amber-400" />}
                    statusColor="amber"
                />

                <KpiCard
                    title="Desembolso Cabeça / Mês"
                    value={`R$ ${data?.desembolsoCabecaMes.toLocaleString('pt-BR', { minimumFractionDigits: 2 }) ?? '0,00'}`}
                    unit="/ cab / mês"
                    description="Custo operacional efetivo (insumos, sal, nutrição e sanidade) por animal/mês."
                    icon={<Layers className="w-5 h-5 text-emerald-400" />}
                    statusColor="green"
                />

                <KpiCard
                    title="Custo da Arroba Produzida"
                    value={`R$ ${data?.custoArrobaProduzida.toLocaleString('pt-BR', { minimumFractionDigits: 2 }) ?? '0,00'}`}
                    unit="/ @"
                    description="Custo efetivo para cada arroba colocada na carcaça no período."
                    icon={<TrendingUp className="w-5 h-5 text-emerald-400" />}
                    statusColor="green"
                />

                <KpiCard
                    title="Ganho Médio Diário (GMD)"
                    value={data?.ganhoMedioDiarioGlobal.toFixed(3) ?? '0,000'}
                    unit="kg / dia"
                    description="Evolução ponderal média global do rebanho em regime de pasto ou cocho."
                    icon={<Scale className="w-5 h-5 text-emerald-400" />}
                    statusColor="green"
                />

                <KpiCard
                    title="Conversão Alimentar (CA)"
                    value={data?.conversaoAlimentarMedia.toFixed(1) ?? '0.0'}
                    unit="kg MS / kg"
                    description="Eficiência zootécnica de cocho: quilos de matéria seca consumidos por kg ganho."
                    icon={<Activity className="w-5 h-5 text-amber-400" />}
                    statusColor="amber"
                />

                <KpiCard
                    title="Taxa de Prenhez & Desmame"
                    value={`${data?.taxaPrenhez?.toFixed(1) ?? '0.0'}%`}
                    unit={`(Desm: ${data?.taxaDesmame?.toFixed(1) ?? '0.0'}%)`}
                    description="Eficiência reprodutiva da estação de monta: matrizes prenhes e bezerros desmamados."
                    icon={<Percent className="w-5 h-5 text-emerald-400" />}
                    statusColor="green"
                />

                <KpiCard
                    title="Taxa de Lotação"
                    value={data?.taxaLotacao.toFixed(2) ?? '0.00'}
                    unit="UA / ha"
                    description="Pressão de pastejo atual nos piquetes (Unidade Animal = 450 kg por hectare)."
                    icon={<MapPin className="w-5 h-5 text-emerald-400" />}
                    statusColor="green"
                />

            </div>
        </div>
    );
};