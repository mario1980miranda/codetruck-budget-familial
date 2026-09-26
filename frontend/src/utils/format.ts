import type { Filtres } from '@/types/budget'

const formatMontant = new Intl.NumberFormat('fr-CA', { style: 'currency', currency: 'CAD' })
const FORMAT_DATE_ISO = /^(\d{4})-(\d{2})-(\d{2})$/

export function formaterMontant(valeur: number | null | undefined): string {
  return formatMontant.format(valeur ?? 0)
}

// Vérifie le format AAAA-MM-JJ, puis que la date existe vraiment
// (ex. 2026-02-30 est refusée) en la reconstruisant et en comparant.
export function estDateIso(valeur: string): boolean {
  const morceaux = FORMAT_DATE_ISO.exec(valeur)
  if (!morceaux) {
    return false
  }
  const date = new Date(Number(morceaux[1]), Number(morceaux[2]) - 1, Number(morceaux[3]))
  return versIso(date) === valeur
}

export function libelle(valeur: string): string {
  return valeur.replace(/_/g, ' ').toLowerCase().replace(/^\w/, (lettre) => lettre.toUpperCase())
}

function versIso(date: Date): string {
  const mois = String(date.getMonth() + 1).padStart(2, '0')
  const jour = String(date.getDate()).padStart(2, '0')
  return `${date.getFullYear()}-${mois}-${jour}`
}

// Période par défaut : du 1er jour d'il y a deux mois jusqu'à la fin du mois
// courant, car les relevés sont importés avec un ou deux mois de retard.
export function filtresTroisDerniersMois(compteId = ''): Filtres {
  const aujourdHui = new Date()
  return {
    debut: versIso(new Date(aujourdHui.getFullYear(), aujourdHui.getMonth() - 2, 1)),
    fin: versIso(new Date(aujourdHui.getFullYear(), aujourdHui.getMonth() + 1, 0)),
    titulaire: '',
    typeCompte: '',
    compteId,
  }
}
