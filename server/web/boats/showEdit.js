function BoatJson(boatName, privateProperty, status, rowersNum, singleOar, wide, helmsman, coastal) {
    this.boatName = boatName;
    this.idNum = 0;
    this.privateProperty = privateProperty;
    this.status = status;
    this.rowersNum = rowersNum;
    this.singleOar = singleOar;
    this.wide = wide;
    this.helmsman = helmsman;
    this.coastal = coastal;
    this.shortName = '';
}




const serialNumEl = document.querySelector('#serialNum');
const boatNameEl = document.querySelector('#currBoatName');
const boatStatusEl = document.querySelector('#boatStatus');
const shortNameEl = document.querySelector('#shortName');
const privateStatusEl = document.querySelector('#privateStatus');

// buttons
const allButtonEl = document.querySelector('#allButtons');
//const updateNameButtonEl = document.querySelector('#updateName');
const updateOarsButtonEl = document.querySelector('updateOars');
const updateOarsErrorEl = document.querySelector('#oarsError');

const queryString = window.location.search;
const urlParams = new URLSearchParams(queryString);
const boatSerialNumber = urlParams.get('boatId');

window.addEventListener('DOMContentLoaded',injectParameters);
async function injectParameters(){

    //const editUrl = new URLSearchParams();
    //editUrl.append('serialNum', boatSerialNumber);
    const response = await fetch('/boats/edit?boatId='+boatSerialNumber);
    const boatJson = await response.json();

    serialNumEl.textContent = boatJson.idNum;
    boatNameEl.textContent = boatJson.boatName;
    shortNameEl.textContent = boatJson.shortName;
    showStatus(boatJson.status);
    showOwnership(boatJson.privateProperty);
}

function showStatus(boolStatus)
{
    if (boolStatus== true)
        boatStatusEl.textContent = 'Out of Order';
    else
        boatStatusEl.textContent = 'Active';
}

function showOwnership(boolPrivate){
    if (boolPrivate)
        privateStatusEl.textContent = 'Private Boat';
    else
        privateStatusEl.textContent = "Club's Property";

}




