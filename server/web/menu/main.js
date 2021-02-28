
const memberNameEl = document.querySelector('#memberName');
window.addEventListener('DOMContentLoaded',setName);

async function setName(){
    const response = await fetch('/userManagement/name', {method: 'get'});
    const name = await response.text();
    memberNameEl.textContent = name+'!';
}

