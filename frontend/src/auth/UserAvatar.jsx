import { useEffect, useState } from 'react'
import apiClient from '../api/client.js'

export default function UserAvatar({ user }) {
  const [image, setImage] = useState('')
  const [error, setError] = useState('')

  useEffect(() => {
    let active = true
    let url = ''
    let version = 0
    async function load() {
      const currentVersion = ++version
      try {
        const { data } = await apiClient.get('/api/users/me/avatar', { responseType: 'blob' })
        if (!active || currentVersion !== version) return
        if (url) URL.revokeObjectURL(url)
        url = URL.createObjectURL(data)
        setImage(url)
        setError('')
      } catch (requestError) {
        if (!active || currentVersion !== version) return
        if (requestError.response?.status !== 404) setError('No se pudo cargar tu foto.')
      }
    }
    load()
    window.addEventListener('bodriopopcorn:avatar-updated', load)
    return () => {
      active = false
      window.removeEventListener('bodriopopcorn:avatar-updated', load)
      if (url) URL.revokeObjectURL(url)
    }
  }, [user?.email])

  const displayName = user?.fullName || user?.email || 'Usuario'
  const initials = displayName.split(/[\s@._-]+/).filter(Boolean).slice(0, 2)
    .map((part) => part[0].toUpperCase()).join('')
  return (
    <>
      {image
        ? <img alt={`Foto de ${displayName}`} className="user-avatar user-avatar--image" src={image} />
        : <span aria-label={`Iniciales de ${displayName}`} className="user-avatar">{initials}</span>}
      {error && <span className="inline-error" role="status">{error}</span>}
    </>
  )
}
