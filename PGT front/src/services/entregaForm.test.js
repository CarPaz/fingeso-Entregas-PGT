import { describe, expect, it } from 'vitest'
import {
  MAX_SIZE_BYTES,
  crearPayloadEntrega,
  obtenerEndpointEntrega,
  validarPdf,
} from './entregaForm'

describe('formulario de entrega', () => {
  it('construye el DTO solamente con proceso e hito seleccionados', () => {
    expect(crearPayloadEntrega(10, 20)).toEqual({
      idProcesoTesis: 10,
      idHitoEntrega: 20,
    })
  })

  it('elige endpoints distintos para avance y final', () => {
    expect(obtenerEndpointEntrega('avance')).toBe('/api/entregas/avance')
    expect(obtenerEndpointEntrega('final')).toBe('/api/entregas/final')
  })

  it('acepta un PDF dentro del límite', () => {
    expect(validarPdf({ type: 'application/pdf', size: 1024 })).toBe(true)
  })

  it('rechaza otro formato y archivos mayores a 20 MB', () => {
    expect(validarPdf({ type: 'image/png', size: 1024 }))
      .toBe('El archivo debe ser un PDF')
    expect(validarPdf({ type: 'application/pdf', size: MAX_SIZE_BYTES + 1 }))
      .toContain('20MB')
  })
})
