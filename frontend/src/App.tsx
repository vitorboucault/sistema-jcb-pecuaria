import { BrowserRouter } from 'react-router-dom';
import { AuthProvider } from './features/auth/hooks/useAuth';
import {AppRoutes} from "../routes.tsx";


export function App() {
  return (
      <BrowserRouter>
        <AuthProvider>
          <AppRoutes />
        </AuthProvider>
      </BrowserRouter>
  );
}