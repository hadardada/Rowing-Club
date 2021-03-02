const queryString = window.location.search;
const urlParams = new URLSearchParams(queryString);
let createdOnId = urlParams.get('createdOn');
let creator = urlParams.get('creator');
let date = urlParams.get('date');


rejectRes()
async function rejectRes() {
    const response = await fetch('/reservation/reject?creator=' + creator + '&createdOn=' + createdOnId + '&date=' + date, {
        method: 'get',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
    });
}