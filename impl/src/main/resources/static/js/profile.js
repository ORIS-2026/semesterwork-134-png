function getCookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
    return '';
}

function escapeHtml(str) {
    if (str == null) return '';
    return String(str)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;');
}

const accountDataDiv = document.getElementById('account_data');

function renderAvatar(data) {
    if (data.avatarUrl) {
        return `<img src="${escapeHtml(data.avatarUrl)}" alt="Аватар" class="avatar avatar--lg">`;
    }
    const initial = (data.name || '?').trim().charAt(0).toUpperCase();
    return `<div class="avatar avatar--lg avatar--placeholder">${escapeHtml(initial)}</div>`;
}

function renderAccount(data) {
    accountDataDiv.innerHTML = `
        <div class="card" style="max-width: 500px; margin: 20px auto;">
            <div class="card__header">Информация об аккаунте</div>
            <div class="card__body">
                <div class="avatar-section">
                    ${renderAvatar(data)}
                    <div class="avatar-section__actions">
                        <label class="btn">
                            Загрузить аватар
                            <input type="file" id="avatarInput" accept="image/png,image/jpeg" hidden>
                        </label>
                        <button type="button" class="btn" id="deleteAvatarBtn" ${data.avatarUrl ? '' : 'disabled'}>
                            Удалить аватар
                        </button>
                    </div>
                </div>
                <table style="width: 100%; border-collapse: collapse;">
                    <tr>
                        <td style="padding: 8px; font-weight: bold;">ID:</td>
                        <td style="padding: 8px;">${escapeHtml(data.id)}</td>
                    </tr>
                    <tr>
                        <td style="padding: 8px; font-weight: bold;">Имя:</td>
                        <td style="padding: 8px;">${escapeHtml(data.name)}</td>
                    </tr>
                    <tr>
                        <td style="padding: 8px; font-weight: bold;">Email:</td>
                        <td style="padding: 8px;">${escapeHtml(data.email)}</td>
                    </tr>
                    <tr>
                        <td style="padding: 8px; font-weight: bold;">Вход через Google:</td>
                        <td style="padding: 8px;">${data.oauthed ? 'Да' : 'Нет'}</td>
                    </tr>
                </table>
            </div>
        </div>
    `;

    document.getElementById('avatarInput').addEventListener('change', handleAvatarUpload);
    document.getElementById('deleteAvatarBtn').addEventListener('click', handleAvatarDelete);
}

function renderError(message) {
    accountDataDiv.innerHTML = `
        <div class="empty" style="color: #b91c1c; text-align: center; padding: 20px;">
            ⚠️ ${escapeHtml(message)}<br>
            <a href="/auth/login" style="color: #2563eb;">Перейти на страницу входа</a>
        </div>
    `;
}

async function loadAccountInfo() {
    try {
        const response = await fetch('/api/account/info', {
            method: 'GET',
            credentials: 'same-origin'
        });

        if (!response.ok) {
            if (response.status === 401) {
                throw new Error('Не авторизован. Возможно, сессия устарела.');
            }
            throw new Error('Ошибка загрузки данных: ' + response.status);
        }

        renderAccount(await response.json());
    } catch (error) {
        console.error('Ошибка:', error);
        renderError(error.message);
    }
}

async function handleAvatarUpload(event) {
    const file = event.target.files[0];
    if (!file) return;

    const formData = new FormData();
    formData.append('avatar_file', file);

    try {
        const response = await fetch('/api/account/avatar', {
            method: 'POST',
            body: formData,
            headers: { 'X-XSRF-TOKEN': getCookie('XSRF-TOKEN') }
        });
        if (!response.ok) throw new Error(`HTTP ${response.status}`);
        await loadAccountInfo();
    } catch (error) {
        console.error('Ошибка загрузки аватара:', error);
        alert('Не удалось загрузить аватар. Файл должен быть в формате PNG или JPEG.');
    } finally {
        event.target.value = '';
    }
}

async function handleAvatarDelete() {
    try {
        const response = await fetch('/api/account/avatar', {
            method: 'DELETE',
            headers: { 'X-XSRF-TOKEN': getCookie('XSRF-TOKEN') }
        });
        if (!response.ok) throw new Error(`HTTP ${response.status}`);
        await loadAccountInfo();
    } catch (error) {
        console.error('Ошибка удаления аватара:', error);
        alert('Не удалось удалить аватар.');
    }
}

document.addEventListener('DOMContentLoaded', loadAccountInfo);
