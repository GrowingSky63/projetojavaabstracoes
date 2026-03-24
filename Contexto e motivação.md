# TEMA

A aplicação realiza a gestão de planos e usuários para atuar como um intermediário entre páginas de checkout, e nodes de hospedagens de servidores para jogos, como pterodactyl, por exemplo.

# CONTEXTO E MOTIVAÇÃO

Em casa eu tenho um servidor homelab que atualmente está hospedando um gerenciador de containers para hospedagens de jogos. Atualmente estou terminando de desenvolver o gerenciador de planos e a página de vendas com o intúito de intermediar o serviço terceiro de checkout, e o gerenciador de containers. Lá, desenvolvi tudo em python.

Trouxe esta ideia para meus colegas, e desenvolvemos um MVC bem simples da minha implementação original.

Este projeto foi paltado em cima do design pattern `Domain Driven Design` (DDD), que consiste em declarar cada classe com seu domínio bem definido, para que seja falcilmente modulado e reutilizado. Escolhemos o DDD, pois ele exige o uso intensivo de abstrações.