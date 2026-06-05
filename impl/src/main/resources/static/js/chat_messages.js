// Функция для безопасного экранирования HTML (предотвращает XSS)
function escapeHtml(str) {
    if (str == null) return '';
    return String(str)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;');
}

// Получаем chatId из атрибута th:id="layout" (он будет в id у div.layout)
const layoutDiv = document.querySelector('.layout');
const chatId = layoutDiv ? layoutDiv.id : null;
if (!chatId) {
    console.error('chatId не найден');
}

const messagesContainer = document.getElementById('chatMessages');
let isLoading = false;   // флаг для предотвращения параллельных запросов
let currentPage = 0;     // текущая загружаемая страница (для будущей пагинации)
let hasMore = true;       // есть ли ещё сообщения на сервере
let isLoadingMore = false;   // флаг для подгрузки (чтобы не дублировать вызовы)

// Отрисовка одного сообщения
function renderMessage(msg) {
    const messageDiv = document.createElement('div');
    // Добавляем базовые классы для стилизации
    messageDiv.classList.add('message');
    messageDiv.classList.add(msg.byBot ? 'message--bot' : 'message--user');

    // Содержимое сообщения
    let contentHtml = '';

    // Если есть картинка (S3Object)
    if (msg.s3Object && msg.s3Object.url) {
        contentHtml += `<img src="${escapeHtml(msg.s3Object.url)}" alt="Generated image" class="message__image">`;
        messageDiv.classList.add('message--image');
    }
    // Если есть текст (даже если есть картинка, текст может быть дополнительно)
    if (msg.textMsgContent) {
        // Поддерживаем простые переводы строк
        const formattedText = escapeHtml(msg.textMsgContent).replace(/\n/g, '<br>');
        contentHtml += `<div class="message__text">${formattedText}</div>`;
        messageDiv.classList.add('message--text');
    }

    // Если нет ни картинки, ни текста – показываем заглушку
    if (!contentHtml) {
        contentHtml = '<em>Пустое сообщение</em>';
    }

    messageDiv.innerHTML = contentHtml;
    return messageDiv;
}

// Загрузить сообщения с указанной страницы
// append = true – добавить в начало (для подгрузки старых), false – полностью заменить контент
async function loadMessages(page = 0, append = false) {
    if (!chatId) return;
    if (isLoading) return;
    isLoading = true;

    // Показываем индикатор загрузки (можно добавить спиннер)
    if (!append) {
        messagesContainer.innerHTML = '<div class="loading-indicator">Загрузка сообщений...</div>';
    } else {
        // Для подгрузки вверх добавим временный индикатор в начало
        const loader = document.createElement('div');
        loader.id = 'scroll-loader';
        loader.className = 'loading-indicator';
        loader.innerText = 'Загрузка более старых сообщений...';
        messagesContainer.prepend(loader);
    }

    try {
        const response = await fetch(`/api/chat/${chatId}?page=${page}`);
        if (!response.ok) {
            throw new Error(`Ошибка HTTP: ${response.status}`);
        }
        const messages = await response.json();

        // Определяем, есть ли ещё сообщения (если получено меньше 15, предполагаем, что это последняя страница)
        const limit = 15; // размер страницы по умолчанию (должен совпадать с серверным)
        if (messages.length < limit) {
            hasMore = false;
        } else {
            hasMore = true;
        }

        // Так как сервер отдаёт сообщения в порядке desc (новые сверху), для чата нужно перевернуть массив,
        // чтобы новые оказались снизу.
        const reversedMessages = [...messages].reverse();

        // Генерируем HTML-элементы для сообщений
        const fragment = document.createDocumentFragment();
        reversedMessages.forEach(msg => {
            fragment.appendChild(renderMessage(msg));
        });

        if (!append) {
            // Первая загрузка – очищаем контейнер и вставляем все сообщения
            messagesContainer.innerHTML = '';
            messagesContainer.appendChild(fragment);
            // Прокручиваем вниз, чтобы показать последнее (новое) сообщение
            messagesContainer.scrollTop = messagesContainer.scrollHeight;
        } else {
            // Подгрузка старых сообщений – добавляем полученные элементы в начало
            // При этом нужно удалить временный индикатор (он уже в начале, но после prepend сместится)
            const existingLoader = document.getElementById('scroll-loader');
            if (existingLoader) existingLoader.remove();

            // Вставляем в начало контейнера
            messagesContainer.prepend(fragment);

            // Сохраняем позицию до вставки
            const oldScrollHeight = messagesContainer.scrollHeight;
            const oldScrollTop = messagesContainer.scrollTop;

            // Вставляем старые сообщения в начало
            messagesContainer.prepend(fragment);

            // Корректируем прокрутку, чтобы сохранить видимое положение
            const newScrollHeight = messagesContainer.scrollHeight;
            messagesContainer.scrollTop = oldScrollTop + (newScrollHeight - oldScrollHeight);
        }
    } catch (error) {
        console.error('Ошибка загрузки сообщений:', error);
        if (!append) {
            messagesContainer.innerHTML = `<div class="error-message">Не удалось загрузить сообщения. ${escapeHtml(error.message)}</div>`;
        } else {
            // При ошибке подгрузки убираем индикатор
            const loader = document.getElementById('scroll-loader');
            if (loader) loader.remove();
        }
    } finally {
        isLoading = false;
    }
}

