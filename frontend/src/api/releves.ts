import { envoyer } from './client'
import type { Releve } from '@/types/budget'

// Le PDF est extrait, masqué puis catégorisé par Claude côté backend :
// l'appel peut prendre plusieurs secondes.
export function importerReleve(compteId: string, fichier: File): Promise<Releve> {
  const formulaire = new FormData()
  formulaire.append('fichier', fichier)
  return envoyer<Releve>(`/comptes/${compteId}/releves`, formulaire)
}
