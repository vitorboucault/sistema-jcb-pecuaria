import { useState, type FormEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

export const LoginPage = () => {
    const [usuario, setUsuario] = useState('dono');
    const [senha, setSenha] = useState('123456');
    const { login } = useAuth();
    const navigate = useNavigate();
    const [erro, setErro] = useState<string | null>(null);

    const handleEntrar = async (e: FormEvent) => {
        e.preventDefault();
        setErro(null);
        try {
            await login(usuario, senha);
            navigate('/dashboard');
        } catch {
            setErro('Não foi possível autenticar. Verifique usuário e senha.');
        }
    };

    return (
        <div className="min-h-screen w-full flex items-center justify-center bg-stone-940 px-4">
            <div className="max-w-md w-full bg-stone-900 border border-stone-800 rounded-2xl p-8 shadow-2xl shadow-black/80">

                <div className="text-center mb-8">
                    <div className="inline-flex items-center justify-center w-14 h-14 rounded-xl bg-green-500 border border-amber-500/30 text-white font-black text-2xl mb-3">
                        JCB
                    </div>
                    <h1 className="text-5xl font-black tracking-tight text-white">SISTEMA JCB</h1>
                    <p className="text-stone-400 text-xs uppercase tracking-widest mt-1 font-light">
                        Cria • Recria • Engorda Intensiva
                    </p>
                </div>

                <form onSubmit={handleEntrar} className="space-y-5">
                    {erro && <p className="text-sm text-red-400">{erro}</p>}
                    <div>
                        <label className="block text-xs font-bold uppercase tracking-wider text-stone-300 mb-1.5">
                            Login:
                        </label>
                        <input
                            type="text"
                            value={usuario}
                            onChange={(e) => setUsuario(e.target.value)}
                            placeholder="Ex: dono ou capataz"
                            className="w-full px-4 py-3 bg-stone-950 border border-stone-800 rounded-xl text-white placeholder-stone-600 focus:outline-none focus:border-amber-500 focus:ring-1 focus:ring-amber-500 transition-colors"
                        />
                    </div>

                    <div>
                        <label className="block text-xs font-bold uppercase tracking-wider text-stone-300 mb-1.5">
                            Senha de Acesso
                        </label>
                        <input
                            type="password"
                            value={senha}
                            onChange={(e) => setSenha(e.target.value)}
                            placeholder="••••••••"
                            className="w-full px-4 py-3 bg-stone-950 border border-stone-800 rounded-xl text-white placeholder-stone-600 focus:outline-none focus:border-amber-500 focus:ring-1 focus:ring-amber-500 transition-colors"
                        />
                    </div>

                    <button
                        type="submit"
                        className="w-full py-3.5 bg-green-400 hover:bg-green-800 text-white font-black text-sm uppercase tracking-wider rounded-xl transition-all cursor-pointer shadow-lg shadow-amber-500/10 active:scale-[1]"
                    >
                        Acessar Fazenda
                    </button>
                </form>

                <div className="mt-8 pt-6 border-t border-stone-800 text-center flex flex-col gap-1">
                </div>
            </div>
        </div>
    );
};
