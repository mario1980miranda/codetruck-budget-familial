import { envoyer, obtenir } from './client'
import type { Compte, CompteEnregistrement } from '@/types/budget'

export function listerComptes(): Promise<Compte[]> {
  return obtenir<Compte[]>('/comptes')
}

export function creerCompte(compte: CompteEnregistrement): Promise<Compte> {
  return envoyer<Compte>('/comptes', compte)
}
