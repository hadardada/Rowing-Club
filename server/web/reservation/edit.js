//url parameters
const queryString = window.location.search;
const urlParams = new URLSearchParams(queryString);
let createdOnId = urlParams.get('createdOn');
let creator = urlParams.get('creator');
let date = urlParams.get('date');
const backEl = document.querySelector('#backButton');
backEl.href = '/boathouse/reservation/showSingle.html?creator=' + creator + '&createdOn=' + createdOnId + '&date=' + date;

//dom elements

const mainRowerCheckboeEl = document.querySelector('#changeMainRower');
const reservationDateCheckBoxEl = document.querySelector('#changeReservationDate');
const additionalRowersCheckBoxEl = document.querySelector('#changeAdditionalRowers');
const boatTypescheckBoxEl = document.querySelector('#changeReservationBoats');

const updateMainRowerButton = document.querySelector('#updateMAinRower');
const updateDateButton = document.querySelector('#updateDate');
const updateRowersButton = document.querySelector('#updateAdditionalRowers');
const updateBoatTypesButton = document.querySelector('#updateBoats');
const fieldsEls = document.getElementsByClassName("field");

//reservation data
let additionalRowers;
let clubMembers;
let boatTypesOnRes;
let  boatTypesOnclub;
let reservationsRower;

let boatTypesChosen =[];
let additionalRowersChosen =[];
let mainRowerEmailChosen;
let dateChosen;

let datesListWasCreated = false;
let boatListWasCreated = false;
let RowersListCreated = false;
let mainRowerListCreated = false;

let updateLists = true;

const FULL_WEEK_DAYS =8;

//Event Libermans
window.addEventListener('DOMContentLoaded',showSingle);

mainRowerCheckboeEl.addEventListener('change', showList);
reservationDateCheckBoxEl.addEventListener('change', showList);
additionalRowersCheckBoxEl.addEventListener('change', showList);
boatTypescheckBoxEl.addEventListener('change', showList);

updateMainRowerButton.addEventListener('click', sendEdit);
updateDateButton.addEventListener('click', sendEdit);
updateRowersButton.addEventListener('click', sendEdit);
updateBoatTypesButton.addEventListener('click', sendEdit);


async function showSingle() {
    const response = await fetch('/boathouse/reservation/showSingle?creator='+creator+'&createdOn='+createdOnId+'&date='+date, {
        method: 'get',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
    });
    const value = await response.json();
    additionalRowers = value.wantedMemberEmails;
    boatTypesOnRes = value.boatTypes;
    reservationsRower = value.participantRowerEmail;
    fillOutFields(value);
}


function fillOutFields (res) {
    //fill out the fiels:
    fieldsEls[0].textContent = res.participantRowerEmail;
    fieldsEls[1].textContent = res.trainingDate;
    fieldsEls[2].textContent = res.activityTime;
    fieldsEls[3].textContent = res.reservationMadeBy;
    fieldsEls[4].textContent = res.getReservationMadeAt;
}

