const reservationFormEl = document.querySelector('#ApproveAction');
const selectBoatEl = document.querySelector('#selectBoat');

const approveAction = document.createElement('button');
approveAction.innerText = 'Approve Reservation'
approveAction.style.position = 'absolute';
approveAction.style.left = '5px'
approveAction.style.backgroundColor = 'green'
reservationFormEl.append(approveAction);
approveAction.addEventListener('click', validateForm);

const BoatListContainerEl = document.querySelector('.boatList');
const wantedRowersListContainerEl = document.querySelector('.wantedRowersList');

//global
let boatToApprove;
let boatToApproveMaxRowers;
let wantedRowersOriginal = new Array();

let actualRowers = new Array();
let boatCounter = 0;

const queryString = window.location.search;
const urlParams = new URLSearchParams(queryString);
let createdOnId = urlParams.get('createdOn');
let creator = urlParams.get('creator');
let date = urlParams.get('date');

const formErrorEl = document.querySelector('#errorSpan');
const addedMsgEl = document.querySelector('#addedMsgSpan')

const TOO_MUCH_MEMBERS = "Not enough or too much members selected, the number should be " + boatToApproveMaxRowers;
const NO_ERROR = '';

main()

async function main(){
    await showAllBoats();
}

//////////////////////////////////////////////////////////////////// display Boat  ////////////////////

async function showAllBoats() {
    const response = await fetch('/reservation/getRelevantBoat?creator='+creator+'&createdOn='+createdOnId+'&date='+date);
    const boatJson = await response.json();
    if (!(boatJson.length ===0)){
        createBoatList(boatJson);
    }
    else {
        selectBoatEl.innerText = "No Available Boats For This Reservation!"
        selectBoatEl.style.color = 'red';
    }
    await showWantedRowersOriginal()
}

function createBoatList(boatList) {
    BoatListContainerEl.innerHTML = '';

    // Create Elements on from data
    boatList.forEach((boat) => {
        const boatEl = createBoatElement(boat);
            BoatListContainerEl.append(boatEl);
            const nameTitle = document.createElement("br");
            BoatListContainerEl.append(nameTitle);
    });
}

function boatToApproveFunc()
{
    boatToApprove = this.id;
    boatToApproveMaxRowers = this.value;
}

function createBoatElement(boat) {

    const el = document.createElement("p");

    //add action button to each element
        const radioBoatEl = document.createElement('input');
        radioBoatEl.setAttribute("type", "radio");
        radioBoatEl.setAttribute("name", "approveBoat");
        radioBoatEl.id = boat.idNum;
        radioBoatEl.value = boat.maxRowers;

        radioBoatEl.addEventListener('change', boatToApproveFunc);
        radioBoatEl.style.position = 'absolute';
        radioBoatEl.style.left = '5px'
        if (boatCounter === 0){
            radioBoatEl.checked = true;
        }
        boatCounter++;
        el.append(radioBoatEl);

    const nameEl = document.createElement('span');
    nameEl.innerText = "Name: " + boat.boatName;
    el.append(nameEl);
    nameEl.style.position = 'absolute';
    nameEl.style.left = '30px'

    const idEl = document.createElement('span');
    idEl.innerText = "ID: " + boat.idNum;
    el.appendChild(idEl)
    idEl.style.position = 'absolute';
    idEl.style.left = '200px'

    const typeEl = document.createElement('span');
    typeEl.innerText = "Type: " + boat.shortName;
    el.appendChild(typeEl)
    typeEl.style.position = 'absolute';
    typeEl.style.left = '300px'

    return el
}

//////////////////////////////////////////////////////////////////// display wanted Original  ///////////////////////////////


async function showWantedRowersOriginal(){
    const response = await fetch('/reservation/approve?creator='+creator+'&createdOn='+createdOnId+'&date='+date, {
        method: 'get',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
    });
    const value = await response.json();
    createMemberList(value);
}

function createMemberList(membersList) {
    wantedRowersListContainerEl.innerHTML = '';

    // Create Elements on from data
    membersCount = membersList.length;
    membersList.forEach((member) => {
        const memberEl = createMemberElement(member);
        wantedRowersListContainerEl.append(memberEl);
        const nameTitle = document.createElement("br");
        wantedRowersListContainerEl.append(nameTitle);
    });

}

function additionalMembers()
{
    if (this.checked){
        actualRowers.push(this.id);
    }
    else {
        const index = actualRowers.indexOf(this.id);
        if (index > -1) {
            actualRowers.splice(index, 1);
        }
    }
}

function createMemberElement(member) {

    const el = document.createElement("p");

    const checkBoxMemberEl = document.createElement('input');
    checkBoxMemberEl.setAttribute("type", "checkBox");
    checkBoxMemberEl.setAttribute("name", "mainMember");
    checkBoxMemberEl.id = member.email;
    checkBoxMemberEl.addEventListener('change', additionalMembers);
    checkBoxMemberEl.style.position = 'absolute';
    checkBoxMemberEl.style.left = '5px'
    el.append(checkBoxMemberEl);

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

    if (membersCount===1){
        checkBoxMemberEl.indeterminate = true;
    }
    membersCount--;
    return el
}


//////////////////////////////////////////////////////////////////// submit Form  /////////////////////////////////////////////////////////////
function ApprovedJson() {
    this.BoatId = boatToApprove;
    this.ActualMembers = actualRowers;
}

function validateForm(event) {
    if (actualRowers.length+1 !== parseInt(boatToApproveMaxRowers)){
        event.preventDefault();
        showError(TOO_MUCH_MEMBERS);
    }
    else {
        submitApproveReservation();
    }
}

async function submitApproveReservation()
{
    const newApproved = new ApprovedJson();
    const response = await fetch('/reservation/approve', {
        method: 'post',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
        body: JSON.stringify(newApproved)
    });

    if (response.status === 200)
    {
        divFormBlock.style.display = "none";
        addedMsgEl.textContent = "The Reservation Approved Successfully!"
    }
    else{
        formErrorEl.textContent = "ERROR! " + await response.text();
        formErrorEl.style.color = "red";
    }
}

function showError(errorMsg) {
    let initMsg = "";
    if (errorMsg !== NO_ERROR)
        initMsg ="Cannot Approve Member: ";
    formErrorEl.textContent = initMsg+ errorMsg;
}