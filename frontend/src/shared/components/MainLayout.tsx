import { Outlet, NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../../features/auth/hooks/useAuth';

export const MainLayout = () => {
    const { user, logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    const navItems = [
        { label: 'Dashboard Executivo', path: '/dashboard' },
        { label: 'Rebanho & Lotes', path: '/rebanho' },
        { label: 'Pesagens & GMD', path: '/pesagens' },
        { label: 'Nutrição & Cocho', path: '/nutricao' },
        { label: 'Pastagens & Lotação', path: '/pastos' },
        { label: 'Reprodução & Monta', path: '/reproducao' },
    ];

    return (
        <div className="min-h-screen flex bg-stone-950 text-stone-100">
            {/* Sidebar Lateral */}
            <aside className="w-64 bg-stone-900 border-r border-stone-800 flex flex-col">
                <div className="p-6 border-b border-stone-800">
                    <h2 className="text-xl font-black text-amber-500 tracking-wider">JCB PECUÁRIA</h2>
                    <span className="text-xs text-stone-400">Cria • Recria • Engorda</span>
                </div>

                <nav className="flex-1 p-4 space-y-1.5">
                    {navItems.map((item) => (
                        <NavLink
                            key={item.path}
                            to={item.path}
                            className={({ isActive }) =>
                                `block px-4 py-2.5 rounded-lg text-sm font-medium transition-colors ${
                                    isActive
                                        ? 'bg-amber-600 text-stone-950 font-bold'
                                        : 'text-stone-300 hover:bg-stone-800 hover:text-white'
                                }`
                            }
                        >
                            {item.label}
                        </NavLink>
                    ))}
                </nav>

                <div className="p-4 border-t border-stone-800">
                    <div className="text-xs text-stone-400 mb-2">Operador: {user?.nome}</div>
                    <button
                        onClick={handleLogout}
                        className="w-full text-left px-3 py-2 text-sm text-red-400 hover:bg-red-950/30 rounded-lg transition-colors"
                    >
                        Sair do Sistema
                    </button>
                </div>
            </aside>

            {/* Conteúdo Principal */}
            <main className="flex-1 flex flex-col min-w-0 overflow-y-auto">
                <header className="h-16 border-b border-stone-800 flex items-center justify-between px-8 bg-stone-900/50">
                    <span className="text-sm text-stone-400">Safra Ativa: 2026/2027</span>
                    <span className="text-xs px-2.5 py-1 bg-emerald-900/50 text-emerald-400 border border-emerald-700 rounded-full">
            Conexão Backend: Online
          </span>
                </header>

                <div className="p-8">
                    <Outlet />
                </div>
            </main>
        </div>
    );
};