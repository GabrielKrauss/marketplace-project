# Usa a imagem oficial do Node.js
FROM node:18 AS builder

# Define o diretório de trabalho dentro do container
WORKDIR /app

# Copia os arquivos de dependência e instala os pacotes
# Copia o restante do código para dentro do container
COPY . .
RUN npm install
 
RUN npm install -g @angular/cli
 
RUN npm run build

FROM  nginx:alpine

COPY --from=builder /app/dist/marketplace-app/browser /usr/share/nginx/html/
COPY nginx.conf /etc/nginx/nginx.conf
COPY mime.types /etc/nginx/mime.types
# Expõe a porta 4200
EXPOSE 4200

CMD ["nginx", "-g", "daemon off;"]
