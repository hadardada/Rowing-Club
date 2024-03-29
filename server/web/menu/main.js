
const memberNameEl = document.querySelector('#memberName');
window.addEventListener('DOMContentLoaded',setName);

async function setName(){
    const response = await fetch('/boathouse/userManagement/name', {method: 'get'});
    if (response.ok) {
        const name = await response.text();
        memberNameEl.textContent = name + '!';
    }
    else{ // server did not responded
        memberNameEl.textContent = 'You!';
    }
}

