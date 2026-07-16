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
function getToken() { return localStorage.getItem('token') || ''; }
function isLoggedIn() { return !!getToken(); }
function getUserInfo() { const s = localStorage.getItem('user'); return s ? JSON.parse(s) : null; }
function getAuthHeaders() { return {headers: {Authorization: 'Bearer ' + getToken()}}; }

function showAlert(msg, type) {
  const a = document.getElementById('adminAlert'); if (!a) return;
  a.className = 'alert alert-' + type; a.textContent = msg;
  a.classList.remove('d-none'); setTimeout(() => a.classList.add('d-none'), 3000);
}

// 初始化页面: 检查登录状态
function initAuth() {
  if (!isLoggedIn()) return; const u = getUserInfo();
  const n = document.getElementById('userNavArea'); if (!n) return;
  n.innerHTML = `<div class="dropdown">
    <button class="btn btn-outline-light btn-sm dropdown-toggle" data-bs-toggle="dropdown">
      <i class="bi bi-person-circle"></i> ${u.nickname || u.username}
    </button> <ul class="dropdown-menu dropdown-menu-end">
      <li><a class="dropdown-item" href="/user/profile"><i class="bi bi-person"></i> 个人中心</a></li>
      <li><hr class="dropdown-divider"></li>
      <li><a class="dropdown-item" href="#" onclick="doLogout()"><i class="bi bi-box-arrow-right"></i> 退出登录</a></li>
    </ul> </div>`;
  initPage();
}

function doLogout() { localStorage.removeItem('token'); localStorage.removeItem('user'); window.location.href = '/'; }

// ============================================================
// 页面特定初始化
// ============================================================
function initPage() {
  const p = window.location.pathname;
  if (p === '/user/profile') initProfilePage();
  else if (p === '/admin/dashboard' || p === '/admin') initAdminDashboard();
  else if (p === '/admin/users') initAdminUsers();
  else if (p === '/admin/chats') initAdminChats();
  else if (p === '/admin/articles') initAdminArticles();
  else if (p === '/admin/plans') initAdminPlans();
  else if (p === '/chat') initChatPage();
  else if (p === '/article') initArticlePage();
  else if (p === '/plan') initPlanPage();
}

// ============================================================
// AI聊天功能
// ============================================================
function initChatPage() {
  if (!isLoggedIn()) { window.location.href = '/auth/login'; return; }
  loadHistory();
}

async function sendMessage() {
  const inp = document.getElementById('messageInput'); const msg = inp.value.trim(); if (!msg) return;
  inp.value = ''; inp.disabled = true;
  const st = document.getElementById('chatStatus');
  st.textContent = '思考中...'; st.className = 'badge bg-warning';
  const c = document.getElementById('chatMessages');
  c.innerHTML += '<div class="message-bubble message-user"><strong>你：</strong><br>' + escapeHtml(msg) + '</div>';
  c.innerHTML += '<div class="message-bubble message-ai"><span class="loading-spinner"></span> AI正在思考..</div>';
  c.scrollTop = c.scrollHeight;
  try {
    const r = await axios.post('/api/chat/send', {message: msg}, getAuthHeaders());
    const d = r.data.data; const bubbles = c.querySelectorAll('.message-ai');
    const last = bubbles[bubbles.length - 1];
    last.innerHTML = '<strong>AI助手：</strong><br>' + marked.parse(d.aiResponse || '');
    safeHighlightAll();
  } catch (err) {
    st.textContent = '错误'; st.className = 'badge bg-danger';
    alert('发送失败: ' + (err.response?.data?.message || err.message));
  }
  st.textContent = '就绪'; st.className = 'badge bg-success';
  inp.disabled = false; inp.focus();
}

async function loadHistory() {
  const c = document.getElementById('chatMessages'); if (!c) return;
  try {
    const r = await axios.get('/api/chat/history?page=1&pageSize=50', getAuthHeaders());
    const list = r.data.data || []; c.innerHTML = '';
    if (list.length === 0) {
      c.innerHTML = '<div class="text-center text-muted py-5"><i class="bi bi-robot" style="font-size:3rem"></i><p class="mt-2">你好！我是AI学习助手，有什么问题可以问我？</p></div>';
      return;
    }
    const items = list.reverse ? list.reverse() : list;
    for (const item of items) {
      c.innerHTML += '<div class="message-bubble message-user"><strong>你：</strong><br>' + escapeHtml(item.userMessage) + '</div>';
      c.innerHTML += '<div class="message-bubble message-ai"><strong>AI助手：</strong><br>' + marked.parse(item.aiResponse || '') + '</div>';
    }
    safeHighlightAll();
  } catch (err) { console.error('加载历史失败:', err); }
}

