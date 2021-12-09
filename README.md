# Sobrevivência para TARDIS 1.18

Se forem jogar no modo de duas equipes, altere o "habilitado" dentro da config.yml para `true`. Caso forem jogar em solo, não altere NADA na config.yml!

---

Os comandos são os seguintes:

Para coordenadas:
- `/addcoord <nome>` ou `/ac <nome>` - Salva uma coordenada com tal nome.
- `/removecoord <nome>` ou `/rc <nome>` - Remove a coordenada de tal nome.
- `/listcoords` ou `/lc` - Lista todas as coordenadas que você salvou e as coordenadas que compartilharam com você.
- `/sharecoord <nome> <jogador>`, `/shcoord <nome> <jogador>`, `/compcoord <nome> <jogador>` ou `/cc <nome> <jogador>` - Compartilha a coordenada de tal nome com tal jogador. Para descompartilhar, basta repetir o mesmo comando.
- `/infocoord <nome>` ou `/ic <nome>` - Verifica informações sobre tal coordenada que você salvou ou que compartilharam com você.


Para o sistema de login:
- `/register <senha>`, `/registro <senha>` ou `/registrar <senha>` - Se registra no servidor.
- `/login <senha>`, `/logar <senha>` ou `/log-in <senha>` - Se identifica no servidor. Não é necessário repetir esse comando toda vez que conectar, a autenticação será feita automaticamente pelo endereço de IP utilizado recentemente.
- `/changepassword <nova senha>` ou `/trocarsenha <nova senha>` - Troca sua senha.


Para o chat:
- `/global <mensagem...>` ou `/g <mensagem...>` - Envia uma mensagem para todos do servidor.
- `/tell <jogador> <mensagem...>` - Envia uma mensagem privada para tal jogador.
- `/r <mensagem...>` - Envia uma mensagem privada para o jogador no qual você conversou recentemente.

OBS.: Se o sistema de equipes estiver habilitado, a mensagem local será enviada para todos da equipe, independentemente da distância e dimensão que esteja. Caso esteja desabilitado, a mensagem local será enviada apenas para quem está próximo de 200 blocos do remetente.

---
