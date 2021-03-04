const queryString = window.location.search;
const urlParams = new URLSearchParams(queryString);
let createdOnId = urlParams.get('createdOn');
let creator = urlParams.get('creator');
let date = urlParams.get('date');


deleteRes()
async function deleteRes() {
    const response = await fetch('/boathouse/reservation/delete?creator=' + creator + '&createdOn=' + createdOnId + '&date=' + date, {
        method: 'post',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
    });
}