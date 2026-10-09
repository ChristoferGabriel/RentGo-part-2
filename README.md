# RentGo — Aluguel de carros (Trabalho 2)

App Android de locação de carros feito em **Kotlin + Jetpack Compose**, com navegação entre telas (NavHost), barra de navegação inferior, listas reativas e telas de detalhes.

> **Integrantes:** ⚠️ PREENCHER (nome de cada integrante do trio)
> **Disciplina / Professor:** ⚠️ PREENCHER

---

## 1. Como rodar o projeto

**Requisitos:** Android Studio (versão recente), JDK 17+ e um emulador ou celular Android (minSdk 24).

1. Clone o repositório:
   ```bash
   git clone <URL-DO-REPOSITORIO>
   ```
2. Abra a pasta `RentGo` no Android Studio e aguarde o **Gradle Sync**.
3. Selecione um emulador/dispositivo e clique em **Run ▶**.
4. Na primeira abertura, use **"Criar conta"** (os dados ficam só na memória; ao fechar o app, as contas somem).

**Versões principais:** Kotlin 2.2.10 · Compose BOM 2026.02.01 · Navigation Compose 2.8.9 · Material 3.

---

## 2. Estrutura do código

```
app/src/main/java/com/example/rentgo/
├── MainActivity.kt          # ponto de entrada, chama AppNavigation()
├── navigation/
│   ├── Rotas.kt             # objeto Rotas com todas as rotas nomeadas
│   └── AppNavigation.kt     # NavHost + Scaffold + BottomNavigation
├── model/                   # data classes (Carro, Reserva, Usuario, DadosReserva)
├── data/
│   ├── CarrosRepositorio.kt   # lista reativa de carros + favoritos (mutableStateListOf)
│   ├── ReservasRepositorio.kt # lista reativa de reservas (mutableStateListOf)
│   └── SessaoUsuario.kt       # contas criadas e usuário logado (em memória)
└── ui/
    ├── screens/             # uma tela por arquivo
    ├── components/          # BarraInferior, TopBar, campos, avatar, logo
    └── theme/               # cores, tipografia, tema
```

---

## 3. Telas e navegação

| Rota (`Rotas.`) | Tela | Função |
|---|---|---|
| `LOGIN` | LoginScreen | Entrar, criar conta ou recuperar senha |
| `CADASTRO` | CadastroScreen | Criar conta (TopAppBar com voltar) |
| `HOME` | HomeScreen | Saudação, busca, ofertas da semana, categorias |
| `CARROS` | TelaPesquisa | **Lista 1** (LazyColumn + Card): busca, filtros, **adicionar** e **remover** carros |
| `DETALHE_CARRO` (`detalhe_carro/{carroId}`) | TelaDetalheCarro | **Detalhe 1**: total calculado, favoritar, carros parecidos, reservar |
| `PAGAMENTO` | TelaPagamento | Cartão/Pix com validação do formulário |
| `RESERVA_CONCLUIDA` | TelaReservaConcluida | Confirmação da reserva |
| `AGENDAMENTO` | TelaAgendamento | **Lista 2** (LazyColumn + Card): reservas, **agendar** e **cancelar** |
| `DETALHE_RESERVA` (`detalhe_reserva/{reservaId}`) | TelaDetalheReserva | **Detalhe 2**: editar diárias com total recalculado, dados do carro, cancelar |
| `FAVORITOS` | TelaFavoritos | Lista dos carros favoritados (clicar abre o detalhe; coração remove) |
| `PERFIL` | PerfilScreen | Dados do usuário e sair da conta |

**Data classes:** `Carro` (lista de Carros) e `Reserva` (lista de Agendamento). Também existem `Usuario` e `DadosReserva`.

**BottomNavigation:** `NavigationBar` com 5 abas — Início, Carros, Agendamento, Favoritos e Perfil. A troca de aba usa `popUpTo` + `launchSingleTop` + `restoreState` para não empilhar cópias da mesma tela. A barra some em Login, Cadastro, Pagamento, Reserva concluída e nos Detalhes.

**Botão de voltar:** `TopAppBar` com `navController.popBackStack()` no Cadastro, no Pagamento e nos dois Detalhes (componente `TopoRentGo`).

---

## 4. Documentação do processo e das decisões

### 4.1 Como estava o projeto no Trabalho 1 e o que mudou?

⚠️ PREENCHER com as palavras do trio. Pontos para citar:
- Como era o app no Trabalho 1 (telas, o que tinha).
- O que foi acrescentado: NavHost/NavController, objeto `Rotas`, BottomNavigation, listas reativas, telas de detalhes, login/cadastro com sessão em memória.

📸 *Print: app no Trabalho 1* → `docs/prints/01-trabalho1.png`
📸 *Print: app agora* → `docs/prints/02-home.png`

### 4.2 Por que essas telas novas?

