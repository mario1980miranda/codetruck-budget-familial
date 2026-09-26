// Point d'entrée unique vers le backend : préfixe /api (proxy Vite ou nginx) et
// en-tête de version exigé par WebConfig.java.

const PREFIXE_API = '/api'
const VERSION_API = 'v1'

// Construit l'URL en ignorant les paramètres vides, appelle le backend, puis
// retourne le JSON (ou lève une erreur si le statut HTTP n'est pas 2xx).
export async function obtenir<T>(chemin: string, parametres: Record<string, string> = {}): Promise<T> {
  const recherche = new URLSearchParams()
  for (const [cle, valeur] of Object.entries(parametres)) {
    if (valeur !== '') {
      recherche.set(cle, valeur)
    }
  }
  const chaine = recherche.toString()
  const url = chaine ? `${PREFIXE_API}${chemin}?${chaine}` : `${PREFIXE_API}${chemin}`

  const reponse = await fetch(url, { headers: { 'X-API-VERSION': VERSION_API } })
  return lireReponse<T>(reponse, chemin)
}

// Envoie un POST : un FormData part tel quel (multipart, ex. fichier PDF),
// tout autre objet est envoyé en JSON.
export async function envoyer<T>(chemin: string, corps: object | FormData): Promise<T> {
  const enTetes: Record<string, string> = { 'X-API-VERSION': VERSION_API }
  let contenu: BodyInit
  if (corps instanceof FormData) {
    contenu = corps
  } else {
    enTetes['Content-Type'] = 'application/json'
    contenu = JSON.stringify(corps)
  }

  const reponse = await fetch(`${PREFIXE_API}${chemin}`, { method: 'POST', headers: enTetes, body: contenu })
  return lireReponse<T>(reponse, chemin)
}

// Retourne le JSON si le statut est 2xx ; sinon lève une erreur avec le message
// renvoyé par le backend (texte brut comme "Compte introuvable.") quand il y en a un.
async function lireReponse<T>(reponse: Response, chemin: string): Promise<T> {
  if (reponse.ok) {
    return (await reponse.json()) as T
  }
  if (reponse.status === 413) {
    throw new Error('Fichier trop volumineux (1 Mo maximum).')
  }
  const texte = await reponse.text()
  if (texte && !texte.trimStart().startsWith('{') && !texte.trimStart().startsWith('<')) {
    throw new Error(texte)
  }
  throw new Error(`Erreur ${reponse.status} sur ${chemin}`)
}
