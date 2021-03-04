const dateContainerEl = document.querySelector('#formBlock');
window.addEventListener('DOMContentLoaded',setName);

async function setName(){
    dateContainerEl.innerHTML = '';
    let i;
    let today = new Date();
    for (i = 0; i < 8; i++) {
        let date = new Date()
        date.setDate((today.getDate()+i));
        let dateForEl = formatDate(date);

        const el = document.createElement("a");
        el.innerText = dateForEl;
        el.href = "/boathouse/reservation/weekly.html?date="+dateForEl+"&status=approved";
        dateContainerEl.append(el);
        const newLine = document.createElement("br");
        dateContainerEl.append(newLine);
        const newLine1 = document.createElement("br");
        dateContainerEl.append(newLine1);
    }
}

function formatDate(date) {
    var d = new Date(date),
        month = '' + (d.getMonth() + 1),
        day = '' + d.getDate(),
        year = d.getFullYear();

    if (month.length < 2)
        month = '0' + month;
    if (day.length < 2)
        day = '0' + day;

    return [year, month, day].join('-');
}

