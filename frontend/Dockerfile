# Étape 1 : build Vite (vérification des types incluse).
FROM node:22-alpine AS construction
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci
COPY . .
RUN npm run build

# Étape 2 : nginx sert les fichiers statiques et redirige /api vers le backend.
FROM nginx:alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=construction /app/dist /usr/share/nginx/html
EXPOSE 80
