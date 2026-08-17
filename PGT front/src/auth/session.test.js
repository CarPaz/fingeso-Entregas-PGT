import { describe, expect, it } from 'vitest'
import {
  guardarSesion,
  leerSesion,
  resolverRedireccion,
  rutaInicialPorRol,
} from './session'

function crearStorage() {
  const datos = new Map()
  return {
    getItem: (clave) => datos.get(clave) ?? null,
    setItem: (clave, valor) => datos.set(clave, valor),
    removeItem: (clave) => datos.delete(clave),
  }
}

describe('sesión de login', () => {
  it.each([
    ['TESISTA', '/mis-entregas'],
    ['PROFESOR', '/entregas'],
    ['COORDINADOR', '/entregas'],
  ])('guarda un login %s y define su inicio', (rol, rutaEsperada) => {
    const storage = crearStorage()
    guardarSesion({
      token: 'jwt-prueba',
      idUsuario: 7,
      nombre: 'Usuario de prueba',
      correo: 'usuario@universidad.cl',
      rol,
    }, storage)

    const sesion = leerSesion(storage)
    expect(sesion.valida).toBe(true)
    expect(sesion.usuario.rol).toBe(rol)
    expect(rutaInicialPorRol(rol)).toBe(rutaEsperada)
  })

  it('descarta una sesión con un rol desconocido', () => {
    const storage = crearStorage()
    expect(() => guardarSesion({ token: 'jwt', rol: 'INVITADO' }, storage))
      .toThrow('sesión válida')
  })
})

describe('navegación protegida por rol', () => {
  const sesionTesista = {
    valida: true,
    usuario: { rol: 'TESISTA' },
  }

  it('envía al login cuando no existe una sesión', () => {
    const destino = resolverRedireccion(
      { name: 'entregas', meta: { requiresAuth: true } },
      { valida: false, usuario: {} }
    )
    expect(destino).toBe('/login')
  })

  it('impide que un tesista abra la vista exclusiva del profesor', () => {
    const destino = resolverRedireccion(
      { name: 'entregas', meta: { requiresAuth: true, roles: ['PROFESOR'] } },
      sesionTesista
    )
    expect(destino).toBe('/mis-entregas')
  })

  it('permite que un tesista abra el formulario de entregas', () => {
    const destino = resolverRedireccion(
      { name: 'subir-entregas', meta: { requiresAuth: true, roles: ['TESISTA'] } },
      sesionTesista
    )
    expect(destino).toBeUndefined()
  })
})
