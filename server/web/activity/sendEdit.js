const errorMsgEl = document.querySelector('#errorSpan');
const oneRowerRadioEl = document.querySelector('#solo');
const twoRowerRadioEl = document.querySelector('#pair');
const eightRowerRadioEl = document.querySelector('#eight');
const coxswainRadioEl = document.querySelector('#coxswain');
const oneOarRadioEl = document.querySelector('#singleOar');
const coastalRadioEl = document.querySelector('#coastal');
const widthRadioEl = document.querySelector('#wide');

const UPDATE_NAME = 1;
const UPDATE_STARTS = 2;
const UPDATE_ENDS = 3;
const UPDATE_TYPE = 4;

//update buttons: //updateNoTypeBttnEl
const updateNameButtonEl = document.querySelector('#updateName');
const updateStartButtonEl = document.querySelector('#updateStarts');
const updateEndsButtonEl = document.querySelector('#updateEnds');

//inputs:
//let startTime = document.querySelector('#starts');
//let endTime = document.querySelector('#ends');
let startTime = startTimeEl;
let endTime = endTimeEl;

//listeners:
updateNameButtonEl.addEventListener('click', createUpdateReqObj);
updateEndsButtonEl.addEventListener('click', createUpdateReqObj);
updateStartButtonEl.addEventListener('click', createUpdateReqObj);
boatTypeFormEl.addEventListener('submit', createUpdateReqObj);
updateNoTypeBttnEl.addEventListener('click', createUpdateReqObj);

function boatType (boatSize, oneOar,width,coxswain, coastalboat){
    this.rowersNum = boatSize;
    this.singleOar = oneOar;
    this.wide = width;
    this.helmsman = coxswain;
    this.coastal = coastalboat;
    this.hasBoat = hasBoat;
}


function updateReq (whatToUpdate, updateTo, activityId){
    this.whatToUpdate = whatToUpdate;
    this.updateTo = updateTo;
    this.parameters = null;
    this.activityId = activityId;
}

function createUpdateReqObj(event) {
    //after every click we would like to reset the error message (and show it again if necessary)
    showError(NO_ERROR);
    let data;
    if (this === updateNameButtonEl) {
        const newNameEl = document.querySelector('#newName');
        data = new updateReq(UPDATE_NAME, newNameEl.value, activityId);
    } else if (this === updateStartButtonEl) {
        startTime = document.querySelector('#starts');
        endTime.value = endTimeEl.textContent;
        let newActivityUpdate = validateForm();
        if (typeof newActivityUpdate !== 'undefined') {
            data = new updateReq(UPDATE_STARTS, startTime.value, activityId);
        }
    } else if (this === updateEndsButtonEl) {
        endTime = document.querySelector('#ends');
        startTime.value = startTimeEl.textContent;
        let newActivityUpdate = validateForm();
        if (typeof newActivityUpdate !== 'undefined') {
            data = new updateReq(UPDATE_ENDS, endTime.value, activityId);
        }
    } else if (this === updateNoTypeBttnEl) {
        data = new updateReq(UPDATE_TYPE, null, activityId);
    } else if (this === boatTypeFormEl) {
        startTime = document.querySelector('#starts');
        endTime = document.querySelector('#ends');
        event.preventDefault();
        let sendForm = true;
        if (oneRowerRadioEl.checked && oneOarRadioEl.checked) {
            //A boat with only one rower cannot be with single oar
            sendForm = false;
            showError(ONE_ROWER_ONE_OAR);
        } else if (oneRowerRadioEl.checked && coxswainRadioEl.checked) {
            //A boat with only one rower cannot have a coxswain
            sendForm = false;
            showError(ONE_ROWER_WITH_COXS);
        } else if (eightRowerRadioEl.checked && !coxswainRadioEl.checked) {
            //A boat with eight rowers cannot be coxless
            sendForm = false;
            showError(EIGHT_ROWERS_NO_COXS);
        } else {
            let boatSize;
            if (oneRowerRadioEl.checked)
                boatSize = oneRowerRadioEl.value;
            else if (twoRowerRadioEl.checked)
                boatSize = twoRowerRadioEl.value;
            else if (eightRowerRadioEl.checked)
                boatSize = eightRowerRadioEl.value;
            else
                boatSize = 'four';

            if (sendForm) {
                let updateTypeBoat = new activityJson('', '', '', boatSize, oneOarRadioEl.checked, widthRadioEl.checked,
                    coxswainRadioEl.checked, coastalRadioEl.checked, true);
                let updateToType = JSON.stringify(updateTypeBoat);
                data = new updateReq(UPDATE_TYPE, updateToType, activityId);
                data.parameters = updateTypeBoat;
            }
        }

    }
    if (typeof data !== 'undefined')
        sendUpdateReq(data);
}
async function sendUpdateReq(data){
    const response = await fetch('/boathouse/activity/edit',
        {
            method: 'post',
            headers: new Headers({
                'Content-Type': 'application/json;charset=utf-8'
            }),
            body: JSON.stringify(data)
        });
    const updatedReq = await response.json();

    if (data.whatToUpdate === UPDATE_NAME) {
        activityNameEl.textContent = updatedReq.updateTo;
        activityNameEl.style = "color:green";

    } else if (data.whatToUpdate === UPDATE_STARTS) {
        startTimeEl.textContent = updatedReq.updateTo;
        startTimeEl.style = "color:green";

    }
    else if (data.whatToUpdate == UPDATE_ENDS){
        endTimeEl.textContent = updatedReq.updateTo;
        endTimeEl.style = "color:green";
    }

    else // Boat Type
    {
        boatTypeEl.textContent = updatedReq.updateTo;
        boatTypeEl.style = "color:green";
    }
}

