const API_URL = "/api/tarefas";

const state = {
    tarefas: [],
    filtro: "TODAS",
    busca: "",
    carregando: false
};

const elements = {
    form: document.querySelector("#task-form"),
    taskId: document.querySelector("#task-id"),
    nome: document.querySelector("#nome"),
    descricao: document.querySelector("#descricao"),
    status: document.querySelector("#status"),
    observacoes: document.querySelector("#observacoes"),
    submit: document.querySelector("#submit-button"),
    cancelEdit: document.querySelector("#cancel-edit"),
    formEyebrow: document.querySelector("#form-eyebrow"),
    formTitle: document.querySelector("#form-title"),
    list: document.querySelector("#task-list"),
    template: document.querySelector("#task-template"),
    empty: document.querySelector("#empty-state"),
    emptyTitle: document.querySelector("#empty-title"),
    emptyDescription: document.querySelector("#empty-description"),
    feedback: document.querySelector("#feedback"),
    search: document.querySelector("#search"),
    filters: document.querySelectorAll(".filter"),
    refresh: document.querySelector("#refresh-button"),
    focusForm: document.querySelector("#focus-form"),
    toast: document.querySelector("#toast"),
    counts: {
        total: document.querySelector("#count-total"),
        pending: document.querySelector("#count-pending"),
        progress: document.querySelector("#count-progress"),
        done: document.querySelector("#count-done")
    }
};

const statusLabels = {
    PENDENTE: "Pendente",
    EM_ANDAMENTO: "Em andamento",
    CONCLUIDA: "Concluída"
};

document.querySelector("#today").textContent = new Intl.DateTimeFormat("pt-BR", {
    weekday: "long",
    day: "2-digit",
    month: "long"
}).format(new Date());

async function api(path = "", options = {}) {
    const response = await fetch(`${API_URL}${path}`, {
        headers: options.body ? { "Content-Type": "application/json" } : {},
        ...options
    });

    if (!response.ok) {
        let error;
        try {
            error = await response.json();
        } catch {
            error = { mensagem: "Não foi possível completar a operação." };
        }
        throw error;
    }

    return response.status === 204 ? null : response.json();
}

async function carregarTarefas() {
    setLoading(true);
    ocultarFeedback();
    try {
        state.tarefas = await api();
        renderizar();
    } catch (error) {
        mostrarFeedback(error.mensagem || "Não foi possível carregar as tarefas. Verifique se a aplicação está conectada ao banco.");
    } finally {
        setLoading(false);
    }
}

function setLoading(loading) {
    state.carregando = loading;
    elements.refresh.disabled = loading;
    if (loading && state.tarefas.length === 0) {
        elements.empty.classList.add("hidden");
        elements.list.replaceChildren(...Array.from({ length: 3 }, () => {
            const skeleton = document.createElement("div");
            skeleton.className = "loading-card";
            return skeleton;
        }));
    }
}

function renderizar() {
    atualizarContadores();
    const termo = normalizar(state.busca);
    const tarefasFiltradas = state.tarefas.filter((tarefa) => {
        const correspondeStatus = state.filtro === "TODAS" || tarefa.status === state.filtro;
        const conteudo = normalizar(`${tarefa.nome} ${tarefa.descricao} ${tarefa.observacoes || ""}`);
        return correspondeStatus && conteudo.includes(termo);
    });

    elements.list.replaceChildren();
    elements.empty.classList.toggle("hidden", tarefasFiltradas.length > 0);

    if (tarefasFiltradas.length === 0) {
        const temFiltro = state.filtro !== "TODAS" || state.busca.trim();
        elements.emptyTitle.textContent = temFiltro ? "Nenhuma tarefa encontrada" : "Tudo tranquilo por aqui";
        elements.emptyDescription.textContent = temFiltro
            ? "Tente mudar os filtros ou o texto da busca."
            : "Crie sua primeira tarefa para começar a organizar o dia.";
        return;
    }

    tarefasFiltradas.forEach((tarefa) => elements.list.appendChild(criarCard(tarefa)));
}

