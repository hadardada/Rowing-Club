const errorMsgEl = document.querySelector('#errorSpan');


const UPDATE_NAME = 1;
const UPDATE_STARTS = 2;
const UPDATE_ENDS = 3;
const UPDATE_TYPE = 4;

//update buttons: //updateNoTypeBttnEl
const updateNameButtonEl = document.querySelector('#updateName');
const updateStartButtonEl = document.querySelector('#updateStarts');
const updateEndsButtonEl = document.querySelector('#updateEnds');

//inputs:
const startTime = document.querySelector('#starts');
const EndTime = document.querySelector('#ends');


//listeners:
updateNameButtonEl.addEventListener('click', createUpdateReqObj);
updateEndsButtonEl.addEventListener('click', createUpdateReqObj);
updateStartButtonEl.addEventListener('click', createUpdateReqObj);
boatTypeFormEl.addEventListener('submit', createUpdateReqObj);
updateNoTypeBttnEl.addEventListener('click', createUpdateReqObj);


function updateReq (whatToUpdate, updateTo, activityId){
    this.whatToUpdate = whatToUpdate;
    this.updateTo = updateTo;
    this.parameters = null;
    this.activityId = activityId;
}

function createUpdateReqObj(event){
    //after every click we would like to reset the error message (and show it again if necessary)
    showError(NO_ERROR);
    let data;
    if (this === updateNameButtonEl) {
        const newNameEl = document.querySelector('#newName');
        data = new updateReq(UPDATE_NAME, newNameEl.value, activityId);
    }
    else if (this === updateStartButtonEl) {
        let newActivityUpdate = validateForm();
        if (typeof newActivityUpdate !== 'undefined') {
            data = new updateReq(UPDATE_STARTS, newStartTime.value, activityId);
        }
    }
    else if (this === updateEndsButtonEl) {
        let newActivityUpdate = validateForm();
        if (typeof newActivityUpdate !== 'undefined') {
            data = new updateReq(UPDATE_ENDS, newEndTime.value, activityId);
        }
    }
    else if(this === updateNoTypeBttnEl){
            data = new updateReq(UPDATE_TYPE, null, activityId);
    }

    else if (this === boatTypeFormEl){
        event.preventDefault();
        let newActivityUpdate = validateForm(boatTypeFormEl);
        if (typeof newActivityUpdate !== 'undefined'){
            data = new updateReq(UPDATE_TYPE, 'boatType', activityId );
            data.parameters = newActivityUpdate;
    }}
    if (typeof data !== 'undefined')
        sendUpdateReq(data);
}

async function sendUpdateReq(data){
    const response = await fetch('/activity/edit',
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