async function showList() {
let divListsEl = document.getElementsByClassName('checkBoxList');
    if(this === reservationDateCheckBoxEl) {
        if (this.checked) {
            let datesListEl = document.querySelector("#reservationDateList");
            if (!datesListWasCreated) {
                //remove exist list so it will be updated
                divListsEl[1].removeChild(datesListEl);
                datesListEl = document.createElement("ol");
                divListsEl[1].insertBefore(datesListEl,updateDateButton);
                datesListEl.setAttribute("id", "reservationDateList");
                //datesListEl = document.getElementById("reservationDateList");
                createDatesList(datesListEl);
                datesListWasCreated = true;
            }
            divListsEl[1].style.display = "block";
        }
        else
            divListsEl[1].style.display = "none";
    }
    else if (this === boatTypescheckBoxEl) {
        if (this.checked) {
            let BoatTypeListElements = document.querySelector("#boatTypeList");
            if (!boatListWasCreated) {
                //remove exist list so it will be updated
                divListsEl[2].removeChild(BoatTypeListElements);
                BoatTypeListElements = document.createElement("ol");
                divListsEl[2].insertBefore(BoatTypeListElements,updateBoatTypesButton);
                BoatTypeListElements.setAttribute("id", "boatTypeList");
                await createBoatsTypeList(BoatTypeListElements);
                boatListWasCreated = true;
            }

            divListsEl[2].style.display = "block";
        } else {
            divListsEl[2].style.display = "none";
        }
    }
    else {// rowers list
        if (mainRowerCheckboeEl === this) {
            if (this.checked) {
                let RowersForRowr = document.querySelector("#mainRowerList");
                if (!datesListWasCreated) {
                    divListsEl[0].removeChild(RowersForRowr);
                    RowersForRowr = document.createElement("ol");
                    divListsEl[0].insertBefore(RowersForRowr,updateMainRowerButton);
                    RowersForRowr.setAttribute("id", "mainRowerList");
                    await createMemberList(RowersForRowr, true);
                    datesListWasCreated = true;
                }
                divListsEl[0].style.display = "block";
            } else
                divListsEl[0].style.display = "none";
        }
        if (additionalRowersCheckBoxEl === this) {
            if (this.checked) {
                let additionalRowers = document.querySelector("#additionalList");
                if (!RowersListCreated) {
                    divListsEl[3].removeChild(additionalRowers);
                    additionalRowers = document.createElement("ol");
                    divListsEl[3].insertBefore(additionalRowers,updateRowersButton);
                    additionalRowers.setAttribute("id", "additionalList");
                    await createMemberList(additionalRowers, false);
                    RowersListCreated = true;
                }
                divListsEl[3].style.display = "block";
            } else
                divListsEl[3].style.display = "none";
        }
    }
}

function createDatesList(datesListEl) {
    for (let i =0; i<FULL_WEEK_DAYS; i++){
        let date = new Date()
        date.setDate((date.getDate()+i));
        let dateForEl = formatDate(date);
        let dateEl = createDateElement(dateForEl);
        datesListEl.append(dateEl);}
}

function formatDate(date) {
    var d = new Date(date),
        month = '' + (d.getMonth() + 1),
        day = '' + d.getDate(),
        year = d.getFullYear();

    if (month.length < 2)
        month = '0' + month;
    if (day.length < 2)
        day = '0' + day;

    return [year, month, day].join('-');
}


function createDateElement(date) {

    const el = document.createElement("li");
    const radioDateEl = document.createElement('input');
    radioDateEl.setAttribute("type", "radio");
    radioDateEl.setAttribute("name", "trainingDate");
    radioDateEl.setAttribute("id", date);
    radioDateEl.required = true;
    el.append(radioDateEl);

    const nameEl = document.createElement('span');
    nameEl.innerText = date;
    el.append(nameEl);
    return el;
}

async function showAllBoatType() {
    const response = await fetch('/boathouse/boatsType/showAll', {
        method: 'get',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
    });
    const value = await response.json();
    boatTypesOnclub = value;
}

async function createBoatsTypeList(BoatTypeListEl) {
    await showAllBoatType();
    // Create Elements on from data
    boatTypesOnclub.forEach((boatType) => {
        const boatTypeEl = createBoatTypeElement(boatType);
        BoatTypeListEl.append(boatTypeEl);
        const nameTitle = document.createElement("br");
        BoatTypeListEl.append(nameTitle);
    });
}

function createBoatTypeElement(type) {
    const el = document.createElement("li");
    //add action button to each element
    const checkBoxBoatTypeEl = document.createElement('input');
    checkBoxBoatTypeEl.setAttribute("type", "checkBox");
    checkBoxBoatTypeEl.setAttribute("name", "boatType");
    checkBoxBoatTypeEl.id = type;
    checkBoxBoatTypeEl.addEventListener('change', boatTypeChecked);
    el.append(checkBoxBoatTypeEl);
    if (boatTypesOnRes.includes(type)){
        checkBoxBoatTypeEl.checked = true;
        boatTypesChosen.push(type);
    }
    const nameEl = document.createElement('span');
    nameEl.innerText = type;
    el.append(nameEl);
    return el;
}

async function showAllMembers() {
    const response = await fetch('/boathouse/member/showAll', {
        method: 'get',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
    });
    const value = await response.json();
    clubMembers = value;
}

async function createMemberList(listEl,main) {
    await showAllMembers();
    // Create Elements  from data
    clubMembers.forEach((member) => {
        if (reservationsRower !== member.email) {
            let memberEl = createMemberElement(member, main);
            listEl.append(memberEl);
            const nameTitle = document.createElement("br");
            listEl.append(nameTitle);
        }

    });
}

