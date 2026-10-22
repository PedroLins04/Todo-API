//const AUTH_URL = "http://localhost:8080/auth";
const AUTH_URL = "https://keeptheflow-api.onrender.com/auth";

const formLogin = document.getElementById("formLogin");
const campoEmail = document.getElementById("email");
const campoSenha = document.getElementById("password");
const mensagem = document.getElementById("loginMensagem");
const btnLogin = document.getElementById("btnLogin");
const togglePassword = document.getElementById("togglePassword");

// Exibir mensagens de erro ou sucesso
function exibirMensagemErro(texto, sucesso = false) {
  mensagem.textContent = texto;
  mensagem.classList.add("visible");
  mensagem.classList.toggle("login-success", sucesso);
}

function exibirMensagem(texto, sucesso = true) {
  mensagem.textContent = texto;
  mensagem.classList.remove("login-message");
  mensagem.classList.add("visible");
  mensagem.classList.add("login-message-success");
  mensagem.classList.toggle("login-success", sucesso);
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

// Autenticar usuário
formLogin.addEventListener("submit", async (event) => {
  event.preventDefault();

  mensagem.textContent = "";
  mensagem.classList.remove("visible", "login-success");

  // Validar campos obrigatórios
  if (!campoEmail.value.trim() || !campoSenha.value) {
    exibirMensagemErro("Preencha todos os campos.");
    return;
  }

  btnLogin.disabled = true;
  btnLogin.innerHTML = "<span>Entrando...</span>";

  try {
    const resposta = await fetch(`${AUTH_URL}/login`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        email: campoEmail.value.trim(),
        password: campoSenha.value,
      }),
    });

    const dados = await resposta.json().catch(() => ({}));

    if (!resposta.ok) {
      throw new Error(
        dados.message ||
          dados.detail ||
          "Não foi possível entrar. Verifique seus dados.",
      );
    }

    if (!dados.token) {
      throw new Error(
        "Login realizado, mas o servidor não retornou o token JWT.",
      );
    }

    sessionStorage.setItem("token", dados.token);

    // Armazenar o token JWT, se retornado pela API
    if (dados.token) {
      sessionStorage.setItem("token", dados.token);
    }

    exibirMensagem(`Redirecionando para o aplicativo...`, true);

    formLogin.reset();

    setTimeout(() => {
      window.location.href = "Tarefas.html";
    }, 1500);
  } catch (erro) {
    exibirMensagemErro(erro.message || "Erro de conexão com o servidor.");
  } finally {
    btnLogin.disabled = false;
    btnLogin.innerHTML = '<span>Entrar</span><i class="bi bi-arrow-right"></i>';
  }
});
