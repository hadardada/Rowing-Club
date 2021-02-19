const UPDATE_NAME = 1;
const UPDATE_STATUS = 2;
const UPDATE_PRIVATE = 3;
const UPDATE_OARS = 4;
const UPDATE_COXSWAIN = 5;
const UPDATE_COASTAL = 6;

const errorMsgEl = document.querySelector('#errorMsg')

//const allButtonEl = document.querySelector('#allButtons');
const updateNameButtonEl = document.querySelector('#updateName');
const updateStatusButtonEl = document.querySelector('#updateStatus');




const currBoatNameEl = document.querySelector('#currBoatName');
const newNameEl = document.querySelector('#boatName');
//allButtonEl.addEventListener('click',createUpdateReqObj);

//buttons listeners:
updateNameButtonEl.addEventListener('click',createUpdateReqObj);
updateStatusButtonEl.addEventListener('click',createUpdateReqObj );
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
    let data;
    if (this === updateNameButtonEl) {
        const newNameEl = document.querySelector("#boatName");
        data = new updateReq(UPDATE_NAME, newNameEl.value, boatSerialNumber);
        console.log(data.whatToUpdate);
    }
    else if (this === updateStatusButtonEl){
        const updateBoatStatusOutOfEl = document.querySelector('#outof');
        let statusVal;
        //const updateBoatStatusActiveEl = document.querySelector('#active');
        if (updateBoatStatusOutOfEl.checked)
            statusVal = true;
        else
            statusVal = false;
        data = new updateReq(UPDATE_STATUS, statusVal, boatSerialNumber);
    }

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
        if (response.ok) {
            boatNameEl.textContent = updatedReq.updateTo;
            boatNameEl.style = "color:green";
        } else {
            errorMsgEl.textContent = "Somthing Went Wrong. Could not update name";
            errorMsgEl.style = "color:red";

        }
    } else if (data.whatToUpdate === UPDATE_STATUS) {
        if (response.ok) {

            showStatus(updatedReq.updateTo);
            boatStatusEl.style = "color:green";
        } else {
            errorMsgEl.textContent = "Something Went Wrong. Could not update status";
            errorMsgEl.style = "color:red";

        }
    }

}