function criarCard(tarefa) {
    const fragment = elements.template.content.cloneNode(true);
    const card = fragment.querySelector(".task-card");
    const completeButton = fragment.querySelector(".complete-button");
    const name = fragment.querySelector(".task-name");
    const badge = fragment.querySelector(".status-badge");
    const description = fragment.querySelector(".task-description");
    const notes = fragment.querySelector(".task-notes");
    const date = fragment.querySelector(".task-date");

    card.dataset.id = tarefa.id;
    card.classList.toggle("is-done", tarefa.status === "CONCLUIDA");
    name.textContent = tarefa.nome;
    badge.textContent = statusLabels[tarefa.status];
    badge.classList.add(`status-${tarefa.status}`);
    description.textContent = tarefa.descricao;
    date.textContent = `Criada em ${formatarData(tarefa.dataCriacao)}`;

    if (tarefa.observacoes) {
        notes.textContent = tarefa.observacoes;
        notes.classList.remove("hidden");
    }

    completeButton.title = tarefa.status === "CONCLUIDA" ? "Marcar como pendente" : "Marcar como concluída";
    completeButton.setAttribute("aria-label", completeButton.title);
    completeButton.addEventListener("click", () => alternarConclusao(tarefa));
    fragment.querySelector(".edit-button").addEventListener("click", () => iniciarEdicao(tarefa));
    fragment.querySelector(".delete-button").addEventListener("click", () => excluirTarefa(tarefa));
    return fragment;
}

function atualizarContadores() {
    elements.counts.total.textContent = state.tarefas.length;
    elements.counts.pending.textContent = state.tarefas.filter((t) => t.status === "PENDENTE").length;
    elements.counts.progress.textContent = state.tarefas.filter((t) => t.status === "EM_ANDAMENTO").length;
    elements.counts.done.textContent = state.tarefas.filter((t) => t.status === "CONCLUIDA").length;
}

async function salvarTarefa(event) {
    event.preventDefault();
    limparErros();

    const id = elements.taskId.value;
    const tarefa = {
        nome: elements.nome.value.trim(),
        descricao: elements.descricao.value.trim(),
        status: elements.status.value,
        observacoes: elements.observacoes.value.trim() || null
    };

    if (!validar(tarefa)) return;

    elements.submit.disabled = true;
    elements.submit.textContent = id ? "Salvando..." : "Criando...";
    try {
        await api(id ? `/${id}` : "", {
            method: id ? "PUT" : "POST",
            body: JSON.stringify(tarefa)
        });
        mostrarToast(id ? "Tarefa atualizada." : "Tarefa criada.");
        resetarFormulario();
        await carregarTarefas();
    } catch (error) {
        if (error.campos) aplicarErros(error.campos);
        mostrarToast(error.mensagem || "Não foi possível salvar a tarefa.", true);
    } finally {
        elements.submit.disabled = false;
        elements.submit.textContent = elements.taskId.value ? "Salvar alterações" : "Salvar tarefa";
    }
}

function validar(tarefa) {
    const erros = {};
    if (!tarefa.nome) erros.nome = "Informe o nome da tarefa.";
    if (!tarefa.descricao) erros.descricao = "Informe uma descrição.";
    aplicarErros(erros);
    return Object.keys(erros).length === 0;
}

async function alternarConclusao(tarefa) {
    try {
        await api(`/${tarefa.id}`, {
            method: "PUT",
            body: JSON.stringify({
                nome: tarefa.nome,
                descricao: tarefa.descricao,
                status: tarefa.status === "CONCLUIDA" ? "PENDENTE" : "CONCLUIDA",
                observacoes: tarefa.observacoes
            })
        });
        mostrarToast(tarefa.status === "CONCLUIDA" ? "Tarefa reaberta." : "Tarefa concluída. Muito bem!");
        await carregarTarefas();
    } catch (error) {
        mostrarToast(error.mensagem || "Não foi possível atualizar a tarefa.", true);
    }
}

