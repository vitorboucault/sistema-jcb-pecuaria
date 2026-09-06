import { Routes, Route, Navigate } from 'react-router-dom';
import type { JSX } from 'react';
import { useAuth } from './features/auth/hooks/useAuth';
import { LoginPage } from './features/auth/pages/LoginPage';
import { DashboardExecutivoPage } from './features/dashboard/pages/DashboardExecutivoPage';
import { RebanhoPage } from './features/rebanho/pages/RebanhoPage';
import { MainLayout } from './shared/components/MainLayout';

const ProtectedRoute = ({ children }: { children: JSX.Element }) => {
    const { isAuthenticated, isLoading } = useAuth();

    if (isLoading) {
        return <div className="min-h-screen bg-stone-950 flex items-center justify-center text-amber-500 font-bold">Carregando dados da fazenda...</div>;
    }

    return isAuthenticated ? children : <Navigate to="/login" replace />;
};

export const AppRoutes = () => {
    return (
        <Routes>
            <Route path="/login" element={<LoginPage />} />

            <Route
                path="/"
                element={
                    <ProtectedRoute>
                        <MainLayout />
                    </ProtectedRoute>
                }
            >
                <Route index element={<Navigate to="/dashboard" replace />} />
                <Route path="dashboard" element={<DashboardExecutivoPage />} />
                <Route path="rebanho" element={<RebanhoPage />} />
                <Route path="pesagens" element={<div>Controle de Balança e GMD</div>} />
                <Route path="nutricao" element={<div>Controle de Cocho e Conversão Alimentar</div>} />
                <Route path="pastos" element={<div>Lotação e Piquetes</div>} />
                <Route path="reproducao" element={<div>Estação de Monta e Nascimentos</div>} />
            </Route>

            <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
    );
};