/**
 * AI校园学习助手系统 - 全局JavaScript
 * 包含认证管理、聊天、文章、计划、代码解释等功能的客户端逻辑
 */

// ============================================================
// 全局状态
// ============================================================
let currentArticleId = null;

// ============================================================
// 认证管理
// ============================================================
function getToken() {
    return localStorage.getItem('token') || '';
}

function isLoggedIn() {
    return !!getToken();
}

function getUserInfo() {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
}

function getAuthHeaders() {
    return {headers: {Authorization: 'Bearer ' + getToken()}};
}

function showAlert(message, type) {
    const alert = document.getElementById('adminAlert');
    if (!alert) return;
    alert.className = 'alert alert-' + type;
    alert.textContent = message;
    alert.classList.remove('d-none');
    setTimeout(() => alert.classList.add('d-none'), 3000);
}

// 初始化页面：检查登录状态
function initAuth() {
    if (isLoggedIn()) {
        const user = getUserInfo();
        const navArea = document.getElementById('userNavArea');
        if (navArea) {
            navArea.innerHTML = `
                <div class="dropdown">
                    <button class="btn btn-outline-light btn-sm dropdown-toggle" data-bs-toggle="dropdown">
                        <i class="bi bi-person-circle"></i> ${user.nickname || user.username}
                    </button>
                    <ul class="dropdown-menu dropdown-menu-end">
                        <li><a class="dropdown-item" href="/user/profile"><i class="bi bi-person"></i> 个人中心</a></li>
                        <li><hr class="dropdown-divider"></li>
                        <li><a class="dropdown-item" href="#" onclick="doLogout()"><i class="bi bi-box-arrow-right"></i> 退出登录</a></li>
                    </ul>
                </div>
            `;
        }
        // 页面特定初始化
        initPage();
    }
}

function doLogout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    window.location.href = '/';
}

// ============================================================
// 页面特定初始化
// ============================================================
function initPage() {
    const path = window.location.pathname;
    if (path === '/user/profile') initProfilePage();
    else if (path === '/admin/dashboard' || path === '/admin') initAdminDashboard();
    else if (path === '/admin/users') initAdminUsers();
    else if (path === '/admin/chats') initAdminChats();
    else if (path === '/admin/articles') initAdminArticles();
    else if (path === '/admin/plans') initAdminPlans();
    else if (path === '/chat') initChatPage();
    else if (path === '/article') initArticlePage();
    else if (path === '/plan') initPlanPage();
}

// ============================================================
// AI聊天功能
// ============================================================
function initChatPage() {
    if (!isLoggedIn()) {
        window.location.href = '/auth/login';
        return;
    }
    loadHistory();
}

async function sendMessage() {
    const input = document.getElementById('messageInput');
    const message = input.value.trim();
    if (!message) return;
    input.value = '';
    input.disabled = true;
    const status = document.getElementById('chatStatus');
    status.textContent = '思考中...';
    status.className = 'badge bg-warning';

    const container = document.getElementById('chatMessages');
    container.innerHTML += '<div class="message-bubble message-user"><strong>你：</strong><br>' + escapeHtml(message) + '</div>';
    container.innerHTML += '<div class="message-bubble message-ai"><span class="loading-spinner"></span> AI正在思考...</div>';
    container.scrollTop = container.scrollHeight;

    try {
        const res = await axios.post('/api/chat/send', {message: message}, getAuthHeaders());
        const data = res.data.data;
        const aiBubbles = container.querySelectorAll('.message-ai');
        const lastBubble = aiBubbles[aiBubbles.length - 1];
        lastBubble.innerHTML = '<strong>AI助手：</strong><br>' + marked.parse(data.aiResponse || '');
        (typeof hljs !== "undefined") && hljs.highlightAll();
    } catch (err) {
        status.textContent = '错误';
        status.className = 'badge bg-danger';
        alert('发送失败: ' + (err.response?.data?.message || err.message));
    }
    status.textContent = '就绪';
    status.className = 'badge bg-success';
    input.disabled = false;
    input.focus();
}

