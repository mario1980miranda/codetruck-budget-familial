import { modifier, obtenir } from './client'
import type {
  CategorieDepense,
  Filtres,
  Transaction,
  TransactionCategorieEnregistrement,
  TransactionReleve,
} from '@/types/budget'

export function listerTransactions(filtres: Filtres, categorie: CategorieDepense | ''): Promise<Transaction[]> {
  return obtenir<Transaction[]>('/transactions', { ...filtres, categorie })
}

export function modifierCategorieTransaction(id: string, categorie: CategorieDepense): Promise<TransactionReleve> {
  const corps: TransactionCategorieEnregistrement = { categorie }
  return modifier<TransactionReleve>(`/transactions/${id}/categorie`, corps)
}
