import { Navigate, Route, Routes } from 'react-router-dom'
import ProtectedRoute from './auth/ProtectedRoute.jsx'
import './App.css'
import AppLayout from './layout/AppLayout.jsx'
import AdminReviewsPage from './pages/AdminReviewsPage.jsx'
import AdminResourcesPage from './pages/AdminResourcesPage.jsx'
import CatalogPage from './pages/CatalogPage.jsx'
import ForgotPasswordPage from './pages/ForgotPasswordPage.jsx'
import LoginPage from './pages/LoginPage.jsx'
import ModerationPage from './pages/ModerationPage.jsx'
import MyReviewsPage from './pages/MyReviewsPage.jsx'
import ProfilePage from './pages/ProfilePage.jsx'
import RegisterPage from './pages/RegisterPage.jsx'
import ResetPasswordPage from './pages/ResetPasswordPage.jsx'
import TitleDetailPage from './pages/TitleDetailPage.jsx'
import TmdbDetailPage from './pages/TmdbDetailPage.jsx'
import TmdbSearchPage from './pages/TmdbSearchPage.jsx'
import WatchlistPage from './pages/WatchlistPage.jsx'

function App() {
  return (
    <Routes>
      <Route element={<LoginPage />} path="/login" />
      <Route element={<RegisterPage />} path="/register" />
      <Route element={<ForgotPasswordPage />} path="/forgot-password" />
      <Route element={<ResetPasswordPage />} path="/reset-password" />
      <Route element={<ProtectedRoute />}>
        <Route element={<AppLayout />}>
          <Route element={<CatalogPage />} index />
          <Route element={<TitleDetailPage />} path="titles/:id" />
          <Route element={<TmdbSearchPage />} path="tmdb" />
          <Route element={<TmdbDetailPage />} path="tmdb/:type/:id" />
          <Route element={<ProtectedRoute allowedRoles={['USER']} />}>
            <Route element={<WatchlistPage />} path="watchlist" />
            <Route element={<MyReviewsPage />} path="my-reviews" />
          </Route>
          <Route element={<ProtectedRoute allowedRoles={['ADMIN', 'MODERATOR']} />}>
            <Route element={<ModerationPage />} path="moderation" />
          </Route>
          <Route element={<ProtectedRoute allowedRoles={['ADMIN']} />}>
            <Route element={<AdminReviewsPage />} path="admin/reviews" />
            <Route element={<AdminResourcesPage />} path="admin/:resource" />
          </Route>
          <Route element={<ProtectedRoute allowedRoles={['USER', 'ADMIN', 'MODERATOR']} />}>
            <Route element={<ProfilePage />} path="profile" />
          </Route>
        </Route>
      </Route>
      <Route element={<Navigate replace to="/" />} path="*" />
    </Routes>
  )
}

export default App
