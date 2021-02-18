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

const UPDATE_NAME = 1;
const UPDATE_STATUS = 2;
const UPDATE_PRIVATE = 3;
const UPDATE_OARS = 4;
const UPDATE_COXSWAIN = 5;
const UPDATE_COASTAL = 6;

const serialNumEl = document.querySelector('#serialNum');
const updateOarsButtonEl = document.querySelector('updateOars');
const updateOarsErrorEl = document.querySelector('#oarsError');

const queryString = window.location.search;
const urlParams = new URLSearchParams(queryString);
const boatSerialNumber = urlParams.get('boatId')

window.addEventListener('DOMContentLoaded',injectParameters)

async function injectParameters(){

    //const editUrl = new URLSearchParams();
    //editUrl.append('serialNum', boatSerialNumber);
    const response = await fetch('/boats/edit?boatId='+boatSerialNumber);
    const boatJson = await response.json();

    serialNumEl.value = boatJson.idNum;
}
