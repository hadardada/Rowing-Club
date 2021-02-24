const reservationFormEl = document.querySelector('#addNewResForm');

const guestListContainerEl = document.querySelector('.membersList');
const datesListContainerEl = document.querySelector('.datesList');
const activityListContainerEl = document.querySelector('.ActivityList');
const additionalRowersListContainerEl = document.querySelector('.additionalRowersList');
const boatTypeListContainerEl = document.querySelector('.boatsTypeList');

const mainRowerEl = document.querySelector('#allMembers');
const divFormBlock = document.querySelector('#Dates');
const memberAge = document.querySelector('#Activity');
const memberEmail = document.querySelector('#BoatsTypes');
const memberPhoneNum = document.querySelector('#additionalRowers');

//global
let mainRowerEmail;
let trainingDate;
let activityIdGlobe;
let additionalRowers = new Array();
let boatTypes = new Array();
let showAllMem;



const formErrorEl = document.querySelector('#errorSpan');
const addedMsgEl = document.querySelector('#addedMsgSpan')
reservationFormEl.addEventListener('submit', validateForm);

const EMAIL_FORMAT = "Email Address is wrong";
const MEMBER_AGE = "Members age is from 10 to 120";
const PHONE_NUM_DIGITS = "Phone number Digits numbers is not 10";
const PHONE_NUM_FORMAT = 'It Looks Like the Phone number Address is wrong';
const NO_ERROR = '';

main()

async function main(){
    await showAllMembers(true);
}

//////////////////////////////////////////////////////////////////// display Dates //////////////////////////////////////


async function showAllDate(){
    datesListContainerEl.innerHTML = '';
    let i;
    let today = new Date();
    for (i = 0; i < 8; i++) {
        let date = new Date()
        date.setFullYear(today.getFullYear());
        date.setMonth(today.getMonth())
        date.setDate((today.getDate()+i));
        let dateEl = createDateElement(date);
        datesListContainerEl.append(dateEl);
        const nameTitle = document.createElement("br");
        datesListContainerEl.append(nameTitle);
    }
    await showAllActivities();
}

function trainingDateFunc()
{
    trainingDate = this.id;

}

function createDateElement(dates) {

    const el = document.createElement("p");
    const radioDateEl = document.createElement('input');
    radioDateEl.setAttribute("type", "radio");
    radioDateEl.setAttribute("name", "trainingDate");
    radioDateEl.id = dates.getFullYear().toString() + "-" + (dates.getMonth()+1).toString()  + "-" + dates.getDate().toString();
    if ((dates.getMonth()+1)%10 === (dates.getMonth()+1))
    {
        radioDateEl.id = dates.getFullYear().toString() + "-0" + (dates.getMonth()+1).toString()  + "-" + dates.getDate().toString();
    }
    if ((dates.getDate()+1)%10 === (dates.getDate()+1))
    {
        radioDateEl.id = dates.getFullYear().toString() + "-" + (dates.getMonth()+1).toString()  + "-0" + dates.getDate().toString();
    }
    radioDateEl.addEventListener('change', trainingDateFunc);

    radioDateEl.style.position = 'absolute';
    radioDateEl.style.left = '5px'
    el.append(radioDateEl);

    const nameEl = document.createElement('span');
    nameEl.innerText = dates.getDate().toString() + "-" + (dates.getMonth()+1).toString()  + "-" + dates.getFullYear().toString();
    el.append(nameEl);
    nameEl.style.position = 'absolute';
    nameEl.style.left = '30px'

    return el;
}


//////////////////////////////////////////////////////////////////// display members  ////////////////////

async function showAllMembers(mainOrAdditonal) {
    const response = await fetch('/member/showAll', {
        method: 'get',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
    });
    const value = await response.json();
    showAllMem = value;
    createMemberList(value,mainOrAdditonal);
    await showAllDate();

}

