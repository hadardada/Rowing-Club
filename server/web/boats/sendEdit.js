const UPDATE_NAME = 1;
const UPDATE_STATUS = 2;
const UPDATE_PRIVATE = 3;
const UPDATE_OARS = 4;
const UPDATE_COXSWAIN = 5;
const UPDATE_COASTAL = 6;

const errorMsgEl = document.querySelector('#errorMsg')
const ONE_ROWER_ONE_OAR = "A boat with only one rower must be with two oars";
const ONE_ROWER_WITH_COXS = "A boat with only one rower must be coxless";
const EIGHT_ROWERS_NO_COXS = "A boat with eight rowers must be with a coxswain";
const NO_ERROR = '';

//button elements:
const updateNameButtonEl = document.querySelector('#updateName');
const updateStatusButtonEl = document.querySelector('#updateStatus');
const updateCoastalButtonEl = document.querySelector('#updateCoastal');
const updateOarsButtonEl = document.querySelector('#updateOars');
const updateCoxswainButtonEl = document.querySelector('#updateCoxswain');
const updatePrivateButtonEl = document.querySelector('#updateOwnership');
const updatePrivateCheckEl = document.querySelector('#checkPrivate');
const updatePublicCheckEl = document.querySelector('#checkPublic');
const boatOwnerEl = document.querySelector('#boatOwnerform');



//allButtonEl.addEventListener('click',createUpdateReqObj);

//buttons listeners:
updateNameButtonEl.addEventListener('click',createUpdateReqObj);
updateStatusButtonEl.addEventListener('click',createUpdateReqObj );
updateCoastalButtonEl.addEventListener('click', createUpdateReqObj);
updateOarsButtonEl.addEventListener('click', createUpdateReqObj);
updateCoxswainButtonEl.addEventListener('click', createUpdateReqObj);
updatePrivateButtonEl.addEventListener('click', createUpdateReqObj);
//const queryString = window.location.search;
//const urlParams = new URLSearchParams(queryString);
//const boatSerialNumber = urlParams.get('boatId');


function updateReq (whatToUpdate, updateTo, boatIdNum){
    this.whatToUpdate = whatToUpdate;
    this.updateTo = updateTo;
    this.boatIdNum = boatIdNum;
}
//checks which 'update' button was pressed and creates an update Request object accordingly
function createUpdateReqObj() {
    //after every click we would like to reset the error message (and show it again if necessary)
    showError(NO_ERROR);
    let data;
    if (this === updateNameButtonEl) {
        const newNameEl = document.querySelector("#boatName");
        data = new updateReq(UPDATE_NAME, newNameEl.value, boatSerialNumber);
    }
    else if (this === updateStatusButtonEl){
        const updateBoatStatusOutOfEl = document.querySelector('#outof');
        if (twoRadiosValCheck(this)){
            let statusVal = updateBoatStatusOutOfEl.checked;
            data = new updateReq(UPDATE_STATUS, statusVal, boatSerialNumber);}
    }
    else if (this === updateOarsButtonEl){
        const updateOarsSingleOarEl = document.querySelector('#singleOar');
        let oarsVal = updateOarsSingleOarEl.checked;
        if (twoRadiosValCheck(this)&&(validCheck(this, oarsVal))) {
            data = new updateReq(UPDATE_OARS, oarsVal, boatSerialNumber);
        }
    }

    else if(this === updatePrivateButtonEl){
        let privateVal = updatePrivateCheckEl.checked;
        if (twoRadiosValCheck(this)) {
            data = new updateReq(UPDATE_PRIVATE, privateVal, boatSerialNumber);
        }
    }

    else if (this === updateCoastalButtonEl){
        const updateCoastalEl = document.querySelector('#coastal');
        data = new updateReq(UPDATE_COASTAL, updateCoastalEl.checked, boatSerialNumber);
    }

    // updateCoxswainButtonEl:
    else {
        const updateCoxswainEl = document.querySelector('#coxswain');
        let coxswainVal = updateCoxswainEl.checked;
        if (twoRadiosValCheck(this) && (validCheck(this, coxswainVal))) {
            data = new updateReq(UPDATE_COXSWAIN, coxswainVal, boatSerialNumber);
        }
    }

    if (typeof data !== 'undefined')
        sendUpdateReq(data);
}

async function sendUpdateReq (data) {

    const response = await fetch('/boats/edit',
        {
            method: 'post',
            headers: new Headers({
                'Content-Type': 'application/json;charset=utf-8'
            }),
            body: JSON.stringify(data)
        });
    const updatedReq = await response.json();

    if (data.whatToUpdate === UPDATE_NAME) {
            boatNameEl.textContent = updatedReq.updateTo;
            boatNameEl.style = "color:green";

    } else if (data.whatToUpdate === UPDATE_STATUS) {
            showStatus(updatedReq.updateTo);
            boatStatusEl.style = "color:green";

    }
    else if (data.whatToUpdate == UPDATE_PRIVATE){
        privateStatusEl.style = "color:green";
        if (updatedReq.updateTo === 'true')
            privateStatusEl.textContent = 'Private Boat';
        else
            privateStatusEl.textContent = "Club's Property";

    }

  else
    {
        shortNameEl.textContent = updatedReq.updateTo;
        shortNameEl.style = "color:green";
    }

}

//checks whether one of the two radios were checked before sending the update request
function twoRadiosValCheck(elemnt){
    const secondOptionEl = elemnt.previousElementSibling.previousElementSibling;
    const firstOptionEl = secondOptionEl.previousElementSibling.previousElementSibling
    if ((!firstOptionEl.checked)&&(!secondOptionEl.checked)){ // both options are not checked
        errorMsgEl.textContent = "Cannot Send Update! you must check one of the update options before sending an update"
        return false;
    }
    else
        return true;
}

function validCheck(elemnt, checkedVal){
    if (elemnt === updateOarsButtonEl){
        if ((checkedVal)&&(numOfRowers === '1')) {
            // if the update request is to change a boat with one rower to be with a single oar
            showError(ONE_ROWER_ONE_OAR);
            return false;
        }}
    else if (elemnt === updateCoxswainButtonEl){
            if ((checkedVal)&&(numOfRowers === '1')){
                // if the update request is to change a boat with one rower to be with a coxswain
                showError(ONE_ROWER_WITH_COXS);
                return false;
            }
            else if ((!checkedVal)&&(numOfRowers === '8')){
                // if the update request is to change a boat with eight rowers to be without a coxswain
                showError(EIGHT_ROWERS_NO_COXS);
                return false;
            }
        }

    return true;
}

function showError(errorMsg) {
    if (errorMsg === NO_ERROR)
        errorMsgEl.textContent='';
    else
        errorMsgEl.textContent = "Cannot Update Boat: "+ errorMsg;
}
