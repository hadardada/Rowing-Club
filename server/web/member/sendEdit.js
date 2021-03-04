//update consts
const UPDATE_NAME = 1;
const UPDATE_EMAIL = 2;
const UPDATE_AGE = 3;
const UPDATE_PHONE = 4;
const UPDATE_PASSWORD = 5;
const UPDATE_PRIVATE = 6;
const UPDATE_MANAGER = 7;
const UPDATE_LEVEL = 8;
const UPDATE_NOTES = 9;
const UPDATE_EXPIREY = 10;

//errors massage
const NO_LEVEL_CHOSEN = "cannot send rowing level update if no level was chosen"
const NO_MANAGER_CHOSEN = "cannot send management update if no radio was chosen"
const NO_ERROR = '';
const EMAIL_FORMAT = "Email Format is Wrong";
const MEMBER_AGE = "Members age is from 10 to 120";
const PHONE_NUM_DIGITS = "Phone number Digits numbers is not 10";
const PHONE_NUM_FORMAT = 'It Looks Like the Phone number Address is wrong';
const TIME_FORMAT = "Date is not in the requested format (YYYY-MM-DD)";


//update-events listeners
updateNameButtonEl.addEventListener('click',createUpdateReqObj );
updateEmailButtonEl.addEventListener('click',createUpdateReqObj );
updateAgeButtonEl.addEventListener('click',createUpdateReqObj );
updatePhoneButtonEl.addEventListener('click',createUpdateReqObj );
updatePasswordButtonEl.addEventListener('click',createUpdateReqObj );
updateLevelButtonEl.addEventListener('click',createUpdateReqObj );
updatePrivateButtonEl.addEventListener('click',createUpdateReqObj );
updateManagerEl.addEventListener('click',createUpdateReqObj );
updateNotesEl.addEventListener('click',createUpdateReqObj );
updateExpireButtonEl.addEventListener('click', createUpdateReqObj);


const memberLevel1 = document.querySelector('#Beginner');
const memberLevel2 = document.querySelector('#Mid');
const memberLevel3 = document.querySelector('#Expert');

function updateReq (whatToUpdate, updateTo, memberId){
    this.whatToUpdate = whatToUpdate;
    this.updateTo = updateTo;
    this.memberId = memberId;
}

function createUpdateReqObj(){
    showError(NO_ERROR);
    //after every click we would like to reset the error message (and show it again if necessary)

    let data;
    if (this === updateNameButtonEl) {
        const newNameEl = document.querySelector('#MemberName');
        data = new updateReq(UPDATE_NAME, newNameEl.value, memberId);
    }
    else if (this === updateEmailButtonEl){
        const newEmail = document.querySelector('#MemberEmail');
        let emailRegex = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        if (!emailRegex.test(newEmail.value)){
            showError(EMAIL_FORMAT);
        }
        else {
            data = new updateReq(UPDATE_EMAIL, newEmail.value, memberId);
        }
    }
    else if (this === updateAgeButtonEl){
        const newAge = document.querySelector('#MemberAge');
        if (parseInt(newAge.value) < 10 || parseInt(newAge.value) > 120){
            showError(MEMBER_AGE);
        }
        else {
            data = new updateReq(UPDATE_AGE, newAge.value, memberId);
        }
    }

    else if(this === updatePhoneButtonEl){
        const newPhone = document.querySelector('#MemberPhoneNum');
        if (newPhone.value.length !== 10){
            showError(PHONE_NUM_DIGITS);
        }
        else if(newPhone.value.match(/^[0-9]+$/) == null){
            showError(PHONE_NUM_FORMAT);
        }
        else {
            data = new updateReq(UPDATE_PHONE, newPhone.value, memberId);
        }
    }

    else if (this === updatePasswordButtonEl) {
        const newPassword = document.querySelector('#MemberPassword');
        data = new updateReq(UPDATE_PASSWORD, newPassword.value, memberId);
    }
    else if (this === updateLevelButtonEl){
        if (memberLevel1.checked)
            data = new updateReq(UPDATE_LEVEL, memberLevel1.value, memberId);
        else if (memberLevel2.checked)
            data = new updateReq(UPDATE_LEVEL, memberLevel2.value, memberId);
        else
            if (!memberLevel3.checked)
                showError(NO_LEVEL_CHOSEN);
            else
                data = new updateReq(UPDATE_LEVEL, '3', memberId);
    }
    else if (this === updatePrivateButtonEl){
        let privateBoat = -1;
        if (privateCheckboxEl.checked)
        {
            const serialNum = document.querySelector('#boatSerNum');
            privateBoat =serialNum.value;
        }
        data = new updateReq(UPDATE_PRIVATE, privateBoat, memberId);
    }

    else if(this === updateManagerEl){
        let isManager;
        const yesToManager = document.querySelector('#managerYes');
        const noToManager = document.querySelector('#managerNo');
        if (yesToManager.checked)
            data = new updateReq(UPDATE_MANAGER, yesToManager.checked, memberId);
        else if (noToManager.checked)
            data = new updateReq(UPDATE_MANAGER, false, memberId);
        else
            showError(NO_MANAGER_CHOSEN);
    }
    else if (this === updateNotesEl) {

        const notesEl = document.querySelector('#MemberNotes')
        data = new updateReq(UPDATE_NOTES, notesEl.value, memberId);

    }
    else {
        let dateRegex = "/^\d{4}\-(0[1-9]|1[012])\-(0[1-9]|[12][0-9]|3[01])$/";
        if (this === updateExpireButtonEl){//expirey date
            const expiration = document.querySelector('#newExpire')
            if(expiration.value.match(dateRegex)==null){
                showError(TIME_FORMAT);
            }
            else
                data = new updateReq(UPDATE_EXPIREY, expiration.value, memberId);
        }
        }

    if (typeof data !== 'undefined')
        sendUpdateReq(data);
}

