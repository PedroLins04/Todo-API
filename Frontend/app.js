//const API_URL = "http://localhost:8080";
const API_URL = "https://keeptheflow-api.onrender.com";
const AUTH_URL = `${API_URL}/auth`;
const TODO_URL = `${API_URL}/todos`;

function obterToken() {
  return sessionStorage.getItem("token");
}

async function requisicaoAPI(url, options = {}) {
  const token = obterToken();

  const headers = {
    ...options.headers,
  };

  if (options.body) {
    headers["Content-Type"] = "application/json";
  }

  if (token) {
    headers["Authorization"] = `Bearer ${token}`;
  }

  const resposta = await fetch(url, {
    ...options,
    headers,
  });

  if (resposta.status === 401 || resposta.status === 403) {
    throw new Error("Sessão inválida ou sem permissão.");
  }

  if (!resposta.ok) {
    const detalhe = await resposta.text();

    console.error("Resposta do backend:", {
      status: resposta.status,
      detalhe,
    });

    throw new Error(
      `Erro HTTP ${resposta.status}: ${detalhe || resposta.statusText}`,
    );
  }

  if (resposta.status === 204) {
    return null;
  }

  return resposta.json();
}

const listaPendentes = document.getElementById("listaPendentes");
const listaConcluidas = document.getElementById("listaConcluidas");
let tarefaSelecionada = null;

document.querySelector("#btnNovaTarefa").addEventListener("click", () => {
  prepararModalCreate();
});

async function buscarTarefas() {
  try {
    const tarefas = await requisicaoAPI(TODO_URL);

    exibirTarefas(tarefas);
    atualizarContadores(tarefas);

    return tarefas;
  } catch (error) {
    console.error("Erro ao buscar tarefas:", error);

    listaPendentes.textContent = "Erro ao buscar tarefas";
    listaConcluidas.textContent = "Erro ao buscar tarefas";
  }
}

function exibirTarefas(tarefas) {
  tarefas = [...tarefas].sort((a, b) => {
    return b.priority - a.priority || a.name.localeCompare(b.name, "pt-BR");
  });

  listaPendentes.replaceChildren();
  listaConcluidas.replaceChildren();

  const prioridades = {
    1: { nome: "Baixa", classe: "text-bg-info" },
    2: { nome: "Média", classe: "text-bg-warning" },
    3: { nome: "Alta", classe: "text-bg-danger" },
  };

  tarefas.forEach((tarefa) => {
    const card = document.createElement("article");
    card.draggable = true;
    card.dataset.id = tarefa.id;
    card.classList.add("task-card");

    const header = document.createElement("div");
    header.classList.add(
      "d-flex",
      "justify-content-between",
      "align-items-start",
      "gap-2",
      "mb-2",
    );

    const name = document.createElement("h3");
    name.classList.add("mb-0", "fs-10");
    name.textContent = tarefa.name;

    const prioridade = prioridades[tarefa.priority];

    const priority = document.createElement("span");
    priority.classList.add(
      "badge",
      "rounded-pill",
      "fs-6",
      prioridade?.classe ?? "text-bg-secondary",
    );
    priority.textContent = prioridade?.nome ?? "Sem prioridade";

    header.append(name, priority);

    const description = document.createElement("p");
    description.classList.add("mb-2");
    description.textContent =
      tarefa.description || "Nenhuma descrição informada.";

    const footer = document.createElement("div");
    footer.classList.add(
      "d-flex",
      "justify-content-between",
      "align-items-center",
      "gap-2",
      "pt-2",
      "mt-2",
    );

    const status = document.createElement("small");
    status.classList.add("text-body-white", "fw-semibold");
    if (tarefa.done) {
      status.classList.add("text-bg-success", "px-2", "py-1", "rounded");
    } else {
      status.classList.add("text-bg-warning", "px-2", "py-1", "rounded");
    }
    status.textContent = tarefa.done ? "Concluída" : "Pendente";

    const actions = document.createElement("div");
    actions.classList.add("d-flex", "gap-2");

    const editButton = document.createElement("button");
    editButton.type = "button";
    editButton.addEventListener("click", () => {
      pepararModalEdit(tarefa);
    });
    editButton.setAttribute("data-bs-toggle", "modal");
    editButton.setAttribute("data-bs-target", "#modalTarefa");
    editButton.classList.add(
      "btn",
      "btn-sm",
      "btn-outline-info",
      "fw-semibold",
      "me-1",
    );
    editButton.textContent = "Editar";

    const deleteButton = document.createElement("button");
    deleteButton.type = "button";
    deleteButton.setAttribute("data-bs-toggle", "modal");
    deleteButton.setAttribute("data-bs-target", "#modalExcluir");
    deleteButton.classList.add(
      "btn",
      "btn-sm",
      "btn-outline-danger",
      "fw-bold",
    );
    deleteButton.textContent = "Excluir";

    deleteButton.addEventListener("click", () => {
      tarefaSelecionada = tarefa.id;
    });

    actions.append(editButton, deleteButton);
    footer.append(status, actions);

    card.append(header, description, footer);

    if (tarefa.done) {
      listaConcluidas.appendChild(card);
    } else {
      listaPendentes.appendChild(card);
    }
  });
}

