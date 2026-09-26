import { obtenir } from './client'
import type { Agregation, Filtres } from '@/types/budget'

export function obtenirAgregation(filtres: Filtres): Promise<Agregation> {
  return obtenir<Agregation>('/agregations', { ...filtres })
}
