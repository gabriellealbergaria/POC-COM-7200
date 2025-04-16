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
# Parar contêineres em execução (se houver)
[ "$(docker ps -q)" ] && docker stop $(docker ps -q)

# Remover todos os contêineres (se houver)
[ "$(docker ps -aq)" ] && docker rm -f $(docker ps -aq)

# Remover todas as imagens (se houver)
[ "$(docker images -q)" ] && docker rmi -f $(docker images -q)

# Remover todos os volumes (se houver)
[ "$(docker volume ls -q)" ] && docker volume rm $(docker volume ls -q)

# Remover redes personalizadas (ignorando bridge, host e none)
custom_networks=$(docker network ls -q | grep -vE 'bridge|host|none')
[ "$custom_networks" ] && docker network rm $custom_networks

# Reiniciar o Docker
sudo systemctl restart docker
```

> 💡 Use com cuidado! Esses comandos removem **tudo** do Docker local.