function atualizarContadores(tarefas) {
  const totalTarefas = document.getElementById("totalTarefas");
  const totalPendentes = document.getElementById("totalPendentes");
  const totalConcluidas = document.getElementById("totalConcluidas");

  totalTarefas.textContent = tarefas.length;

  totalPendentes.textContent = tarefas.filter((tarefa) => !tarefa.done).length;

  totalConcluidas.textContent = tarefas.filter((tarefa) => tarefa.done).length;

  countPendentes.textContent = tarefas.filter((tarefa) => !tarefa.done).length;

  countConcluidas.textContent = tarefas.filter((tarefa) => tarefa.done).length;
}

async function adicionarTarefa(tarefa) {
  try {
    await requisicaoAPI(TODO_URL, {
      method: "POST",
      body: JSON.stringify(tarefa),
    });

    await buscarTarefas();
  } catch (error) {
    console.error("Erro ao adicionar tarefa:", error);
  }
}

async function atualizarTarefa(tarefaId, tarefaAtualizada) {
  try {
    await requisicaoAPI(`${TODO_URL}/${tarefaId}`, {
      method: "PUT",
      body: JSON.stringify(tarefaAtualizada),
    });

    await buscarTarefas();
  } catch (error) {
    console.error("Erro ao atualizar tarefa:", error);
  }
}

const formTarefa = document.querySelector("#formTarefa");
const campoPrioridade = document.querySelector("#priority");

formTarefa.addEventListener("submit", async (event) => {
  event.preventDefault();

  if (!campoPrioridade.value || campoPrioridade.value === "default") {
    campoPrioridade.classList.add("is-invalid");

    let mensagem =
      campoPrioridade.parentElement.querySelector(".invalid-feedback");

    if (!mensagem) {
      mensagem = document.createElement("div");
      mensagem.classList.add("invalid-feedback");
      campoPrioridade.insertAdjacentElement("afterend", mensagem);
    }

    mensagem.textContent = "Selecione uma prioridade.";

    campoPrioridade.focus();
    return;
  }

  campoPrioridade.classList.remove("is-invalid");

  const tarefa = {
    name: document.querySelector("#name").value.trim(),
    description: document.querySelector("#description").value.trim(),
    priority: Number(campoPrioridade.value),
    done: document.querySelector("#done").checked,
  };

  if (document.querySelector("#tarefaId").value) {
    const tarefaId = document.querySelector("#tarefaId").value;
    await atualizarTarefa(tarefaId, tarefa);
  } else {
    await adicionarTarefa(tarefa);
  }

  fecharModal();
});

campoPrioridade.addEventListener("change", () => {
  if (campoPrioridade.value && campoPrioridade.value !== "default") {
    campoPrioridade.classList.remove("is-invalid");
  }
});