async function sendUpdateReq(data){
    const response = await fetch('/member/edit',
        {
            method: 'post',
            headers: new Headers({
                'Content-Type': 'application/json;charset=utf-8'
            }),
            body: JSON.stringify(data)
        });
    if (response.status ===200) {
        const updatedReq = await response.json();
        if (data.whatToUpdate === UPDATE_NAME) {
            currNameEl.textContent = updatedReq.updateTo;
            currNameEl.style = "color:green";
        } else if (data.whatToUpdate === UPDATE_EMAIL) {
            currEmailEl.textContent = updatedReq.updateTo;
            currEmailEl.style = "color:green";
            memberId = updatedReq.updateTo;
        } else if (data.whatToUpdate == UPDATE_AGE) {
            currAgeEl.textContent = updatedReq.updateTo;
            currAgeEl.style = "color:green";
        } else if (data.whatToUpdate === UPDATE_PHONE) {
            currPhoneEl.textContent = updatedReq.updateTo;
            currPhoneEl.style = "color:green";
        } else if (data.whatToUpdate == UPDATE_PASSWORD) {
            currPasswordEl.textContent = updatedReq.updateTo;
            currPasswordEl.style = "color:green";
        } else if (data.whatToUpdate === UPDATE_LEVEL) {
            showRowingLevel(updatedReq.updateTo, currLevelEl);
            currLevelEl.style = "color:green";
        } else if (data.whatToUpdate === UPDATE_PRIVATE) {
            if (updatedReq.updateTo === '-1')
                currPrivateEl.textContent = "No";
            else
                currPrivateEl.textContent = "Yes. Serial num: " + updatedReq.updateTo;
            currPrivateEl.style = "color:green";
        } else if (data.whatToUpdate == UPDATE_MANAGER) {
            if (updatedReq.updateTo === 'true')
                currManagerEl.textContent = "Yes"
            else
                currManagerEl.textContent = "No"
            currManagerEl.style = "color:green";
        } else if (data.whatToUpdate === UPDATE_NOTES) {
            currNotesEl.textContent = updatedReq.updateTo;
            currNotesEl.style = "color:green";
        } else if (data.whatToUpdate === UPDATE_EXPIREY) {
            currExpirationEl.textContent = updatedReq.updateTo;
            currExpirationEl.style = "color:green";
        }
    }
    else{
        const updatedReqErr = await response.text();
        showError(updatedReqErr);
    }


}

function showError(errorMsg){
    if (errorMsg === NO_ERROR)
        errorMsgEl.textContent = '';
    else{
        errorMsgEl.textContent = "Error! "+ errorMsg;
        alert("Error! "+ errorMsg);
    }
}