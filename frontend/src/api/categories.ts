import { envoyer, obtenir, remplacer, supprimer } from './client'
import type { Categorie, CategorieEnregistrement } from '@/types/budget'

export function listerCategories(): Promise<Categorie[]> {
  return obtenir<Categorie[]>('/categories')
}

export function creerCategorie(categorie: CategorieEnregistrement): Promise<Categorie> {
  return envoyer<Categorie>('/categories', categorie)
}

export function modifierCategorie(id: string, categorie: CategorieEnregistrement): Promise<Categorie> {
  return remplacer<Categorie>(`/categories/${id}`, categorie)
}

export function supprimerCategorie(id: string): Promise<string> {
  return supprimer(`/categories/${id}`)
}
