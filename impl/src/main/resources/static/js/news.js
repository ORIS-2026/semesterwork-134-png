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

function formatDate(isoString) {
    if (!isoString) return '';
    return new Date(isoString).toLocaleString('ru-RU', {
        day: '2-digit', month: '2-digit', year: 'numeric',
        hour: '2-digit', minute: '2-digit'
    });
}

const feed = document.getElementById('newsFeed');
let currentPage = 0;
let isLoading = false;
let hasMore = true;
const PAGE_SIZE = 7;

function renderPost(post) {
    const card = document.createElement('div');
    card.className = 'news-card';
    card.innerHTML = `
        <div class="news-card__header">
            <span class="news-card__author">${escapeHtml(post.name)}</span>
            <span class="news-card__date">${formatDate(post.publishedAt)}</span>
        </div>
        <img src="${escapeHtml(post.imageUrl)}" alt="Сгенерированное изображение" class="news-card__image">
        <div class="news-card__prompt">${escapeHtml(post.prompt)}</div>
        <div class="news-card__footer">
            <button class="like-btn ${post.likedByMe ? 'like-btn--active' : ''}" data-news-id="${escapeHtml(post.newsId)}">
                ♥ <span class="like-count">${post.likedAccounts}</span>
            </button>
        </div>
    `;

    card.querySelector('.like-btn').addEventListener('click', async (e) => {
        const btn = e.currentTarget;
        const newsId = btn.dataset.newsId;

        const resp = await fetch(`/api/news/${newsId}/like`, {
            method: 'POST',
            headers: { 'X-XSRF-TOKEN': getCookie('XSRF-TOKEN') }
        });
        if (!resp.ok) return;

        const liked = btn.classList.toggle('like-btn--active');
        const countEl = btn.querySelector('.like-count');
        countEl.textContent = liked
            ? parseInt(countEl.textContent) + 1
            : parseInt(countEl.textContent) - 1;
    });

    return card;
}

async function loadNews(page, append) {
    if (isLoading || !hasMore) return;
    isLoading = true;

    if (!append) {
        feed.innerHTML = '<div class="loading-indicator">Загрузка новостей...</div>';
    } else {
        const loader = document.createElement('div');
        loader.id = 'news-loader';
        loader.className = 'loading-indicator';
        loader.textContent = 'Загрузка...';
        feed.appendChild(loader);
    }

    try {
        const resp = await fetch(`/api/news?page=${page}`);
        if (!resp.ok) throw new Error(`HTTP ${resp.status}`);
        const posts = await resp.json();

        if (posts.length < PAGE_SIZE) hasMore = false;

        const fragment = document.createDocumentFragment();
        posts.forEach(p => fragment.appendChild(renderPost(p)));

        if (!append) {
            feed.innerHTML = '';
        } else {
            const loader = document.getElementById('news-loader');
            if (loader) loader.remove();
        }

        feed.appendChild(fragment);
    } catch (e) {
        console.error('Ошибка загрузки новостей:', e);
        const err = document.createElement('div');
        err.className = 'error-message';
        err.textContent = 'Не удалось загрузить новости.';
        feed.appendChild(err);
    } finally {
        isLoading = false;
    }
}

function handleScroll() {
    if (isLoading || !hasMore) return;
    const { scrollTop, scrollHeight, clientHeight } = feed;
    if (scrollTop + clientHeight >= scrollHeight * 0.9) {
        currentPage++;
        loadNews(currentPage, true);
    }
}

feed.addEventListener('scroll', handleScroll);

document.addEventListener('DOMContentLoaded', () => {
    loadNews(0, false);
});
