const queryString = window.location.search;
const urlParams = new URLSearchParams(queryString);
let date = urlParams.get('date');
let status = urlParams.get('status');

show()
async function show(){
    const response = await fetch("/menu/showDailyAll?date="+date+"&status=" + status, {method: 'get'});
    if (response.ok) {
        const name = await response.text();
        memberNameEl.textContent = name + '!';
    }
    else{
        memberNameEl.textContent = 'You!';
    }
}

