// Miroir des enums et des objets JSON exposés par le backend Java.

export type TitulaireCompte = 'MARIO' | 'CRISTINA' | 'CONJOINT'
export type TypeCompte = 'COURANT' | 'CARTE_CREDIT'
export type TypeTransaction = 'DEPENSE' | 'REVENU' | 'EPARGNE' | 'TRANSFERT_INTERNE'
// Nom d'une catégorie (CategorieModele.nom) : la liste vient de GET /categories.
export type CategorieDepense = string

export const TITULAIRES: TitulaireCompte[] = ['MARIO', 'CRISTINA', 'CONJOINT']
export const TYPES_COMPTE: TypeCompte[] = ['COURANT', 'CARTE_CREDIT']

// CategorieModele
export interface Categorie {
  id: string
  nom: CategorieDepense
  description: string
}

// CategorieEnregistrementDto (corps de POST /categories)
export interface CategorieEnregistrement {
  nom: CategorieDepense
  description: string
}

// CompteModele
export interface Compte {
  id: string
  nom: string
  titulaire: TitulaireCompte
  typeCompte: TypeCompte
}

// CompteEnregistrementDto (corps de POST /comptes)
export interface CompteEnregistrement {
  nom: string
  titulaire: TitulaireCompte
  typeCompte: TypeCompte
}

// TransactionModele, tel qu'inclus dans un relevé (et réponse de PATCH /transactions/{id}/categorie)
export interface TransactionReleve {
  id: string
  date: string
  description: string
  montant: number
  typeTransaction: TypeTransaction
  categorie: CategorieDepense | null
}

// ReleveModele (réponse de POST /comptes/{compteId}/releves)
export interface Releve {
  id: string
  compte: Compte
  dateImport: string
  periodeDebut: string | null
  periodeFin: string | null
  transactions: TransactionReleve[]
}

// AgregationDto
export interface Agregation {
  periodeDebut: string
  periodeFin: string
  totalRevenus: number
  totalDepenses: number
  totalEpargne: number
  depensesParCategorie: Partial<Record<CategorieDepense, number>>
}

// TransactionAffichageDto
export interface Transaction {
  id: string
  date: string
  description: string
  montant: number
  typeTransaction: TypeTransaction
  categorie: CategorieDepense | null
  compteNom: string
  titulaire: TitulaireCompte
}

// TransactionCategorieEnregistrementDto (corps de PATCH /transactions/{id}/categorie)
export interface TransactionCategorieEnregistrement {
  categorie: CategorieDepense
}

// Filtres communs à /agregations et /transactions. Une chaîne vide = pas de filtre.
export interface Filtres {
  debut: string
  fin: string
  titulaire: TitulaireCompte | ''
  typeCompte: TypeCompte | ''
  compteId: string
}
