//details :
const currNameEl = document.querySelector('#currName');
const currEmailEl = document.querySelector('#currEmail');
const currAgeEl = document.querySelector('#currAge');
const currPhoneEl = document.querySelector('#currPhone');
const currLevelEl = document.querySelector('#currLevel');
const currPrivateEl = document.querySelector('#currPrivate');
const currManagerEl = document.querySelector('#currManager');
const currNotesEl = document.querySelector('#currNotes');
const currPasswordEl = document.querySelector('#currPassword');
const currSignUpEl = document.querySelector('#currSignUp');
const currExpirationEl = document.querySelector('#currExpire');
const errorMsgEl =document.querySelector('#errorSpan');
const backEl =document.querySelector('#back');


//buttons and checkboxes:
const updateNameButtonEl = document.querySelector('#updateName');
const updateEmailButtonEl = document.querySelector('#updateEmail');
const updateAgeButtonEl = document.querySelector('#updateAge');
const updatePhoneButtonEl = document.querySelector('#updatePhone');
const updatePasswordButtonEl = document.querySelector('#updatePassword');
const updateLevelButtonEl = document.querySelector('#updateLevel');
const updatePrivateButtonEl = document.querySelector('#updatePrivate');
const privateCheckboxEl = document.querySelector('#checkPrivate');
const updateManagerEl = document.querySelector('#updateManager');
const updateNotesEl = document.querySelector('#updateNotes');
const updateExpireButtonEl = document.querySelector('#updateExpire');

//get Id
const queryString = window.location.search;
const urlParams = new URLSearchParams(queryString);
let memberId = urlParams.get('email');

//listeners
window.addEventListener('DOMContentLoaded',injectParameters);
privateCheckboxEl.addEventListener('change', privateChecked);

setDifferentBackForNoneManager()

async function setDifferentBackForNoneManager(){
    const response = await fetch('/boathouse/isUserManager', {method: 'get'});
    const isManager = await response.text();
    if (isManager==='false'){
        backEl.href = "/boathouse/menu/mainMember.html";
    }
}

async function injectParameters(){

    const response = await fetch('/boathouse/member/edit?email='+memberId);
    if (response.ok) {
        const memberJson = await response.json();
        currNameEl.textContent = memberJson.name;
        currEmailEl.textContent = memberJson.email;
        if (memberId==='me'){
            memberId = memberJson.email;
        }
        currPasswordEl.textContent = memberJson.password;
        currAgeEl.textContent = memberJson.age;
        currPhoneEl.textContent = memberJson.phoneNumber;
        currLevelEl.textContent = memberJson.rowingLevel;
        currSignUpEl.textContent = memberJson.signUpDate;
        currExpirationEl.textContent = memberJson.expirationDate;
        if (memberJson.havePrivateBoat)
            currPrivateEl.textContent = "Private Boat Serial Number: "+ memberJson.privateBoatSerialNumber;
        else
            currPrivateEl.textContent = "No"
        if (memberJson.isManager)
            currManagerEl.textContent = "Yes"
        else
            currManagerEl.textContent = "No"
        currNotesEl.textContent = memberJson.notes;
        showRowingLevel(memberJson.rowingLevel,currLevelEl );
    }
    else{
        errorMsgEl.textContent = "Error! "+ await response.text();
        errorMsgEl.style.color="red";
    }
}