// Function for clear the form
function limparFormulario() {
  document.querySelector("#tarefaId").value = "";
  document.querySelector("#name").value = "";
  document.querySelector("#description").value = "";
  document.querySelector("#priority").value = "default";
  document.querySelector("#done").checked = false;
}

// Function for close the modal
function fecharModal() {
  const modalElement = document.querySelector("#modalTarefa");
  const modalInstance = bootstrap.Modal.getInstance(modalElement);
  modalInstance?.hide(modalElement);
}

const modalElement = document.querySelector("#modalTarefa");
//clearing the modal EventListener
modalElement.addEventListener("hidden.bs.modal", () => {
  limparFormulario();
});

//preparing the modal for crate a new task
function prepararModalCreate() {
  document.querySelector("#tarefaId").value = "";
  document.querySelector("#modalTarefaLabel").textContent = "Nova tarefa";
  document.querySelector("#btnModalTarefa").textContent = "Salvar tarefa";
}

//preparing the modal for the task you'll edit
function pepararModalEdit(tarefa) {
  document.querySelector("#tarefaId").value = tarefa.id;
  document.querySelector("#name").value = tarefa.name;
  document.querySelector("#description").value = tarefa.description;
  document.querySelector("#priority").value = tarefa.priority;
  document.querySelector("#done").checked = tarefa.done;

  document.querySelector("#modalTarefaLabel").textContent = "Editar tarefa";
  document.querySelector("#btnModalTarefa").textContent = "Atualizar tarefa";
}

async function deletarTarefa(tarefaId) {
  if (tarefaId == null || tarefaId === undefined) {
    console.log("Erro ao buscar ID da tarefa.");
  }

  try {
    const response = await requisicaoAPI(`${TODO_URL}/${tarefaId}`, {
      method: "DELETE",
    });
  } catch (error) {
    console.error("Erro ao deletar tarefa:", error);
  }

  await buscarTarefas();
}

//DRAG AND DROP

document.addEventListener("dragstart", (e) => {
  const card = e.target.closest("[data-id]");

  if (!card || !e.dataTransfer) return;

  e.target.classList.add("dragging");

  e.dataTransfer.setData("text/plain", e.target.dataset.id);
});

document.addEventListener("dragend", (e) => {
  e.target.classList.remove("dragging");
});

const listas = [listaPendentes, listaConcluidas];

listas.forEach((lista) => {
  lista.addEventListener("dragover", (e) => {
    e.preventDefault();

    lista.classList.add("drag-over");
  });

  lista.addEventListener("dragleave", () => {
    lista.classList.remove("drag-over");
  });

  lista.addEventListener("drop", async (e) => {
    e.preventDefault();
    lista.classList.remove("drag-over");

    if (!e.dataTransfer) {
      console.error("Evento sem dataTransfer:", e);
      return;
    }

    const tarefaId = Number(e.dataTransfer.getData("text/plain"));

    if (!Number.isFinite(tarefaId)) {
      console.error("ID inválido:", tarefaId);
      return;
    }

    const tarefas = await buscarTarefas();
    if (!tarefas) return;

    const tarefa = tarefas.find((t) => t.id === tarefaId);
    if (!tarefa) return;

    const concluida = lista === listaConcluidas;

    if (tarefa.done === concluida) return;

    const tarefaAtualizada = {
      ...tarefa,
      done: concluida,
    };

    await atualizarTarefa(tarefaId, tarefaAtualizada);
    await buscarTarefas();
  });
});

async function BuscarPorTexto(texto) {
  try {
    const tarefas = await requisicaoAPI(TODO_URL);
    const termo = texto.trim().toLowerCase();

    return tarefas.filter((tarefa) =>
      tarefa.name?.toLowerCase().includes(termo),
    );
  } catch (error) {
    console.error("Erro ao buscar tarefas:", error);
    return [];
  }
}

const formularioPesquisa = document.getElementById("searchTarefa");
const inputPesquisa = document.getElementById("inputPesquisa");