async function loadHistory() {
    try {
        const res = await axios.get('/api/chat/history?page=1&pageSize=50', getAuthHeaders());
        const list = res.data.data || [];
        const container = document.getElementById('chatMessages');
        if (!container) return;
        if (list.length === 0) {
            container.innerHTML = '<div class="text-center text-muted py-5"><i class="bi bi-robot" style="font-size: 3rem;"></i><p class="mt-2">你好！我是AI学习助手，有什么问题可以问我。</p></div>';
            return;
        }
        container.innerHTML = '';
        for (const item of list.reverse ? list.reverse() : list) {
            container.innerHTML += '<div class="message-bubble message-user"><strong>你：</strong><br>' + escapeHtml(item.userMessage) + '</div>';
            container.innerHTML += '<div class="message-bubble message-ai"><strong>AI助手：</strong><br>' + marked.parse(item.aiResponse || '') + '</div>';
        }
        container.scrollTop = container.scrollHeight;
        (typeof hljs !== "undefined") && hljs.highlightAll();
    } catch (err) {
        console.error('加载历史失败:', err);
    }
}

async function deleteAllChats() {
    if (!confirm('确定要清空所有聊天记录吗？此操作不可恢复。')) return;
    try {
        await axios.delete('/api/chat/all', getAuthHeaders());
        const container = document.getElementById('chatMessages');
        if (container) {
            container.innerHTML = '<div class="text-center text-muted py-5"><i class="bi bi-robot" style="font-size: 3rem;"></i><p class="mt-2">聊天记录已清空，有什么问题可以问我。</p></div>';
        }
    } catch (err) {
        alert('删除失败: ' + (err.response?.data?.message || err.message));
    }
}

// ============================================================
// AI作文生成
// ============================================================
function initArticlePage() {
    loadArticleHistory();
}

async function generateArticle() {
    const title = document.getElementById('articleTitle').value.trim();
    const articleType = document.getElementById('articleType').value;
    const requirements = document.getElementById('articleReqs').value.trim();
    if (!title) { alert('请输入文章标题'); return; }

    const resultDiv = document.getElementById('articleResult');
    resultDiv.innerHTML = '<div class="text-center py-5"><div class="spinner-border text-primary"></div><p class="mt-2">AI正在生成文章...</p></div>';

    try {
        const res = await axios.post('/api/article/generate', {title, requirements, articleType}, getAuthHeaders());
        const data = res.data.data;
        currentArticleId = data.id;
        document.getElementById('resultTitle').textContent = data.title;
        resultDiv.innerHTML = marked.parse(data.content || '');
        document.getElementById('exportBtns').classList.remove('d-none');
        (typeof hljs !== "undefined") && hljs.highlightAll();
        loadArticleHistory();
    } catch (err) {
        resultDiv.innerHTML = '<div class="alert alert-danger">生成失败: ' + (err.response?.data?.message || err.message) + '</div>';
    }
}

async function loadArticleHistory() {
    try {
        const res = await axios.get('/api/article/list?page=1&pageSize=20', getAuthHeaders());
        const list = res.data.data || [];
        const container = document.getElementById('articleHistory');
        if (!container) return;
        if (list.length === 0) {
            container.innerHTML = '<small class="text-muted">暂无历史文章</small>';
            return;
        }
        container.innerHTML = list.map(a =>
            '<a href="#" class="list-group-item list-group-item-action" onclick="viewArticle(' + a.id + ')">' +
            '<small>' + escapeHtml(a.title) + '</small><br>' +
            '<small class="text-muted">' + (a.articleType || '') + ' | ' + (a.createdAt || '').substring(0, 10) + '</small></a>'
        ).join('');
    } catch (err) {
        console.error('加载文章历史失败:', err);
    }
}

async function viewArticle(id) {
    try {
        const res = await axios.get('/api/article/' + id, getAuthHeaders());
        const article = res.data.data;
        currentArticleId = article.id;
        document.getElementById('resultTitle').textContent = article.title;
        document.getElementById('articleResult').innerHTML = marked.parse(article.content || '');
        document.getElementById('exportBtns').classList.remove('d-none');
        (typeof hljs !== "undefined") && hljs.highlightAll();
    } catch (err) {
        alert('加载文章失败');
    }
}

function exportMD() {
    if (!currentArticleId) return;
    window.open('/api/article/' + currentArticleId + '/export/md', '_blank');
}

function exportDocx() {
    if (!currentArticleId) return;
    window.open('/api/article/' + currentArticleId + '/export/docx', '_blank');
}

// ============================================================
// AI学习计划
// ============================================================
function initPlanPage() {
    loadPlanHistory();
}

