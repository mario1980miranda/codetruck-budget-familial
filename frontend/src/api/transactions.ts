import { obtenir } from './client'
import type { CategorieDepense, Filtres, Transaction } from '@/types/budget'

export function listerTransactions(filtres: Filtres, categorie: CategorieDepense | ''): Promise<Transaction[]> {
  return obtenir<Transaction[]>('/transactions', { ...filtres, categorie })
}