async function deleteAllChats() {
  if (!confirm('确定要清空所有聊天记录吗？此操作不可恢复')) return;
  try {
    await axios.delete('/api/chat/all', getAuthHeaders());
    const c = document.getElementById('chatMessages');
    if (c) {
      c.innerHTML = '<div class="text-center text-muted py-5"><i class="bi bi-robot" style="font-size:3rem"></i><p class="mt-2">聊天记录已清空，有什么问题可以问我？</p></div>';
    }
  } catch (err) { alert('删除失败: ' + (err.response?.data?.message || err.message)); }
}

// ============================================================
// AI文章生成
// ============================================================
async function generateArticle() {
  const t = document.getElementById('articleTitle').value.trim();
  const type = document.getElementById('articleType').value;
  const r = document.getElementById('articleReqs').value.trim();
  if (!t) { alert('请输入文章标题'); return; }
  const div = document.getElementById('articleResult');
  div.innerHTML = '<div class="text-center py-5"><div class="spinner-border text-primary"></div><p class="mt-2">AI正在生成文章...</p></div>';
  try {
    const res = await axios.post('/api/article/generate', {title:t,articleType:type,requirements:r}, getAuthHeaders());
    const d = res.data.data;
    document.getElementById('resultTitle').textContent = d.title || t;
    div.innerHTML = marked.parse(d.content || '');
    document.getElementById('exportBtns').classList.remove('d-none');
    safeHighlightAll(); loadArticleHistory();
  } catch (err) {
    div.innerHTML = '<div class="alert alert-danger">生成失败: ' + (err.response?.data?.message || err.message) + '</div>';
  }
}

async function loadArticleHistory() {
  try {
    const r = await axios.get('/api/article/list?page=1&pageSize=20', getAuthHeaders());
    const list = r.data.data || [];
    const c = document.getElementById('articleHistory'); if (!c) return;
    c.innerHTML = list.length === 0 ? '<small class="text-muted">暂无历史文章</small>'
      : list.map(a => '<a href="#" class="list-group-item list-group-item-action" onclick="loadArticleFromHistory('+a.id+')">'+escapeHtml(a.title)+'</a>').join('');
  } catch (err) { console.error('加载文章历史失败:', err); }
}

async function loadArticleFromHistory(id) {
  try {
    const r = await axios.get('/api/article/' + id, getAuthHeaders());
    const a = r.data.data;
    document.getElementById('resultTitle').textContent = a.title;
    document.getElementById('articleResult').innerHTML = marked.parse(a.content || '');
    document.getElementById('exportBtns').classList.remove('d-none');
    safeHighlightAll();
  } catch (err) { alert('加载文章失败'); }
}

function exportMD() {
  const c = (document.getElementById('articleResult').innerText || '');
  const b = new Blob([c], {type:'text/markdown'});
  const a = document.createElement('a'); a.href = URL.createObjectURL(b); a.download = 'article.md'; a.click();
}

function exportDocx() { alert('Word导出功能待实现'); }

// ============================================================
// AI学习计划
// ============================================================
async function generatePlan() {
  const major = document.getElementById('planMajor').value.trim();
  const course = document.getElementById('planCourse').value.trim();
  const goal = document.getElementById('planGoal').value.trim();
  if (!major || !course) { alert('请填写专业和课程'); return; }
  const div = document.getElementById('planResult');
  div.innerHTML = '<div class="text-center py-5"><div class="spinner-border text-primary"></div><p class="mt-2">AI正在生成学习计划...</p></div>';
  try {
    const r = await axios.post('/api/plan/generate', {major, course, goal}, getAuthHeaders());
    const d = r.data.data;
    document.getElementById('planResultTitle').textContent = d.course + ' - 学习计划';
    div.innerHTML = marked.parse(d.planContent || '');
    safeHighlightAll(); loadPlanHistory();
  } catch (err) {
    div.innerHTML = '<div class="alert alert-danger">生成失败: ' + (err.response?.data?.message || err.message) + '</div>';
  }
}

async function loadPlanHistory() {
  try {
    const r = await axios.get('/api/plan/list?page=1&pageSize=20', getAuthHeaders());
    const list = r.data.data || [];
    const c = document.getElementById('planHistory'); if (!c) return;
    c.innerHTML = list.length === 0 ? '<small class="text-muted">暂无历史计划</small>'
      : list.map(p => '<a href="#" class="list-group-item list-group-item-action" onclick="loadPlanFromHistory('+p.id+')">'+escapeHtml(p.course)+' - '+escapeHtml(p.major)+'</a>').join('');
  } catch (err) { console.error('加载计划历史失败:', err); }
}

