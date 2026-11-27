export function validateAvatar(file) {
  if (!file || !['image/jpeg', 'image/png'].includes(file.type)) {
    return 'Selecciona una imagen JPEG o PNG.'
  }
  if (!file.size || file.size > 2 * 1024 * 1024) {
    return 'La imagen debe pesar hasta 2 MB.'
  }
  return ''
}
