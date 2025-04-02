## 🧹 Limpeza de Recursos Docker

Este guia contém comandos úteis para limpar completamente o ambiente Docker local.

### 1. Parar todos os containers em execução

```bash
docker stop $(docker ps -q)
```

### 2. Remover todos os containers

```bash
docker rm $(docker ps -aq)
```

### 3. Remover todas as imagens

```bash
docker rmi -f $(docker images -q)
```

### 4. Remover todos os volumes

```bash
docker volume rm $(docker volume ls -q)
```

### 5. (Opcional) Limpar tudo com um único comando

Para remover containers, imagens, volumes e redes **não utilizados**, use:

```bash
docker system prune -a --volumes
```

---

### 🧨 Tudo de uma vez (com `&&`)

Se quiser rodar todos os comandos de limpeza de uma vez, execute:

```bash
docker stop $(docker ps -q) && \
docker rm $(docker ps -aq) && \
docker rmi -f $(docker images -q) && \
docker volume rm $(docker volume ls -q)
```

> 💡 Use com cuidado! Esses comandos removem **tudo** do Docker local.