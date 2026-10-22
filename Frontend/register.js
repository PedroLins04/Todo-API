//const AUTH_URL = "http://localhost:8080/auth";
const AUTH_URL = "https://keeptheflow-api.onrender.com/auth";

const formRegister = document.getElementById("formRegister");
const campoNome = document.getElementById("name");
const campoEmail = document.getElementById("email");
const campoSenha = document.getElementById("password");
const mensagem = document.getElementById("registerMensagem");
const btnRegister = document.getElementById("btnRegister");
const togglePassword = document.getElementById("togglePassword");

function exibirMensagemErro(texto, sucesso = false) {
  mensagem.textContent = texto;
  mensagem.classList.add("visible");

  mensagem.classList.toggle("register-success", sucesso);
}

function exibirMensagem(texto, sucesso = true) {
  mensagem.textContent = texto;
  mensagem.classList.remove("login-message");
  mensagem.classList.add("visible");
  mensagem.classList.add("login-message-success");
  mensagem.classList.toggle("register-success", sucesso);
}

// Mostrar ou ocultar a senha
togglePassword.addEventListener("click", () => {
  const mostrarSenha = campoSenha.type === "password";

  campoSenha.type = mostrarSenha ? "text" : "password";

  togglePassword.innerHTML = mostrarSenha
    ? '<i class="bi bi-eye"></i>'
    : '<i class="bi bi-eye-slash"></i>';

  togglePassword.setAttribute(
    "aria-label",
    mostrarSenha ? "Ocultar senha" : "Mostrar senha",
  );
});

// Cadastrar usuário
formRegister.addEventListener("submit", async (event) => {
  event.preventDefault();

  mensagem.textContent = "";
  mensagem.classList.remove("visible", "register-success");

  if (campoSenha.value.length < 4) {
    exibirMensagemErro("A senha deve ter pelo menos 4 caracteres.");
    campoSenha.focus();
    return;
  }

  btnRegister.disabled = true;
  btnRegister.innerHTML = "<span>Criando conta...</span>";

  try {
    const resposta = await fetch(`${AUTH_URL}/register`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        name: campoNome.value.trim(),
        email: campoEmail.value.trim(),
        password: campoSenha.value,
      }),
    });

    const dados = await resposta.json().catch(() => ({}));

    if (!resposta.ok) {
      throw new Error(
        dados.message || dados.detail || "Não foi possível criar a conta.",
      );
    }

    exibirMensagem(
      `Conta criada com sucesso! Bem-vindo, ${dados.name || campoNome.value.trim()}. Redirecionando para a página de login...`,
      true,
    );

    formRegister.reset();

    setTimeout(() => {
      window.location.href = "/login";
    }, 1800);
  } catch (erro) {
    exibirMensagemErro(erro.message || "Erro de conexão com o servidor.");
  } finally {
    btnRegister.disabled = false;
    btnRegister.innerHTML =
      '<span>Criar conta</span><i class="bi bi-arrow-right"></i>';
  }
});
