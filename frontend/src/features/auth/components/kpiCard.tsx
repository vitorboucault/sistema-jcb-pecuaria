import type { ReactNode } from 'react';

interface KpiCardProps {
    title: string;
    value: string | number;
    unit?: string;
    description: string;
    icon?: ReactNode;
    statusColor?: 'green' | 'amber' | 'neutral';
}

export const KpiCard = ({
                            title,
                            value,
                            unit,
                            description,
                            icon,
                            statusColor = 'green',
                        }: KpiCardProps) => {
    const colorStyles = {
        green: 'border-emerald-500/30 bg-stone-900/80 text-emerald-400 shadow-emerald-950/20',
        amber: 'border-amber-500/30 bg-stone-900/80 text-amber-400 shadow-amber-950/20',
        neutral: 'border-stone-800 bg-stone-900/80 text-stone-100 shadow-black/40',
    };

    const valueColor = {
        green: 'text-emerald-400',
        amber: 'text-amber-400',
        neutral: 'text-white',
    };

    return (
        <div className={`border rounded-2xl p-6 shadow-xl backdrop-blur-sm flex flex-col justify-between transition-all duration-200 hover:border-emerald-500/60 ${colorStyles[statusColor]}`}>
            <div className="flex items-center justify-between mb-4">
        <span className="text-xs font-bold uppercase tracking-widest text-stone-400">
          {title}
        </span>
                {icon && <div className="text-emerald-500">{icon}</div>}
            </div>

            <div className="flex items-baseline gap-2 mb-2">
        <span className={`text-3xl font-black tracking-tight ${valueColor[statusColor]}`}>
          {value}
        </span>
                {unit && <span className="text-sm font-semibold text-stone-400">{unit}</span>}
            </div>

            <p className="text-xs text-stone-400 font-medium leading-relaxed border-t border-stone-800/80 pt-3 mt-1">
                {description}
            </p>
        </div>
    );
};