async function generatePlan() {
    const major = document.getElementById('planMajor').value.trim();
    const course = document.getElementById('planCourse').value.trim();
    const goal = document.getElementById('planGoal').value.trim();
    if (!major || !course) { alert('请填写专业和课程'); return; }

    const resultDiv = document.getElementById('planResult');
    resultDiv.innerHTML = '<div class="text-center py-5"><div class="spinner-border text-primary"></div><p class="mt-2">AI正在生成学习计划...</p></div>';

    try {
        const res = await axios.post('/api/plan/generate', {major, course, goal}, getAuthHeaders());
        const data = res.data.data;
        document.getElementById('planResultTitle').textContent = data.course + ' - 学习计划';
        resultDiv.innerHTML = marked.parse(data.planContent || '');
        (typeof hljs !== "undefined") && hljs.highlightAll();
        loadPlanHistory();
    } catch (err) {
        resultDiv.innerHTML = '<div class="alert alert-danger">生成失败: ' + (err.response?.data?.message || err.message) + '</div>';
    }
}

async function loadPlanHistory() {
    try {
        const res = await axios.get('/api/plan/list?page=1&pageSize=20', getAuthHeaders());
        const list = res.data.data || [];
        const container = document.getElementById('planHistory');
        if (!container) return;
        if (list.length === 0) {
            container.innerHTML = '<small class="text-muted">暂无历史计划</small>';
            return;
        }
        container.innerHTML = list.map(p =>
            '<a href="#" class="list-group-item list-group-item-action" onclick="viewPlan(' + p.id + ')">' +
            '<small>' + escapeHtml(p.course) + '</small><br>' +
            '<small class="text-muted">' + escapeHtml(p.major) + ' | ' + (p.createdAt || '').substring(0, 10) + '</small></a>'
        ).join('');
    } catch (err) {
        console.error('加载计划历史失败:', err);
    }
}

async function viewPlan(id) {
    try {
        const res = await axios.get('/api/plan/' + id, getAuthHeaders());
        const plan = res.data.data;
        document.getElementById('planResultTitle').textContent = plan.course + ' - 学习计划';
        document.getElementById('planResult').innerHTML = marked.parse(plan.planContent || '');
        (typeof hljs !== "undefined") && hljs.highlightAll();
    } catch (err) {
        alert('加载学习计划失败');
    }
}

// ============================================================
// AI代码解释
// ============================================================
async function explainCode() {
    await handleCodeAction('/api/code/explain', '代码解释结果');
}
async function optimizeCode() {
    await handleCodeAction('/api/code/optimize', '代码优化结果');
}
async function addComments() {
    await handleCodeAction('/api/code/comment', '注释生成结果');
}

async function handleCodeAction(url, title) {
    const code = document.getElementById('codeInput').value.trim();
    if (!code) { alert('请输入代码'); return; }
    document.getElementById('codeResultTitle').textContent = title;
    const resultDiv = document.getElementById('codeResult');
    resultDiv.innerHTML = '<div class="text-center py-5"><div class="spinner-border text-primary"></div><p class="mt-2">AI正在分析代码...</p></div>';

    try {
        const res = await axios.post(url, {code: code}, getAuthHeaders());
        resultDiv.innerHTML = marked.parse(res.data.data || '');
        (typeof hljs !== "undefined") && hljs.highlightAll();
    } catch (err) {
        resultDiv.innerHTML = '<div class="alert alert-danger">分析失败: ' + (err.response?.data?.message || err.message) + '</div>';
    }
}

// ============================================================
// 用户中心
// ============================================================
function initProfilePage() {
    if (!isLoggedIn()) { window.location.href = '/auth/login'; return; }
    loadProfile();
    loadLoginLogs();
}

async function loadProfile() {
    try {
        const res = await axios.get('/api/user/profile', getAuthHeaders());
        const user = res.data.data;
        document.getElementById('profileNickname').textContent = user.nickname || user.username;
        document.getElementById('profileEmail').textContent = user.email;
        if (user.avatar && user.avatar !== 'default_avatar.png') {
            document.getElementById('avatarImg').src = '/uploads/avatars/' + user.avatar;
        }
        document.getElementById('newNickname').value = user.nickname || '';
    } catch (err) {
        console.error('加载用户信息失败:', err);
    }
}

async function updateNickname() {
    const nickname = document.getElementById('newNickname').value.trim();
    if (!nickname) { alert('请输入昵称'); return; }
    try {
        await axios.post('/api/auth/update-profile', {nickname: nickname}, getAuthHeaders());
        alert('昵称修改成功');
        loadProfile();
    } catch (err) {
        alert('修改失败: ' + (err.response?.data?.message || err.message));
    }
}

