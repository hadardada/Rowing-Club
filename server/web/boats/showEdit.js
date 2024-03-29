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

var numOfRowers;

const serialNumEl = document.querySelector('#serialNum');
const boatNameEl = document.querySelector('#currBoatName');
const boatStatusEl = document.querySelector('#boatStatus');
const shortNameEl = document.querySelector('#shortName');
const privateStatusEl = document.querySelector('#privateStatus');

//get serialNum
const queryString = window.location.search;
const urlParams = new URLSearchParams(queryString);
const boatSerialNumber = urlParams.get('boatId');

window.addEventListener('DOMContentLoaded',injectParameters);
async function injectParameters(){

    //const editUrl = new URLSearchParams();
    //editUrl.append('serialNum', boatSerialNumber);
    const response = await fetch('/boathouse/boats/edit?boatId='+boatSerialNumber);
    const boatJson = await response.json();

    serialNumEl.textContent = boatJson.idNum;
    boatNameEl.textContent = boatJson.boatName;
    shortNameEl.textContent = boatJson.shortName;
    numOfRowers = boatJson.shortName[0];
    showStatus(boatJson.status);
    showOwnership(boatJson.privateProperty);
}

function showStatus(boolStatus)
{
    if ((boolStatus=== true)||(boolStatus === 'true'))
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





