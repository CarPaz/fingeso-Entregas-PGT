export const MAX_SIZE_MB = 20
export const MAX_SIZE_BYTES = MAX_SIZE_MB * 1024 * 1024

export function obtenerArchivo(valor) {
  return Array.isArray(valor) ? valor[0] : valor
}

/*
 * Validación rápida para dar feedback antes de enviar. El backend vuelve a
 * validar MIME, tamaño y firma real del PDF porque el navegador no es confiable.
 */
export function validarPdf(valor) {
  const archivo = obtenerArchivo(valor)
  if (!archivo) return 'Este campo es obligatorio'
  if (archivo.type !== 'application/pdf') return 'El archivo debe ser un PDF'
  if (archivo.size > MAX_SIZE_BYTES) {
    return `El archivo no debe superar ${MAX_SIZE_MB}MB`
  }
  return true
}

export function crearPayloadEntrega(idProcesoTesis, idHitoEntrega) {
  return { idProcesoTesis, idHitoEntrega }
}

export function obtenerEndpointEntrega(tipoEnvio) {
  return tipoEnvio === 'final'
    ? '/api/entregas/final'
    : '/api/entregas/avance'
}
