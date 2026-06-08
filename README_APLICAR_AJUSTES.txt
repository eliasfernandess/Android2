AJUSTES REALIZADOS PARA O PROJETO ANDROID2

Nome do app: StudyFlow
Logo: adicionada em app/src/main/res/drawable/ic_studyflow_logo.xml
Menu: adicionada a tela TelaMenu
Banco: Firebase Auth + Firestore

TELAS DO PROJETO
1. FormLogin - Login
2. FormCadastro - Cadastro de usuário, já faz INSERT em Usuarios
3. TelaMenu - Menu principal
4. TelaPerfil - Perfil do usuário com UPDATE do nome
5. TelaCadastroTarefa - Novo INSERT em Tarefas
6. TelaBuscarTarefa - Busca apenas 1 registro pelo código, exibe em tabela e permite UPDATE
7. TelaListaTarefasPaginada - Busca vários registros com paginação de 5 em 5
8. TelaSobre - Tela institucional/sobre o app

COMO APLICAR
1. Extraia este ZIP na raiz do seu projeto Android2.
2. Confirme a substituição dos arquivos quando o Windows perguntar.
3. Abra o projeto no Android Studio.
4. Clique em Sync Project with Gradle Files.
5. Execute o app.

OBSERVAÇÃO
Eu não dei push direto no GitHub porque isso exige autenticação da sua conta.
Após testar, você pode enviar com:

git add .
git commit -m "Finaliza projeto com menu, 8 telas, inserts, busca e update"
git push origin master

COLEÇÕES FIRESTORE USADAS
Usuarios:
- nome
- email
- uid
- criadoEm

Tarefas:
- codigo
- titulo
- descricao
- status
- dataEntrega
- usuarioId
- criadoEm
- atualizadoEm