function createMemberElement(member,main) {

    const el = document.createElement("li");
    //add action button to each element
    if (main) {
        const radioMemberEl = document.createElement('input');
        radioMemberEl.setAttribute("type", "radio");
        radioMemberEl.setAttribute("name", "mainMember");
        radioMemberEl.setAttribute("id",'Main'+member.email);
        radioMemberEl.required = true;
        if (member === reservationsRower.email)
            radioMemberEl.checked = true;
        el.append(radioMemberEl);
    }
    else {
        const checkBoxMemberEl = document.createElement('input');
        checkBoxMemberEl.setAttribute("type", "checkBox");
        checkBoxMemberEl.setAttribute("name", "mainMember");
        checkBoxMemberEl.setAttribute("id",'add'+member.email);
        if (additionalRowers.includes(member.email)) {
            checkBoxMemberEl.checked = true;
            additionalRowersChosen.push(member.email);

        }
        checkBoxMemberEl.addEventListener('change', additionalMembers);
        el.append(checkBoxMemberEl);
    }

    const nameEl = document.createElement('span');
    nameEl.innerText = "Name: " + member.name+ " Email: "+member.email;
    el.append(nameEl);

    return el
}

function additionalMembers()
{
    if (this.checked){
        additionalRowersChosen.push(this.id.substring('add'.length));
    }
    else {
        const index = additionalRowers.indexOf(this.id.substring('add'.length));
        if (index > -1) {
            additionalRowersChosen.splice(index, 1);
        }
    }
}

function boatTypeChecked()
{
    if (this.checked){
        boatTypesChosen.push(this.id);
    }
    else {
        const index = boatTypesChosen.indexOf(this.id);
        if (index > -1) {
            boatTypesChosen.splice(index, 1);
        }
    }
}

function mainMember()
{
    mainRowerEmailChosen = this.id.substring('main'.length);
}


async function sendEdit (){
let whatToUpdate;
let data;
let contentType;
    if (this === updateMainRowerButton){
        let radiosMainEl = document.getElementsByName("mainMember");
        for (let i=0; i<radiosMainEl.length;i++){
            if (radiosMainEl[i].checked) {
                mainRowerEmailChosen = radiosMainEl[i].id.substring('main'.length);
                if (additionalRowers.includes(mainRowerEmailChosen))
                    alert("This member is already on additional rowers list. You must uncheck him " +
                        "from this list before making him the main rower ");
            }
        }
        mainRowerCheckboeEl.checked = false;
        data = mainRowerEmailChosen;
        whatToUpdate = "mainRower";
        contentType = "text/plain;charset=UTF-8";
    }
    else if (this === updateDateButton){
        let DateEls = document.getElementsByName("trainingDate");
        for (let i=0; i<DateEls.length;i++){
            if (DateEls[i].checked)
                dateChosen = DateEls[i].id;
        }
        reservationDateCheckBoxEl.checked = false;
        contentType = "text/plain;charset=UTF-8";
        whatToUpdate = "trainingDate";
        data =dateChosen;
        date = dateChosen;
    }
    else if (this === updateRowersButton){
        data = JSON.stringify(additionalRowersChosen);
        whatToUpdate = "rowers";
        contentType = 'application/json;charset=utf-8';
        additionalRowersCheckBoxEl.checked = false;

    }
    else{ // boat types
        boatTypescheckBoxEl.checked = false;
        data = JSON.stringify(boatTypesChosen);
        whatToUpdate = "boats";
        contentType = 'application/json;charset=utf-8';
    }
    const response = await fetch('/boathouse/reservation/edit?creator='+creator+"&createdOn="
        +createdOnId+"&date="+date+"&what="+whatToUpdate,{
        method: 'post',
        headers: new Headers({
            'Content-Type': contentType
        }),
        body : data,
    });


    if (response.status !== 200)
    {
        let errorSpanEl = document.querySelector('#errorSpan');
        errorSpanEl.textContent ='';
        errorSpanEl.textContent = await response.text();
    }
    location.replace('/boathouse/reservation/edit.html?creator='+creator+"&createdOn="
        +createdOnId+"&date="+date);

}