// Обработчик скролла: подгружает старые сообщения, когда до верха осталось 10%
function handleScroll() {
    if (isLoadingMore) {
        console.log("Loading already started")
        return;
    }
    if (!hasMore) {
        console.log("No more messages")
        return;
    }
    if (isLoading) return;

    console.log("Scroll change happened")

    const scrollTop = messagesContainer.scrollTop;
    const maxScrollTop = messagesContainer.scrollHeight - messagesContainer.clientHeight;
    if (maxScrollTop <= 0) return;

    const scrollPercentage = scrollTop / maxScrollTop;  // 0 = верх, 1 = низ
    // Условие: осталось меньше 10% до верха (т.е. прокрутка вверх на 90%)
    if (scrollPercentage <= 0.1) {
        console.log("Condition for loading messages")

        isLoadingMore = true;
        currentPage++;
        loadMessages(currentPage, true).finally(() => {
            isLoadingMore = false;
        });
    }
}

messagesContainer.addEventListener('scroll', () => {
    console.log("Reaction on scroll")
    handleScroll()

});

// Обработчик кнопки "Обновить чат" – перезагрузка страницы
function refreshChat() {
    window.location.reload();
}


// Вешаем обработчик на кнопку обновления
const updateBtn = document.getElementById('chatUpdate');
if (updateBtn) {
    updateBtn.addEventListener('click', refreshChat);
}

//отправка промптов
// Отправка нового промпта на сервер
const chatInput = document.getElementById('chatInput');
const chatSubmit = document.getElementById('chatSubmit');

async function sendPrompt() {
    const promptText = chatInput.value.trim();
    if (!promptText) {
        // Можно добавить всплывающее уведомление
        alert('Введите текст запроса');
        return;
    }

    // Блокируем кнопку и поле ввода на время отправки
    chatSubmit.disabled = true;
    chatInput.disabled = true;
    chatSubmit.textContent = 'Отправка...';

    try {
        const response = await fetch(`/api/chat/${chatId}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ prompt: promptText })
        });

        if (!response.ok) {
            throw new Error(`Ошибка HTTP: ${response.status}`);
        }

        // Очищаем поле ввода
        chatInput.value = '';

        // Показываем сообщение о том, что запрос отправлен
        const infoMsg = document.createElement('div');
        infoMsg.className = 'message message--info';
        infoMsg.innerHTML = '<div class="message__text">✅ Запрос отправлен. Генерация ответа... страница обновится через несколько секунд.</div>';
        messagesContainer.appendChild(infoMsg);
        messagesContainer.scrollTop = messagesContainer.scrollHeight;

        // Перезагружаем страницу через 3 секунды, чтобы показать ответ бота
        setTimeout(() => {
            window.location.reload();
        }, 3000);

    } catch (error) {
        console.error('Ошибка отправки:', error);
        alert('Не удалось отправить запрос: ' + error.message);
        // Разблокируем кнопку и поле
        chatSubmit.disabled = false;
        chatInput.disabled = false;
        chatSubmit.textContent = 'Отправить';
    }
}



chatSubmit.addEventListener('click', sendPrompt);

document.addEventListener('DOMContentLoaded', function (){
    loadMessages(0, false);
    currentPage++;
})