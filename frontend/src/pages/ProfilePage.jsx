import { useState } from 'react'
import apiClient from '../api/client.js'
import useApiResource from '../api/useApiResource.js'
import { validateAvatar } from '../auth/avatar.js'
import GoogleButton from '../auth/GoogleButton.jsx'

export default function ProfilePage() {
  const profile = useApiResource('/api/users/me')
  const [editedValues, setEditedValues] = useState(null)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')
  const [notice, setNotice] = useState('')
  const [image, setImage] = useState(null)
  const [uploading, setUploading] = useState(false)
  const values = editedValues || {
    fullName: profile.data?.fullName || '',
    phone: profile.data?.phone || '',
    avatarUrl: profile.data?.avatarUrl || '',
  }

  async function uploadAvatar(event) {
    event.preventDefault()
    const validationError = validateAvatar(image)
    if (validationError) {
      setError(validationError)
      return
    }
    setUploading(true)
    setError('')
    setNotice('')
    try {
      const form = new FormData()
      form.append('image', image)
      await apiClient.post('/api/users/me/avatar', form)
      window.dispatchEvent(new Event('bodriopopcorn:avatar-updated'))
      setNotice('Tu foto de perfil se actualizó correctamente.')
    } catch (requestError) {
      setError(requestError.response?.data?.message || 'No se pudo subir tu foto de perfil.')
    } finally {
      setUploading(false)
    }
  }

  async function linkGoogle(credential) {
    setError('')
    setNotice('')
    try {
      await apiClient.post('/api/auth/google/link', { credential })
      setNotice('Tu cuenta de Google se vinculó correctamente.')
    } catch (requestError) {
      setError(requestError.response?.data?.message || 'No se pudo vincular tu cuenta de Google.')
    }
  }

  async function saveProfile(event) {
    event.preventDefault()
    setSaving(true)
    setError('')
    setNotice('')
    try {
      const { data } = await apiClient.put('/api/users/me', {
        fullName: values.fullName.trim(),
        phone: values.phone.trim() || null,
        avatarUrl: values.avatarUrl.trim() || null,
      })
      setEditedValues({
        fullName: data.fullName || '',
        phone: data.phone || '',
        avatarUrl: data.avatarUrl || '',
      })
      setNotice('Tu perfil se actualizó correctamente.')
    } catch (requestError) {
      setError(requestError.response?.data?.message || 'No se pudo guardar tu perfil.')
    } finally {
      setSaving(false)
    }
  }

  if (profile.loading) return <p className="loading-message" role="status">Cargando perfil...</p>
  if (profile.error) return <p className="inline-error" role="alert">{profile.error}</p>

  return (
    <section className="domain-page profile-page" aria-labelledby="profile-title">
      <div className="page-heading">
        <div>
          <h1 id="profile-title">Mi perfil</h1>
          <p>Actualiza la información de tu cuenta.</p>
        </div>
      </div>
      {error && <p className="inline-error" role="alert">{error}</p>}
      {notice && <p className="success-message" role="status">{notice}</p>}
      <form className="profile-form" onSubmit={saveProfile}>
        <label>
          Nombre completo
          <input
            maxLength={120}
            onChange={(event) => setEditedValues((current) => ({ ...(current || values), fullName: event.target.value }))}
            required
            value={values.fullName}
          />
        </label>
        <label>
          Teléfono
          <input
            maxLength={30}
            onChange={(event) => setEditedValues((current) => ({ ...(current || values), phone: event.target.value }))}
            value={values.phone}
          />
        </label>
        <button className="primary-button" disabled={saving} type="submit">
          {saving ? 'Guardando...' : 'Guardar cambios'}
        </button>
      </form>
      <form className="profile-form" onSubmit={uploadAvatar}>
        <label>
          Foto de perfil (JPEG o PNG, máximo 2 MB)
          <input accept="image/jpeg,image/png" onChange={(event) => setImage(event.target.files?.[0] || null)} type="file" />
        </label>
        <button className="primary-button" disabled={uploading} type="submit">
          {uploading ? 'Subiendo...' : 'Guardar foto'}
        </button>
      </form>
      {import.meta.env?.VITE_GOOGLE_CLIENT_ID && (
        <div>
          <h2>Vincular cuenta de Google</h2>
          <GoogleButton onCredential={linkGoogle} />
        </div>
      )}
    </section>
  )
}
