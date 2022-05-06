> # Resultados de jogos de Tênis de Mesa

Esta é uma API para o registro de pontuação de jogos de tênis de mesa.

The plan is make a full stack application with a REST API (Java) using MySQL, Web application (Angular) and a Mobile application (Kotlin). Currently, the REST API is under development. The Web and Mobile applications development shall start soon.
# Endpoints
>## Jogador
GET /jogadores
GET /jogadores/{jogadorId}
POST /jogadores
PUT /jogadores/{jogadorId}
DELETE /jogadores/{jogadorId}
>## Partida
GET /partidas
GET /partidas/resumo
GET /partidas/{partidaId}
GET /partidas/{partidaId}/games/{gameId}/sacador
POST /partidas/{jogadorAId}/{jogadorBId}
PUT /partidas/{partidaId}/iniciar
PUT /partidas/{partidaId}/continuar
PUT /partidas/{partidaId}/primeiro-sacador/{jogadorId}
PUT /partidas/{partidaId}/completar
PUT /partidas/{partidaId}/cancelar
DELETE /partidas/{partidaId}
>## Game
GET /games
GET /games/{gameId}
PUT /games/{gameId}/pontuar/{pontuacaoA}/{pontuacaoB}
PUT /games/finalizado/{gameId}/pontuar/{pontuacaoA}/{pontuacaoB}
PUT /games/{gameId}/pontuar/{pontoId}/somar
PUT /games/{gameId}/pontuar/{pontoId}/subtrair
>## Pontuacao