async function changePassword() {
    const oldPwd = document.getElementById('oldPassword').value;
    const newPwd = document.getElementById('newPassword').value;
    if (!oldPwd || !newPwd) { alert('请填写原密码和新密码'); return; }
    try {
        await axios.post('/api/auth/change-password', {oldPassword: oldPwd, newPassword: newPwd}, getAuthHeaders());
        alert('密码修改成功，请重新登录');
        doLogout();
    } catch (err) {
        alert('修改失败: ' + (err.response?.data?.message || err.message));
    }
}

async function uploadAvatar() {
    const fileInput = document.getElementById('avatarInput');
    const file = fileInput.files[0];
    if (!file) return;
    const formData = new FormData();
    formData.append('file', file);
    try {
        const res = await axios.post('/api/user/upload-avatar', formData, {
            headers: {...getAuthHeaders().headers, 'Content-Type': 'multipart/form-data'}
        });
        document.getElementById('avatarImg').src = res.data.data;
        alert('头像上传成功');
    } catch (err) {
        alert('上传失败: ' + (err.response?.data?.message || err.message));
    }
}

async function loadLoginLogs() {
    try {
        const res = await axios.get('/api/user/login-logs', getAuthHeaders());
        const logs = res.data.data || [];
        const tbody = document.getElementById('loginLogsTable');
        if (!tbody) return;
        if (logs.length === 0) {
            tbody.innerHTML = '<tr><td colspan="3" class="text-center text-muted">暂无登录记录</td></tr>';
            return;
        }
        tbody.innerHTML = logs.map(l =>
            '<tr><td>' + escapeHtml(l.ipAddress) + '</td><td><small>' + escapeHtml(l.userAgent || '').substring(0, 80) + '</small></td><td>' + (l.loginTime || '') + '</td></tr>'
        ).join('');
    } catch (err) {
        console.error('加载登录日志失败:', err);
    }
}

// ============================================================
// 管理员后台
// ============================================================
function adminLogout() {
    localStorage.removeItem('adminToken');
    localStorage.removeItem('adminInfo');
    window.location.href = '/admin/login';
}

function getAdminHeaders() {
    return {headers: {Authorization: 'Bearer ' + (localStorage.getItem('adminToken') || '')}};
}

async function initAdminDashboard() {
    try {
        const res = await axios.get('/api/admin/dashboard', getAdminHeaders());
        const d = res.data.data;
        document.getElementById('totalUsers').textContent = d.totalUsers || 0;
        document.getElementById('totalChats').textContent = d.totalChats || 0;
        document.getElementById('totalArticles').textContent = d.totalArticles || 0;
        document.getElementById('totalPlans').textContent = d.totalPlans || 0;
    } catch (err) {
        console.error('加载仪表盘失败:', err);
    }
}

async function initAdminUsers() {
    try {
        const res = await axios.get('/api/admin/users?page=1&pageSize=100', getAdminHeaders());
        const users = res.data.data || [];
        const tbody = document.getElementById('userTableBody');
        if (!tbody) return;
        tbody.innerHTML = users.map(u =>
            '<tr><td>' + u.id + '</td><td>' + escapeHtml(u.username) + '</td><td>' + escapeHtml(u.email) + '</td>' +
            '<td>' + escapeHtml(u.nickname) + '</td>' +
            '<td><span class="badge bg-' + (u.status === 1 ? 'success' : 'danger') + '">' + (u.status === 1 ? '正常' : '禁用') + '</span></td>' +
            '<td>' + (u.createdAt || '').substring(0, 10) + '</td>' +
            '<td><button class="btn btn-sm btn-warning" onclick="toggleUserStatus(' + u.id + ', ' + u.status + ')">' + (u.status === 1 ? '禁用' : '启用') +
            '</button> <button class="btn btn-sm btn-danger" onclick="deleteUser(' + u.id + ')">删除</button></td></tr>'
        ).join('');
    } catch (err) {
        console.error('加载用户列表失败:', err);
    }
}

async function toggleUserStatus(id, currentStatus) {
    const newStatus = currentStatus === 1 ? 0 : 1;
    try {
        await axios.put('/api/admin/users/' + id + '/status', {status: newStatus}, getAdminHeaders());
        initAdminUsers();
    } catch (err) {
        alert('操作失败: ' + (err.response?.data?.message || err.message));
    }
}

