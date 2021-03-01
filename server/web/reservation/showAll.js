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
    let lineContent = "Date: "+reservation.date+ " Status: "+ reservation.status;
    lineContent = lineContent+" Main Rower: "+reservation.mainRower +" ";
    newResLine.textContent = lineContent;
    let resLink = document.createElement('a');
    let link = "/reservation/showSingle.html?creator="+reservation.createdBy;
    link = link + "&createdOn="+ reservation.createdOn;
    link = link + "&date="+resDate;
    resLink.href = link;
    resLink.textContent = "Manage Reservation";
    newResLine.appendChild(link);
    listEl.appendChild(newResLine);
}