function iniciarEdicao(tarefa) {
    elements.taskId.value = tarefa.id;
    elements.nome.value = tarefa.nome;
    elements.descricao.value = tarefa.descricao;
    elements.status.value = tarefa.status;
    elements.observacoes.value = tarefa.observacoes || "";
    elements.formEyebrow.textContent = "Editar";
    elements.formTitle.textContent = "Editar tarefa";
    elements.submit.textContent = "Salvar alterações";
    elements.cancelEdit.classList.remove("hidden");
    limparErros();
    elements.nome.focus({ preventScroll: true });
    document.querySelector(".form-panel").scrollIntoView({ behavior: "smooth", block: "start" });
}

async function excluirTarefa(tarefa) {
    if (!window.confirm(`Excluir a tarefa “${tarefa.nome}”?`)) return;
    try {
        await api(`/${tarefa.id}`, { method: "DELETE" });
        if (String(tarefa.id) === elements.taskId.value) resetarFormulario();
        mostrarToast("Tarefa excluída.");
        await carregarTarefas();
    } catch (error) {
        mostrarToast(error.mensagem || "Não foi possível excluir a tarefa.", true);
    }
}

function resetarFormulario() {
    elements.form.reset();
    elements.taskId.value = "";
    elements.status.value = "PENDENTE";
    elements.formEyebrow.textContent = "Adicionar";
    elements.formTitle.textContent = "Nova tarefa";
    elements.submit.textContent = "Salvar tarefa";
    elements.cancelEdit.classList.add("hidden");
    limparErros();
}

function aplicarErros(erros) {
    Object.entries(erros).forEach(([campo, mensagem]) => {
        const input = document.querySelector(`#${campo}`);
        const error = document.querySelector(`#error-${campo}`);
        if (input && error) {
            input.classList.add("invalid");
            input.setAttribute("aria-invalid", "true");
            error.textContent = mensagem;
        }
    });
}

function limparErros() {
    document.querySelectorAll(".field-error").forEach((item) => item.textContent = "");
    document.querySelectorAll(".invalid").forEach((item) => {
        item.classList.remove("invalid");
        item.removeAttribute("aria-invalid");
    });
}

function formatarData(value) {
    if (!value) return "data indisponível";
    const valueWithTimezone = /Z$|[+-]\d{2}:?\d{2}$/.test(value) ? value : `${value}Z`;
    return new Intl.DateTimeFormat("pt-BR", {
        day: "2-digit",
        month: "short",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit"
    }).format(new Date(valueWithTimezone));
}

function normalizar(value) {
    return value.normalize("NFD").replace(/[\u0300-\u036f]/g, "").toLowerCase();
}

let toastTimer;
function mostrarToast(message, error = false) {
    window.clearTimeout(toastTimer);
    elements.toast.textContent = message;
    elements.toast.classList.toggle("error", error);
    elements.toast.classList.add("show");
    toastTimer = window.setTimeout(() => elements.toast.classList.remove("show"), 2800);
}

function mostrarFeedback(message) {
    elements.feedback.textContent = message;
    elements.feedback.classList.remove("hidden");
    elements.list.replaceChildren();
}

function ocultarFeedback() {
    elements.feedback.classList.add("hidden");
}

elements.form.addEventListener("submit", salvarTarefa);
elements.cancelEdit.addEventListener("click", resetarFormulario);
elements.refresh.addEventListener("click", carregarTarefas);
elements.focusForm.addEventListener("click", () => {
    resetarFormulario();
    elements.nome.focus({ preventScroll: true });
    document.querySelector(".form-panel").scrollIntoView({ behavior: "smooth", block: "start" });
});
elements.search.addEventListener("input", (event) => {
    state.busca = event.target.value;
    renderizar();
});
elements.filters.forEach((filter) => filter.addEventListener("click", () => {
    state.filtro = filter.dataset.filter;
    elements.filters.forEach((item) => item.classList.toggle("active", item === filter));
    renderizar();
}));

carregarTarefas();