function createMemberList(memberList,mainOrAdditonal) {
    if (mainOrAdditonal){
        guestListContainerEl.innerHTML = '';
    }
    else {
        additionalRowersListContainerEl.innerHTML = '';
    }

   // guestListContainerEl.style.position = 'absolute';
   // guestListContainerEl.style.top = '150px'


    // Create Elements on from data
    memberList.forEach((member) => {
        const memberEl = createMemberElement(member,mainOrAdditonal);
        if (mainOrAdditonal){
            guestListContainerEl.append(memberEl);
            const nameTitle = document.createElement("br");
            guestListContainerEl.append(nameTitle);
        }
        else
        {
            additionalRowersListContainerEl.append(memberEl);
            const nameTitle = document.createElement("br");
            additionalRowersListContainerEl.append(nameTitle);
        }

    });
}

function mainMember()
{
    mainRowerEmail = this.id.substring('main'.length);
}

function additionalMembers()
{
    if (this.checked){
        additionalRowers.push(this.id.substring('add'.length));
    }
    else {
        const index = additionalRowers.indexOf(this.id);
        if (index > -1) {
            additionalRowers.splice(index, 1);
        }
    }
}

function createMemberElement(member,mainOrAdditonal) {

        const el = document.createElement("p");

        //add action button to each element
        if (mainOrAdditonal) {
            const radioMemberEl = document.createElement('input');
            radioMemberEl.setAttribute("type", "radio");
            radioMemberEl.setAttribute("name", "mainMember");
            radioMemberEl.id = 'Main'+member.email;
            radioMemberEl.addEventListener('change', mainMember);
            radioMemberEl.style.position = 'absolute';
            radioMemberEl.style.left = '5px'
            el.append(radioMemberEl);
        }
        else {
            const checkBoxMemberEl = document.createElement('input');
            checkBoxMemberEl.setAttribute("type", "checkBox");
            checkBoxMemberEl.setAttribute("name", "mainMember");
            checkBoxMemberEl.id = 'Add'+member.email;
            checkBoxMemberEl.addEventListener('change', additionalMembers);
            checkBoxMemberEl.style.position = 'absolute';
            checkBoxMemberEl.style.left = '5px'
            el.append(checkBoxMemberEl);

        }

        const nameEl = document.createElement('span');
        nameEl.innerText = "Name: " + member.name;
        el.append(nameEl);
        nameEl.style.position = 'absolute';
        nameEl.style.left = '30px'

        const idEl = document.createElement('span');
        idEl.innerText = "Email: " + member.email;
        el.appendChild(idEl)
        idEl.style.position = 'absolute';
        idEl.style.left = '200px'

    return el
}

//////////////////////////////////////////////////////////////////// display activity  ////////////////////////////////////////////////////////////////

async function showAllActivities() {
    const response = await fetch('/activity/showAll', {
        method: 'get',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
    });
    const value = await response.json();
    createActivityList(value);
    await showAllBoatType();
}

function createActivityList(activityList) {
    activityListContainerEl.innerHTML = '';

    // activityListContainerEl.style.position = 'absolute';
    // activityListContainerEl.style.top = '50px'

    // Create Elements on from data
    activityList.forEach((activity) => {
        const activityEl = createActivityElement(activity);
        activityListContainerEl.append(activityEl);
        const nameTitle = document.createElement("br");
        activityListContainerEl.append(nameTitle);
    });
}

function activityId()
{
    activityIdGlobe = this.id;
}

function createActivityElement(activity) {

    const el = document.createElement("p");

    //add action button to each element
    const radioActivityEl = document.createElement('input');
    radioActivityEl.setAttribute("type", "radio");
    radioActivityEl.setAttribute("name", "activity");
    radioActivityEl.id = activity.id;
    radioActivityEl.addEventListener('change', activityId);

    radioActivityEl.style.position = 'absolute';
    radioActivityEl.style.left = '5px'
    el.append(radioActivityEl);

    const nameEl = document.createElement('span');
    nameEl.innerText = "Name: " + activity.activityName;
    el.append(nameEl);
    nameEl.style.position = 'absolute';
    nameEl.style.left = '30px'

    const startEl = document.createElement('span');
    startEl.innerText = "Start Time: " + activity.startTime;
    el.appendChild(startEl)
    startEl.style.position = 'absolute';
    startEl.style.left = '150px'

    const endEl = document.createElement('span');
    endEl.innerText = "End Time: " + activity.endTime;
    el.appendChild(endEl)
    endEl.style.position = 'absolute';
    endEl.style.left = '300px'

    if (!(activity.boatName === '')){
        const boatNameEl = document.createElement('span');
        boatNameEl.innerText = "Boat Type: " + activity.boatName;
        el.appendChild(boatNameEl)
        boatNameEl.style.position = 'absolute';
        boatNameEl.style.left = '450px'
    }
    return el;
}