inputPesquisa.addEventListener("input", async () => {
  const texto = inputPesquisa.value;

  if (!texto.trim()) {
    const tarefas = await requisicaoAPI(TODO_URL);
    exibirTarefas(tarefas);
    atualizarContadores(tarefas);
    return;
  }

  const tarefas = await BuscarPorTexto(texto);

  exibirTarefas(tarefas);
  atualizarContadores(tarefas);
});

formularioPesquisa.addEventListener("submit", (e) => {
  e.preventDefault();
});

function limparBarra() {
  document.querySelector("#inputPesquisa").value = "";
}

const formExcluir = document.getElementById("formExcluir");

formExcluir.addEventListener("submit", async (e) => {
  e.preventDefault();

  if (tarefaSelecionada === null) return;

  await deletarTarefa(tarefaSelecionada);

  const modalExcluir = document.getElementById("modalExcluir");
  const modal = bootstrap.Modal.getInstance(modalExcluir);
  modal.hide();
  tarefaSelecionada = null;
});

const btnPerfil = document.getElementById("btnPerfil");
const perfilPopup = document.getElementById("perfilPopup");
const fecharPerfil = document.getElementById("fecharPerfil");

const perfilNome = document.getElementById("perfilNome");
const perfilEmail = document.getElementById("perfilEmail");

btnPerfil.addEventListener("click", async () => {
  const aberto = perfilPopup.classList.contains("d-none");

  perfilPopup.classList.toggle("d-none");
  btnPerfil.setAttribute("aria-expanded", String(aberto));

  if (!aberto) return;

  try {
    const usuario = await requisicaoAPI(`${AUTH_URL}/me`);

    perfilNome.textContent = usuario.name;
    perfilEmail.textContent = usuario.email;
  } catch (error) {
    perfilNome.textContent = "Não foi possível carregar";
    perfilEmail.textContent = "Tente novamente";

    console.error("Erro ao carregar perfil:", error);
  }
});

fecharPerfil.addEventListener("click", () => {
  perfilPopup.classList.add("d-none");
  btnPerfil.setAttribute("aria-expanded", "false");
});

const formSair = document.getElementById("formSair");
const modalSairElement = document.getElementById("modalSair");

const modalSair = bootstrap.Modal.getOrCreateInstance(modalSairElement);

formSair.addEventListener("submit", (event) => {
  event.preventDefault();

  sessionStorage.removeItem("token");

  modalSair.hide();

  window.location.href = "/login";
});

const formEdit = document.getElementById("formEdit");
const modalEditElement = document.getElementById("modalEdit");
const modalEdit = bootstrap.Modal.getOrCreateInstance(modalEditElement);

const campoNovoNome = document.getElementById("novoNome");
const mensagemEditNome = document.getElementById("editNomeMensagem");
const btnSalvarNome = document.getElementById("btnSalvarNome");

formEdit.addEventListener("submit", async (event) => {
  event.preventDefault();

  console.log("1. Formulário enviado");

  const novoNome = campoNovoNome.value.trim();
  console.log("2. Novo nome:", novoNome);

  try {
    console.log("3. Enviando requisição para:", `${AUTH_URL}/me`);

    const dados = await requisicaoAPI(`${AUTH_URL}/me`, {
      method: "PUT",
      body: JSON.stringify({
        name: novoNome,
      }),
    });

    console.log("4. Resposta da API:", dados);

    // Atualiza o nome na interface
    const elementoNome = document.getElementById("nomeUsuario");

    if (elementoNome) {
      elementoNome.textContent = dados.name;
    }

    perfilNome.textContent = dados.name;

    formEdit.reset();
    modalEdit.hide();
  } catch (error) {
    console.error("Erro na atualização:", error);

    mensagemEditNome.textContent = "Erro ao atualizar o nome.";
    mensagemEditNome.className = "text-danger mt-2";
  } finally {
    btnSalvarNome.disabled = false;
  }
});

buscarTarefas();