async function loadPlanFromHistory(id) {
  try {
    const r = await axios.get('/api/plan/' + id, getAuthHeaders());
    const p = r.data.data;
    document.getElementById('planResultTitle').textContent = p.course + ' - 学习计划';
    document.getElementById('planResult').innerHTML = marked.parse(p.planContent || '');
    safeHighlightAll();
  } catch (err) { alert('加载学习计划失败'); }
}

// ============================================================
// AI代码解释
// ============================================================
async function explainCode() { await handleCodeAction('/api/code/explain', '代码解释结果'); }
async function optimizeCode() { await handleCodeAction('/api/code/optimize', '代码优化结果'); }
async function addComments() { await handleCodeAction('/api/code/comment', '注释生成结果'); }

async function handleCodeAction(url, title) {
  const code = document.getElementById('codeInput').value.trim();
  if (!code) { alert('请输入代码'); return; }
  const div = document.getElementById('codeResult');
  document.getElementById('codeResultTitle').textContent = title;
  div.innerHTML = '<div class="text-center py-5"><div class="spinner-border text-primary"></div><p class="mt-2">AI正在分析代码...</p></div>';
  try {
    const r = await axios.post(url, {code}, getAuthHeaders());
    div.innerHTML = marked.parse(r.data.data || '');
    safeHighlightAll();
  } catch (err) {
    div.innerHTML = '<div class="alert alert-danger">分析失败: ' + (err.response?.data?.message || err.message) + '</div>';
  }
}

// ============================================================
// 用户中心
// ============================================================
async function initProfilePage() {
  try {
    const r = await axios.get('/api/user/profile', getAuthHeaders());
    const u = r.data.data || {};
    document.getElementById('nickname').value = u.nickname || '';
    const img = document.getElementById('avatarImg');
    if (img) img.src = u.avatar || '/static/img/default-avatar.png';
  } catch (err) { console.error('加载用户信息失败:', err); }
}

async function saveProfile() {
  const n = document.getElementById('nickname').value.trim();
  if (!n) { alert('请输入昵称'); return; }
  try {
    await axios.put('/api/user/profile', {nickname:n}, getAuthHeaders());
    alert('昵称修改成功');
  } catch (err) { alert('修改失败: ' + (err.response?.data?.message || err.message)); }
}

async function changePassword() {
  const o = document.getElementById('oldPassword').value;
  const n = document.getElementById('newPassword').value;
  if (!o || !n) { alert('请填写原密码和新密码'); return; }
  try {
    await axios.put('/api/user/password', {oldPassword:o, newPassword:n}, getAuthHeaders());
    alert('密码修改成功，请重新登录'); doLogout();
  } catch (err) { alert('修改失败: ' + (err.response?.data?.message || err.message)); }
}

async function uploadAvatar() {
  const fi = document.getElementById('avatarInput');
  if (!fi || !fi.files || !fi.files[0]) return;
  const fd = new FormData(); fd.append('file', fi.files[0]);
  try {
    const r = await axios.post('/api/user/avatar', fd, {headers:{Authorization:'Bearer '+getToken(),'Content-Type':'multipart/form-data'}});
    const img = document.getElementById('avatarImg');
    if (img) img.src = r.data.data + '?t=' + Date.now();
    alert('头像上传成功');
  } catch (err) { alert('上传失败: ' + (err.response?.data?.message || err.message)); }
}

// ============================================================
// 管理员后台
// ============================================================
function getAdminHeaders() { return {headers:{Authorization:'Bearer '+getToken()}}; }

async function initAdminDashboard() {
  try {
    const r = await axios.get('/api/admin/dashboard', getAdminHeaders());
    const d = r.data.data || {};
    document.getElementById('totalUsers').textContent = d.totalUsers || 0;
    document.getElementById('totalChats').textContent = d.totalChats || 0;
    document.getElementById('totalArticles').textContent = d.totalArticles || 0;
    document.getElementById('totalPlans').textContent = d.totalPlans || 0;
  } catch (err) { console.error('加载仪表盘失败:', err); }
}

async function initAdminUsers() {
  try {
    const r = await axios.get('/api/admin/users?page=1&pageSize=100', getAdminHeaders());
    const users = r.data.data || [];
    const tb = document.getElementById('userTableBody'); if (!tb) return;
    tb.innerHTML = users.map(u => '<tr><td>'+u.id+'</td><td>'+escapeHtml(u.username)+'</td>'+
      '<td>'+escapeHtml(u.nickname||'')+'</td>'+
      '<td><span class="badge bg-'+(u.status===1?'success':'danger')+'">'+(u.status===1?'正常':'禁用')+'</span></td>'+
      '<td>'+(u.createdAt||'').substring(0,10)+'</td>'+
      '<td><button class="btn btn-sm btn-warning" onclick="toggleUserStatus('+u.id+','+u.status+')">'+(u.status===1?'禁用':'启用')+'</button> '+
      '<button class="btn btn-sm btn-danger" onclick="deleteUser('+u.id+')">删除</button></td></tr>').join('');
  } catch (err) { console.error('加载用户列表失败:', err); }
}