//////////////////////////////////////////////////////////////////// display BoatTypes  ////////////////////////////////////////////////////////////////

async function showAllBoatType() {
    const response = await fetch('/boatsType/showAll', {
        method: 'get',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
    });
    const value = await response.json();
    createBoatsTypeList(value);
    showAllAdditMembers(false);
}

function createBoatsTypeList(boatsTypeList) {
    boatTypeListContainerEl.innerHTML = '';

  //  boatTypeListContainerEl.style.position = 'absolute';
  //  boatTypeListContainerEl.style.top = '700px'


    // Create Elements on from data
    boatsTypeList.forEach((boatType) => {
        const boatTypeEl = createBoatTypeElement(boatType);
        boatTypeListContainerEl.append(boatTypeEl);
        const nameTitle = document.createElement("br");
        boatTypeListContainerEl.append(nameTitle);
    });
}

function boatTypeChecked()
{
    if (this.checked){
        boatTypes.push(this.id);
    }
    else {
        const index = boatTypes.indexOf(this.id);
            if (index > -1) {
                boatTypes.splice(index, 1);
            }
    }
}

function createBoatTypeElement(type) {

    const el = document.createElement("p");

    //add action button to each element
        const checkBoxBoatTypeEl = document.createElement('input');
        checkBoxBoatTypeEl.setAttribute("type", "checkBox");
        checkBoxBoatTypeEl.setAttribute("name", "boatType");
        checkBoxBoatTypeEl.id = type;
        checkBoxBoatTypeEl.addEventListener('change', boatTypeChecked);
        checkBoxBoatTypeEl.style.position = 'absolute';
        checkBoxBoatTypeEl.style.left = '5px'
        el.append(checkBoxBoatTypeEl);

    const nameEl = document.createElement('span');
    nameEl.innerText = type;
    el.append(nameEl);
    nameEl.style.position = 'absolute';
    nameEl.style.left = '30px'

    return el
}

function showAllAdditMembers(mainOrAdditonal) {
    createMemberList(showAllMem, mainOrAdditonal);
}


//////////////////////////////////////////////////////////////////// submit Form  /////////////////////////////////////////////////////////////

function validateForm() {
        submitReservation(mainRowerEmail,trainingDate,activityIdGlobe,boatTypes,
            additionalRowers,"tiki@444");

}

function ReservationJson(participantRowerEmail,trainingDate,activityID,boatTypes,wantedMemberEmails,reservationMadeBy) {
    this.participantRowerEmail = participantRowerEmail;
    this.trainingDate = trainingDate;
    this.activityID = activityID;
    this.boatTypes = boatTypes;
    this.wantedMemberEmails = wantedMemberEmails;
    this.reservationMadeBy = reservationMadeBy;
}


async function submitReservation(participantRowerEmail,trainingDate,activityID,boatTypes,wantedMemberEmails,reservationMadeBy)
{
    const reservation = new ReservationJson(participantRowerEmail,trainingDate,activityID,boatTypes,wantedMemberEmails,reservationMadeBy);

    const response = await fetch('/reservation/addNew', {
        method: 'post',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
        body: JSON.stringify(reservation)
    });

    if (response.status === 200)
    {
        divFormBlock.style.display = "none";
        addedMsgEl.textContent = "A new Member was successfully added to the club!"
    }
    else{
        formErrorEl.textContent = "ERROR! " + await response.text();
        formErrorEl.style.color = "red";
    }
}

function showError(errorMsg) {
    let initMsg = "";
    if (errorMsg !== NO_ERROR)
        initMsg ="Cannot Add Member: ";
    formErrorEl.textContent = initMsg+ errorMsg;
}