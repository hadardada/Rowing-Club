const queryString = window.location.search;
const urlParams = new URLSearchParams(queryString);
const status = urlParams.get('status');
const titleEl = document.querySelector('#title');
const listEl = document.getElementsByTagName('ol')[0];
const infoMsgEl = document.querySelector('#pageInfo');

if (status ==="future") {
    infoMsgEl.textContent = "Displaying all reservations you are taking a part of for this week:";
    titleEl.textContent = "Future Reservations"
}
else if (status === "history"){
    infoMsgEl.textContent = "Displaying all past reservations you were taking a part of";
    titleEl.textContent = "History Reservations"

}


window.addEventListener('DOMContentLoaded', createList);

async function createList(){
    const response =  await fetch ('/reservation/weekly?status='+status);
    let reservations = await response.json();
    for (let i=0; i<reservations.length; i++){
        createListElemnt(reservations[i]);
    }
}

function createListElemnt(reservation){
    let newResLine = document.createElement("li");
    //date:
    let dateField = document.createElement("sp");
    newResLine.appendChild(dateField);
    dateField.textContent = " Date: "
    dateField.class = "field";
    let dateValue = document.createElement("sp");
    newResLine.appendChild(dateValue);
    dateValue.textContent = reservation.date;
    // status:
    let statusField = document.createElement("sp");
    newResLine.appendChild(statusField);
    statusField.textContent = " Status: "
    dateField.class = "field";
    let statusValue = document.createElement("sp");
    newResLine.appendChild(statusValue);
    statusValue.textContent = reservation.status;
    //Main Rower
    let rowerField = document.createElement("sp");
    newResLine.appendChild(rowerField);
    rowerField.textContent = " Main Rower: "
    rowerField.class = "field";
    let rowerValue = document.createElement("sp");
    newResLine.appendChild(rowerValue);
    rowerValue.textContent = reservation.mainRower+" ";
    let resLink = document.createElement('a');
    let link = "/reservation/showSingle.html?creator="+reservation.createdBy;
    link = link + "&createdOn="+ reservation.createdOn;
    link = link + "&date="+reservation.date;
    resLink.href = link;
    resLink.textContent = "Manage Reservation";
    newResLine.appendChild(resLink);
    listEl.appendChild(newResLine);
}