async function deleteUser(id) {
    if (!confirm('确定要删除此用户吗？此操作将同时删除该用户的所有数据。')) return;
    try {
        await axios.delete('/api/admin/users/' + id, getAdminHeaders());
        initAdminUsers();
    } catch (err) {
        alert('删除失败: ' + (err.response?.data?.message || err.message));
    }
}

async function initAdminChats() {
    try {
        const res = await axios.get('/api/admin/chats?page=1&pageSize=100', getAdminHeaders());
        const chats = res.data.data || [];
        const tbody = document.getElementById('chatTableBody');
        if (!tbody) return;
        tbody.innerHTML = chats.map(c =>
            '<tr><td>' + c.id + '</td><td>' + c.userId + '</td>' +
            '<td><small>' + escapeHtml(c.userMessage || '').substring(0, 50) + '</small></td>' +
            '<td><small>' + escapeHtml(c.aiResponse || '').substring(0, 80) + '</small></td>' +
            '<td>' + (c.createdAt || '').substring(0, 16) + '</td>' +
            '<td><button class="btn btn-sm btn-danger" onclick="deleteChat(' + c.id + ')">删除</button></td></tr>'
        ).join('');
    } catch (err) {
        console.error('加载聊天列表失败:', err);
    }
}

async function deleteChat(id) {
    if (!confirm('确定要删除此聊天记录吗？')) return;
    try {
        await axios.delete('/api/admin/chats/' + id, getAdminHeaders());
        initAdminChats();
    } catch (err) {
        alert('删除失败: ' + (err.response?.data?.message || err.message));
    }
}

async function initAdminArticles() {
    try {
        const res = await axios.get('/api/admin/articles?page=1&pageSize=100', getAdminHeaders());
        const articles = res.data.data || [];
        const tbody = document.getElementById('articleTableBody');
        if (!tbody) return;
        tbody.innerHTML = articles.map(a =>
            '<tr><td>' + a.id + '</td><td>' + a.userId + '</td>' +
            '<td>' + escapeHtml(a.title) + '</td><td>' + escapeHtml(a.articleType) + '</td>' +
            '<td>' + (a.createdAt || '').substring(0, 10) + '</td>' +
            '<td><button class="btn btn-sm btn-danger" onclick="deleteArticle(' + a.id + ')">删除</button></td></tr>'
        ).join('');
    } catch (err) {
        console.error('加载文章列表失败:', err);
    }
}

async function deleteArticle(id) {
    if (!confirm('确定要删除此文章吗？')) return;
    try {
        await axios.delete('/api/admin/articles/' + id, getAdminHeaders());
        initAdminArticles();
    } catch (err) {
        alert('删除失败: ' + (err.response?.data?.message || err.message));
    }
}

async function initAdminPlans() {
    try {
        const res = await axios.get('/api/admin/plans?page=1&pageSize=100', getAdminHeaders());
        const plans = res.data.data || [];
        const tbody = document.getElementById('planTableBody');
        if (!tbody) return;
        tbody.innerHTML = plans.map(p =>
            '<tr><td>' + p.id + '</td><td>' + p.userId + '</td>' +
            '<td>' + escapeHtml(p.major) + '</td><td>' + escapeHtml(p.course) + '</td>' +
            '<td>' + escapeHtml(p.goal || '').substring(0, 40) + '</td>' +
            '<td>' + (p.createdAt || '').substring(0, 10) + '</td>' +
            '<td><button class="btn btn-sm btn-danger" onclick="deletePlan(' + p.id + ')">删除</button></td></tr>'
        ).join('');
    } catch (err) {
        console.error('加载计划列表失败:', err);
    }
}

async function deletePlan(id) {
    if (!confirm('确定要删除此学习计划吗？')) return;
    try {
        await axios.delete('/api/admin/plans/' + id, getAdminHeaders());
        initAdminPlans();
    } catch (err) {
        alert('删除失败: ' + (err.response?.data?.message || err.message));
    }
}

// ============================================================
// 工具函数
// ============================================================
function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// marked.js 配置
if (typeof marked !== 'undefined') {
    marked.setOptions({breaks: true, gfm: true});
}

// ============================================================
// 页面加载完成后初始化
// ============================================================
document.addEventListener('DOMContentLoaded', function() {
    initAuth();
});
