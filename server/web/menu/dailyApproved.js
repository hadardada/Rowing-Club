const dateContainerEl = document.querySelector('#formBlock');
window.addEventListener('DOMContentLoaded',setName);

async function setName(){
    dateContainerEl.innerHTML = '';
    let i;
    let today = new Date();
    for (i = 0; i < 8; i++) {
        let date = new Date()
        date.setFullYear(today.getFullYear());
        date.setMonth(today.getMonth());
        date.setDate((today.getDate()+i));
        let dateForEl = date.toISOString().substring(0, 10)

        const el = document.createElement("a");
        el.innerText = dateForEl;
        el.href = "/reservation/weekly.html?date="+dateForEl+"&status=approved";
        dateContainerEl.append(el);
        const newLine = document.createElement("br");
        dateContainerEl.append(newLine);
        const newLine1 = document.createElement("br");
        dateContainerEl.append(newLine1);
    }
}

