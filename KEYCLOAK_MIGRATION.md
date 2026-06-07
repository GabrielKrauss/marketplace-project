# Keycloak: Backup, Migração e Persistência de Dados

Este documento descreve como criar e usar um volume Docker persistente para o Keycloak, como exportar/importar dados (realm) e como migrar o volume para outro computador.

Arquivos relevantes
- `marketplace-project/start-containers.ps1` — script local que agora monta o volume `marketplace-keycloak-data`.
- `marketplace-project/kc-deployment.yml` — manifesto Kubernetes (atualmente usa `emptyDir` — não é persistente).

1) Criar volume (local)

```powershell
# Cria o volume Docker para Keycloak (se ainda não existir)
docker volume create marketplace-keycloak-data
```

2) Fazer backup dos dados atuais do container Keycloak (host -> tar)

```powershell
# Parar o container (opcional)
docker stop marketplace-keycloak || true

# Copiar dados do container para uma pasta local
docker cp marketplace-keycloak:/opt/keycloak/data ./kc-data-backup

# Empacotar em um tar para transferência
tar -czf kc-data-backup.tar.gz -C kc-data-backup .
```

3) Criar tar diretamente do volume (se preferir salvar a partir do volume)

```powershell
# Se você já tem o volume montado e quer criar um tar a partir dele
docker run --rm -v marketplace-keycloak-data:/data -v ${PWD}:/backup alpine \
  sh -c "tar czf /backup/marketplace-keycloak-data.tar.gz -C /data ."
```

4) Restaurar tar em um volume no outro computador

```powershell
# No computador de destino:
# 1) criar o volume
docker volume create marketplace-keycloak-data

# 2) copiar o tar para o diretório atual e extrair no volume
docker run --rm -v marketplace-keycloak-data:/data -v ${PWD}:/backup alpine \
  sh -c "tar xzf /backup/marketplace-keycloak-data.tar.gz -C /data"
```

5) Subir Keycloak com o volume (script já atualizado)

```powershell
# Na pasta do repositório
.\start-containers.ps1
# escolha 'Subir todos' ou 'Subir especifico' -> Keycloak
```

6) Verificações rápidas após restaurar
- Abra `http://localhost:8081/realms/GamerHeaven/.well-known/openid-configuration` e confirme o campo `issuer`.
- Confirme que `marketplace-keycloak-data` contém os arquivos esperados: `docker run --rm -v marketplace-keycloak-data:/data alpine ls -la /data`.

7) Exportar/Importar realm (recomendado além do backup bruto)

- Pelo Admin Console: Realm → Realm settings → Export → `Full export` ou `Realm only`.
- Exportar via container (Keycloak 26.x - exemplo):

```powershell
# Dentro do container ou usando docker run, gerar export
docker run --rm --name kc-temp -v ${PWD}:/backup quay.io/keycloak/keycloak:26.1.0 \
  sh -c "kc.sh start-dev --export-realm --export-path=/backup" 
# Isso cria um arquivo de export no diretório atual.
```

- Importar em outro Keycloak: colocar o arquivo JSON em `/opt/keycloak/data/import` e iniciar com `--import-realm` ou usar a UI para importar.

8) Kubernetes: usar PVC (exemplo básico)

Se for rodar em Kubernetes e quiser persistência, troque `emptyDir` por um `PersistentVolumeClaim`. Exemplo de `PersistentVolumeClaim` (provisionamento dinâmico):

```yaml
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: keycloak-pvc
spec:
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 5Gi
```

E monte o claim no `kc-deployment.yml`:

```yaml
      volumes:
        - name: keycloak-data
          persistentVolumeClaim:
            claimName: keycloak-pvc
```

9) Boas práticas
- Sempre exporte o realm via Admin UI ou API além do backup do diretório de dados.
- Verifique permissões/UIDs dos arquivos se encontrar problemas de leitura.
- Versione o arquivo `kc-data-backup.tar.gz` em storage externo (S3, drive, repositório privado) para recuperação.

Se quiser, eu:
- Gero o YAML do `PersistentVolumeClaim` integrado ao `kc-deployment.yml` para seu ambiente, ou
- Gero os comandos exatos para exportar o realm via API/Admin CLI para sua versão do Keycloak.

---
Gerado automaticamente a partir do estado do repositório em 2026-06-07.