async function toggleUserStatus(id, status) {
  const ns = status === 1 ? 0 : 1;
  try {
    await axios.put('/api/admin/users/'+id+'/status', {status:ns}, getAdminHeaders());
    initAdminUsers();
  } catch (err) { alert('操作失败: ' + (err.response?.data?.message || err.message)); }
}

async function deleteUser(id) {
  if (!confirm('确定要删除此用户吗？此操作将同时删除该用户的所有数据')) return;
  try { await axios.delete('/api/admin/users/'+id, getAdminHeaders()); initAdminUsers(); }
  catch (err) { alert('删除失败: ' + (err.response?.data?.message || err.message)); }
}

async function initAdminChats() {
  try {
    const r = await axios.get('/api/admin/chats?page=1&pageSize=100', getAdminHeaders());
    const chats = r.data.data || [];
    const tb = document.getElementById('chatTableBody'); if (!tb) return;
    tb.innerHTML = chats.map(c => '<tr><td>'+c.id+'</td><td>'+c.userId+'</td>'+
      '<td><small>'+escapeHtml((c.userMessage||'')).substring(0,50)+'</small></td>'+
      '<td><small>'+escapeHtml((c.aiResponse||'')).substring(0,80)+'</small></td>'+
      '<td>'+(c.createdAt||'').substring(0,16)+'</td>'+
      '<td><button class="btn btn-sm btn-danger" onclick="deleteChat('+c.id+')">删除</button></td></tr>').join('');
  } catch (err) { console.error('加载聊天列表失败:', err); }
}

async function deleteChat(id) {
  if (!confirm('确定要删除此聊天记录吗？')) return;
  try { await axios.delete('/api/admin/chats/'+id, getAdminHeaders()); initAdminChats(); }
  catch (err) { alert('删除失败: ' + (err.response?.data?.message || err.message)); }
}

async function initAdminArticles() {
  try {
    const r = await axios.get('/api/admin/articles?page=1&pageSize=100', getAdminHeaders());
    const arts = r.data.data || [];
    const tb = document.getElementById('articleTableBody'); if (!tb) return;
    tb.innerHTML = arts.map(a => '<tr><td>'+a.id+'</td><td>'+a.userId+'</td>'+
      '<td>'+escapeHtml(a.title)+'</td><td>'+escapeHtml(a.articleType)+'</td>'+
      '<td>'+(a.createdAt||'').substring(0,10)+'</td>'+
      '<td><button class="btn btn-sm btn-danger" onclick="deleteArticle('+a.id+')">删除</button></td></tr>').join('');
  } catch (err) { console.error('加载文章列表失败:', err); }
}

async function deleteArticle(id) {
  if (!confirm('确定要删除此文章吗？')) return;
  try { await axios.delete('/api/admin/articles/'+id, getAdminHeaders()); initAdminArticles(); }
  catch (err) { alert('删除失败: ' + (err.response?.data?.message || err.message)); }
}

async function initAdminPlans() {
  try {
    const r = await axios.get('/api/admin/plans?page=1&pageSize=100', getAdminHeaders());
    const plans = r.data.data || [];
    const tb = document.getElementById('planTableBody'); if (!tb) return;
    tb.innerHTML = plans.map(p => '<tr><td>'+p.id+'</td><td>'+p.userId+'</td>'+
      '<td>'+escapeHtml(p.major)+'</td><td>'+escapeHtml(p.course)+'</td>'+
      '<td>'+escapeHtml((p.goal||'')).substring(0,40)+'</td>'+
      '<td>'+(p.createdAt||'').substring(0,10)+'</td>'+
      '<td><button class="btn btn-sm btn-danger" onclick="deletePlan('+p.id+')">删除</button></td></tr>').join('');
  } catch (err) { console.error('加载计划列表失败:', err); }
}

async function deletePlan(id) {
  if (!confirm('确定要删除此学习计划吗？')) return;
  try { await axios.delete('/api/admin/plans/'+id, getAdminHeaders()); initAdminPlans(); }
  catch (err) { alert('删除失败: ' + (err.response?.data?.message || err.message)); }
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

/**
 * 安全的代码高亮函数 - 防止 hljs 未加载时报错
 */
function safeHighlightAll() {
  try {
    if (typeof hljs !== "undefined" && hljs.highlightAll) {
      hljs.highlightAll();
    }
  } catch (e) {
    console.warn('代码高亮执行失败:', e.message);
  }
}

// ============================================================
// 页面加载完成后初始化
// ============================================================
document.addEventListener('DOMContentLoaded', function() { initAuth(); });