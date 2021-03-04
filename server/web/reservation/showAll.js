const queryString = window.location.search;
const urlParams = new URLSearchParams(queryString);
const status = urlParams.get('status');
const titleEl = document.querySelector('#title');
const listEl = document.getElementsByTagName('ol')[0];
const infoMsgEl = document.querySelector('#pageInfo');
const divEl = document.getElementsByTagName('div')[0];

if (status ==="future") {
    infoMsgEl.textContent = "Displaying all reservations you are taking a part of for this week:";
    titleEl.textContent = "My Future Reservations"
}
else if (status === "history"){
    infoMsgEl.textContent = "Displaying all past reservations you were taking a part of";
    titleEl.textContent = "My History Reservations"

}


window.addEventListener('DOMContentLoaded', createList);

async function createList(){
    const response =  await fetch ('/boathouse/reservation/weekly?status='+status);
    let reservations = await response.json();
    if (reservations.length === 0)
        noResToShow;
    for (let i=0; i<reservations.length; i++){
        createListElemnt(reservations[i]);
    }
}

function createListElemnt(reservation){
    let newResLine = document.createElement("li");
    //date:
    let dateField = document.createElement("span");
    newResLine.appendChild(dateField);
    dateField.textContent = " Date: "
    dateField.setAttribute("class", "field" );
    let dateValue = document.createElement("span");
    newResLine.appendChild(dateValue);
    dateValue.textContent = reservation.date;
    // status:
    let statusField = document.createElement("span");
    newResLine.appendChild(statusField);
    statusField.textContent = " Status: ";
    statusField.setAttribute("class", "field" );
    let statusValue = document.createElement("span");
    newResLine.appendChild(statusValue);
    statusValue.textContent = reservation.status;
    if (reservation.status === "Pending")
        statusValue.style.color="red";
    else if (reservation.status === "Approved")
        statusValue.style.color="green";

    //Main Rower
    let rowerField = document.createElement("span");
    newResLine.appendChild(rowerField);
    rowerField.textContent = " Main Rower: "
    rowerField.setAttribute("class", "field" );
    let rowerValue = document.createElement("span");
    newResLine.appendChild(rowerValue);
    rowerValue.textContent = reservation.mainRower+" ";
    let resLink = document.createElement('a');
    let link = "/boathouse/reservation/showSingle.html?creator="+reservation.createdBy;
    link = link + "&createdOn="+ reservation.createdOn;
    link = link + "&date="+reservation.date;
    resLink.href = link;
    resLink.textContent = "Manage Reservation";
    newResLine.appendChild(resLink);
    listEl.appendChild(newResLine);
}

function noResToShow (){
    let noRes = document.createElement("span");
    divEl.appendChild(noRes);
    noRes.textContent= "No reservations to show ";

}