O fluxo segue o caminho de quem aluga um carro: **entrar → ver ofertas → pesquisar → ver detalhes → pagar → acompanhar a reserva**.

- **Login / Cadastro:** identificam o usuário; o nome aparece no "Olá, ..." da Home.
- **Home:** vitrine com ofertas (LazyRow), busca, ordenação e categorias.
- **Carros (Pesquisa):** lista completa de veículos, com busca por nome, filtros, botão "Adicionar carro" e lixeira para remover.
- **Detalhe do carro:** mostra o carro escolhido, calcula o total pelas diárias e leva ao pagamento.
- **Pagamento / Reserva concluída:** fecham o fluxo de locação; a reserva paga entra na lista de Agendamento.
- **Agendamento:** lista de reservas do usuário (segunda lista do app), com formulário para agendar e botão para cancelar.
- **Detalhe da reserva:** permite editar as diárias da reserva e ver os dados do carro.
- **Favoritos:** carros salvos com o coração do Detalhe.
- **Perfil:** mostra a conta logada e permite sair.

📸 *Prints:* `03-login.png`, `04-cadastro.png`, `05-carros.png`, `06-detalhe.png`, `07-pagamento.png`, `08-reserva-concluida.png`, `09-agendamento.png`, `10-favoritos.png`, `11-perfil.png`

### 4.3 Decisões de configuração e organização do código

- **Rotas num objeto só (`Rotas`):** evita strings soltas pelo código e erros de digitação. A rota de detalhe tem função auxiliar `Rotas.detalheCarro(id)`.
- **Um único `NavHost`** dentro de um `Scaffold` em `AppNavigation.kt`; a `NavigationBar` só aparece nas rotas listadas em `rotasComBarra`.
- **Dados em `object` com `mutableStateListOf`:** `CarrosRepositorio` (carros) e `SessaoUsuario` (contas). Qualquer tela que lê a lista se atualiza sozinha. Ficam só na memória, como o trabalho permite.
- **Telas e componentes separados em pastas** (`screens`, `components`, `theme`), um composable principal por arquivo.
- **Passagem de dados pela rota:** a Home/Lista envia só o `id` do carro; a tela de detalhes busca o item com `CarrosRepositorio.buscarPorId(id)`.
- **`PagamentoViewModel` compartilhado:** criado fora do NavHost para que Pagamento e Reserva Concluída usem os mesmos dados.
- **Duas listas, duas data classes:** `CarrosRepositorio` guarda `Carro` e `ReservasRepositorio` guarda `Reserva`. Cada tela de lista usa `LazyColumn` + `items(lista, key = { it.id })` + `Card`.
- **Remover não abre o detalhe:** o card inteiro é clicável (abre o Detalhe) e a lixeira é um `IconButton` separado, que consome o clique.
- **Formulários em `AlertDialog`:** "Novo carro" (nome, preço numérico, categoria) e "Nova reserva" (escolher carro, diárias numéricas), com validação e mensagem de erro.
- **Componentes reaproveitados:** `TopoRentGo` (TopAppBar com voltar), `SeletorDiarias` (− / +) e `CardDoCarro` (usado em Carros e Favoritos).
- **Pilha de navegação:** após login e após o pagamento usamos `popUpTo(... inclusive = true)` para o botão voltar não retornar a telas que não fazem sentido.

### 4.4 Complexidade extra na tela de Detalhes

**Detalhe do carro** (vai além de reexibir os campos):
- **Calcula** o valor total (preço da diária × número de diárias escolhido com − / +) e mostra o preço no Pix (5% de desconto).
- **Combina as duas áreas do app:** mostra "Outros Sedã/SUV/Elétrico" (carros da mesma categoria) e, ao tocar, abre o detalhe de outro carro (navegação secundária).
- Botão de **favoritar** na TopAppBar e botão **Reservar e pagar**, que leva ao Pagamento já com as diárias escolhidas.

**Detalhe da reserva:**
- **Edita a reserva ali mesmo:** − / + muda as diárias e o total é recalculado na hora.
- **Combina as duas listas:** busca o `Carro` da reserva para mostrar categoria e nota.
- Botão **Cancelar reserva** (remove da lista e volta).

**Por que escolhemos isso:** ⚠️ PREENCHER com as palavras do trio (ex.: faz sentido num app de aluguel o cliente ver quanto vai pagar antes de reservar e poder ajustar os dias depois).

📸 *Print/vídeo da tela de detalhes com a funcionalidade extra* → `docs/prints/12-detalhe-extra.png`

### 4.5 Dificuldades e como foram resolvidas

⚠️ PREENCHER com o relato real de cada integrante (dificuldade → como resolveram).

---

## 5. Limitações conhecidas

- Os dados (contas, listas, reservas) ficam só na memória e se perdem ao fechar o app; persistência fica para o próximo trabalho.
