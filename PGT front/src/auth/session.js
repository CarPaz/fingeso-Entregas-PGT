/*
 * Funciones centrales de sesión. Mantenerlas fuera de las vistas permite que
 * login y navegación apliquen exactamente las mismas reglas y puedan probarse.
 */
export const ROLES_VALIDOS = ['TESISTA', 'PROFESOR', 'COORDINADOR']

export function rutaInicialPorRol(rol) {
  return rol === 'TESISTA' ? '/mis-entregas' : '/entregas'
}

export function guardarSesion(respuestaLogin, storage = localStorage) {
  const { token, idUsuario, nombre, correo, rol } = respuestaLogin

  if (!token || !ROLES_VALIDOS.includes(rol)) {
    throw new Error('La respuesta de autenticación no contiene una sesión válida')
  }

  const usuario = { idUsuario, nombre, correo, rol }
  storage.setItem('token', token)
  storage.setItem('usuario', JSON.stringify(usuario))
  return { token, usuario }
}

export function leerSesion(storage = localStorage) {
  try {
    const token = storage.getItem('token')
    const usuario = JSON.parse(storage.getItem('usuario') || '{}')
    const valida = Boolean(token && ROLES_VALIDOS.includes(usuario.rol))

    if (!valida) {
      storage.removeItem('token')
      storage.removeItem('usuario')
      return { valida: false, token: null, usuario: {} }
    }

    return { valida: true, token, usuario }
  } catch {
    storage.removeItem('token')
    storage.removeItem('usuario')
    return { valida: false, token: null, usuario: {} }
  }
}

/*
 * Devuelve una ruta solamente cuando Vue Router debe redirigir al usuario.
 */
export function resolverRedireccion(to, sesion) {
  if (to.meta.requiresAuth && !sesion.valida) return '/login'

  if (to.meta.roles && !to.meta.roles.includes(sesion.usuario.rol)) {
    return sesion.valida ? rutaInicialPorRol(sesion.usuario.rol) : '/login'
  }

  if (to.name === 'login' && sesion.valida) {
    return rutaInicialPorRol(sesion.usuario.rol)
  }

  return undefined
}
